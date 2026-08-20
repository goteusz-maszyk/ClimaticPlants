package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> FLINT_KNIFE = createKey("flint_knife");
    public static final ResourceKey<Item> COPPER_KNIFE = createKey("copper_knife");
    public static final ResourceKey<Item> IRON_KNIFE = createKey("iron_knife");
    public static final ResourceKey<Item> DIAMOND_KNIFE = createKey("diamond_knife");

    private static ResourceKey<Item> createKey(String name) {
        return ResourceKey.create(Registries.ITEM, ClimaticPlants.identifier(name));
    }
}
