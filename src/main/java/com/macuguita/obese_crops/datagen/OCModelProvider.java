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

package com.macuguita.obese_crops.datagen;

import java.util.Arrays;
import java.util.Optional;

import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.block.AppleBlock;
import com.macuguita.obese_crops.common.block.ObeseCropBlock;
import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

public class OCModelProvider extends FabricModelProvider {

	public OCModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
		createObeseModel(blockModelGenerators, OCObjects.OBESE_BEETROOT.get());
		createObeseModel(blockModelGenerators, OCObjects.OBESE_POISONOUS_POTATO.get());
		createObeseModel(blockModelGenerators, OCObjects.OBESE_POTATO.get());
		createObeseModel(blockModelGenerators, OCObjects.OBESE_APPLE.get(), true);
		createObeseModel(blockModelGenerators, OCObjects.OBESE_CARROT.get(), true);

		blockModelGenerators.createCrossBlock(OCObjects.OBESE_BEETROOT_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		blockModelGenerators.createCrossBlock(OCObjects.OBESE_CARROT_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		blockModelGenerators.createCrossBlock(OCObjects.OBESE_POTATO_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);

		createThinLogBlock(blockModelGenerators, OCObjects.FLOWERING_OAK_LOG.get());
		createThinLogBlock(blockModelGenerators, OCObjects.STRIPPED_FLOWERING_OAK_LOG.get());
		createFloweringLeavesBlock(blockModelGenerators, OCObjects.FLOWERING_OAK_LEAVES.get(), Blocks.OAK_LEAVES);
		blockModelGenerators.createPlant(OCObjects.FLOWERING_OAK_SAPLING.get(), OCObjects.POTTED_FLOWERING_OAK_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED);

		createFruitBlock(blockModelGenerators, OCObjects.APPLE.get());
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		itemModelGenerator.generateFlatItem(OCObjects.APPLE_SEED.get(), ModelTemplates.FLAT_ITEM);
	}

	private static final ModelTemplate CARVED_BLOCK_1_4 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/1_4_carved_block")),
			Optional.of("_1_4"),
			TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.INSIDE);

