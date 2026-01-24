package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.ConfigUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.climatic_plants.PlantKiller.tryKillSapling;

@Mixin(BambooSaplingBlock.class)
public class BambooSaplingMixin {
    @Inject(method = "growBamboo", at = @At("HEAD"), cancellable = true)
    public void modifyGrowth(Level level, BlockPos blockPos, CallbackInfo ci) {
        if (tryKillSapling(level.getBiome(blockPos), ConfigUtils.getTemperatureRange(Blocks.BAMBOO), blockPos, level)) {
            ci.cancel();
        }
    }
}
