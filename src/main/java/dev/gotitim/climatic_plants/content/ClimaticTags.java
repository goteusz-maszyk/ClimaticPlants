package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ClimaticTags {
    public static final TagKey<Block> MINEABLE_WITH_KNIFE = TagKey.create(Registries.BLOCK, ClimaticPlants.identifier("mineable/knife"));
    public static final TagKey<Item> FLINT_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ClimaticPlants.identifier("flint_tool_materials"));
    public static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/knife"));
}
