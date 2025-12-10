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

package com.macuguita.obese_crops.common.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Lists;
import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.reg.OCWorldgen;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import org.jspecify.annotations.Nullable;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public class ThinTrunkPlacer extends TrunkPlacer {
	public static final MapCodec<ThinTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> trunkPlacerParts(instance).apply(instance, ThinTrunkPlacer::new)
	);

	private static final double TRUNK_HEIGHT_SCALE = 0.618;
	private static final double CLUSTER_DENSITY_MAGIC = 1.382;
	private static final double BRANCH_SLOPE = 0.381;
	private static final double BRANCH_LENGTH_MAGIC = 0.328;

	private final Set<BlockPos> allLogPositions = new HashSet<>();
	private final Set<BlockPos> mainTrunkPositions = new HashSet<>();
	private final List<List<BlockPos>> branches = new ArrayList<>();
	private @Nullable List<BlockPos> currentBranch = null;

	public ThinTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
		super(baseHeight, heightRandA, heightRandB);
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return OCWorldgen.THIN_TRUNK_PLACER.get();
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(
			LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, int freeTreeHeight,
			BlockPos pos,
			TreeConfiguration config
	) {
		allLogPositions.clear();
		mainTrunkPositions.clear();
		branches.clear();
		currentBranch = null;

		int j = freeTreeHeight + 2;
		int k = Mth.floor(j * TRUNK_HEIGHT_SCALE);
		setDirtAt(level, blockSetter, random, pos.below(), config);
		int l = Math.min(1, Mth.floor(CLUSTER_DENSITY_MAGIC + Math.pow(1.0 * j / 13.0, 2.0)));
		int m = pos.getY() + k;
		int n = j - 5;
		List<ThinTrunkPlacer.FoliageCoords> list = Lists.newArrayList();
		list.add(new ThinTrunkPlacer.FoliageCoords(pos.above(n), m));

		for (; n >= 0; n--) {
			float f = treeShape(j, n);
			if (!(f < 0.0F)) {
				for (int o = 0; o < l; o++) {
					double g = 1.0 * f * (random.nextFloat() + BRANCH_LENGTH_MAGIC);
					double h = random.nextFloat() * 2.0F * Math.PI;
					double p = g * Math.sin(h) + 0.5;
					double q = g * Math.cos(h) + 0.5;
					BlockPos blockPos = pos.offset(Mth.floor(p), n - 1, Mth.floor(q));
					if (blockPos.getX() == pos.getX() && blockPos.getZ() == pos.getZ() || isTooCloseToExistingBranch(pos, blockPos)) {
						continue;
					}
					BlockPos blockPos2 = blockPos.above(5);

					currentBranch = new ArrayList<>();
					if (this.makeLimb(level, blockPos, blockPos2, false)) {
						int r = pos.getX() - blockPos.getX();
						int s = pos.getZ() - blockPos.getZ();
						double t = blockPos.getY() - Math.sqrt(r * r + s * s) * BRANCH_SLOPE;
						int u = t > m ? m : (int) t;
						BlockPos blockPos3 = new BlockPos(pos.getX(), u, pos.getZ());
						if (this.makeLimb(level, blockPos3, blockPos, false)) {
							list.add(new ThinTrunkPlacer.FoliageCoords(blockPos, blockPos3.getY()));
						}
					}
					if (!currentBranch.isEmpty()) {
						branches.add(currentBranch);
					}
					currentBranch = null;
				}
			}
		}

		currentBranch = null;
		this.makeLimb(level, pos, pos.above(k), true);
		mainTrunkPositions.addAll(allLogPositions);

		this.makeBranches(level, j, pos, list);

		this.placeAllLogsWithConnections(level, blockSetter, random, config);

		List<FoliagePlacer.FoliageAttachment> list2 = Lists.newArrayList();

		for (ThinTrunkPlacer.FoliageCoords foliageCoords : list) {
			if (this.trimBranches(j, foliageCoords.branchBase() - pos.getY())) {
				list2.add(foliageCoords.attachment);
			}
		}

		return list2;
	}

	private boolean makeLimb(
			LevelSimulatedReader level,
			BlockPos start,
			BlockPos end,
			boolean modifyWorld
	) {
		BlockPos current = start;
		if (modifyWorld) {
			allLogPositions.add(current.immutable());
			if (currentBranch != null) currentBranch.add(current.immutable());
		} else if (!this.isFree(level, current)) {
			return false;
		}

		while (!current.equals(end)) {
			int dx = end.getX() - current.getX();
			int dy = end.getY() - current.getY();
			int dz = end.getZ() - current.getZ();

			int stepX = Integer.signum(dx);
			int stepY = Integer.signum(dy);
			int stepZ = Integer.signum(dz);

			if (Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= Math.abs(dz) && dx != 0) {
				current = current.offset(stepX, 0, 0);
			} else if (Math.abs(dz) >= Math.abs(dx) && Math.abs(dz) >= Math.abs(dy) && dz != 0) {
				current = current.offset(0, 0, stepZ);
			} else if (dy != 0) {
				current = current.offset(0, stepY, 0);
			} else if (dx != 0) {
				current = current.offset(stepX, 0, 0);
			} else if (dz != 0) {
				current = current.offset(0, 0, stepZ);
			}

			if (modifyWorld) {
				allLogPositions.add(current.immutable());
				if (currentBranch != null) currentBranch.add(current.immutable());
			} else if (!this.isFree(level, current)) {
				return false;
			}
		}

		return true;
	}

	private void placeAllLogsWithConnections(
			LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random,
			TreeConfiguration config
	) {
		for (BlockPos logPos : allLogPositions) {
			this.placeLog(
					level, blockSetter, random, logPos, config,
					blockState -> this.setDirectionalConnections(blockState, logPos)
			);
		}
	}

	private BlockState setDirectionalConnections(BlockState state, BlockPos currentPos) {
		state = state.setValue(ThinLogBlock.NORTH, false)
				.setValue(ThinLogBlock.EAST, false)
				.setValue(ThinLogBlock.SOUTH, false)
				.setValue(ThinLogBlock.WEST, false)
				.setValue(ThinLogBlock.UP, false)
				.setValue(ThinLogBlock.DOWN, false);

		if (mainTrunkPositions.contains(currentPos) && currentPos.equals(currentPos.below().above(1))) {
			if (currentPos.equals(currentPos.below().above(1))) {
				state = state.setValue(ThinLogBlock.DOWN, true);
			}
		}

		boolean isMainTrunk = mainTrunkPositions.contains(currentPos);
		List<BlockPos> myBranch = null;

		if (!isMainTrunk) {
			for (List<BlockPos> branch : branches) {
				if (branch.contains(currentPos)) {
					myBranch = branch;
					break;
				}
			}
		}

		for (Direction dir : Direction.values()) {
			BlockPos neighbor = currentPos.relative(dir);
			if (!allLogPositions.contains(neighbor)) {
				continue;
			}

			boolean neighborIsMainTrunk = mainTrunkPositions.contains(neighbor);

			if (myBranch != null) {
				if (myBranch.contains(neighbor)) {
					state = state.setValue(getPropertyForDirection(dir), true);
				} else if (neighborIsMainTrunk && myBranch.indexOf(currentPos) == 0) {
					state = state.setValue(getPropertyForDirection(dir), true);
				}
			} else if (isMainTrunk) {
				if (neighborIsMainTrunk) {
					state = state.setValue(getPropertyForDirection(dir), true);
				} else {
					for (List<BlockPos> branch : branches) {
						if (!branch.isEmpty() && branch.get(0).equals(neighbor)) {
							state = state.setValue(getPropertyForDirection(dir), true);
							break;
						}
					}
				}
			}
		}

		return state;
	}

	private BooleanProperty getPropertyForDirection(Direction direction) {
		return switch (direction) {
			case NORTH -> ThinLogBlock.NORTH;
			case EAST -> ThinLogBlock.EAST;
			case SOUTH -> ThinLogBlock.SOUTH;
			case WEST -> ThinLogBlock.WEST;
			case UP -> ThinLogBlock.UP;
			case DOWN -> ThinLogBlock.DOWN;
		};
	}

	private boolean trimBranches(int maxHeight, int currentHeight) {
		return currentHeight >= maxHeight * 0.2;
	}

	private void makeBranches(
			LevelSimulatedReader level,
			int maxHeight,
			BlockPos pos,
			List<ThinTrunkPlacer.FoliageCoords> foliageCoords
	) {
		for (ThinTrunkPlacer.FoliageCoords foliageCoords2 : foliageCoords) {
			int i = foliageCoords2.branchBase();
			BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
			if (!blockPos.equals(foliageCoords2.attachment.pos()) && this.trimBranches(maxHeight, i - pos.getY())) {
				currentBranch = new ArrayList<>();
				this.makeLimb(level, blockPos, foliageCoords2.attachment.pos(), true);

				if (!currentBranch.isEmpty() && currentBranch.get(0).equals(blockPos)) {
					currentBranch.remove(0);
				}

				if (!currentBranch.isEmpty()) {
					branches.add(currentBranch);
				}
				currentBranch = null;
			}
		}
	}

	private static float treeShape(int height, int currentY) {
		if (currentY < height * 0.3F) {
			return -1.0F;
		} else {
			float f = height / 2.0F;
			float g = f - currentY;
			float h = Mth.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	private boolean isTooCloseToExistingBranch(BlockPos trunk, BlockPos candidateStart) {
		int y = candidateStart.getY();
		Direction candidateDir = getBranchDirection(trunk, candidateStart);

		for (List<BlockPos> branch : branches) {
			if (branch.isEmpty())
				continue;

			BlockPos trueStart = null;
			for (BlockPos pos : branch) {
				if (pos.getX() != trunk.getX() || pos.getZ() != trunk.getZ()) {
					trueStart = pos;
					break;
				}
			}

			if (trueStart == null)
				continue;

			Direction dir = getBranchDirection(trunk, trueStart);
			if (dir != candidateDir)
				continue;

			int y2 = trueStart.getY();

			if (Math.abs(y - y2) <= 1)
				return true;

			if (candidateStart.getX() == trueStart.getX() && candidateStart.getZ() == trueStart.getZ())
				return true;
		}

		return false;
	}

	private Direction getBranchDirection(BlockPos trunk, BlockPos start) {
		int dx = start.getX() - trunk.getX();
		int dz = start.getZ() - trunk.getZ();

		if (Math.abs(dx) > Math.abs(dz)) {
			return dx > 0 ? Direction.EAST : Direction.WEST;
		} else {
			return dz > 0 ? Direction.SOUTH : Direction.NORTH;
		}
	}

	record FoliageCoords(FoliagePlacer.FoliageAttachment attachment, int branchBase) {

		FoliageCoords(BlockPos pos, int branchBase) {
			this(new FoliagePlacer.FoliageAttachment(pos, 0, false), branchBase);
		}
	}
}
