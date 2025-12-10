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

import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.reg.OCItemTags;
import com.macuguita.obese_crops.common.utils.OCUtils;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ObeseCropBlock extends HorizontalDirectionalBlock {

	public static final MapCodec<ObeseCropBlock> CODEC = simpleCodec(ObeseCropBlock::new);
	public static final IntegerProperty CARVED = IntegerProperty.create("carved", 0, 3);
	public static final VoxelShape[] VOXEL_SHAPES = {
			Shapes.block(),
			Shapes.or(
					Block.box(8, 0, 0, 16, 16, 8),
					Block.box(0, 0, 8, 16, 16, 16)
			),
			Block.box(0, 0, 8, 16, 16, 16),
			Block.box(0, 0, 8, 8, 16, 16),
	};

	public ObeseCropBlock(Properties settings) {
		super(settings
				.pushReaction(PushReaction.DESTROY)
		);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(CARVED, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, CARVED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public void dropPart(Level level, BlockPos pos) {
		popResource(level, pos, new ItemStack(ObeseCrops.getObeseDrops(this).orElse(Items.AIR), level.getRandom().nextIntBetweenInclusive(2, 4)));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.is(OCItemTags.SHARP_TOOLS)) {
			if (state.getValue(CARVED) == 3) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			} else {
				level.setBlockAndUpdate(pos, state.setValue(CARVED, state.getValue(CARVED) + 1));
			}
			dropPart(level, pos);
			Item item = stack.getItem();
			level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			if (!level.isClientSide()) player.awardStat(Stats.ITEM_USED.get(item));
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = VOXEL_SHAPES[state.getValue(CARVED)];
		return switch (state.getValue(FACING)) {
			case NORTH -> shape;
			case SOUTH -> OCUtils.rotateVoxelShape(shape, Direction.Axis.Y, 180);
			case WEST -> OCUtils.rotateVoxelShape(shape, Direction.Axis.Y, 270);
			case EAST -> OCUtils.rotateVoxelShape(shape, Direction.Axis.Y, 90);
			default -> Shapes.empty();
		};
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}
}
