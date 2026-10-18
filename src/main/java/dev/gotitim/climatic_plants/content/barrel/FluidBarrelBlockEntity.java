package dev.gotitim.climatic_plants.content.barrel;

import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.ClimaticSounds;
import dev.gotitim.climatic_plants.content.container.BarrelContainerMenu;
import dev.gotitim.climatic_plants.content.container.StorageContainer;
import dev.gotitim.climatic_plants.content.recipe.BarrelRecipe;
import dev.gotitim.climatic_plants.util.ItemUtils;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FluidBarrelBlockEntity extends BaseContainerBlockEntity
        implements StorageContainer, SidedStorageBlockEntity, ExtendedMenuProvider<BlockPos> {
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_MAIN = 2;
    public static final int SLOT_OVERFLOW = 3;
    public static final int SLOTS = 4;
    public static final long CAPACITY = FluidConstants.BUCKET * 10;
    public final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final BarrelFluidStorage fluidStorage = new BarrelFluidStorage();
    private final ContainerStorage containerStorage = ContainerStorage.of(this, null);

    private @Nullable BarrelRecipe currentRecipe;
    private @Nullable Component recipeOutput;
    private long recipeTick = Long.MAX_VALUE;
    private boolean needsRecipeRefresh = false;

    public FluidBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ClimaticBlockEntities.FLUID_BARREL, pos, state);
    }

    public static void tickOuter(Level level, BlockPos pos, BlockState state, FluidBarrelBlockEntity barrel) {
        if (!level.isClientSide()) {
            barrel.tickServer(pos, state);
        }
    }

    private void tickServer(BlockPos pos, BlockState state) {
        assert level != null;
        if (needsRecipeRefresh) {
            refreshRecipe();
        }
        if (getItem(SLOT_MAIN).isEmpty() && !getItem(SLOT_OVERFLOW).isEmpty()) {
            items.set(SLOT_MAIN, getItem(SLOT_OVERFLOW));
            items.set(SLOT_OVERFLOW, ItemStack.EMPTY);
            setChanged();
        }

        final boolean sealed = state.getValue(FluidBarrelBlock.SEALED);

        if (!sealed && level.getGameTime() % 5 == 0) {
            updateFluidIOSlots();
        }

        if (getItem(SLOT_OVERFLOW).isEmpty()
                && currentRecipe != null
                && (currentRecipe.isInstant()
                        || sealed && level.getDefaultClockTime() - recipeTick > currentRecipe.duration)) {
            craftCurrentRecipe();
            setChanged();
        }

        if (!sealed
                && state.getValue(FluidBarrelBlock.FACING) == Direction.UP
                && level.getGameTime() % 4 == 0
                && level.isRainingAt(pos.above())) {
            try (Transaction tx = Transaction.openOuter()) {
                if (fluidStorage.insert(FluidVariant.of(Fluids.WATER), FluidConstants.fromBucketFraction(1, 1000), tx)
                        > 0) {
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
        if (level instanceof ServerLevel serverLevel) {
            currentRecipe = null;
            recipeOutput = null;
            needsRecipeRefresh = false;
            if (!items.get(SLOT_OVERFLOW).isEmpty()) return;

            BarrelRecipe.get(serverLevel, getItem(SLOT_MAIN), fluidStorage).ifPresent(holder -> {
                currentRecipe = holder.value();
                recipeOutput = currentRecipe.mainOutput();
                serverLevel.getChunkSource().blockChanged(worldPosition);
            });
        }
    }

    private void craftCurrentRecipe() {
        if (currentRecipe == null) throw new IllegalStateException("Called craft on empty recipe");
        assert level != null;
        final ItemStack mainStack = items.get(SLOT_MAIN);
        final long itemInputCount = currentRecipe.inputItem == null ? 0 : currentRecipe.inputItem.count();

        long count;
        try (Transaction tx = Transaction.openOuter()) {
            final FluidVariant variant = fluidStorage.getResource();
            final long fluidAmount = fluidStorage.getAmount();
            final long drained = variant.isBlank() ? 0 : fluidStorage.extract(variant, fluidAmount, tx);

            final long fluidInput = currentRecipe.inputFluid.amount();
            if (itemInputCount == 0) {
                count = fluidInput == 0 ? mainStack.getCount() : drained / fluidInput;
            } else if (fluidInput == 0) {
                count = mainStack.getCount() / itemInputCount;
            } else {
                count = Math.min(drained / fluidInput, mainStack.getCount() / itemInputCount);
            }

            final FluidVariant outputVariant = currentRecipe.outputFluid.fluidVariant();
            if (!outputVariant.isBlank()) {
                long capacity = CAPACITY;
                if (outputVariant.equals(variant)) {
                    capacity -= drained;
                }
                count = Math.min(count, capacity / currentRecipe.outputFluid.amount());
            }

            if (outputVariant.isBlank()) {
                final long retain = drained - count * fluidInput;
                if (retain > 0) {
                    fluidStorage.insert(variant, retain, tx);
                }
            } else {
                long amount = currentRecipe.outputFluid.amount() * count;
                if (outputVariant.equals(variant)) {
                    amount += drained - count * fluidInput;
                }
                amount = Math.min(CAPACITY, amount);
                if (amount > 0) {
                    fluidStorage.insert(outputVariant, amount, tx);
                }
            }
            tx.commit();
        }

        items.set(SLOT_MAIN, ItemStack.EMPTY);

        if (count > 0 && currentRecipe.outputItem != null) {
            final ItemStack output = currentRecipe.outputItem.create();
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

        if (count > 0) {
            level.playSound(null, worldPosition, currentRecipe.sound.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        setChanged();
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
            final ItemEntity entity = new ItemEntity(
                    level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
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
        final ItemStack result =
                items.set(items.get(SLOT_OVERFLOW).isEmpty() ? SLOT_MAIN : SLOT_OVERFLOW, ItemStack.EMPTY);
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
    protected Component getDefaultName() {
        return Component.translatable("container.climatic_plants.barrel");
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        recipeTick = input.getLongOr("recipeTick", Long.MAX_VALUE);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        fluidStorage.readValue(input);
        needsRecipeRefresh = true;
        recipeOutput = input.read("recipe", ComponentSerialization.CODEC).orElse(null);
        if (level != null) {
            final BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        refreshRecipe();
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
        CompoundTag tag = saveWithoutMetadata(registries);
        if (recipeOutput != null) {
            tag.put(
                    "recipe",
                    ComponentSerialization.CODEC
                            .encodeStart(NbtOps.INSTANCE, recipeOutput)
                            .getOrThrow());
        }
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BarrelContainerMenu(containerId, inventory, this);
    }

    public long getRecipeTick() {
        return recipeTick;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    public void onSealed() {
        assert level != null;
        if (level.isClientSide()) return;
        recipeTick = level.getDefaultClockTime();

        final ItemStack fluidIn = getItem(SLOT_FLUID_CONTAINER_IN);
        final ItemStack fluidOut = getItem(SLOT_FLUID_CONTAINER_OUT);
        if (!fluidIn.isEmpty()) {
            level.addFreshEntity(new ItemEntity(
                    level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    fluidIn));
            items.set(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
        }
        if (!fluidOut.isEmpty()) {
            level.addFreshEntity(new ItemEntity(
                    level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    fluidOut));
            items.set(SLOT_FLUID_CONTAINER_OUT, ItemStack.EMPTY);
        }
        level.playSound(null, worldPosition, ClimaticSounds.BARREL_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void onUnsealed() {
        assert level != null;
        if (level.isClientSide()) return;
        recipeTick = Long.MAX_VALUE;
        level.playSound(null, worldPosition, ClimaticSounds.BARREL_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void updateFluidIOSlots() {
        assert level != null;
        final ItemStack input = getItem(SLOT_FLUID_CONTAINER_IN);
        if (input.isEmpty() || !getItem(SLOT_FLUID_CONTAINER_OUT).isEmpty()) {
            return;
        }

        try (Transaction tx = Transaction.openOuter()) {
            Storage<FluidVariant> itemStorage = FluidStorage.ITEM.find(
                    input, ContainerItemContext.ofSingleSlot(containerStorage.getSlot(SLOT_FLUID_CONTAINER_IN)));
            if (itemStorage == null) return;

            long moved = 0;
            if (itemStorage.supportsExtraction()) {
                moved = StorageUtil.move(itemStorage, fluidStorage, _ -> true, Long.MAX_VALUE, tx);
                if (moved > 0) {
                    level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    tx.commit();
                }
            }
            if (moved == 0 && itemStorage.supportsInsertion()) {
                moved = StorageUtil.move(fluidStorage, itemStorage, _ -> true, Long.MAX_VALUE, tx);
                if (moved > 0) {
                    level.playSound(null, worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    tx.commit();
                }
            }

            if (moved == 0) {
                return;
            }
        }

        final ItemStack filled = getItem(SLOT_FLUID_CONTAINER_IN);
        items.set(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
        items.set(SLOT_FLUID_CONTAINER_OUT, filled);
        setChanged();
    }

    @Override
    public BarrelFluidStorage getFluidStorage(@Nullable Direction side) {
        return fluidStorage;
    }

    public BarrelFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        if (level != null && level.getBlockState(worldPosition).getValue(FluidBarrelBlock.SEALED)) {
            return Storage.empty();
        }
        return containerStorage;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
    }

    public @Nullable Component getRecipeOutput() {
        return this.recipeOutput;
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
