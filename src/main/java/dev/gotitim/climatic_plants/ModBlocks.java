package dev.gotitim.climatic_plants;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static dev.gotitim.climatic_plants.ClimaticPlants.MOD_ID;

public class ModBlocks {
    public static final Block FROZEN_BUSH = registerBlock("frozen_bush", () -> new DeadBush(
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH).sound(SoundType.CHERRY_SAPLING).noOcclusion().noCollission()), true);
    public static final Block DEAD_SAPLING = registerBlock("dead_sapling", () -> new DeadBush(
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH).sound(SoundType.CHERRY_SAPLING).noOcclusion().noCollission()), true);
    public static final Block DEAD_FUNGUS = registerBlock("dead_fungus", () -> new DeadBush(
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH).sound(SoundType.FUNGUS).noOcclusion().noCollission()), true);
    public static final Block POTTED_FROZEN_BUSH = registerBlock("potted_frozen_bush",
            () -> new FlowerPotBlock(FROZEN_BUSH, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_DEAD_BUSH).noOcclusion()), false);
    public static final Block POTTED_DEAD_SAPLING = registerBlock("potted_dead_sapling",
            () -> new FlowerPotBlock(DEAD_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_DEAD_BUSH).noOcclusion()), false);
    public static final Block POTTED_DEAD_FUNGUS = registerBlock("potted_dead_fungus",
            () -> new FlowerPotBlock(DEAD_FUNGUS, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CRIMSON_FUNGUS).noOcclusion()), false);

    public static void init() {

    }

    public static class DeadBush extends DeadBushBlock {
        public DeadBush(Properties properties) {
            super(properties);
        }
    }
    public static <T extends Block> Block registerBlock(String name, Supplier<T> sup, boolean registerItem) {
        var block = sup.get();
        if (registerItem) {
            Registry.register(BuiltInRegistries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
                    new BlockItem(block, new Item.Properties()));
        }
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), block);
    }
}