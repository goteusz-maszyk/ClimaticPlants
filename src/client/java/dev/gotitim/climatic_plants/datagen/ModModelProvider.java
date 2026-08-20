package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import org.jspecify.annotations.NonNull;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public @NonNull String getName() {
        return "ClimaticPlantsModelProvider";
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators generator) {
        for (Crop crop : Crop.ALL_CROPS) {
            Material deadTexture = new Material(ClimaticPlants.identifier("block/crop/" + crop.name() + "_dead"));
            Material youngTexture = new Material(ClimaticPlants.identifier("block/crop/" + crop.name() + "_dead_young"));

            var block = ClimaticBlocks.DEAD_CROPS.get(crop);

            var deadModel = ModelTemplates.CROP.create(block, TextureMapping.crop(deadTexture), generator.modelOutput);
            var youngModel = ModelTemplates.CROP.createWithSuffix(block, "_young", TextureMapping.crop(youngTexture), generator.modelOutput);

            generator.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block)
                                         .with(PropertyDispatch.initial(DeadCropBlock.MATURE)
                                                               .select(true, BlockModelGenerators.plainVariant(deadModel))
                                                               .select(false, BlockModelGenerators.plainVariant(youngModel))
                                         )
            );
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(ClimaticItems.CARROT_SEEDS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_GRAIN, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(ClimaticItems.FLINT_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.IRON_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.COPPER_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.DIAMOND_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
    }
}
