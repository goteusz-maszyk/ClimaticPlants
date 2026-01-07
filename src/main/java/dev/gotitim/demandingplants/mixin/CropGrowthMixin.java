package dev.gotitim.demandingplants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.demandingplants.PlantKiller.tryCancelCrop;

@Mixin(CropBlock.class)
public class CropGrowthMixin {
    @Inject(method = "growCrops", at = @At("HEAD"), cancellable = true)
    private void changeArtificialGrowth(Level level, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (tryCancelCrop(level, pos, blockState.getBlock())) {
            ci.cancel();
        }
    }

    @Inject(method = "randomTick", cancellable = true, at= @At(value = "HEAD"))
    private void changeRandomGrowth(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci) {
        if (tryCancelCrop(level, pos, blockState.getBlock())) {
            ci.cancel();
        }
    }
}
