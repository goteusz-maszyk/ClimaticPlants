package dev.gotitim.demandingplants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.demandingplants.PlantKiller.tryCancelCrop;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneGrowthMixin {
    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void modifySugarCaneAge(
            BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci
    ) {
        if (tryCancelCrop(level, pos, state.getBlock())) {
            ci.cancel();
        }
    }
}