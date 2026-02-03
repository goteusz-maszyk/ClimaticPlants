package dev.gotitim.climatic_plants.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.gotitim.climatic_plants.ClimaticPlants;
import dev.gotitim.climatic_plants.PlantKiller;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreeGrower.class)
public abstract class TreeGrowthMixin {
    @WrapOperation(
            method = "growTree",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    public boolean generate(ConfiguredFeature<?, ?> instance, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) BlockState state) {
        var biome = level.getBiomeManager().getBiome(pos);
        if (PlantKiller.tryKillSapling(biome, ClimaticPlants.getClimate(state.getBlock()), pos, (Level) level, state)) {
            return true;
        }
        return instance.place(level, chunkGenerator, randomSource, pos);
    }
}