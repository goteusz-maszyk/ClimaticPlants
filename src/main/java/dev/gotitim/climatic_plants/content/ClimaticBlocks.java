package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import dev.gotitim.climatic_plants.content.block.BarrelRackBlock;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import dev.gotitim.climatic_plants.content.quern.QuernBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClimaticBlocks {
    public static final QuernBlock QUERN = register(Ids.QUERN, QuernBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 2.0F).sound(SoundType.BASALT)
                                     .noOcclusion()
    );

    public static final FluidBarrelBlock FLUID_BARREL = register(Ids.FLUID_BARREL, FluidBarrelBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.5f).noOcclusion()
    );
    public static final Block BARREL_RACK = register(Ids.BARREL_RACK, BarrelRackBlock::new, BlockBehaviour.Properties.of());

    public static final Map<Crop, DeadCropBlock> DEAD_CROPS = mapOf(Crop.ALL_CROPS,
            crop -> register(
                    ResourceKey.create(Registries.BLOCK, ClimaticPlants.identifier("dead_" + crop.name())),
                    p -> new DeadCropBlock(crop, p),
                    BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak()
                                             .sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)
            )
    );

    public static <K, V> Map<K, V> mapOf(List<K> entries, Function<K, V> valueMapper) {
        return entries.stream().collect(Collectors.toMap(k -> k, valueMapper));
    }

    private static <T extends Block> T register(BlockItemId id, Function<BlockBehaviour.Properties, T> blockFactory,
                                                BlockBehaviour.Properties properties) {
        T block = register(id.block(), blockFactory, properties);

        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);

        return block;
    }

    private static <T extends Block> T register(ResourceKey<Block> id, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, id, blockFactory.apply(properties.setId(id)));
    }

    public static void init() {

    }

    public static class Ids {
        public static final BlockItemId QUERN = create("quern");
        public static final BlockItemId FLUID_BARREL = create("fluid_barrel");
        public static final BlockItemId BARREL_RACK = create("barrel_rack");

        private static BlockItemId create(String name) {
            Identifier id = ClimaticPlants.identifier(name);
            return BlockItemId.create(id, id);
        }
    }
}
