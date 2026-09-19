package dev.gotitim.climatic_plants.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

public record SizedIngredient(Ingredient ingredient, int count) implements Predicate<ItemStack> {
    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient, ByteBufCodecs.VAR_INT, SizedIngredient::count, SizedIngredient::new
    );
    public static final MapCodec<SizedIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
            ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(SizedIngredient::count)
    ).apply(i, SizedIngredient::new));

    public static SizedIngredient single(HolderSet<Item> tag) {
        return new SizedIngredient(Ingredient.of(tag), 1);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ingredient.isEmpty() || (ingredient.test(itemStack) && count <= itemStack.getCount());
    }
}
