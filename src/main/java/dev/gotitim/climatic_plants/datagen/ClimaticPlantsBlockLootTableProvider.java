package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.concurrent.CompletableFuture;

public class ClimaticPlantsBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ClimaticPlantsBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.DEAD_FUNGUS);
        dropOther(ModBlocks.FROZEN_CROP, Items.SNOWBALL);
        add(ModBlocks.BURNT_CROP, LootTable.lootTable().withPool(applyExplosionCondition(Items.CHARCOAL, LootPool.lootPool()
                .conditionally(LootItemRandomChanceCondition.randomChance(0.1f).build())
                .add(LootItem.lootTableItem(Items.CHARCOAL))
        )));
        add(ModBlocks.POTTED_FROZEN_BUSH, createPotFlowerItemTable(ModBlocks.FROZEN_BUSH));
        add(ModBlocks.POTTED_DEAD_SAPLING, createPotFlowerItemTable(ModBlocks.DEAD_SAPLING));
        add(ModBlocks.POTTED_DEAD_FUNGUS, createPotFlowerItemTable(ModBlocks.DEAD_FUNGUS));

        add(ModBlocks.DEAD_SAPLING, createShearsDispatchTable(ModBlocks.DEAD_SAPLING,
                applyExplosionDecay(ModBlocks.DEAD_SAPLING, LootItem.lootTableItem(Items.STICK))));
        add(ModBlocks.FROZEN_BUSH, createShearsDispatchTable(ModBlocks.FROZEN_BUSH,
                applyExplosionDecay(ModBlocks.FROZEN_BUSH, LootItem.lootTableItem(Items.STICK))));
    }
}
//(LootPoolEntryContainer.Builder)this.applyExplosionDecay(block, ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.WHEAT_SEEDS).when(LootItemRandomChanceCondition.randomChance(0.125F))).apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 2))));
