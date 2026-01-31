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
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.FLOWERING_AZALEA), new float[]{0.1f, 0.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.ACACIA_SAPLING), new float[]{1.9f, 2.1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BIRCH_SAPLING), new float[]{0.4f, 0.8f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CHERRY_SAPLING), new float[]{0.4f, 0.6f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.DARK_OAK_SAPLING), new float[]{0.65f, 0.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.JUNGLE_SAPLING), new float[]{0.85f, 1.15f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.MANGROVE_PROPAGULE), new float[]{0.7f, 0.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.OAK_SAPLING), new float[]{0.1f, 1.9f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.SPRUCE_SAPLING), new float[]{-0.5f, 0.4f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.WHEAT), new float[]{0.3f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CARROTS), new float[]{0.3f, 1.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.POTATOES), new float[]{0.2f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BEETROOTS), new float[]{0f, 0.8f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.PUMPKIN_STEM), new float[]{0.5f, 1f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.MELON_STEM), new float[]{0.9f, 1.75f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.CACTUS), new float[]{1.5f, 2.5f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.SUGAR_CANE), new float[]{1f, 2f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.COCOA), new float[]{0.85f, 1.15f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.SWEET_BERRY_BUSH), new float[]{0f, 0.7f}),
            Map.entry(BuiltInRegistries.BLOCK.getKey(Blocks.BAMBOO), new float[]{0.9f, 2f}),
            Map.entry(naturesSpirit("redwood_sapling"), new float[]{-0.1f, 0.5f}),
            Map.entry(naturesSpirit("sugi_sapling"), new float[]{0.85f, 1.1f}),
            Map.entry(naturesSpirit("blue_wisteria_sapling"), new float[]{0.7f, 0.95f}),
            Map.entry(naturesSpirit("white_wisteria_sapling"), new float[]{0.7f, 0.95f}),
            Map.entry(naturesSpirit("pink_wisteria_sapling"), new float[]{0.7f, 0.95f}),
            Map.entry(naturesSpirit("purple_wisteria_sapling"), new float[]{0.7f, 0.95f}),
            Map.entry(naturesSpirit("fir_sapling"), new float[]{-0.1f, 0.75f}),
            Map.entry(naturesSpirit("willow_sapling"), new float[]{0.65f, 0.95f}),
            Map.entry(naturesSpirit("aspen_sapling"), new float[]{0.35f, 0.55f}),
            Map.entry(naturesSpirit("red_maple_sapling"), new float[]{0.35f, 0.55f}),
            Map.entry(naturesSpirit("orange_maple_sapling"), new float[]{0.35f, 0.55f}),
            Map.entry(naturesSpirit("yellow_maple_sapling"), new float[]{0.35f, 0.55f}),
            Map.entry(naturesSpirit("cypress_sapling"), new float[]{0.9f, 1.3f}),
            Map.entry(naturesSpirit("olive_sapling"), new float[]{0.9f, 1.3f}),
            Map.entry(naturesSpirit("joshua_sapling"), new float[]{1.6f, 2.5f}),
            Map.entry(naturesSpirit("ghaf_sapling"), new float[]{1.6f, 2.5f}),
            Map.entry(naturesSpirit("palo_verde_sapling"), new float[]{1.6f, 2.5f}),
            Map.entry(naturesSpirit("cedar_sapling"), new float[]{1f, 1.4f}),
            Map.entry(naturesSpirit("larch_sapling"), new float[]{0.6f, 1.95f}),
            Map.entry(naturesSpirit("mahogany_sapling"), new float[]{1.7f, 2.2f}),
            Map.entry(naturesSpirit("saxaul_sapling"), new float[]{1.9f, 2.5f}),
            Map.entry(ResourceLocation.fromNamespaceAndPath("expandeddelight", "cinnamon_sapling"), new float[]{0.85f, 1.15f}),
            Map.entry(farmersDelight("rice"), new float[]{0.8f, 2f}),
            Map.entry(farmersDelight("cabbages"), new float[]{0f, 0.8f}),
            Map.entry(farmersDelight("onions"), new float[]{0.2f, 1f}),
            Map.entry(farmersDelight("budding_tomatoes"), new float[]{0.7f, 1.5f})
    );

    public static ResourceLocation naturesSpirit(String id) {
        return ResourceLocation.fromNamespaceAndPath("natures_spirit", id);
    }

    public static ResourceLocation farmersDelight(String id) {
        return ResourceLocation.fromNamespaceAndPath("farmersdelight", id);
    }
}