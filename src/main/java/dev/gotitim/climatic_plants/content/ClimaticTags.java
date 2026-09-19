package dev.gotitim.climatic_plants.content;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.gotitim.climatic_plants.ClimaticPlants.identifier;

public class ClimaticTags {
    public static final TagKey<Block> MINEABLE_WITH_KNIFE = TagKey.create(Registries.BLOCK, identifier("mineable/knife"));

    public static final TagKey<Item> POLISHED_STONES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "stones/polished"));
    public static final TagKey<Item> FLINT_TOOL_MATERIALS = TagKey.create(Registries.ITEM, identifier("flint_tool_materials"));
    public static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/knife"));
    public static final TagKey<Item> YEAST_STARTER_INGREDIENTS = TagKey.create(Registries.ITEM, identifier("yeast_starter_ingredients"));

    public static HolderSet<Item> nameForDatagen(TagKey<Item> tagKey) {
        return HolderSet.emptyNamed(BuiltInRegistries.ITEM, tagKey);
    }
}
