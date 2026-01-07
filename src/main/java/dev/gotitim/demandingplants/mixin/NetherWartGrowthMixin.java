package dev.gotitim.demandingplants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherWartBlock.class)
public abstract class NetherWartGrowthMixin {
    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void modifyWartAge(
            BlockState blockState, ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci
    ) {
        Holder<Biome> biome = level.getBiomeManager().getBiome(pos);
        if (!biome.is(BiomeTags.IS_NETHER)){
            ci.cancel();
        }
    }
}