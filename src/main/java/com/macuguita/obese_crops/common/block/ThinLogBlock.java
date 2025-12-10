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

package com.macuguita.obese_crops.common.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.macuguita.obese_crops.common.reg.OCBlockTags;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

public class ThinLogBlock extends PipeBlock {

	public static final Map<Block, Block> STRIPPED_THIN_LOGS = new HashMap<>();
	public static final float APOTHEM = 0.25f;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final MapCodec<ThinLogBlock> CODEC = simpleCodec(ThinLogBlock::new);
	private final boolean strippable;

	public ThinLogBlock(Properties properties) {
		this(properties, false);
	}

	public ThinLogBlock(Properties properties, boolean strippable) {
		super(APOTHEM, properties);
		this.registerDefaultState(
				this.stateDefinition
						.any()
						.setValue(NORTH, false)
						.setValue(EAST, false)
						.setValue(SOUTH, false)
						.setValue(WEST, false)
						.setValue(UP, false)
						.setValue(DOWN, false)
						.setValue(WATERLOGGED, false)
		);
		this.strippable = strippable;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		BlockState rotated = state;
		for (Direction dir : Direction.values()) {
			Direction newDir = rotation.rotate(dir);
			rotated = rotated.setValue(PROPERTY_BY_DIRECTION.get(newDir), state.getValue(PROPERTY_BY_DIRECTION.get(dir)));
		}
		return rotated;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Rotation rotation = mirror.getRotation(Direction.NORTH);
		if (rotation != Rotation.NONE) {
			return this.rotate(state, rotation);
		}

		BlockState mirrored = state;
		for (Direction dir : Direction.values()) {
			Direction newDir = mirror.mirror(dir);
			mirrored = mirrored.setValue(PROPERTY_BY_DIRECTION.get(newDir), state.getValue(PROPERTY_BY_DIRECTION.get(dir)));
		}
		return mirrored;

	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return this.shouldConnectWithNeighbor(super.updateShape(state, direction, neighborState, level, pos, neighborPos), neighborState, direction);
	}

	private BlockState shouldConnectWithNeighbor(BlockState state, BlockState neighborState, Direction dir) {
		if (!neighborState.is(OCBlockTags.THIN_LOGS) || !(neighborState.getBlock() instanceof ThinLogBlock)) {
			return state;
		}

		if (neighborState.getValue(PROPERTY_BY_DIRECTION.get(dir.getOpposite()))) {
			return state.setValue(PROPERTY_BY_DIRECTION.get(dir), true);
		}
		return state;
	}

	private BlockState shouldConnectWithNeighbors(BlockState state, BlockPos pos, Level level) {
		BlockState temp = state;
		for (Direction direction : Direction.values()) {
			temp = this.shouldConnectWithNeighbor(temp, level.getBlockState(pos.relative(direction)), direction);
		}
		return temp;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction side = ctx.getClickedFace();

		BlockState state = this.defaultBlockState()
				.setValue(PROPERTY_BY_DIRECTION.get(side.getOpposite()), true)
				.setValue(PROPERTY_BY_DIRECTION.get(side), ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown())
				.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER));

		return this.shouldConnectWithNeighbors(state, ctx.getClickedPos(), ctx.getLevel());
	}

	private static Optional<Direction> getDirectionByVec(Vec3 hit, BlockPos pos) {
		double radius = Math.floor(APOTHEM * 16.0f);
		Vec3 relativePos = hit.add(-pos.getX(), -pos.getY(), -pos.getZ()).scale(16.0F);
		if (relativePos.x < (8.0d - radius)) return Optional.of(Direction.WEST);
		else if (relativePos.x > (8.0d + radius)) return Optional.of(Direction.EAST);
		else if (relativePos.z < (8.0d - radius)) return Optional.of(Direction.NORTH);
		else if (relativePos.z > (8.0d + radius)) return Optional.of(Direction.SOUTH);
		else if (relativePos.y < (8.0d - radius)) return Optional.of(Direction.DOWN);
		else if (relativePos.y > (8.0d + radius)) return Optional.of(Direction.UP);
		return Optional.empty();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		Item item = stack.getItem();
		if (stack.is(ItemTags.AXES) && strippable) {
			Block stripped = STRIPPED_THIN_LOGS.get(this);
			if (stripped != null) {
				BlockState strippedState = stripped.defaultBlockState();
				for (BooleanProperty prop : PROPERTY_BY_DIRECTION.values()) {
					strippedState = strippedState.setValue(prop, state.getValue(prop));
				}
				strippedState = strippedState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
				level.setBlockAndUpdate(pos, strippedState);

				level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				if (!player.getAbilities().instabuild)
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
				level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
				return ItemInteractionResult.SUCCESS;
			}
		}
		if (stack.is(ConventionalItemTags.SHEAR_TOOLS)) {
			Direction dir = getDirectionByVec(hitResult.getLocation(), pos)
					.orElse(hitResult.getDirection());
			BooleanProperty prop = PROPERTY_BY_DIRECTION.get(dir);

			boolean current = state.getValue(prop);
			boolean next = !current;

			BlockState newState = state.setValue(prop, next);
			level.setBlockAndUpdate(pos, newState);
			BlockPos neighborPos = pos.relative(dir);
			BlockState neighborState = level.getBlockState(neighborPos);

			if (neighborState.getBlock() instanceof ThinLogBlock && neighborState.is(OCBlockTags.THIN_LOGS)) {
				BooleanProperty opp = PROPERTY_BY_DIRECTION.get(dir.getOpposite());
				BlockState newNeighborState = neighborState.setValue(opp, next);
				level.setBlockAndUpdate(neighborPos, newNeighborState);
			}

			level.gameEvent(player, next ? GameEvent.BLOCK_ATTACH : GameEvent.BLOCK_DETACH, pos);
			if (!player.getAbilities().instabuild)
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			if (!level.isClientSide())
				player.awardStat(Stats.ITEM_USED.get(item));
			level.playSound(player, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);

			return ItemInteractionResult.SUCCESS;
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	protected MapCodec<? extends PipeBlock> codec() {
		return CODEC;
	}
}
