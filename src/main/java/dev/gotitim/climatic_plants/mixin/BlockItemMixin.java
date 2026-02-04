package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.ClimaticPlants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
        var climate = ClimaticPlants.getClimateNullable(getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null);
        if (climate == null) {
            return;
        }
        float currentTemp = player.level().getBiome(player.blockPosition()).value().getHeightAdjustedTemperature(player.blockPosition());
        float currentDownfall = player.level().getBiome(player.blockPosition()).value().climateSettings.downfall();

        if (tooltipFlag.isAdvanced()) {
            cir.getReturnValue().add(Component.literal("Temperature: " + climate.minTemperature + "..." + climate.maxTemperature + " (C: " + currentTemp + ")"));
            cir.getReturnValue().add(Component.literal("Downfall: " + climate.minDownfall + "..." + climate.maxDownfall + " (C: " + currentDownfall + ")"));
        }
        var isPerfect = true;
        if (currentTemp < climate.minTemperature) {
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toocold").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
            isPerfect = false;
        }
        if (currentTemp > climate.maxTemperature) {
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toohot").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
            isPerfect = false;
        }
        if (currentDownfall < climate.minDownfall) {
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toodry").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
            isPerfect = false;
        }
        if (currentDownfall > climate.maxDownfall) {
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.toohumid").withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.ITALIC));
            isPerfect = false;
        }
        if (isPerfect) {
            cir.getReturnValue().add(Component.translatable("item.climatic_plants.lore.perfect").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));
        }
    }
}
