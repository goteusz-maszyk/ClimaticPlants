package dev.gotitim.demandingplants.mixin;

import dev.gotitim.demandingplants.ConfigUtils;
import dev.gotitim.demandingplants.PlantKiller;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AzaleaBlock.class)
public abstract class AzaleaBlockGrowthMixin {

    @Inject(at = @At("HEAD"), method = "performBonemeal", cancellable = true)
    public void grow(ServerLevel world, RandomSource randomSource, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (PlantKiller.tryKillSapling(world.getBiomeManager().getBiome(pos), ConfigUtils.getTemperatureRange(Blocks.AZALEA), pos, world)) {
            ci.cancel();
        }
    }
}