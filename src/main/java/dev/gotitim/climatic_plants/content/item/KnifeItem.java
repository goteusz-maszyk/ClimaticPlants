package dev.gotitim.climatic_plants.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class KnifeItem extends Item {
	public KnifeItem(Properties properties) {
		super(properties);
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

	public static ItemAttributeModifiers createAttributes(ToolMaterial material, float attackDamage, float attackSpeed) {
		return ItemAttributeModifiers.builder()
                                     .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                     .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                     .build();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		if (state.getBlock() == Blocks.PUMPKIN) {
			Player player = context.getPlayer();
			if (player != null && level instanceof ServerLevel serverLevel) {
				Direction clickedDirection = context.getClickedFace();
				Direction direction = clickedDirection.getAxis().isVertical() ? player.getDirection().getOpposite() : clickedDirection;

				LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.CARVE_PUMPKIN);
				ItemStack itemStack = context.getItemInHand();

				LootParams params = new LootParams.Builder(serverLevel)
						.withParameter(LootContextParams.BLOCK_STATE, state)
						.withParameter(LootContextParams.INTERACTING_ENTITY, player)
						.withParameter(LootContextParams.TOOL, itemStack)
						.create(LootContextParamSets.BLOCK_INTERACT);

				for (ItemStack drop : lootTable.getRandomItems(params)) {
					ItemEntity entity = new ItemEntity(
							level,
							pos.getX() + 0.5 + direction.getStepX() * 0.65,
							pos.getY() + 0.1,
							pos.getZ() + 0.5 + direction.getStepZ() * 0.65,
							drop
					);
					RandomSource random = level.getRandom();
					entity.setDeltaMovement(
							0.05 * direction.getStepX() + random.nextDouble() * 0.02,
							0.05,
							0.05 * direction.getStepZ() + random.nextDouble() * 0.02
					);
					level.addFreshEntity(entity);
				}

				level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), 11);

				itemStack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());

				level.gameEvent(player, GameEvent.SHEAR, pos);
				player.awardStat(Stats.ITEM_USED.get(this));
			}
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}
}