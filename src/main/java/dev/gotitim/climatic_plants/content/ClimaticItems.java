package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.item.KnifeItem;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Function;

public class ClimaticItems {
    public static final ToolMaterial FLINT = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5,
            ClimaticTags.FLINT_TOOL_MATERIALS
    );

    public static final Item CARROT_SEEDS = registerItem("carrot_seeds",
            p -> new BlockItem(Blocks.CARROTS, p.useItemDescriptionPrefix())
    );

    public static final Item FLINT_KNIFE = registerItem(ModItemIds.FLINT_KNIFE, KnifeItem::new, knifeItem(FLINT));
    public static final Item COPPER_KNIFE = registerItem(ModItemIds.COPPER_KNIFE, KnifeItem::new,
            knifeItem(ToolMaterial.COPPER)
    );
    public static final Item IRON_KNIFE = registerItem(ModItemIds.IRON_KNIFE, KnifeItem::new,
            knifeItem(ToolMaterial.IRON)
    );
    public static final Item DIAMOND_KNIFE = registerItem(ModItemIds.DIAMOND_KNIFE, KnifeItem::new,
            knifeItem(ToolMaterial.DIAMOND)
    );

    public static final Item WHEAT_GRAIN = registerItem("wheat_grain",
            new Item.Properties().food(new FoodProperties(1, 1, false))
    );
    public static final Item WHEAT_FLOUR = registerItem(ModItemIds.WHEAT_FLOUR);

    public static final Item WHEAT_DOUGH = registerItem(ModItemIds.WHEAT_DOUGH);
    public static final Item WHEAT_FLATBREAD_DOUGH = registerItem(ModItemIds.WHEAT_FLATBREAD_DOUGH);
    public static final Item WHEAT_FLATBREAD = registerItem("wheat_flatbread",
            new Item.Properties().food(new FoodProperties(3, 0.3f, false))
    );

    public static final Item HANDSTONE = registerItem("handstone", new Item.Properties().durability(250).stacksTo(1));

    public static final Item YEAST_BUCKET = registerItem(
            "yeast_bucket", p -> new BucketItem(ClimaticFluids.YEAST, p), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
    );
    public static final Item YEAST_CULTURE = registerItem(ModItemIds.YEAST_CULTURE);
    public static void init() {

    }

    private static Item registerItem(ResourceKey<Item> id) {
        return registerItem(id, Item::new, new Item.Properties());
    }

    public static <T extends Item> T registerItem(String id, Function<Item.Properties, T> itemFactory) {
        return registerItem(id, itemFactory, new Item.Properties());
    }

    private static Item registerItem(String id, Item.Properties properties) {
        return registerItem(id, Item::new, properties);
    }

    private static <T extends Item> T registerItem(String id, Function<Item.Properties, T> itemFactory,
                                     Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ClimaticPlants.identifier(id));
        return registerItem(key, itemFactory, properties);
    }

    private static <T extends Item> T registerItem(ResourceKey<Item> key, Function<Item.Properties, T> itemFactory,
                                      Item.Properties properties) {
        T item = itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            Item.BY_BLOCK.put(blockItem.getBlock(), item);
        }

        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static Item.Properties knifeItem(ToolMaterial material) {
        HolderGetter<Block> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(
                BuiltInRegistries.BLOCK);
        return new Item.Properties().durability(material.durability()).repairable(material.repairItems())
                                    .enchantable(material.enchantmentValue())
                                    .attributes(KnifeItem.createAttributes(material, 0.5F, -2.0F))
                                    .component(DataComponents.TOOL, new Tool(List.of(Tool.Rule.deniesDrops(
                                                            holderGetter.getOrThrow(material.incorrectBlocksForDrops())),
                                                    Tool.Rule.minesAndDrops(
                                                            holderGetter.getOrThrow(ClimaticTags.MINEABLE_WITH_KNIFE),
                                                            material.speed()
                                                    )
                                            ), 1.0F, 1, false
                                            )
                                    ).component(DataComponents.WEAPON, new Weapon(2));
    }
}
