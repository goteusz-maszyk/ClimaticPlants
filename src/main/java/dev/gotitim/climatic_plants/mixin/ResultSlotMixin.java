package dev.gotitim.climatic_plants.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.gotitim.climatic_plants.content.ClimaticTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {
    @WrapOperation(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/CraftingContainer;removeItem(II)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack reduceDurabilityInstead(CraftingContainer instance, int slot, int amount,
                                              Operation<ItemStack> original,
                                              @Local(argsOnly = true) Player player) {
        ItemStack stack = instance.getItem(slot);
        if (!(player.level() instanceof ServerLevel serverLevel)) return stack;

        if (stack.is(ClimaticTags.KNIVES)) {
            stack.hurtAndBreak(
                    amount,
                    serverLevel,
                    (ServerPlayer) player,
                    _ -> {}
            );
            return stack;
        }
        return original.call(instance, slot, amount);
    }
}
