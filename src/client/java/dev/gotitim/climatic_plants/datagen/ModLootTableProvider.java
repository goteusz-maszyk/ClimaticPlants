package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.concurrent.CompletableFuture;

import static dev.gotitim.climatic_plants.content.ClimaticBlocks.DEAD_CROPS;
import static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.hasBlockStateProperties;

public class ModLootTableProvider extends FabricBlockLootSubProvider {
    private static final StatePropertiesPredicate.Builder IS_CROP_MATURE = StatePropertiesPredicate.Builder.properties().hasProperty(
            BlockStateProperties.AGE_7, 7);

    private static final StatePropertiesPredicate.Builder IS_DEAD_CROP_MATURE = StatePropertiesPredicate.Builder.properties().hasProperty(
            DeadCropBlock.MATURE, true);

    protected ModLootTableProvider(FabricPackOutput packOutput,
                                   CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        LootItemFunction.Builder addCropFromFortune = ApplyBonusCount.addBonusBinomialDistributionCount(
                registries.lookup(Registries.ENCHANTMENT).get().get(Enchantments.FORTUNE).get(),
                0.5714286f,
                3
        );
        for (Crop crop : Crop.ALL_CROPS) {
            add(crop.block(), LootTable.lootTable()
                                       .withPool(new LootPool.Builder().add(LootItem.lootTableItem(crop.seedsItem())))
                                       .withPool(new LootPool.Builder()
                                                 .when(hasBlockStateProperties(crop.block()).setProperties(IS_CROP_MATURE))
                                                 .add(LootItem.lootTableItem(crop.harvestItem()).apply(addCropFromFortune))
                                       )
            );
            Block dead = DEAD_CROPS.get(crop);
            add(dead, LootTable.lootTable()
                                         .withPool(new LootPool.Builder().add(LootItem.lootTableItem(crop.seedsItem())))
                                         .withPool(new LootPool.Builder()
                                               .when(hasBlockStateProperties(dead).setProperties(IS_DEAD_CROP_MATURE))
                                               .add(LootItem.lootTableItem(crop.seedsItem()).apply(addCropFromFortune))
                                       )
            );
        }

        add(ClimaticBlocks.QUERN, createSingleItemTable(ClimaticBlocks.QUERN));
        add(ClimaticBlocks.FLUID_BARREL, LootTable.lootTable()
                .withPool(new LootPool.Builder()
                        .add(LootItem.lootTableItem(ClimaticBlocks.FLUID_BARREL)))
                .withPool(new LootPool.Builder()
                        .when(hasBlockStateProperties(ClimaticBlocks.FLUID_BARREL)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FluidBarrelBlock.RACK, true)))
                        .add(LootItem.lootTableItem(ClimaticBlocks.BARREL_RACK))));
        add(ClimaticBlocks.BARREL_RACK, createSingleItemTable(ClimaticBlocks.BARREL_RACK));
    }
}
