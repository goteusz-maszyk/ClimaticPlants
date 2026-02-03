package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.PreferredClimate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.climatic_plants.PlantKiller.tryKillSapling;

@Mixin(BambooStalkBlock.class)
public class BambooStalkMixin {
    @Inject(method = "growBamboo", at = @At("HEAD"), cancellable = true)
    public void modifyGrowth(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, int i, CallbackInfo ci) {
        if (tryKillSapling(level.getBiome(blockPos), new PreferredClimate(level.registryAccess().registry(Registries.BIOME).get().get(Biomes.BAMBOO_JUNGLE)), blockPos, level, blockState)) {
            ci.cancel();
        }
    }
}
