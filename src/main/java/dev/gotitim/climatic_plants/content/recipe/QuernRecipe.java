package dev.gotitim.climatic_plants.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gotitim.climatic_plants.ClimaticPlants;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class QuernRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<QuernRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(Ingredient.CODEC.fieldOf("ingredient").forGetter(o -> o.ingredient),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
            ).apply(i, QuernRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuernRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient, ItemStackTemplate.STREAM_CODEC, c -> c.result,
            QuernRecipe::new
    );

    public static final RecipeType<QuernRecipe> TYPE = Registry.register(BuiltInRegistries.RECIPE_TYPE,
            ClimaticPlants.identifier("quern"), new Type()
    );

    public static final RecipeSerializer<QuernRecipe> SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, ClimaticPlants.identifier("quern"),
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC)
    );

    public static final RecipeBookCategory QUERN_CATEGORY = new RecipeBookCategory();

    private final Ingredient ingredient;
    private final ItemStackTemplate result;

    public QuernRecipe(Ingredient ingredient, ItemStackTemplate result) {
        this.ingredient = ingredient;
        this.result = result;
    }

    public static void init() {
    }

    public static Optional<RecipeHolder<QuernRecipe>> get(@Nullable ServerLevel level, ItemStack inputStack) {
        return level.getServer().getRecipeManager()
                    .getRecipeFor(QuernRecipe.TYPE, new SingleRecipeInput(inputStack), level);
    }

    public static void save(Ingredient ingredient, Item result,
                            Criterion<InventoryChangeTrigger.TriggerInstance> unlockedBy,
                            RecipeOutput output) {
        QuernRecipe recipe = new QuernRecipe(ingredient, new ItemStackTemplate(result));
        ResourceKey<Recipe<?>> key = RecipeBuilder.getDefaultRecipeId(recipe.result);

        var advancementBuilder = new RecipeUnlockAdvancementBuilder();
        advancementBuilder.unlockedBy("has_item", unlockedBy);
        output.accept(key, recipe, advancementBuilder.build(output, key, RecipeCategory.FOOD));
    }

    @Override
    public boolean matches(SingleRecipeInput input, @NonNull Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull SingleRecipeInput input) {
        return result.create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return QUERN_CATEGORY;
    }

    private static class Type implements RecipeType<QuernRecipe> {
        @Override
        public String toString() {
            return "quern";
        }
    }
}
