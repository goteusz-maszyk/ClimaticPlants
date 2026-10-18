package dev.gotitim.climatic_plants.content.container;

import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BarrelContainerMenu extends AbstractContainerMenu {
    private static final int BARREL_SLOT_COUNT = 3;
    private static final int PLAYER_INV_START = BARREL_SLOT_COUNT;
    private static final int PLAYER_INV_SIZE = 36;

    private final FluidBarrelBlockEntity barrel;

    public BarrelContainerMenu(int containerId, Inventory playerInventory, BlockPos barrelPos) {
        this(containerId, playerInventory, (FluidBarrelBlockEntity)
                playerInventory.player.level().getBlockEntity(barrelPos));
    }

    public BarrelContainerMenu(int containerId, Inventory playerInventory, FluidBarrelBlockEntity barrel) {
        super(ClimaticBlockEntities.BARREL_CONTAINER, containerId);
        this.barrel = barrel;

        addSlot(new FluidItemSlot(barrel, FluidBarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 20));
        addSlot(new FluidItemSlot(barrel, FluidBarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));
        addSlot(new Slot(barrel, FluidBarrelBlockEntity.SLOT_MAIN, 89, 37) {
            @Override
            public boolean mayPickup(Player player) {
                return !isSealed();
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 96 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 154));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(barrel, player);
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player) {
        if (slotIndex >= 0 && slotIndex < BARREL_SLOT_COUNT && isSealed()) return;
        super.clicked(slotIndex, buttonNum, containerInput, player);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == 0 && barrel.getLevel() != null) {
            FluidBarrelBlock.toggleSeal(barrel.getLevel(), barrel.getBlockPos(), barrel.getBlockState());
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (isSealed()) return ItemStack.EMPTY;

        final Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < BARREL_SLOT_COUNT) {
            if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_START + PLAYER_INV_SIZE, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, BARREL_SLOT_COUNT, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    public boolean isSealed() {
        return barrel.getBlockState().getValue(FluidBarrelBlock.SEALED);
    }

    public FluidBarrelBlockEntity getBarrel() {
        return barrel;
    }

    public class FluidItemSlot extends Slot {
        public FluidItemSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack)) != null;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !isSealed();
        }
    }
}
