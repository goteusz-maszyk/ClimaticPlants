package dev.gotitim.climatic_plants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.climatic_plants.PlantKiller.tryCancelCrop;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryGrowthMixin {
    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void changeArtificialGrowth(ServerLevel level, RandomSource randomSource, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if (tryCancelCrop(level, pos, blockState.getBlock())) {
            ci.cancel();
        }
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void modifyAge(
            BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci
    ) {
        if (tryCancelCrop(serverLevel, blockPos, blockState.getBlock())) {
            ci.cancel();
        }
    }
}
