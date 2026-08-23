package dev.gotitim.climatic_plants.content;

import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.content.crop.Crop;
import dev.gotitim.climatic_plants.content.crop.DeadCropBlock;
import dev.gotitim.climatic_plants.content.quern.QuernBlock;
import dev.gotitim.climatic_plants.content.quern.QuernBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClimaticBlocks {
    public static final Block QUERN = register(
            "quern",
            QuernBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F, 2.0F).sound(SoundType.BASALT).noOcclusion()
    );

    public static final Map<Crop, Block> DEAD_CROPS = mapOf(Crop.ALL_CROPS, crop ->
            register("dead_" + crop.name(), p -> new DeadCropBlock(crop, p), BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))
    );
    public static final BlockEntityType<QuernBlockEntity> QUERN_BLOCK_ENTITY = registerBlockEntity("quern", QuernBlockEntity::new, QUERN);

    public static <K, V> Map<K, V> mapOf(List<K> entries, Function<K, V> valueMapper) {
        return entries.stream().collect(Collectors.toMap(k -> k, valueMapper));
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ClimaticPlants.identifier(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, blockFactory.apply(properties.setId(key)));
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = ClimaticPlants.identifier(name);
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    public static void init() {

    }
}
