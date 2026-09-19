package dev.gotitim.climatic_plants.content.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface StorageContainer extends Container {

    @Override
    default void clearContent() {
        getItems().clear();
    }

    @Override
    default void setItem(int slot, ItemStack itemStack) {
        getItems().set(slot, itemStack);
    }

    @Override
    default ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);
        if (!result.isEmpty()) {
            this.setChanged();
        }

        return result;
    }

    @Override
    default ItemStack removeItemNoUpdate(final int slot) {
        return slot >= 0 && slot < getContainerSize() ? getItems().set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        return getItems().stream().allMatch(ItemStack::isEmpty);
    }

    NonNullList<ItemStack> getItems();

    default void insertOrExtract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.isEmpty()) {
            player.setItemInHand(hand, extractItem());
        } else {
            player.setItemInHand(hand, insertItem(itemStack));
        }
    }

    ItemStack insertItem(ItemStack itemStack);

    ItemStack extractItem();
}
