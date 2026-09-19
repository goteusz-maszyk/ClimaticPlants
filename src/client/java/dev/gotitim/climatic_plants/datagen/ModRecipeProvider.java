package dev.gotitim.climatic_plants.datagen;

import dev.gotitim.climatic_plants.content.ClimaticFluids;
import dev.gotitim.climatic_plants.content.recipe.BarrelRecipe;
import dev.gotitim.climatic_plants.content.recipe.FluidStack;
import dev.gotitim.climatic_plants.content.recipe.QuernRecipe;
import dev.gotitim.climatic_plants.content.recipe.SizedIngredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

import static dev.gotitim.climatic_plants.ClimaticPlants.identifier;
import static dev.gotitim.climatic_plants.content.ClimaticBlocks.QUERN;
import static dev.gotitim.climatic_plants.content.ClimaticItems.*;
import static dev.gotitim.climatic_plants.content.ClimaticTags.*;
import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.fromBucketFraction;
import static net.minecraft.data.recipes.RecipeCategory.*;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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
            shapeless(FOOD, WHEAT_GRAIN)
                    .requires(Items.WHEAT).requires(KNIVES)
                    .unlockedBy(getHasName(Items.WHEAT), has(Items.WHEAT)).save(output);

            shaped(TOOLS, QUERN)
                    .pattern("PPP").pattern("SSS")
                    .define('P', POLISHED_STONES)
                    .define('S', ConventionalItemTags.STONES)
                    .unlockedBy("has_polished_stone", has(POLISHED_STONES)).save(output);

            shaped(TOOLS, HANDSTONE)
                    .pattern("T  ").pattern("SSS").define('S', ConventionalItemTags.STONES)
                    .define('T', ConventionalItemTags.WOODEN_RODS)
                    .unlockedBy("has_polished_stone", has(POLISHED_STONES)).save(output);

            QuernRecipe.save(Ingredient.of(WHEAT_GRAIN), WHEAT_FLOUR, has(WHEAT_GRAIN), output);

            for (int i = 1; i <= 4; i++) {
                shapeless(FOOD, WHEAT_FLATBREAD_DOUGH, 4 * i)
                        .requires(WHEAT_FLOUR, i).requires(Items.WATER_BUCKET)
                        .unlockedBy(getHasName(WHEAT_FLOUR), has(WHEAT_FLOUR))
                        .group("wheat_flatbread_dough")
                        .save(output, createKey("wheat_flatbread_dough_x" + i));
            }

            SimpleCookingRecipeBuilder
                    .smoking(Ingredient.of(WHEAT_FLATBREAD_DOUGH), MISC, WHEAT_FLATBREAD, 0.35f, 100)
                    .unlockedBy(getHasName(WHEAT_FLATBREAD_DOUGH), has(WHEAT_FLATBREAD_DOUGH))
                    .save(output, createKey("wheat_flatbread"));

            SimpleCookingRecipeBuilder
                    .campfireCooking(Ingredient.of(WHEAT_FLATBREAD_DOUGH), MISC, WHEAT_FLATBREAD, 0.35f, 100)
                    .unlockedBy(getHasName(WHEAT_FLATBREAD_DOUGH), has(WHEAT_FLATBREAD_DOUGH))
                    .save(output, createKey("wheat_flatbread_campfire"));

            int quarterBucket = (int) fromBucketFraction(250, 1000);
            BarrelRecipe
                    .save("yeast_seed",
                            new SizedIngredient(Ingredient.of(nameForDatagen(YEAST_STARTER_INGREDIENTS)), 4),
                            FluidStack.of(Fluids.WATER, quarterBucket),
                            null,
                            FluidStack.of(ClimaticFluids.YEAST, quarterBucket),
                            20*60*20, has(YEAST_STARTER_INGREDIENTS), output);
            BarrelRecipe
                    .save("yeast_multiply",
                            SizedIngredient.single(nameForDatagen(YEAST_STARTER_INGREDIENTS)),
                            FluidStack.of(ClimaticFluids.YEAST, (int) fromBucketFraction(100, 1000)),
                            null,
                            FluidStack.of(ClimaticFluids.YEAST, (int) fromBucketFraction(600, 1000)),
                            5*60*20, has(YEAST_STARTER_INGREDIENTS), output);
        }

        private ResourceKey<Recipe<?>> createKey(String id) {
            return ResourceKey.create(Registries.RECIPE, identifier(id));
        }
    }
}
