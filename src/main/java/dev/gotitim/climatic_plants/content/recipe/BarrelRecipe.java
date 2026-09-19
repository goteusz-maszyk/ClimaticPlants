package dev.gotitim.climatic_plants.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gotitim.climatic_plants.ClimaticPlants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class BarrelRecipe implements Recipe<BarrelRecipe.BarrelRecipeInput> {
    public static final MapCodec<BarrelRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(
                    SizedIngredient.MAP_CODEC.codec().optionalFieldOf("input_item").forGetter(c -> Optional.ofNullable(
                            c.inputItem)),
                    FluidStack.MAP_CODEC.codec().fieldOf("input_fluid").forGetter(c -> c.inputFluid),
                    ItemStackTemplate.CODEC.optionalFieldOf("output_item").forGetter(c -> Optional.ofNullable(c.outputItem)),
                    FluidStack.MAP_CODEC.codec().optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(c -> c.outputFluid),
                    SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.BREWING_STAND_BREW)).forGetter(c -> c.sound),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("duration", 0).forGetter(c -> c.duration)
            ).apply(i, BarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BarrelRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC), r -> Optional.ofNullable(r.inputItem),
            FluidStack.STREAM_CODEC, r -> r.inputFluid,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), r -> Optional.ofNullable(r.outputItem),
            FluidStack.STREAM_CODEC, r -> r.outputFluid,
            SoundEvent.STREAM_CODEC, r -> r.sound,
            ByteBufCodecs.VAR_INT, r -> r.duration,
            BarrelRecipe::new
    );

    public static final RecipeType<BarrelRecipe> TYPE = Registry.register(BuiltInRegistries.RECIPE_TYPE,
            ClimaticPlants.identifier("barrel"), new Type()
    );

    public static final RecipeSerializer<BarrelRecipe> SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, ClimaticPlants.identifier("barrel"),
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC)
    );

    public static final RecipeBookCategory BARREL_CATEGORY = new RecipeBookCategory();

    public final @Nullable SizedIngredient inputItem;
    public final FluidStack inputFluid;
    public final @Nullable ItemStackTemplate outputItem;
    public final FluidStack outputFluid;
    public final Holder<SoundEvent> sound;
    public final int duration;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected BarrelRecipe(Optional<SizedIngredient> inputItem, FluidStack inputFluid, Optional<ItemStackTemplate> outputItem, FluidStack outputFluid, Holder<SoundEvent> sound, int duration) {
        this.inputItem = inputItem.orElse(null);
        this.inputFluid = inputFluid;
        this.outputItem = outputItem.orElse(null);
        this.outputFluid = outputFluid;
        this.sound = sound;
        this.duration = duration;
    }
    protected BarrelRecipe(@Nullable SizedIngredient inputItem, FluidStack inputFluid, @Nullable ItemStackTemplate outputItem, FluidStack outputFluid, Holder<SoundEvent> sound, int duration) {
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.sound = sound;
        this.duration = duration;
    }

    public boolean isInstant() {
        return duration <= 0;
    }

    public static Optional<RecipeHolder<BarrelRecipe>> get(ServerLevel level, ItemStack inputStack, SingleFluidStorage fluidStorage) {
        return level.getServer().getRecipeManager()
                    .getRecipeFor(BarrelRecipe.TYPE, new BarrelRecipeInput(inputStack, fluidStorage), level);
    }

    public static void save(String name, SizedIngredient ingredient, FluidStack inputFluid, @Nullable ItemStackTemplate outputItem, FluidStack outputFluid, int duration,
                            Criterion<InventoryChangeTrigger.TriggerInstance> unlockedBy,
                            RecipeOutput output) {
        save(name, ingredient, inputFluid, outputItem, outputFluid, duration, Holder.direct(SoundEvents.BREWING_STAND_BREW), unlockedBy, output);
    }

    public static void save(String name, SizedIngredient ingredient, FluidStack inputFluid, @Nullable ItemStackTemplate outputItem, FluidStack outputFluid, int duration, Holder<SoundEvent> sound,
                            Criterion<InventoryChangeTrigger.TriggerInstance> unlockedBy,
                            RecipeOutput output) {
        BarrelRecipe recipe = new BarrelRecipe(ingredient, inputFluid, outputItem, outputFluid, sound, duration);
        ResourceKey<Recipe<?>> key = ResourceKey.create(
                Registries.RECIPE, ClimaticPlants.identifier(name));

        var advancementBuilder = new RecipeUnlockAdvancementBuilder();
        advancementBuilder.unlockedBy("has_item", unlockedBy);
        output.accept(key, recipe, advancementBuilder.build(output, key, RecipeCategory.FOOD));
    }

    @Override
    public boolean matches(BarrelRecipeInput input, Level level) {
        return (inputItem == null || inputItem.test(input.itemStack)) && inputFluid.test(input.fluidStack);
    }

    @Override
    public ItemStack assemble(BarrelRecipeInput input) {
        return outputItem == null ? ItemStack.EMPTY : outputItem.create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<BarrelRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<BarrelRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return inputItem == null ? PlacementInfo.NOT_PLACEABLE : PlacementInfo.create(inputItem.ingredient());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return BARREL_CATEGORY;
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }

    private static class Type implements RecipeType<BarrelRecipe> {
        @Override
        public String toString() {
            return "barrel";
        }
    }

    public record BarrelRecipeInput(ItemStack itemStack, SingleFluidStorage fluidStack) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            return itemStack;
        }

        @Override
        public int size() {
            return 2;
        }
    }
}
