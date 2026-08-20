package dev.gotitim.climatic_plants.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ClimaticPlantsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);

		pack.addProvider(ModLangProvider.EnUS::new);
		pack.addProvider(ModLangProvider.PlPL::new);

		pack.addProvider(ModTagsProvider.Block::new);
		pack.addProvider(ModTagsProvider.Item::new);

		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ClimateRangeProvider::new);
	}
}