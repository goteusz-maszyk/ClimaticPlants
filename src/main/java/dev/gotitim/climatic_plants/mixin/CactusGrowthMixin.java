package dev.gotitim.climatic_plants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static dev.gotitim.climatic_plants.PlantKiller.tryCancelCrop;

@Mixin(CactusBlock.class)
public abstract class CactusGrowthMixin {

    @ModifyVariable(
            method = "randomTick",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    private int modifySugarCaneAge(
            int originalAge,
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (tryCancelCrop(level, pos, state.getBlock())) {
            return originalAge - 1;
        } else {
            return originalAge;
        }
    }
}