package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.ConfigUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class BlockItemMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getTooltipLines", at = @At(value = "TAIL"))
    public void appendHoverText(Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        if (player == null) {
            return;
        }
        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null);
        if (ConfigUtils.CONFIG.ranges.containsKey(loc)) {
            float[] range = ConfigUtils.CONFIG.ranges.get(loc);
            float temp = player.level().getBiome(player.blockPosition()).value().getHeightAdjustedTemperature(player.blockPosition());
            if (temp - ConfigUtils.CONFIG.sapling_survival_margin / 2 < range[0]) {
                cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toocold").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
                return;
            }
            if (temp + ConfigUtils.CONFIG.sapling_survival_margin / 2 > range[1]) {
                cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toohot").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
                return;
            }
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.perfecttemp").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));
        }
    }
}
