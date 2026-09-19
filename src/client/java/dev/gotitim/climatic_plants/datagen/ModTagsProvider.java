package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticTags;
import dev.gotitim.climatic_plants.content.ModItemIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;

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

            builder(BlockTags.MINEABLE_WITH_AXE)
                    .add(ClimaticBlocks.Ids.FLUID_BARREL);
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

            builder(ClimaticTags.YEAST_STARTER_INGREDIENTS)
                    .add(BlockItemIds.POTATO_CROP)
                    .add(ModItemIds.WHEAT_FLOUR)
                    .add(BlockItemIds.SWEET_BERRY_CROP)
                    .add(ItemIds.APPLE)
                    .add(ItemIds.SUGAR)
                    .add(ItemIds.BEETROOT);

            builder(ClimaticTags.SWEETENERS)
                    .add(ItemIds.SUGAR)
                    .add(ItemIds.HONEY_BOTTLE);

            builder(ClimaticTags.PLANKS)
                    .add(plank("oak_planks"))
                    .add(plank("spruce_planks"))
                    .add(plank("birch_planks"))
                    .add(plank("jungle_planks"))
                    .add(plank("acacia_planks"))
                    .add(plank("dark_oak_planks"))
                    .add(plank("mangrove_planks"))
                    .add(plank("cherry_planks"))
                    .add(plank("bamboo_planks"))
                    .add(plank("crimson_planks"))
                    .add(plank("warped_planks"));
        }

        private static ResourceKey<net.minecraft.world.item.Item> plank(String name) {
            return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", name));
        }
    }
}
