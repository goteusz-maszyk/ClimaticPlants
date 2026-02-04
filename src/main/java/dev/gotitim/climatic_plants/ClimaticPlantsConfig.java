package dev.gotitim.climatic_plants;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

@Config(name = "climatic_plants")
@Config.Gui.Background("minecraft:textures/block/dirt.png")
public class ClimaticPlantsConfig implements ConfigData {
    @Comment("Difference from safe temperature for a sapling to have a chance of growing")
    public float sapling_survival_margin = 0.25F;

    @Comment("Difference from safe temperature for a crop to still grow, but much slower")
    public float crop_survival_margin = 0.33333F;

    @Comment("Here you can add values for plants that aren't generated on runtime or your personal overrides." +
            "\nNOTE: values here may be not precisely equal to the supposed ones due to the way java stores floating-point numbers")
    public Map<ResourceLocation, PreferredClimate> ranges = new HashMap<>(Map.ofEntries(
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.WHEAT), new PreferredClimate(0.3f, 1.0f, 0f, 1f)),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CARROTS), new PreferredClimate(0.3f, 1.75f, 0f, 1f)),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.POTATOES), new PreferredClimate(0.2f, 1f, 0f, 1f)),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BEETROOTS), new PreferredClimate(0f, 0.8f, 0f, 1f)),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.PUMPKIN_STEM), new PreferredClimate(0.5f, 1f, 0f, 1f)),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.MELON_STEM), new PreferredClimate(0.9f, 1.75f, 0f, 1f)),
            Map.entry(farmersDelight("rice"), new PreferredClimate(0.8f, 2f, 0f, 1f)),
            Map.entry(farmersDelight("cabbages"), new PreferredClimate(0f, 0.8f, 0f, 1f)),
            Map.entry(farmersDelight("onions"), new PreferredClimate(0.2f, 1f, 0f, 1f)),
            Map.entry(farmersDelight("budding_tomatoes"), new PreferredClimate(0.7f, 1.5f, 0f, 1f))
    ));

    public static ResourceLocation farmersDelight(String id) {
        return ResourceLocation.fromNamespaceAndPath("farmersdelight", id);
    }
}