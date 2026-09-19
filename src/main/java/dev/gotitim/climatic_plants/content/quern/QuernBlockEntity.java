package dev.gotitim.climatic_plants.content.quern;

import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.ClimaticSounds;
import dev.gotitim.climatic_plants.content.recipe.QuernRecipe;
import dev.gotitim.climatic_plants.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class QuernBlockEntity extends BlockEntity implements Container {
    public static final int SLOT_HANDSTONE = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final int MANUAL_TICKS = 90;
    public static final float MANUAL_SPEED = Mth.TWO_PI / MANUAL_TICKS; // In radians / tick

    private static final float MANUAL_RECIPE_PER_TICK = 1f; // Exactly 90 ticks at 1/tick

    public final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    public float recipeTimer;
    private QuernRecipe activeRecipe;

    public QuernBlockEntity(BlockPos pos, BlockState state) {
        super(ClimaticBlockEntities.QUERN, pos, state);

        this.recipeTimer = 0;
    }

    public static void tick(Level level, BlockPos pos, BlockState ignored, QuernBlockEntity quern) {
        final boolean wasGrinding = quern.recipeTimer > 0;
        if (quern.recipeTimer > 0) {
            quern.recipeTimer -= MANUAL_RECIPE_PER_TICK;
        }

        if (!(level instanceof ServerLevel serverLevel && wasGrinding)) return;

        sendParticle(serverLevel, pos, quern.getItem(SLOT_INPUT).getItem(), 1);

        if (quern.recipeTimer <= 0) {
            quern.finishGrinding();
            level.playSound(null, pos, SoundEvents.ARMOR_STAND_FALL, SoundSource.BLOCKS,
                    1.0F + level.getRandom().nextFloat(), level.getRandom().nextFloat() + 0.7F + 0.3F
            );

            final ItemStack handstone = quern.getItem(SLOT_HANDSTONE);
            final Item item = handstone.getItem();

            handstone.hurtAndBreak(1, serverLevel, null, _ -> {});

            if (!quern.hasHandstone()) {
                quern.updateHandstone();
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS,
                        1.0F + level.getRandom().nextFloat(), level.getRandom().nextFloat() + 0.7F + 0.3F
                );
                level.playSound(null, pos, SoundEvents.ITEM_BREAK.value(), SoundSource.BLOCKS,
                        1.0F + level.getRandom().nextFloat(), level.getRandom().nextFloat() + 0.7F + 0.3F
                );

                sendParticle(serverLevel, pos, item, 15);
            }
            quern.setChanged();
        }
    }

    private static void sendParticle(ServerLevel level, BlockPos pos, Item item, int count) {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + 0.5D, pos.getY() + 0.875D,
                pos.getZ() + 0.5D, count, MathUtils.triangleRandom(level.getRandom()) / 2.0D,
                level.getRandom().nextDouble() / 4.0D, MathUtils.triangleRandom(level.getRandom()) / 2.0D, 0.15f
        );
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null) return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void updateHandstone() {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        if (hasHandstone() != state.getValue(QuernBlock.HAS_HANDSTONE)) {
            state.setValue(QuernBlock.HAS_HANDSTONE, hasHandstone());
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return slot != SLOT_HANDSTONE || stack.is(ClimaticBlocks.QUERN.asItem());
    }

    public boolean isGrinding() {
        return recipeTimer > 0;
    }

    public boolean hasHandstone() {
        return !getItem(SLOT_HANDSTONE).isEmpty();
    }

    public boolean startGrinding() {
        if (!(level instanceof ServerLevel serverLevel)) return true;
        final ItemStack inputStack = getItem(SLOT_INPUT);

        if (inputStack.isEmpty() || !hasHandstone()) {
            return false;
        }
        final Optional<RecipeHolder<QuernRecipe>> recipe = QuernRecipe.get(serverLevel, inputStack);
        if (recipe.isPresent()) {
            this.activeRecipe = recipe.get().value();
            recipeTimer = MANUAL_TICKS;
            level.playSound(null, worldPosition, ClimaticSounds.QUERN_DRAG, SoundSource.BLOCKS, 1,
                    1 + ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) / 16)
            );
            setChanged();
            return true;
        }
        return false;
    }

    public float getRotationSpeed() {
        return isGrinding() ? MANUAL_SPEED : 0f;
    }

    private void finishGrinding() {
        recipeTimer = 0f;

        assert level != null;
        if (level.isClientSide()) return;
        final ItemStack inputStack = getItem(SLOT_INPUT);
        if (inputStack.isEmpty() || activeRecipe == null) return;

        ItemStack outputStack = activeRecipe.assemble(new SingleRecipeInput(inputStack));
        activeRecipe = null;

        final ItemStack currentOutput = getItem(SLOT_OUTPUT);
        if (currentOutput.isEmpty()) {
            setItem(SLOT_OUTPUT, outputStack.copy());
            outputStack.setCount(0);
        } else if (ItemStack.isSameItemSameComponents(currentOutput, outputStack)) {
            final int mergeAmount = Math.min(outputStack.getCount(),
                    currentOutput.getMaxStackSize() - currentOutput.getCount()
            );
            outputStack.shrink(mergeAmount);
            currentOutput.grow(mergeAmount);
        }

        if (!outputStack.isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D, outputStack
            ));
        }

        inputStack.shrink(1);
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NonNull ItemStack getItem(final int slot) {
        return this.items.get(slot);
    }

    @Override
    public @NonNull ItemStack removeItem(final int slot, final int count) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
        if (!result.isEmpty()) {
            this.setChanged();
        }

        return result;
    }

    public @NonNull ItemStack removeItem(final int slot) {
        ItemStack stack = this.removeItemNoUpdate(slot);
        if (slot != SLOT_OUTPUT) {
            finishGrinding();
        }
        if (!stack.isEmpty()) {
            this.setChanged();
        }

        return stack;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(final int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(final int slot, final @NonNull ItemStack itemStack) {
        this.items.set(slot, itemStack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(final @NonNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public ItemStack insert(ItemStack stack) {
        if (stack.is(ClimaticItems.HANDSTONE) && getItem(SLOT_HANDSTONE).isEmpty()) {
            setItem(SLOT_HANDSTONE, stack);
            return ItemStack.EMPTY;
        }
        if (getItem(SLOT_INPUT).isEmpty()) {
            setItem(SLOT_INPUT, stack);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("recipeTimer", recipeTimer);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        recipeTimer = input.getFloatOr("recipeTimer", 0);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
    }
}
