package dev.gotitim.climatic_plants;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

@Config(name = "climatic_plants")
@Config.Gui.Background("minecraft:textures/block/dirt.png")
public class ClimaticPlantsConfig implements ConfigData {
    @Comment("Difference from safe temperature for a sapling to have a chance of growing")
    public float sapling_survival_margin = 0.25F;

    @Comment("Difference from safe temperature for a crop to still grow, but much slower")
    public float crop_survival_margin = 0.33333F;

    @Comment("NOTE: values here may be not precisely equal to the supposed ones due to the way java stores floating-point numbers" +
            "\nTemperature range of the plant, minimum = -1, maximum = 2.5")
    public float[] default_range = new float[]{-1f, 2.5f};

    public Map<ResourceLocation, float[]> ranges = Map.ofEntries(
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.AZALEA), new float[]{0.1f, 0.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.ACACIA_SAPLING), new float[]{1.9f, 2.1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BIRCH_SAPLING), new float[]{0.4f, 0.8f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CHERRY_SAPLING), new float[]{0.4f, 0.6f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.DARK_OAK_SAPLING), new float[]{0.65f, 0.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.JUNGLE_SAPLING), new float[]{0.85f, 1.15f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.MANGROVE_PROPAGULE), new float[]{0.7f, 0.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.OAK_SAPLING), new float[]{0.1f, 1.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.SPRUCE_SAPLING), new float[]{-0.6f, 0.4f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.WHEAT), new float[]{0.3f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CARROTS), new float[]{0.3f, 1.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.POTATOES), new float[]{0.2f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BEETROOTS), new float[]{0f, 0.8f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.PUMPKIN_STEM), new float[]{0.5f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.MELON_STEM), new float[]{0.9f, 1.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CACTUS), new float[]{1.25f, 2.5f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.SUGAR_CANE), new float[]{1f, 2f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.COCOA), new float[]{0.85f, 1.15f})
    );
}