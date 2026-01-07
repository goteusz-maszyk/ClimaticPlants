package dev.gotitim.demandingplants.mixin;

import dev.gotitim.demandingplants.PlantKiller;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.gotitim.demandingplants.ConfigUtils.getTemperatureRange;
import static net.minecraft.world.level.block.SaplingBlock.STAGE;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockGrowthMixin {

    @Inject(at = @At("HEAD"), method = "advanceTree", cancellable = true)
    public void generate(ServerLevel world, BlockPos pos, BlockState state, RandomSource random, CallbackInfo ci) {
        Holder<Biome> biome = world.getBiomeManager().getBiome(pos);
        Block sapling = world.getBlockState(pos).getBlock();

        if (state.getValue(STAGE) == 0) return;
        if (PlantKiller.tryKillSapling(biome, getTemperatureRange(sapling), pos, world)) {
            ci.cancel();
        }
    }
}