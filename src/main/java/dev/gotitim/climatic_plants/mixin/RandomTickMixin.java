package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.PlantKiller;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class RandomTickMixin {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void modifyRandomTick(ServerLevel level, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (PlantKiller.tryCancelGeneral(level, blockPos, asState())) {
            ci.cancel();
        }
    }
}
