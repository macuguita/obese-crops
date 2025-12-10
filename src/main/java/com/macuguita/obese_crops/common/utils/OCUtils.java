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

package com.macuguita.obese_crops.common.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OCUtils {

	public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction.Axis axis, int degrees) {
		int times = ((degrees % 360) + 360) % 360 / 90;
		if (times == 0 || shape.isEmpty()) return shape;

		VoxelShape result = shape;
		for (int i = 0; i < times; ++i) {
			VoxelShape rotated = Shapes.empty();
			for (AABB aabb : result.toAabbs()) {
				AABB rotatedBox = switch (axis) {
					case Y -> new AABB(
							1 - aabb.maxZ, aabb.minY, aabb.minX,
							1 - aabb.minZ, aabb.maxY, aabb.maxX
					);
					case X -> new AABB(
							aabb.minX, 1 - aabb.maxZ, aabb.minY,
							aabb.maxX, 1 - aabb.minZ, aabb.maxY
					);
					case Z -> new AABB(
							aabb.minY, aabb.minX, aabb.minZ,
							aabb.maxY, aabb.maxX, aabb.maxZ
					);
				};
				rotated = Shapes.or(rotated, Shapes.create(rotatedBox));
			}
			result = rotated;
		}
		return result;
	}
}
