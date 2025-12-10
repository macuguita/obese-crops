/*
 * Copyright (c) 2025 macuguita
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.macuguita.obese_crops.common.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.macuguita.obese_crops.common.reg.OCBlockTags;
import com.macuguita.obese_crops.common.reg.OCComponents;
import com.macuguita.obese_crops.common.reg.OCEnchantmentComponents;
import com.macuguita.obese_crops.mixin.HoeItemAccessor;
import org.apache.commons.lang3.mutable.MutableFloat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ScytheItem extends DiggerItem {

	public ScytheItem(Tier material, int damage, float speed, float pullingSpeed, Properties settings) {
		super(material, OCBlockTags.SCYTHE_MINABLE, settings
				.attributes(createAttributes(material, damage, speed))
				.component(DataComponents.TOOL, createToolProperties())
				.component(OCComponents.PULLING_SPEED.get(), pullingSpeed));
	}

	private static Tool createToolProperties() {
		return new Tool(
				List.of(Tool.Rule.overrideSpeed(OCBlockTags.SCYTHE_MINABLE, 1.5F)), 1.0F, 2
		);
	}

	public static ItemAttributeModifiers createAttributes(Tier material, int damage, float speed) {
		return ItemAttributeModifiers.builder()
				.add(
						Attributes.ATTACK_DAMAGE,
						new AttributeModifier(
								BASE_ATTACK_DAMAGE_ID, ((float) damage + material.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
						),
						EquipmentSlotGroup.MAINHAND
				)
				.add(
						Attributes.ATTACK_SPEED,
						new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				)
				.build();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		boolean success = false;

		if (!level.isClientSide) {
			BlockPos playerSteppingPos = player.getOnPos();
			BlockPos playerBlockPos = player.blockPosition();

			BlockPos playerPos = playerSteppingPos.getY() > playerBlockPos.getY() ? playerSteppingPos : playerBlockPos;

			double yawRad = Math.toRadians(player.getYRot());
			int dirX = (int) Math.round(-Math.sin(yawRad)) * 2;
			int dirZ = (int) Math.round(Math.cos(yawRad)) * 2;

			BlockPos centerPos = playerPos.offset(dirX, 0, dirZ);

			int radius = getCollectionRadius(itemStack, player);

			for (BlockPos pos : BlockPos.betweenClosed(
					centerPos.getX() - radius, centerPos.getY() - radius, centerPos.getZ() - radius,
					centerPos.getX() + radius, centerPos.getY() + radius, centerPos.getZ() + radius)) {
				BlockState state = level.getBlockState(pos);
				if (state.is(OCBlockTags.SCYTHE_WEEDS)) {
					success |= handleWeed(level, pos, state, player);
				}
			}

			Vec3 forward = player.getLookAngle();
			Vec3 center = player.position().add(forward.scale(2.0));

			double halfSize = radius + 0.5;
			AABB area = new AABB(
					center.x - halfSize, center.y - 1.5, center.z - halfSize,
					center.x + halfSize, center.y + 1.5, center.z + halfSize
			);

			double strength = 1.0D;
			Float pullingSpeed = itemStack.get(OCComponents.PULLING_SPEED.get());
			if (pullingSpeed != null) {
				strength = pullingSpeed;
			}

			Vec3 playerPosVec = player.position();
			List<Entity> entities = level.getEntities(player, area);

			for (Entity entity : entities) {
				if (entity instanceof ItemEntity itemEntity) {
					itemEntity.setDeltaMovement(playerPosVec.subtract(itemEntity.position()).scale(strength));
					itemEntity.hurtMarked = true;
					itemEntity.setThrower(player);
				}
			}

			player.getCooldowns().addCooldown(this, 10);
			if (success) {
				itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			}
		}

		return InteractionResultHolder.success(itemStack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos centerPos = context.getClickedPos();
		Player player = context.getPlayer();

		if (player == null) return super.useOn(context);

		boolean success = false;

		boolean gatherCrops = level.getBlockState(centerPos).getBlock() instanceof CropBlock;
		boolean breakWeeds = level.getBlockState(centerPos).is(OCBlockTags.SCYTHE_WEEDS);

		int radius = getCollectionRadius(context.getItemInHand(), player);

		for (BlockPos pos : BlockPos.betweenClosed(
				centerPos.getX() - radius, centerPos.getY(), centerPos.getZ() - radius,
				centerPos.getX() + radius, centerPos.getY(), centerPos.getZ() + radius)) {

			BlockState state = level.getBlockState(pos);

			if (gatherCrops) {
				success |= handleCrop(level, pos, state, player, context);
			} else if (breakWeeds) {
				success |= handleWeed(level, pos, state, player);
			} else {
				success |= handleTillable(level, pos, state, player, context);
			}
		}

		if (success && !level.isClientSide) {
			context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
			level.playSound(player, centerPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
			player.getCooldowns().addCooldown(this, 10);
		}

		return success ? InteractionResult.sidedSuccess(level.isClientSide) : super.useOn(context);
	}

	private boolean handleCrop(Level level, BlockPos pos, BlockState state,
							   Player player, UseOnContext context) {

		if (!(state.getBlock() instanceof CropBlock cropBlock) || !cropBlock.isMaxAge(state)) {
			return false;
		}

		if (!level.isClientSide) {
			Block.dropResources(state, level, pos, null, player, context.getItemInHand());
			return level.setBlock(pos, cropBlock.getStateForAge(1), 2);
		}

		return true;
	}

	private boolean handleWeed(Level level, BlockPos pos, BlockState state, Player player) {
		if (!state.is(OCBlockTags.SCYTHE_WEEDS)) {
			return false;
		}

		if (!level.isClientSide) {
			return level.destroyBlock(pos, true, player);
		}

		return true;
	}

	private boolean handleTillable(Level level, BlockPos pos, BlockState state,
								   Player player, UseOnContext context) {

		var pair = HoeItemAccessor.obese_crops$getTillables().get(state.getBlock());
		if (pair == null) return false;

		Predicate<UseOnContext> predicate = pair.getFirst();
		Consumer<UseOnContext> consumer = pair.getSecond();

		UseOnContext modifiedContext = new UseOnContext(
				context.getLevel(), player, context.getHand(), context.getItemInHand(),
				new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, context.isInside())
		);

		if (predicate.test(modifiedContext)) {
			if (!level.isClientSide) {
				consumer.accept(modifiedContext);
			}
			return true;
		}

		return false;
	}

	public static int getCollectionRadius(ItemStack stack, LivingEntity attacker) {
		MutableFloat mutableFloat = new MutableFloat(1.0F);
		EnchantmentHelper.runIterationOnItem(
				stack,
				(holder, i) -> holder.value().modifyUnfilteredValue(
						OCEnchantmentComponents.SCYTHE_PULLING_RANGE, attacker.getRandom(), i, mutableFloat
				)
		);
		return Mth.floor(Math.max(0.0F, mutableFloat.floatValue()));
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player miner) {
		return !miner.isCreative();
	}
}
