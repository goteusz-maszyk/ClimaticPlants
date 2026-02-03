package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.PreferredClimate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BambooSaplingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.climatic_plants.PlantKiller.tryKillSapling;

@Mixin(BambooSaplingBlock.class)
public class BambooSaplingMixin {
    @Inject(method = "growBamboo", at = @At("HEAD"), cancellable = true)
    public void modifyGrowth(Level level, BlockPos blockPos, CallbackInfo ci) {
        if (tryKillSapling(level.getBiome(blockPos), new PreferredClimate(level.registryAccess().registry(Registries.BIOME).get().get(Biomes.BAMBOO_JUNGLE)), blockPos, level, level.getBlockState(blockPos))) {
            ci.cancel();
        }
    }
}
