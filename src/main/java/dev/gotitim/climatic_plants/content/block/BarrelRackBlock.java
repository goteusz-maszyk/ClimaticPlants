package dev.gotitim.climatic_plants.content.block;

import dev.gotitim.climatic_plants.content.barrel.FluidBarrelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class BarrelRackBlock extends Block {
    public BarrelRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                              Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
                                              RandomSource random) {
        return directionToNeighbour == Direction.DOWN && !neighbourState.isFaceSturdy(level, neighbourPos, Direction.UP) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return FluidBarrelBlock.RACK_SHAPE;
    }
}
