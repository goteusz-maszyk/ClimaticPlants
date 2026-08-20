package dev.gotitim.climatic_plants.content.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class DeadCropBlock extends BushBlock {
    public static final VoxelShape QUARTER_SHAPE = box(2, 0, 2, 14, 4, 14);
    public static final VoxelShape FULL_SHAPE = box(2, 0, 2, 14, 16, 14);
    public static final BooleanProperty MATURE = BooleanProperty.create("mature");

    public DeadCropBlock(Crop crop, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MATURE, false));
        Item.BY_BLOCK.put(this, crop.seedsItem());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MATURE);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(MATURE) ? FULL_SHAPE : QUARTER_SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return state.is(BlockTags.SUPPORTS_CROPS);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }
}
