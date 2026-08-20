package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
    public static final Map<Crop, Block> DEAD_CROPS = mapOf(Crop.ALL_CROPS, crop ->
            register("dead_" + crop.name(), p -> new DeadCropBlock(crop, p), BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))
    );

    public static <K, V> Map<K, V> mapOf(List<K> entries, Function<K, V> valueMapper) {
        return entries.stream().collect(Collectors.toMap(k -> k, valueMapper));
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ClimaticPlants.identifier(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, blockFactory.apply(properties.setId(key)));
    }

    public static void init() {

    }
}
