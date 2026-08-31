package dev.gotitim.climatic_plants;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlockEntity;
import dev.gotitim.climatic_plants.content.quern.QuernBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClimaticBlockEntities {
    public static final BlockEntityType<QuernBlockEntity> QUERN = register("quern", QuernBlockEntity::new,
            ClimaticBlocks.QUERN
    );
    public static final BlockEntityType<FluidBarrelBlockEntity> FLUID_BARREL = register("fluid_barrel", FluidBarrelBlockEntity::new,
            ClimaticBlocks.FLUID_BARREL
    );


    private static <T extends BlockEntity> BlockEntityType<T> register(
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
}
