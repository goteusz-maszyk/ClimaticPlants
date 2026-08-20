package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticItems;
import dev.gotitim.climatic_plants.content.ClimaticTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output,
                             CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new IntlRecipeProvider(registries, output);
    }

    @Override
    public String getName() {
        return "Recipes";
    }

    public static class IntlRecipeProvider extends RecipeProvider {
        public IntlRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            super(registries, output);
        }

        @Override
        public void buildRecipes() {
            shapeless(RecipeCategory.FOOD, ClimaticItems.WHEAT_GRAIN)
                    .requires(Items.WHEAT)
                    .requires(ClimaticTags.KNIVES)
                    .unlockedBy(getHasName(Items.WHEAT), has(Items.WHEAT))
                    .save(output);
        }
    }
}