	private static final ModelTemplate CARVED_BLOCK_2_4 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/2_4_carved_block")),
			Optional.of("_2_4"),
			TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.INSIDE);

	private static final ModelTemplate CARVED_BLOCK_3_4 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/3_4_carved_block")),
			Optional.of("_3_4"),
			TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.INSIDE);

	private static final ModelTemplate OBESE_CROP = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/obese_crop")),
			Optional.empty(),
			TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.INSIDE);

	private static final ModelTemplate THIN_LOG_CORE = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/thin_log_core")),
			Optional.of("_core"),
			TextureSlot.SIDE);

	private static final ModelTemplate THIN_LOG_DOWN = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/thin_log_down")),
			Optional.of("_down"),
			TextureSlot.SIDE, TextureSlot.TOP);

	private static final ModelTemplate THIN_LOG_UP = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/thin_log_up")),
			Optional.of("_up"),
			TextureSlot.SIDE, TextureSlot.TOP);

	private static final ModelTemplate THIN_LOG_INVENTORY = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/thin_log_inventory")),
			Optional.of("_inventory"),
			TextureSlot.SIDE, TextureSlot.TOP);

	private static final ModelTemplate FLOWERING_LEAVES = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/flowering_leaves")),
			Optional.of(""),
			TextureSlot.ALL, TextureSlot.LAYER0);

	private static final ModelTemplate FRUIT_STAGE0 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/fruit_stage0")),
			Optional.of("_stage0"),
			TextureSlot.PLANT);

	private static final ModelTemplate FRUIT_STAGE1 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/fruit_stage1")),
			Optional.of("_stage1"),
			TextureSlot.PLANT);

	private static final ModelTemplate FRUIT_STAGE2 = new ModelTemplate(
			Optional.of(ObeseCrops.id("obese/fruit_stage2")),
			Optional.of("_stage2"),
			TextureSlot.PLANT);

	private void createObeseModel(BlockModelGenerators blockModelGenerators, Block obeseCrop) {
		createObeseModel(blockModelGenerators, obeseCrop, false);
	}

	private void createObeseModel(BlockModelGenerators blockModelGenerators, Block obeseCrop, boolean hasTop) {
		TextureMapping tm = makeObeseMap(obeseCrop, hasTop);
		ResourceLocation id_1_4 = CARVED_BLOCK_1_4.create(obeseCrop, tm, blockModelGenerators.modelOutput);
		ResourceLocation id_2_4 = CARVED_BLOCK_2_4.create(obeseCrop, tm, blockModelGenerators.modelOutput);
		ResourceLocation id_3_4 = CARVED_BLOCK_3_4.create(obeseCrop, tm, blockModelGenerators.modelOutput);
		ResourceLocation id_full = OBESE_CROP.create(obeseCrop, tm, blockModelGenerators.modelOutput);
		ResourceLocation[] models = {
				id_full,
				id_3_4,
				id_2_4,
				id_1_4
		};
		var map = PropertyDispatch.properties(ObeseCropBlock.CARVED, HorizontalDirectionalBlock.FACING);
		for (int i = 0; i < models.length; i++) {
			ResourceLocation model = models[i];
			for (Direction direction : Arrays.stream(Direction.values()).filter(direction -> direction.getAxis().isHorizontal()).toArray(Direction[]::new)) {
				map.select(i, direction, Variant.variant()
						.with(VariantProperties.MODEL, model)
						.with(
								VariantProperties.Y_ROT,
								VariantProperties.Rotation.valueOf("R" + (int) (direction.toYRot() + 180) % 360)
						));
			}
		}
		blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(obeseCrop).with(map));
		blockModelGenerators.delegateItemModel(obeseCrop, id_full);
	}

	private void createThinLogBlock(BlockModelGenerators blockModelGenerators, Block thinLog) {
		TextureMapping tmCore = makeThinLogCoreMap(thinLog);
		TextureMapping tmSide = makeThinLogSideMap(thinLog);
		ResourceLocation idCore = THIN_LOG_CORE.create(thinLog, tmCore, blockModelGenerators.modelOutput);
		ResourceLocation idUp = THIN_LOG_UP.create(thinLog, tmSide, blockModelGenerators.modelOutput);
		ResourceLocation idDown = THIN_LOG_DOWN.create(thinLog, tmSide, blockModelGenerators.modelOutput);

		MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(thinLog);
		runThinLogCoreMultipartGenerator(multiPartGenerator, idCore);

		for (Direction dir : Direction.values()) {
			BooleanProperty sideProp = ThinLogBlock.PROPERTY_BY_DIRECTION.get(dir);
			multiPartGenerator.with(Condition.condition().term(sideProp, true), rotateBeamModel(Variant.variant().with(VariantProperties.MODEL, getSidedModel(idUp, idDown, dir)), dir));
		}

		blockModelGenerators.blockStateOutput.accept(multiPartGenerator);

		ResourceLocation inventoryId = THIN_LOG_INVENTORY.create(thinLog, tmSide, blockModelGenerators.modelOutput);
		blockModelGenerators.delegateItemModel(thinLog, inventoryId);
	}

	private void createFloweringLeavesBlock(BlockModelGenerators blockModelGenerators, Block floweringLeaves, Block baseLeaves) {
		ResourceLocation id = FLOWERING_LEAVES.create(floweringLeaves, new TextureMapping()
				.put(TextureSlot.ALL, TextureMapping.getBlockTexture(baseLeaves))
				.put(TextureSlot.LAYER0, TextureMapping.getBlockTexture(floweringLeaves)), blockModelGenerators.modelOutput);
		blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(floweringLeaves, Variant.variant().with(VariantProperties.MODEL, id)));
		blockModelGenerators.delegateItemModel(floweringLeaves, id);
	}

	private void createFruitBlock(BlockModelGenerators blockModelGenerators, Block fruit) {
		ModelTemplate[] models = {
				FRUIT_STAGE0,
				FRUIT_STAGE1,
				FRUIT_STAGE2,
		};
		PropertyDispatch propertyDispatch = PropertyDispatch.property(AppleBlock.AGE)
				.generate((i) -> {
					ResourceLocation id = models[i].create(fruit, makeFruitMap(fruit, i), blockModelGenerators.modelOutput);
					return Variant.variant().with(VariantProperties.MODEL, id);
				});
		blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(fruit).with(propertyDispatch));
	}

	private TextureMapping makeObeseMap(Block block, boolean hasTop) {
		return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, hasTop ? "_top" : ""))
				.put(TextureSlot.INSIDE, TextureMapping.getBlockTexture(block, "_inside"));
	}

	private TextureMapping makeFruitMap(Block block, int stage) {
		return new TextureMapping().put(TextureSlot.PLANT, TextureMapping.getBlockTexture(block, "_stage" + stage));
	}

	private TextureMapping makeThinLogSideMap(Block block) {
		return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"));
	}

	private TextureMapping makeThinLogCoreMap(Block block) {
		return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block));
	}

	private ResourceLocation getSidedModel(ResourceLocation sideUp, ResourceLocation sideDown, Direction dir) {
		return switch (dir) {
			case UP, NORTH, EAST -> sideUp;
			case DOWN, SOUTH, WEST -> sideDown;
		};
	}

	private Variant rotateBeamModel(Variant variant, Direction dir) {
		switch (dir) {
			case EAST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
			case SOUTH -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
			case WEST -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
			case UP -> variant.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270);
			case DOWN -> variant.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90);
		}

		return variant;
	}

	private void runThinLogCoreMultipartGenerator(MultiPartGenerator multiPartGenerator, ResourceLocation idCore) {
		multiPartGenerator.with(Condition.and(
				Condition.or(
						Condition.condition().negatedTerm(ThinLogBlock.UP, true),
						Condition.condition().negatedTerm(ThinLogBlock.DOWN, true),
						Condition.condition().negatedTerm(ThinLogBlock.NORTH, true),
						Condition.condition().negatedTerm(ThinLogBlock.SOUTH, true),
						Condition.condition().negatedTerm(ThinLogBlock.EAST, true),
						Condition.condition().negatedTerm(ThinLogBlock.WEST, true)
				),
				Condition.or(
						Condition.condition().term(ThinLogBlock.UP, true),
						Condition.condition().term(ThinLogBlock.DOWN, true),
						Condition.and(
								Condition.condition().term(ThinLogBlock.UP, false),
								Condition.condition().term(ThinLogBlock.DOWN, false),
								Condition.condition().term(ThinLogBlock.NORTH, false),
								Condition.condition().term(ThinLogBlock.SOUTH, false),
								Condition.condition().term(ThinLogBlock.EAST, false),
								Condition.condition().term(ThinLogBlock.WEST, false)
						),
						Condition.and(
								Condition.condition().term(ThinLogBlock.UP, false),
								Condition.condition().term(ThinLogBlock.DOWN, false),
								Condition.or(
										Condition.condition().term(ThinLogBlock.NORTH, false),
										Condition.condition().term(ThinLogBlock.SOUTH, false)
								),
								Condition.or(
										Condition.condition().term(ThinLogBlock.WEST, false),
										Condition.condition().term(ThinLogBlock.EAST, false)
								)
						),
						Condition.and(
								Condition.condition().term(ThinLogBlock.UP, false),
								Condition.condition().term(ThinLogBlock.DOWN, false),
								Condition.condition().term(ThinLogBlock.NORTH, true),
								Condition.condition().term(ThinLogBlock.SOUTH, true),
								Condition.condition().term(ThinLogBlock.EAST, true),
								Condition.condition().term(ThinLogBlock.WEST, true)
						)
				)
		), Variant.variant().with(VariantProperties.MODEL, idCore));

		multiPartGenerator.with(Condition.and(
				Condition.condition().term(ThinLogBlock.UP, false),
				Condition.condition().term(ThinLogBlock.DOWN, false),
				Condition.and(
						Condition.and(
								Condition.condition().term(ThinLogBlock.NORTH, true),
								Condition.condition().term(ThinLogBlock.SOUTH, true)
						),
						Condition.or(
								Condition.condition().term(ThinLogBlock.EAST, false),
								Condition.condition().term(ThinLogBlock.WEST, false)
						)
				)
		), Variant.variant().with(VariantProperties.MODEL, idCore).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90));

		multiPartGenerator.with(Condition.and(
				Condition.condition().term(ThinLogBlock.UP, false),
				Condition.condition().term(ThinLogBlock.DOWN, false),
				Condition.and(
						Condition.and(
								Condition.condition().term(ThinLogBlock.EAST, true),
								Condition.condition().term(ThinLogBlock.WEST, true)
						),
						Condition.or(
								Condition.condition().term(ThinLogBlock.NORTH, false),
								Condition.condition().term(ThinLogBlock.SOUTH, false)
						)
				)
		), Variant.variant().with(VariantProperties.MODEL, idCore).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
	}
}
