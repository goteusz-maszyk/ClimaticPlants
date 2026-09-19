package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.ClimaticTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static dev.gotitim.climatic_plants.content.ClimaticSounds.QUERN_DRAG;

public abstract class ModLangProvider extends FabricLanguageProvider {
    protected ModLangProvider(FabricPackOutput packOutput, String lang,
                              CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, lang, registryLookup);
    }

    @Override
    public @NonNull String getName() {
        return "ClimaticPlantsLangProvider::" + getClass().getSimpleName();
    }

    public static class EnUS extends ModLangProvider {
        public EnUS(FabricPackOutput packOutput,
                                             CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
            builder.add(ClimaticItems.CARROT_SEEDS, "Carrot Seeds");
            builder.add(ClimaticBlocks.QUERN.asItem(), "Quern");
            builder.add(ClimaticBlocks.FLUID_BARREL.asItem(), "Fluid Barrel");
            builder.add(ClimaticBlocks.BARREL_RACK.asItem(), "Barrel Rack");
            builder.add(ClimaticItems.HANDSTONE, "Handstone");
            builder.add(ClimaticItems.FLINT_KNIFE, "Flint Knife");
            builder.add(ClimaticItems.COPPER_KNIFE, "Copper Knife");
            builder.add(ClimaticItems.IRON_KNIFE, "Iron Knife");
            builder.add(ClimaticItems.DIAMOND_KNIFE, "Diamond Knife");

            builder.add(ClimaticItems.WHEAT_GRAIN, "Wheat Grain");
            builder.add(ClimaticItems.WHEAT_FLOUR, "Wheat Flour");
            builder.add(ClimaticItems.WHEAT_DOUGH, "Wheat Dough");
            builder.add(ClimaticItems.WHEAT_FLATBREAD_DOUGH, "Wheat Flatbread Dough");
            builder.add(ClimaticItems.WHEAT_FLATBREAD, "Wheat Flatbread");
            builder.add(ClimaticItems.YEAST_BUCKET, "Yeast Bucket");
            builder.add(ClimaticItems.YEAST_CULTURE, "Yeast Culture");

            builder.add("block.climatic_plants.yeast", "Yeast");
            builder.add("fluid.climatic_plants.yeast", "Yeast");

            builder.add(QUERN_DRAG, "Quern grinding");

            builder.add(ClimaticTags.POLISHED_STONES, "Polished stones");
            builder.add(ClimaticTags.KNIVES, "Knives");
            builder.add(ClimaticTags.FLINT_TOOL_MATERIALS, "Flint tool materials");
            builder.add(ClimaticTags.MINEABLE_WITH_KNIFE, "Mineable with knife");
            builder.add(ClimaticTags.YEAST_STARTER_INGREDIENTS, "Yeast starter ingredients");
            builder.add(ClimaticTags.SWEETENERS, "Sweeteners");
            builder.add(ClimaticTags.PLANKS, "Planks");
        }
    }

    public static class PlPL extends ModLangProvider {
        public PlPL(FabricPackOutput packOutput,
                    CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, "pl_pl", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
            builder.add(ClimaticItems.CARROT_SEEDS, "Nasiona marchewki");
            builder.add(ClimaticBlocks.QUERN.asItem(), "Żarno");
            builder.add(ClimaticItems.HANDSTONE, "Kamień ręczny");
            builder.add(ClimaticItems.FLINT_KNIFE, "Krzemienny nóż");
            builder.add(ClimaticItems.IRON_KNIFE, "Żelazny nóż");
            builder.add(ClimaticItems.COPPER_KNIFE, "Miedziany nóż");
            builder.add(ClimaticItems.DIAMOND_KNIFE, "Diamentowy nóż");

            builder.add(QUERN_DRAG, "Żarna mielą");
        }
    }
}
