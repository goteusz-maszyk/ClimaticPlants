package dev.gotitim.climatic_plants.mixin;

import net.minecraft.references.BlockItemIds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

@Mixin(Items.class)
public class ItemsMixin {

    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static void climaticPlants$makeCarrotNonPlaceable(Args args) {
        if (args.get(0) == BlockItemIds.CARROT_CROP) {
            args.set(1, (Function<Item.Properties, Item>) Item::new);
        }
    }
}