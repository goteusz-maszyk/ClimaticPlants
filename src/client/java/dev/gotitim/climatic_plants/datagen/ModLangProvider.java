package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

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
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(ClimaticItems.CARROT_SEEDS, "Carrot Seeds");
        }
    }

    public static class PlPL extends ModLangProvider {
        public PlPL(FabricPackOutput packOutput,
                    CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, "pl_pl", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(ClimaticItems.CARROT_SEEDS, "Nasiona marchewki");
        }
    }
}
