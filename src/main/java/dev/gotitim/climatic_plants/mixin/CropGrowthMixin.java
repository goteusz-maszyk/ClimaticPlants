package dev.gotitim.climatic_plants.mixin;

import dev.gotitim.climatic_plants.content.crop.GrowthHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropGrowthMixin {
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CropBlock;getMaxAge()I"))
    public void modifyRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                 CallbackInfo ci) {
        GrowthHelper.allowVanillaCropGrowth(level, pos, state);
    }

    /**
     * @author gotitim
     * @reason crops will die when growing for too long
     */
    @Overwrite
    public boolean isRandomlyTicking(final BlockState state) {
        return true;
    }
}
