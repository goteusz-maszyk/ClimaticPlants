package dev.gotitim.climatic_plants.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ItemUtils {
    private ItemUtils() {
    }

    /**
     * Transfers fluid between a held item (fluid container) and a block entity's fluid tank.
     * If the item is empty or the block is full, transfers block -> item.
     * Otherwise, transfers item -> block.
     *
     * @return whether transfer was successful
     */
    public static boolean transferFluid(ItemStack heldStack, BlockEntity blockEntity, Player player, InteractionHand hand) {
        Level level = blockEntity.getLevel();
        if (level == null) return false;

        Storage<FluidVariant> blockStorage = FluidStorage.SIDED.find(level, blockEntity.getBlockPos(), null);
        ContainerItemContext itemContext = ContainerItemContext.forPlayerInteraction(player, hand);
        Storage<FluidVariant> itemStorage = FluidStorage.ITEM.find(heldStack, itemContext);

        if (itemStorage == null || blockStorage == null) return false;

        if (isEmpty(itemStorage) || isFull(blockStorage)){
            if (tryTransfer(blockStorage, itemStorage)) {
                blockEntity.getLevel().playSound(null, player, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1,
                        1 + ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) / 16)
                );
                return true;
            }
        } else {
            if (tryTransfer(itemStorage, blockStorage)) {
                blockEntity.getLevel().playSound(null, player, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1,
                        1 + ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) / 16)
                );
                return true;
            }
        }
        return false;
    }

    private static <T> boolean tryTransfer(Storage<T> from, Storage<T> to) {
        try (Transaction tx = Transaction.openOuter()) {
            long moved = StorageUtil.move(from, to, variant -> true, Long.MAX_VALUE, tx);
            if (moved > 0) {
                tx.commit();
            }
            return moved > 0;
        }
    }

    private static boolean isEmpty(Storage<?> storage) {
        for (StorageView<?> view : storage) {
            if (!view.isResourceBlank()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFull(Storage<?> storage) {
        for (StorageView<?> view : storage) {
            if (view.getCapacity() - view.getAmount() > 0) {
                return false;
            }
        }
        return true;
    }

    public static ItemStack addStack(ItemStack stack, NonNullList<ItemStack> items, int slot) {
        ItemStack item = items.get(slot);
        if (ItemStack.isSameItemSameComponents(stack, item)) {
            int count = item.getCount() + stack.getCount();
            int overflow = count - item.getMaxStackSize();
            stack.setCount(Math.max(overflow, 0));
            item.setCount(Math.min(count, item.getMaxStackSize()));
            return stack;
        } else {
            return items.set(slot, stack);
        }
    }
}
