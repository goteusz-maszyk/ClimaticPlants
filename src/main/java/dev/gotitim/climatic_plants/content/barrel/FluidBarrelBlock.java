package dev.gotitim.climatic_plants.content.barrel;

import com.mojang.serialization.MapCodec;
import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.ClimaticBlocks;
import dev.gotitim.climatic_plants.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class FluidBarrelBlock extends BaseEntityBlock {
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class,
            d -> d != Direction.DOWN
    );
    public static final BooleanProperty RACK = BooleanProperty.create("rack");

    public static final VoxelShape SHAPE_Z = box(2, 0, 0, 14, 12, 16);
    public static final VoxelShape SHAPE_X = box(0, 0, 2, 16, 12, 14);
    public static final VoxelShape RACK_SHAPE = Shapes.or(box(0, 0, 0, 2, 16, 2), box(14, 0, 14, 16, 16, 16),
            box(14, 0, 0, 16, 16, 2), box(0, 0, 14, 2, 16, 16), box(0, 14, 0, 16, 16, 16)
    );
    public static final VoxelShape SHAPE_Z_RACK = Shapes.or(SHAPE_Z, RACK_SHAPE);
    public static final VoxelShape SHAPE_X_RACK = Shapes.or(SHAPE_X, RACK_SHAPE);
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_UNSEALED = Shapes.join(SHAPE, box(3, 1, 3, 13, 16, 13), BooleanOp.ONLY_FIRST);

    public FluidBarrelBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(SEALED, false).setValue(RACK, false));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
                                                                            BlockEntityType<T> type) {
        return createTickerHelper(type, ClimaticBlockEntities.FLUID_BARREL, FluidBarrelBlockEntity::tickOuter);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        final Optional<FluidBarrelBlockEntity> barrelOpt = level.getBlockEntity(pos,
                ClimaticBlockEntities.FLUID_BARREL
        );
        if (barrelOpt.isEmpty()) return InteractionResult.PASS;
        FluidBarrelBlockEntity barrel = barrelOpt.get();

        if (heldStack.isEmpty() && player.isShiftKeyDown()) {
            if (state.getValue(RACK) && level.getBlockState(pos.above())
                                             .isAir() && hitResult.getLocation().y - pos.getY() > 0.875f) {
                player.setItemInHand(hand, new ItemStack(ClimaticBlocks.BARREL_RACK));
                level.setBlockAndUpdate(pos, state.setValue(RACK, false));
            } else {
                final boolean nextSealed = !state.getValue(SEALED);
                level.setBlockAndUpdate(pos, state.setValue(SEALED, nextSealed));
                barrel.setChanged();
            }
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.85f);
            return InteractionResult.SUCCESS_SERVER;
        }

        if (heldStack.is(ClimaticBlocks.BARREL_RACK.asItem()) && state.getValue(
                FACING) != Direction.UP && !state.getValue(RACK)) {
            if (!player.isCreative()) {
                heldStack.shrink(1);
            }
            level.setBlockAndUpdate(pos,
                    state.setValue(RACK, true).setValue(FACING, player.getDirection().getOpposite())
            );
            var sound = state.getSoundType();
            level.playSound(player, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F,
                    sound.getPitch() * 0.8F
            );
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!state.getValue(SEALED)) {
            if (ItemUtils.transferFluid(heldStack, barrel, player, hand)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            barrel.insertOrExtract(player, hand);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
                                     RandomSource random) {
        if (state.getValue(FACING).getAxis().isHorizontal() && directionToNeighbour == Direction.DOWN && !level
                .getBlockState(neighbourPos).isFaceSturdy(level, neighbourPos, Direction.UP, SupportType.CENTER)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            Direction dir = context.getClickedFace();
            if (dir == Direction.DOWN) {
                dir = Direction.UP;
            }
            state = state.setValue(FACING, dir);

            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();

            // case of replacing a barrel rack block
            if (level.getBlockState(pos).is(ClimaticBlocks.BARREL_RACK)) {
                return state.setValue(FACING, context.getHorizontalDirection()).setValue(RACK, true);
            }

            // Require a supporting block below to be placing on.
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.CENTER)) {
                return null;
            }
        }
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        final Direction direction = rot.rotate(state.getValue(FACING));
        return state.setValue(FACING, direction == Direction.DOWN ? Direction.UP : direction);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        final boolean rack = state.getValue(RACK);
        return switch (state.getValue(FACING).getAxis()) {
            case X -> rack ? SHAPE_X_RACK : SHAPE_X;
            case Z -> rack ? SHAPE_Z_RACK : SHAPE_Z;
            case Y -> state.getValue(SEALED) ? SHAPE : SHAPE_UNSEALED;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SEALED, RACK, POWERED);
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NonNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof FluidBarrelBlockEntity barrel) {
            final SingleFluidStorage tank = barrel.getFluidStorage(direction);
            if (!tank.isResourceBlank()) {
                return (int) Mth.clamp(tank.getAmount() * 15 / FluidBarrelBlockEntity.CAPACITY, 1, 15);
            }
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                   @Nullable Orientation orientation, boolean movedByPiston) {
        final boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED)) {
            if (signal == state.getValue(SEALED)) {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal));
            } else {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, signal).setValue(SEALED, signal));

                level.getBlockEntity(pos, ClimaticBlockEntities.FLUID_BARREL).ifPresent(
                        FluidBarrelBlockEntity::setChanged);
            }
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(FluidBarrelBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(pos, state);
    }
}
