package dev.gotitim.demandingplants.mixin;

import dev.gotitim.demandingplants.PlantKiller;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FungusBlock.class)
public abstract class FungusBlockGrowthMixin {
    @Inject(at = @At("HEAD"), method = "performBonemeal", cancellable = true)
    public void grow(ServerLevel world, RandomSource randomSource, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        var biome = world.getBiomeManager().getBiome(pos);

        if (!biome.is(BiomeTags.IS_NETHER)) {
            PlantKiller.killFungus(world, pos);
            ci.cancel();
        }
    }
}