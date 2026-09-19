package dev.gotitim.climatic_plants.content.block;

import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BarrelRackBlock extends Block {
    public BarrelRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                              BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos,
                                              BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN && !neighbourState.isFaceSturdy(level, neighbourPos, Direction.UP)) {
            if (level instanceof Level lv) lv.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                           CollisionContext context) {
        return FluidBarrelBlock.RACK_SHAPE;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return context.getItemInHand().is(ClimaticBlocks.FLUID_BARREL.asItem()) && context.getLevel().getBlockState(
                context.getClickedPos().below()).isFaceSturdy(context.getLevel(), context.getClickedPos().below(),
                Direction.UP
        );
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getLevel().getBlockState(context.getClickedPos().below())
                      .isFaceSturdy(context.getLevel(), context.getClickedPos().below(),
                              Direction.UP
                      ) ? super.getStateForPlacement(context) : null;
    }
}
