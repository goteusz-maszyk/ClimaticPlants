package dev.gotitim.climatic_plants.content.barrel;

import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.container.StorageContainer;
import dev.gotitim.climatic_plants.content.recipe.BarrelRecipe;
import dev.gotitim.climatic_plants.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FluidBarrelBlockEntity extends BlockEntity implements StorageContainer, SidedStorageBlockEntity {
    public static final int SLOT_MAIN = 0;
    public static final int SLOT_OVERFLOW = 1;
    public static final long CAPACITY = FluidConstants.BUCKET * 10;
    public final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final BarrelFluidStorage fluidStorage = new BarrelFluidStorage();
    private final ContainerStorage containerStorage = ContainerStorage.of(this, null);

    private @Nullable BarrelRecipe currentRecipe = null;
    private long recipeTick = Long.MAX_VALUE;

    public FluidBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ClimaticBlockEntities.FLUID_BARREL, pos, state);
    }

    public static void tickOuter(Level level, BlockPos pos, BlockState state, FluidBarrelBlockEntity barrel) {
        if (!level.isClientSide()) {
            barrel.tickServer(pos, state);
        }
    }

    private void tickServer(BlockPos pos, BlockState state) {
        if (getItem(SLOT_MAIN).isEmpty() && !getItem(SLOT_OVERFLOW).isEmpty()) {
            items.set(SLOT_MAIN, getItem(SLOT_OVERFLOW));
            items.set(SLOT_OVERFLOW, ItemStack.EMPTY);
            setChanged();
        }

        final boolean sealed = state.getValue(FluidBarrelBlock.SEALED);
        if (getItem(
                SLOT_OVERFLOW).isEmpty() && currentRecipe != null && (currentRecipe.isInstant() || sealed && level.getDefaultClockTime() - recipeTick > currentRecipe.duration)) {
            craft(currentRecipe);
            setChanged();
        }

        if (!sealed && state.getValue(
                FluidBarrelBlock.FACING) == Direction.UP && level.getGameTime() % 4 == 0 && level.isRainingAt(
                pos.above())) {
            try (Transaction tx = Transaction.openOuter()) {
                if (fluidStorage.insert(FluidVariant.of(Fluids.WATER), FluidConstants.fromBucketFraction(1, 1000),
                        tx
                ) > 0) {
                    tx.commit();
                    setChanged();
                }
            }
        }
    }

    public float getFluidFillPercent() {
        if (fluidStorage.isResourceBlank()) return 0f;
        return (float) fluidStorage.getAmount() / fluidStorage.getCapacity();
    }

    private void refreshRecipe() {
        currentRecipe = null;
        if (!items.get(SLOT_OVERFLOW).isEmpty()) return;
        if (level instanceof ServerLevel serverLevel) {
            BarrelRecipe.get(serverLevel, getItem(SLOT_MAIN), fluidStorage)
                        .ifPresent(holder -> currentRecipe = holder.value());
        }
    }

    private void craft(BarrelRecipe recipe) {
        final ItemStack mainStack = items.get(SLOT_MAIN);
        final long itemInputCount = recipe.inputItem == null ? 0 : recipe.inputItem.count();

        long count;
        try (Transaction tx = Transaction.openOuter()) {
            final FluidVariant variant = fluidStorage.getResource();
            final long fluidAmount = fluidStorage.getAmount();
            final long drained = variant.isBlank() ? 0 : fluidStorage.extract(variant, fluidAmount, tx);

            final long fluidInput = recipe.inputFluid.amount();
            if (itemInputCount == 0) {
                count = fluidInput == 0 ? mainStack.getCount() : drained / fluidInput;
            } else if (fluidInput == 0) {
                count = mainStack.getCount() / itemInputCount;
            } else {
                count = Math.min(drained / fluidInput, mainStack.getCount() / itemInputCount);
            }

            final FluidVariant outputVariant = recipe.outputFluid.fluidVariant();
            if (!outputVariant.isBlank()) {
                long capacity = CAPACITY;
                if (outputVariant.equals(variant)) {
                    capacity -= drained;
                }
                count = Math.min(count, capacity / recipe.outputFluid.amount());
            }

            if (outputVariant.isBlank()) {
                final long retain = drained - count * fluidInput;
                if (retain > 0) {
                    fluidStorage.insert(variant, retain, tx);
                }
            } else {
                long amount = recipe.outputFluid.amount() * count;
                if (outputVariant.equals(variant)) {
                    amount += drained;
                }
                amount = Math.min(CAPACITY, amount);
                if (amount > 0) {
                    fluidStorage.insert(outputVariant, amount, tx);
                }
            }
            tx.commit();
        }

        items.set(SLOT_MAIN, ItemStack.EMPTY);

        if (count > 0 && recipe.outputItem != null) {
            final ItemStack output = recipe.outputItem.create();
            long remaining = output.getCount() * count;
            while (remaining > 0) {
                final int chunk = (int) Math.min(remaining, output.getMaxStackSize());
                insertItemWithOverflow(output.copyWithCount(chunk));
                remaining -= chunk;
            }
        }
        final long leftover = mainStack.getCount() - count * itemInputCount;
        if (leftover > 0) {
            insertItemWithOverflow(mainStack.copyWithCount((int) Math.min(leftover, mainStack.getMaxStackSize())));
        }

        setChanged();
        if (count > 0) {
            playRecipeSound(recipe);
        }
    }

    private void playRecipeSound(BarrelRecipe recipe) {
        if (level != null) {
            level.playSound(null, worldPosition, recipe.sound.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void insertItemWithOverflow(ItemStack stack) {
        if (stack.isEmpty()) return;
        final ItemStack main = items.get(SLOT_MAIN);
        if (main.isEmpty() || ItemStack.isSameItemSameComponents(stack, main)) {
            stack = ItemUtils.addStack(stack, items, SLOT_MAIN);
        }
        if (!stack.isEmpty()) {
            final ItemStack overflow = items.get(SLOT_OVERFLOW);
            if (overflow.isEmpty() || ItemStack.isSameItemSameComponents(stack, overflow)) {
                stack = ItemUtils.addStack(stack, items, SLOT_OVERFLOW);
            }
        }
        if (!stack.isEmpty() && level != null) {
            final ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, stack
            );
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
        setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        final ItemStack result = ContainerHelper.removeItem(items, slot, count);
        setChanged();
        return result;
    }

    public ItemStack extractItem() {
        final ItemStack result = items.set(items.get(SLOT_OVERFLOW).isEmpty() ? SLOT_MAIN : SLOT_OVERFLOW, ItemStack.EMPTY);
        setChanged();
        return result;
    }

    public ItemStack insertItem(ItemStack stack) {
        final ItemStack remainder = ItemUtils.addStack(stack, items, SLOT_MAIN);
        setChanged();
        return remainder;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("recipeTick", recipeTick);
        ContainerHelper.saveAllItems(output, this.items);
        fluidStorage.writeValue(output);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        recipeTick = input.getLongOr("recipeTick", Long.MAX_VALUE);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        fluidStorage.readValue(input);
        refreshRecipe();
        if (level != null) {
            final BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        refreshRecipe();
        recipeTick = level == null || !getBlockState().getValue(FluidBarrelBlock.SEALED) ? Long.MAX_VALUE : level.getDefaultClockTime();
        if (level != null) {
            final BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!state.getValue(FluidBarrelBlock.SEALED)) {
            super.preRemoveSideEffects(pos, state);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public BarrelFluidStorage getFluidStorage(@Nullable Direction side) {
        return fluidStorage;
    }

    public BarrelFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public ContainerStorage getItemStorage(@Nullable Direction side) {
        return containerStorage;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public class BarrelFluidStorage extends SingleFluidStorage {
        @Override
        protected long getCapacity(FluidVariant variant) {
            return CAPACITY;
        }

        @Override
        protected void onFinalCommit() {
            setChanged();
        }
    }
}