package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticTags;
import dev.gotitim.climatic_plants.content.ModItemIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;

import java.util.concurrent.CompletableFuture;

public class ModTagsProvider {

    public static class Block extends FabricTagsProvider.BlockTagsProvider {
        public Block(FabricPackOutput output,
                     CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(ClimaticTags.MINEABLE_WITH_KNIFE)
                    .add(BlockItemIds.CACTUS)
                    .add(BlockItemIds.MELON)
                    .add(BlockItemIds.PUMPKIN)
                    .add(BlockItemIds.PUMPKIN)
                    .add(BlockItemIds.CARVED_PUMPKIN)
                    .add(BlockItemIds.JACK_O_LANTERN)
                    .add(BlockItemIds.COBWEB)
                    .add(BlockItemIds.CAKE);
        }
    }

    public static class Item extends FabricTagsProvider.ItemTagsProvider {
        public Item(FabricPackOutput output,
                     CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(ClimaticTags.FLINT_TOOL_MATERIALS)
                    .add(ItemIds.FLINT);

            builder(ClimaticTags.KNIVES)
                    .add(ModItemIds.FLINT_KNIFE)
                    .add(ModItemIds.COPPER_KNIFE)
                    .add(ModItemIds.IRON_KNIFE)
                    .add(ModItemIds.DIAMOND_KNIFE);

            builder(ClimaticTags.POLISHED_STONES)
                    .add(BlockItemIds.POLISHED_ANDESITE)
                    .add(BlockItemIds.POLISHED_GRANITE)
                    .add(BlockItemIds.POLISHED_DIORITE)
                    .add(BlockItemIds.POLISHED_SULFUR)
                    .add(BlockItemIds.POLISHED_TUFF)
                    .add(BlockItemIds.POLISHED_CINNABAR)
                    .add(BlockItemIds.POLISHED_DEEPSLATE);
        }
    }
}
