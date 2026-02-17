package com.macuguita.obese_crops.common.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Lists;
import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.reg.OCWorldgen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;

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

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public class ThinTrunkPlacer extends TrunkPlacer {
	public static final MapCodec<ThinTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> trunkPlacerParts(instance).and(
					instance.group(
							Codec.doubleRange(0.0, 1.0).fieldOf("trunk_height_scale").forGetter(tp -> tp.trunkHeightScale),
							Codec.doubleRange(0.0, 4.0).fieldOf("cluster_density").forGetter(tp -> tp.clusterDensity),
							Codec.doubleRange(0.0, 2.0).fieldOf("branch_slope").forGetter(tp -> tp.branchSlope),
							Codec.doubleRange(0.0, 1.0).fieldOf("branch_length").forGetter(tp -> tp.branchLength)
					)
			).apply(instance, ThinTrunkPlacer::new)
	);

	private final double trunkHeightScale;
	private final double clusterDensity;
	private final double branchSlope;
	private final double branchLength;

	private final Set<BlockPos> allLogPositions = new HashSet<>();
	private final Set<BlockPos> mainTrunkPositions = new HashSet<>();
	private final List<List<BlockPos>> branches = new ArrayList<>();
	private @Nullable List<BlockPos> currentBranch = null;

	public ThinTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
						   double trunkHeightScale, double clusterDensity,
						   double branchSlope, double branchLength) {
		super(baseHeight, heightRandA, heightRandB);
		this.trunkHeightScale = trunkHeightScale;
		this.clusterDensity = clusterDensity;
		this.branchSlope = branchSlope;
		this.branchLength = branchLength;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return OCWorldgen.THIN_TRUNK_PLACER.get();
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(
			LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random,
			int freeTreeHeight,
			BlockPos rootPos,
			TreeConfiguration config
	) {
		allLogPositions.clear();
		mainTrunkPositions.clear();
		branches.clear();
		currentBranch = null;

		int adjustedHeight = freeTreeHeight + 2;
		int trunkTopYOffset = Mth.floor(adjustedHeight * trunkHeightScale);
		setDirtAt(level, blockSetter, random, rootPos.below(), config);

		int clusterCount = Math.min(1, Mth.floor(clusterDensity + Math.pow(1.0 * adjustedHeight / 13.0, 2.0)));
		int trunkMaxY = rootPos.getY() + trunkTopYOffset;
		int foliageStartY = adjustedHeight - 5;

		List<ThinTrunkPlacer.FoliageCoords> foliageList = Lists.newArrayList();
		foliageList.add(new ThinTrunkPlacer.FoliageCoords(rootPos.above(foliageStartY), trunkMaxY));

		for (int yStep = foliageStartY; yStep >= 0; yStep--) {
			float shapeRadius = treeShape(adjustedHeight, yStep);
			if (shapeRadius >= 0.0F) {
				for (int i = 0; i < clusterCount; i++) {
					double length = shapeRadius * (random.nextFloat() + branchLength);
					double angle = random.nextFloat() * 2.0F * Math.PI;

					double offsetX = length * Math.sin(angle) + 0.5;
					double offsetZ = length * Math.cos(angle) + 0.5;

					BlockPos branchStart = rootPos.offset(Mth.floor(offsetX), yStep - 1, Mth.floor(offsetZ));

					if ((branchStart.getX() == rootPos.getX() && branchStart.getZ() == rootPos.getZ()) || isTooCloseToExistingBranch(rootPos, branchStart)) {
						continue;
					}

					BlockPos branchEnd = branchStart.above(5);
					currentBranch = new ArrayList<>();

					if (makeLimb(level, branchStart, branchEnd, false)) {
						int deltaX = rootPos.getX() - branchStart.getX();
						int deltaZ = rootPos.getZ() - branchStart.getZ();
						double calculatedY = branchStart.getY() - Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * branchSlope;
						int connectionY = calculatedY > trunkMaxY ? trunkMaxY : (int) calculatedY;

						BlockPos trunkConnectionPoint = new BlockPos(rootPos.getX(), connectionY, rootPos.getZ());
						if (makeLimb(level, trunkConnectionPoint, branchStart, false)) {
							foliageList.add(new ThinTrunkPlacer.FoliageCoords(branchStart, trunkConnectionPoint.getY()));
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
		makeLimb(level, rootPos, rootPos.above(trunkTopYOffset), true);
		mainTrunkPositions.addAll(allLogPositions);

		makeBranches(level, adjustedHeight, rootPos, foliageList);
		placeAllLogsWithConnections(level, blockSetter, random, config);

		List<FoliagePlacer.FoliageAttachment> attachments = Lists.newArrayList();
		for (ThinTrunkPlacer.FoliageCoords coords : foliageList) {
			if (isTallEnoughForFoliage(adjustedHeight, coords.branchBase() - rootPos.getY())) {
				attachments.add(coords.attachment);
			}
		}

		return attachments;
	}

	private boolean makeLimb(LevelSimulatedReader level, BlockPos start, BlockPos end, boolean modifyWorld) {
		BlockPos current = start;
		if (modifyWorld) {
			allLogPositions.add(current.immutable());
			if (currentBranch != null) currentBranch.add(current.immutable());
		} else if (!isFree(level, current)) {
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
			} else if (!isFree(level, current)) {
				return false;
			}
		}

		return true;
	}

	private void placeAllLogsWithConnections(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, TreeConfiguration config) {
		for (BlockPos logPos : allLogPositions) {
			placeLog(level, blockSetter, random, logPos, config, state -> setDirectionalConnections(state, logPos));
		}
	}

	private BlockState setDirectionalConnections(BlockState state, BlockPos currentPos) {
		state = state.setValue(ThinLogBlock.NORTH, false)
				.setValue(ThinLogBlock.EAST, false)
				.setValue(ThinLogBlock.SOUTH, false)
				.setValue(ThinLogBlock.WEST, false)
				.setValue(ThinLogBlock.UP, false)
				.setValue(ThinLogBlock.DOWN, false);

		if (mainTrunkPositions.contains(currentPos)) {
			state = state.setValue(ThinLogBlock.DOWN, true);
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
			if (!allLogPositions.contains(neighbor)) continue;

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

	private boolean isTallEnoughForFoliage(int maxHeight, int currentHeight) {
		return currentHeight >= maxHeight * 0.2;
	}

	private void makeBranches(LevelSimulatedReader level, int maxHeight, BlockPos rootPos, List<ThinTrunkPlacer.FoliageCoords> foliageCoords) {
		for (ThinTrunkPlacer.FoliageCoords coords : foliageCoords) {
			int branchY = coords.branchBase();
			BlockPos branchStartOnTrunk = new BlockPos(rootPos.getX(), branchY, rootPos.getZ());

			if (!branchStartOnTrunk.equals(coords.attachment.pos()) && isTallEnoughForFoliage(maxHeight, branchY - rootPos.getY())) {
				currentBranch = new ArrayList<>();
				makeLimb(level, branchStartOnTrunk, coords.attachment.pos(), true);

				if (!currentBranch.isEmpty() && currentBranch.get(0).equals(branchStartOnTrunk)) {
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
			float halfHeight = height / 2.0F;
			float distanceToCenter = halfHeight - currentY;
			float radius = Mth.sqrt(halfHeight * halfHeight - distanceToCenter * distanceToCenter);

			if (distanceToCenter == 0.0F) {
				radius = halfHeight;
			} else if (Math.abs(distanceToCenter) >= halfHeight) {
				return 0.0F;
			}

			return radius * 0.5F;
		}
	}

	private boolean isTooCloseToExistingBranch(BlockPos trunkPos, BlockPos candidateStart) {
		int candidateY = candidateStart.getY();
		Direction candidateDir = getBranchDirection(trunkPos, candidateStart);

		for (List<BlockPos> branch : branches) {
			if (branch.isEmpty()) continue;

			BlockPos trueStart = null;
			for (BlockPos pos : branch) {
				if (pos.getX() != trunkPos.getX() || pos.getZ() != trunkPos.getZ()) {
					trueStart = pos;
					break;
				}
			}

			if (trueStart == null) continue;

			Direction existingDir = getBranchDirection(trunkPos, trueStart);
			if (existingDir != candidateDir) continue;

			if (Math.abs(candidateY - trueStart.getY()) <= 1) return true;
			if (candidateStart.getX() == trueStart.getX() && candidateStart.getZ() == trueStart.getZ()) return true;
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
