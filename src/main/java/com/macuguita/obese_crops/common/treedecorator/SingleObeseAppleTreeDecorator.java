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

package com.macuguita.obese_crops.common.treedecorator;

import java.util.List;

import com.macuguita.obese_crops.common.reg.OCObjects;
import com.macuguita.obese_crops.common.reg.OCWorldgen;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class SingleObeseAppleTreeDecorator extends TreeDecorator {

	public static final MapCodec<SingleObeseAppleTreeDecorator> CODEC = MapCodec.unit(SingleObeseAppleTreeDecorator::new);

	@Override
	protected @NotNull TreeDecoratorType<?> type() {
		return OCWorldgen.APPLE_DECORATOR;
	}

	@Override
	public void place(@NotNull Context generator) {
		RandomSource random = generator.random();

		List<BlockPos> leaves = Util.shuffledCopy(generator.leaves(), random);

		for (BlockPos leafPos : leaves) {

			Direction direction = Direction.DOWN;
			BlockPos applePos = leafPos.relative(direction);

			if (meetsRequiredEmptyBlocks(generator, leafPos, direction, 1)) {
				generator.setBlock(applePos, OCObjects.OBESE_APPLE.get().defaultBlockState());
				break;
			}
		}
	}

	private boolean meetsRequiredEmptyBlocks(TreeDecorator.Context generator, BlockPos pos, Direction direction, int requiredEmptyBlocks) {
		for (int i = 1; i <= requiredEmptyBlocks; i++) {
			BlockPos checkPos = pos.relative(direction, i);
			if (!generator.isAir(checkPos)) {
				return false;
			}
		}
		return true;
	}
}
