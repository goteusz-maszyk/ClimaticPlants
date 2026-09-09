package dev.gotitim.climatic_plants.content.fluid;

import dev.gotitim.climatic_plants.content.ClimaticFluids;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class YeastFluid extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.EMPTY;
    }

    @Override
    public Fluid getSource() {
        return ClimaticFluids.YEAST;
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 0;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 8;
    }

    @Override
    public Item getBucket() {
        return ClimaticItems.YEAST_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other,
                                        Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return true;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }
}
