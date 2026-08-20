package dev.gotitim.climatic_plants.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @Shadow
    protected abstract ItemLike getBaseSeedId();

    /**
     * @author gotitim
     * @reason adding custom block
     */
    @Overwrite
    protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
        Item item = ((CropBlock) (Object) this).asItem();
        return new ItemStack(item == Items.AIR ? getBaseSeedId() : item);
    }
}
