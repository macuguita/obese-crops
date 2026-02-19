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

package com.macuguita.obese_crops.mixin;

import java.util.Optional;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.reg.OCBlockTags;
import com.macuguita.obese_crops.common.resourcereloader.ObeseMapResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

	@Shadow
	public abstract int getAge(BlockState state);

	@Shadow
	public abstract int getMaxAge();

	@Definition(id = "level", local = @Local(type = ServerLevel.class, argsOnly = true))
	@Definition(id = "setBlock", method = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Definition(id = "getStateForAge", method = "Lnet/minecraft/world/level/block/CropBlock;getStateForAge(I)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "i", local = @Local(type = int.class))
	@Expression("level.setBlock(pos, this.getStateForAge(i + 1), 2)")
	@ModifyArg(
			method = "randomTick",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	protected BlockState obese_crops$randomTickObeseBlockReplacement(
			BlockState blockState,
			@Local(argsOnly = true) RandomSource random,
			@Local(type = float.class, ordinal = 0) float growthSpeed,
			@Share("turnsToObese") LocalBooleanRef turnsToObese,
			@Share("blockAndChance") LocalRef<ObeseMapResourceReloadListener.ObeseBlockData.Entry> obeseBACRef
	) {
		Optional<ObeseMapResourceReloadListener.ObeseBlockData> obeseBlockData =
				ObeseCrops.getObeseBlockData(blockState.getBlock());

		if (obeseBlockData.isPresent() && this.getAge(blockState) == this.getMaxAge() - 1) {

			int chance = obeseBlockData.get().primary().chance() *
					Math.max(1, Math.round(12 - Math.max(1.0F, Math.min(growthSpeed, 9.0F))));

			if (random.nextInt(chance) == 0) {
				blockState = obese_crops$pickObeseBlock(obeseBlockData.get(), random, obeseBACRef).defaultBlockState();
				turnsToObese.set(true);
			}
		}

		return blockState;
	}

	@Definition(id = "level", local = @Local(type = ServerLevel.class, argsOnly = true))
	@Definition(id = "setBlock", method = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Definition(id = "getStateForAge", method = "Lnet/minecraft/world/level/block/CropBlock;getStateForAge(I)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "i", local = @Local(type = int.class))
	@Expression("level.setBlock(pos, this.getStateForAge(i + 1), 2)")
	@WrapOperation(
			method = "randomTick",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	protected boolean obese_crops$randomTickRootBlocks(
			ServerLevel instance,
			BlockPos blockPos,
			BlockState blockState,
			int i,
			Operation<Boolean> original,
			@Share("turnsToObese") LocalBooleanRef turnsToObese,
			@Share("blockAndChance") LocalRef<ObeseMapResourceReloadListener.ObeseBlockData.Entry> obeseBACRef
	) {
		boolean toReturn = original.call(instance, blockPos, blockState, i);
		obese_crops$transformBlocks(instance, blockPos, turnsToObese.get(), obeseBACRef.get());
		return toReturn;
	}

	@Definition(id = "level", local = @Local(type = Level.class, argsOnly = true))
	@Definition(id = "setBlock", method = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Definition(id = "getStateForAge", method = "Lnet/minecraft/world/level/block/CropBlock;getStateForAge(I)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "i", local = @Local(type = int.class, ordinal = 0))
	@Expression("level.setBlock(pos, this.getStateForAge(i), 2)")
	@ModifyArg(
			method = "growCrops",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	protected BlockState obese_crops$applyGrowthObeseBlockReplacement(
			BlockState blockState,
			@Local(argsOnly = true) Level level,
			@Local(type = int.class, ordinal = 0) int futureAge,
			@Share("turnsToObese") LocalBooleanRef turnsToObese,
			@Share("blockAndChance") LocalRef<ObeseMapResourceReloadListener.ObeseBlockData.Entry> obeseBACRef
	) {
		Optional<ObeseMapResourceReloadListener.ObeseBlockData> obeseBlockData =
				ObeseCrops.getObeseBlockData(blockState.getBlock());

		if (obeseBlockData.isPresent() && futureAge == this.getMaxAge()) {
			int primaryChance = obeseBlockData.get().primary().chance();

			if (level.getRandom().nextInt(primaryChance) == 0) {
				blockState = obese_crops$pickObeseBlock(obeseBlockData.get(), level.getRandom(), obeseBACRef).defaultBlockState();
				turnsToObese.set(true);
			}
		}

		return blockState;
	}


	@Definition(id = "level", local = @Local(type = Level.class, argsOnly = true))
	@Definition(id = "setBlock", method = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Definition(id = "getStateForAge", method = "Lnet/minecraft/world/level/block/CropBlock;getStateForAge(I)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "i", local = @Local(type = int.class, ordinal = 0))
	@Expression("level.setBlock(pos, this.getStateForAge(i), 2)")
	@WrapOperation(
			method = "growCrops",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	protected boolean obese_crops$applyGrowthRootBlocks(
			Level instance,
			BlockPos pos,
			BlockState newState,
			int flags,
			Operation<Boolean> original,
			@Share("turnsToObese") LocalBooleanRef turnsToObese,
			@Share("blockAndChance") LocalRef<ObeseMapResourceReloadListener.ObeseBlockData.Entry> obeseBACRef
	) {
		boolean toReturn = original.call(instance, pos, newState, flags);;
		obese_crops$transformBlocks(instance, pos, turnsToObese.get(), obeseBACRef.get());
		return toReturn;
	}

	@Unique
	private Block obese_crops$pickObeseBlock(
			ObeseMapResourceReloadListener.ObeseBlockData data,
			RandomSource random,
			LocalRef<ObeseMapResourceReloadListener.ObeseBlockData.Entry> obeseBACRef
	) {
		for (var secondary : data.secondaries()) {
			if (random.nextInt(secondary.chance()) == 0) {
				obeseBACRef.set(secondary);
				return secondary.obese();
			}
		}

		obeseBACRef.set(data.primary());
		return data.primary().obese();
	}

	@Unique
	private void obese_crops$transformBlocks(
			Level level, BlockPos pos,
			boolean turnsToObese,
			ObeseMapResourceReloadListener.ObeseBlockData.Entry blockAndChance
	) {
		if (turnsToObese) {
			if (level.getBlockState(pos.below()).getBlock() instanceof FarmBlock) {
				BlockState obese = blockAndChance.obese().defaultBlockState();
				BlockState below = ObeseCrops.CONFIG.doubleTallCrops && obese.is(OCBlockTags.DOUBLE_OBESE_CROP)
						? obese
						: ObeseCrops.CONFIG.rootedDirtUnderCrops
						? Blocks.ROOTED_DIRT.defaultBlockState()
						: null;
				if (below != null) {
					level.setBlock(pos.below(), below, Block.UPDATE_CLIENTS);
				}
				level.setBlock(pos.above(),
						blockAndChance.foliage().defaultBlockState(),
						Block.UPDATE_CLIENTS);
			}
		}
	}
}
