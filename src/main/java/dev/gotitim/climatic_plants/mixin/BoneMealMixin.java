package dev.gotitim.climatic_plants.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.gotitim.climatic_plants.content.crop.GrowthHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BoneMealItem.class)
public class BoneMealMixin {
    @WrapOperation(
            method = "growCrop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BonemealableBlock;performBonemeal(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private static void cancelBonemeal(BonemealableBlock block, ServerLevel serverLevel, RandomSource randomSource,
                                       BlockPos blockPos, BlockState blockState, Operation<Void> original,
                                       @Local(name = "state") BlockState state) {
        if (block instanceof CropBlock && !GrowthHelper.allowVanillaCropGrowth(serverLevel, blockPos, state)) {
            return;
        }
        original.call(block, serverLevel, randomSource, blockPos, blockState);
    }
}
