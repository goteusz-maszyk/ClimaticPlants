package dev.gotitim.demandingplants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.demandingplants.PlantKiller.tryCancelCrop;

@Mixin(CocoaBlock.class)
public abstract class CocoaGrowthMixin {
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
    private void modifyCocoaAge(
            BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci
    ) {
        if (tryCancelCrop(serverLevel, blockPos, blockState.getBlock())) {
            ci.cancel();
        }
    }
}