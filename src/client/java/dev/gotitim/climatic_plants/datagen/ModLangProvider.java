package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.ClimaticTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public abstract class ModLangProvider extends FabricLanguageProvider {
    protected ModLangProvider(
            FabricPackOutput packOutput, String lang, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, lang, registryLookup);
    }

    @Override
    public String getName() {
        return "ClimaticPlantsLangProvider::" + getClass().getSimpleName();
    }

    public static class EnUS extends ModLangProvider {
        public EnUS(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

            builder.add("sound.climatic_plants.block.quern.drag", "Quern grinding");
            builder.add("sound.climatic_plants.block.barrel.open", "Barrel opens");
            builder.add("sound.climatic_plants.block.barrel.close", "Barrel closes");
            builder.add("sound.climatic_plants.block.barrel.drip", "Barrel drips");

            builder.add("container.climatic_plants.barrel", "Fluid Barrel");
            builder.add("tooltip.climatic_plants.seal_barrel", "Seal");
            builder.add("tooltip.climatic_plants.unseal_barrel", "Unseal");
            builder.add("tooltip.climatic_plants.fluid_units_of", "%s dp (%s mB) of %s");
            builder.add("tooltip.climatic_plants.making_recipe", "Making %s");
            builder.add("tooltip.climatic_plants.sealed_on", "Sealed on day %s, %s");
            builder.add("recipe.climatic_plants.barrel.yeast_seed", "Yeast Seed");
            builder.add("recipe.climatic_plants.barrel.yeast_multiply", "Yeast Multiply");

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
        public PlPL(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

            builder.add("sound.climatic_plants.block.quern.drag", "Żarna mielą");
        }
    }
}
