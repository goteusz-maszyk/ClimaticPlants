package dev.gotitim.climatic_plants.content.barrel;

import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.container.StorageContainer;
import dev.gotitim.climatic_plants.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FluidBarrelBlockEntity extends BlockEntity implements StorageContainer, SidedStorageBlockEntity {
    public static final int SLOT_MAIN = 0;
    public static final int SLOT_OVERFLOW = 1;
    public final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public static final long CAPACITY = 81000 * 10;
    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(CAPACITY, this::fluidTankChanged);
    private final ContainerStorage containerStorage = ContainerStorage.of(this, null);

    public static void tick(Level level, BlockPos pos, BlockState state, FluidBarrelBlockEntity barrel) {
        if (level.isClientSide()) return;
    }

    public FluidBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ClimaticBlockEntities.FLUID_BARREL, pos, state);
    }

    public float getFluidFillPercent() {
        if (fluidStorage.isResourceBlank()) return 0f;
        return (float) fluidStorage.getAmount() / fluidStorage.getCapacity();
    }

    public void fluidTankChanged() {
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        fluidStorage.writeValue(output);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        fluidStorage.readValue(input);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
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

    @NotNull
    @Override
    public SingleFluidStorage getFluidStorage(Direction side) {
        return fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public ContainerStorage getItemStorage(Direction side) {
        return containerStorage;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void updateRecipe() {
        setChanged();
    }

    public ItemStack extractItem() {
        if (items.get(SLOT_OVERFLOW).isEmpty()) {
            return items.set(SLOT_MAIN, ItemStack.EMPTY);
        } else {
            return items.set(SLOT_OVERFLOW, ItemStack.EMPTY);
        }
    }

    public ItemStack insertItem(ItemStack stack) {
        return ItemUtils.addStack(stack, items, SLOT_MAIN);
    }
}
