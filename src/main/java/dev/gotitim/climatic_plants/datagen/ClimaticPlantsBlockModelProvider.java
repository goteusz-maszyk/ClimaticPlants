package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

public class ClimaticPlantsBlockModelProvider extends FabricModelProvider {
    public ClimaticPlantsBlockModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createPlant(ModBlocks.FROZEN_BUSH, ModBlocks.POTTED_FROZEN_BUSH, BlockModelGenerators.TintState.NOT_TINTED);
        generator.createPlant(ModBlocks.DEAD_SAPLING, ModBlocks.POTTED_DEAD_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
        generator.createPlant(ModBlocks.DEAD_FUNGUS, ModBlocks.POTTED_DEAD_FUNGUS, BlockModelGenerators.TintState.NOT_TINTED);

        generator.createTrivialBlock(
                ModBlocks.BURNT_CROP,
                TextureMapping.crop(ResourceLocation.fromNamespaceAndPath("climatic_plants", "block/burnt_crop")),
                ModelTemplates.CROP);

        generator.createTrivialBlock(
                ModBlocks.FROZEN_CROP,
                TextureMapping.crop(ResourceLocation.fromNamespaceAndPath("climatic_plants", "block/frozen_crop")),
                ModelTemplates.CROP);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }
}
