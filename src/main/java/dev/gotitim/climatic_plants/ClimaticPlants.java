package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.ClimaticSounds;
import dev.gotitim.climatic_plants.content.quern.QuernRecipe;
import dev.gotitim.climatic_plants.data.ClimateRange;
import dev.gotitim.climatic_plants.data.UniversalDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClimaticPlants implements ModInitializer {
    public static final String MOD_ID = "climatic_plants";
    public static final Logger LOGGER = LoggerFactory.getLogger("ClimaticPlants");

    public static final UniversalDataManager<ClimateRange> climateRanges = new UniversalDataManager<>(
            "climate_range",
            ClimateRange.CODEC,
            new ClimateRange(Integer.MIN_VALUE, Integer.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE)
    );

    @Override
    public void onInitialize() {
        ClimaticItems.init();
        ClimaticBlocks.init();
        ClimaticSounds.init();
        QuernRecipe.init();
        climateRanges.register();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
            output.insertAfter(Items.WHEAT_SEEDS, ClimaticItems.CARROT_SEEDS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.insertAfter(Items.STONE_HOE, ClimaticItems.FLINT_KNIFE);
            output.insertAfter(Items.COPPER_HOE, ClimaticItems.COPPER_KNIFE);
            output.insertAfter(Items.IRON_HOE, ClimaticItems.IRON_KNIFE);
            output.insertAfter(Items.DIAMOND_HOE, ClimaticItems.DIAMOND_KNIFE);
        });
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
