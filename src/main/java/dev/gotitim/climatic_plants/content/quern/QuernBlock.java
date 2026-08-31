package dev.gotitim.climatic_plants.content.quern;

import com.mojang.math.Constants;
import com.mojang.serialization.MapCodec;
import dev.gotitim.climatic_plants.ClimaticBlockEntities;
import dev.gotitim.climatic_plants.content.ClimaticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static dev.gotitim.climatic_plants.content.quern.QuernBlockEntity.*;

public class QuernBlock extends BaseEntityBlock {
    public static final BooleanProperty HAS_HANDSTONE = BooleanProperty.create("has_handstone");

    private static final VoxelShape BASE_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    private static final AABB BASE_AABB = BASE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape HANDSTONE_SHAPE = box(3.0D, 10.0D, 3.0D, 13.0D, 13.76D, 13.0D);
    private static final AABB HANDSTONE_AABB = HANDSTONE_SHAPE.bounds().inflate(0.01D);
    private static final Vec3 HANDSTONE_CENTER = HANDSTONE_SHAPE.bounds().getCenter();

    private static final VoxelShape HANDLE_SHAPE = box(4.34D, 13.76D, 4.34D, 5.36D, 16.24D, 5.36D);
    private static final AABB HANDLE_AABB = HANDLE_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape INPUT_SLOT_SHAPE = box(6.0D, 13.76D, 6.0D, 10.0D, 16.24D, 10.0D);
    private static final AABB INPUT_SLOT_AABB = INPUT_SLOT_SHAPE.bounds().inflate(0.01D);

    private static final VoxelShape FULL_SHAPE = Shapes.join(Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE, HANDLE_SHAPE), INPUT_SLOT_SHAPE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape COLLISION_FULL_SHAPE = Shapes.or(BASE_SHAPE, HANDSTONE_SHAPE);

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState blockState,
                                                                            @NonNull BlockEntityType<T> type) {
        return createTickerHelper(type, ClimaticBlockEntities.QUERN, QuernBlockEntity::tick);
    }

    public QuernBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HAS_HANDSTONE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(QuernBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HAS_HANDSTONE));
    }

    @Override
    protected void entityInside(
            final BlockState state, final Level level, final BlockPos pos, final Entity entity,
            final InsideBlockEffectApplier effectApplier, final boolean isPrecise
    ) {
        if (!(level.getBlockEntity(pos) instanceof QuernBlockEntity quern)) return;

        final float rotationSpeed = quern.getRotationSpeed();
        if (rotationSpeed != 0f &&
                HANDSTONE_AABB.move(pos).contains(entity.position()) &&
                !BASE_AABB.move(pos).contains(entity.position())) {
            Vec3 origin = HANDSTONE_CENTER.add(pos.getX(), pos.getY(), pos.getZ());
            float speed = rotationSpeed * Constants.RAD_TO_DEG;

            if (!entity.onGround() || entity.getDeltaMovement().y > 0 || speed == 0f) {
                return;
            }

            final float rot = (entity.getYHeadRot() + speed) % 360f;
            entity.setYRot(rot);
            if (level.isClientSide() && entity instanceof Player) {
                final Vec3 offset = entity.position().subtract(origin).normalize();
                final Vec3 movement = new Vec3(-offset.z, 0, offset.x).scale(speed / 48f);
                entity.setDeltaMovement(entity.getDeltaMovement().add(movement));
                entity.hurtMarked = true;
                return;
            }

            if (entity instanceof LivingEntity living) {
                entity.setYHeadRot(rot);
                entity.setYBodyRot(rot);
                entity.setOnGround(false);
                living.setNoActionTime(20);
                living.hurtMarked = true;
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof QuernBlockEntity quern)) return InteractionResult.PASS;

        if (quern.isGrinding()) return InteractionResult.PASS;

        final SelectionPlace selection = getPlayerSelection(quern, pos, player, hitResult);
        return switch (selection) {
            case HANDLE -> attemptGrind(quern);
            case INPUT_SLOT -> insertOrExtract(quern, hand, player, false);
            case HANDSTONE -> player.isShiftKeyDown() || player.getItemInHand(hand).is(ClimaticItems.HANDSTONE)
                    ? insertOrExtract(quern, hand, player, false)
                    : attemptGrind(quern);
            case BASE -> insertOrExtract(quern, hand, player, true);
        };
    }

    private static SelectionPlace getPlayerSelection(QuernBlockEntity quern, BlockPos pos, Player player, BlockHitResult result) {
        final ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        final Vec3 hit = result.getLocation();

        if (quern.hasHandstone()) {
            if (!quern.isGrinding()) {
                if (HANDLE_AABB.move(pos).contains(hit)) {
                    return SelectionPlace.HANDLE;
                } else if (!held.isEmpty()) {
                    return SelectionPlace.INPUT_SLOT;
                }
            }
            if (!quern.getItem(SLOT_INPUT).isEmpty() && INPUT_SLOT_AABB.move(pos).contains(hit)) {
                return SelectionPlace.INPUT_SLOT;
            }
        }
        if ((quern.hasHandstone() || quern.isItemValid(SLOT_HANDSTONE, held)) && HANDSTONE_AABB.move(pos).contains(hit))
            return SelectionPlace.HANDSTONE;
        return SelectionPlace.BASE;
    }

    private static InteractionResult insertOrExtract(QuernBlockEntity quern, InteractionHand hand, Player player, boolean outputSlot) {
        final ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty()) {
            if (outputSlot) {
                player.setItemInHand(hand, quern.removeItem(SLOT_OUTPUT));
            } else {
                ItemStack inputStack = quern.removeItem(SLOT_INPUT);
                if (!inputStack.isEmpty()) {
                    player.setItemInHand(hand, inputStack);
                } else {
                    player.setItemInHand(hand, quern.removeItem(SLOT_HANDSTONE));
                }
            }
        } else {
            player.setItemInHand(hand, quern.insert(heldStack));
        }
        quern.updateHandstone();
        quern.setChanged();
        return InteractionResult.SUCCESS;
    }

    private InteractionResult attemptGrind(QuernBlockEntity quern)
    {
        return quern.startGrinding() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HAS_HANDSTONE) ? FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HAS_HANDSTONE) ? COLLISION_FULL_SHAPE : BASE_SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuernBlockEntity(pos, state);
    }

    private enum SelectionPlace {
        HANDLE,
        HANDSTONE,
        INPUT_SLOT,
        BASE
    }
}
