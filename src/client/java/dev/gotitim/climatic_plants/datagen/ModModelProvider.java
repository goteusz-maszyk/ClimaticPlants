package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public String getName() {
        return "ClimaticPlantsModelProvider";
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        for (Crop crop : Crop.ALL_CROPS) {
            Material deadTexture = new Material(ClimaticPlants.identifier("block/crop/" + crop.name() + "_dead"));
            Material youngTexture = new Material(
                    ClimaticPlants.identifier("block/crop/" + crop.name() + "_dead_young"));

            var block = ClimaticBlocks.DEAD_CROPS.get(crop);

            var deadModel = ModelTemplates.CROP.create(block, TextureMapping.crop(deadTexture), generator.modelOutput);
            var youngModel = ModelTemplates.CROP.createWithSuffix(block, "_young", TextureMapping.crop(youngTexture),
                    generator.modelOutput
            );

            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                                                                   .with(PropertyDispatch.initial(DeadCropBlock.MATURE)
                                                                                         .select(true,
                                                                                                 BlockModelGenerators.plainVariant(
                                                                                                         deadModel)
                                                                                         ).select(false,
                                                                                   BlockModelGenerators.plainVariant(
                                                                                           youngModel)
                                                                           )));
        }
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(ClimaticBlocks.QUERN,
                BlockModelGenerators.plainVariant(ClimaticPlants.identifier("block/quern"))
        ));

        generator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ClimaticBlocks.FLUID_BARREL,
                        BlockModelGenerators.plainVariant(ClimaticPlants.identifier("block/fluid_barrel"))
                ).with(PropertyDispatch.modify(FluidBarrelBlock.FACING, FluidBarrelBlock.SEALED, FluidBarrelBlock.RACK)
                                        .generate((facing, sealed, rack) -> {
                                            VariantMutator mutator;
                                            if (facing == Direction.UP) {
                                                mutator = VariantMutator.MODEL.withValue(
                                                        ClimaticPlants.identifier(sealed
                                                                ? "block/barrel_sealed"
                                                                : "block/fluid_barrel"));
                                            } else {
                                                String name = sealed ? "barrel_sealed_side" : "barrel_side";
                                                if (rack) {
                                                    name += "_rack";
                                                }
                                                mutator = VariantMutator.MODEL.withValue(
                                                        ClimaticPlants.identifier("block/" + name));
                                            }
                                            return switch (facing) {
                                                case WEST -> mutator.then(Y_ROT_180);
                                                case SOUTH -> mutator.then(Y_ROT_90);
                                                case NORTH -> mutator.then(Y_ROT_270);
                                                default -> mutator;
                                            };
                                        })
                )
        );

        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(ClimaticBlocks.BARREL_RACK,
                BlockModelGenerators.plainVariant(ClimaticPlants.identifier("block/barrel_rack"))
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(ClimaticItems.CARROT_SEEDS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_GRAIN, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_FLOUR, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_FLATBREAD_DOUGH, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_FLATBREAD, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ClimaticItems.WHEAT_DOUGH, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(ClimaticItems.YEAST_CULTURE, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(ClimaticItems.FLINT_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.IRON_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.COPPER_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.DIAMOND_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(ClimaticItems.YEAST_BUCKET, ModelTemplates.FLAT_HANDHELD_ITEM);

        generator.itemModelOutput.accept(ClimaticBlocks.QUERN.asItem(),
                ItemModelUtils.plainModel(ClimaticPlants.identifier("block/quern"))
        );
        generator.itemModelOutput.accept(ClimaticItems.HANDSTONE,
                ItemModelUtils.plainModel(ClimaticPlants.identifier("item/handstone"))
        );
        generator.itemModelOutput.accept(ClimaticBlocks.FLUID_BARREL.asItem(),
                ItemModelUtils.conditional(
                        ItemModelUtils.hasComponent(DataComponents.BLOCK_ENTITY_DATA),
                        ItemModelUtils.plainModel(ClimaticPlants.identifier("block/barrel_sealed")),
                        ItemModelUtils.plainModel(ClimaticPlants.identifier("block/fluid_barrel"))
                )
        );
        generator.itemModelOutput.accept(ClimaticBlocks.BARREL_RACK.asItem(),
                ItemModelUtils.plainModel(ClimaticPlants.identifier("block/barrel_rack"))
        );
    }
}
