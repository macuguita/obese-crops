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

import com.macuguita.obese_crops.common.reg.OCObjects;
import org.jetbrains.annotations.NotNull;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

public class OCModelProvider extends FabricModelProvider {

	public OCModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
		blockStateModelGenerator.family(OCObjects.OBESE_BEETROOT.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POISONOUS_POTATO.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POTATO.get());
	public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_APPLE.get(), makeTopMap(OCObjects.OBESE_APPLE.get()));
		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_CARROT.get(), makeTopMap(OCObjects.OBESE_CARROT.get()));

		createThinLogBlock(blockModelGenerators, OCObjects.FLOWERING_OAK_LOG.get());
		createThinLogBlock(blockModelGenerators, OCObjects.STRIPPED_FLOWERING_OAK_LOG.get());
		createFloweringLeavesBlock(blockModelGenerators, OCObjects.FLOWERING_OAK_LEAVES.get(), Blocks.OAK_LEAVES);
		blockModelGenerators.createPlant(OCObjects.FLOWERING_OAK_SAPLING.get(), OCObjects.POTTED_FLOWERING_OAK_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED);

		createFruitBlock(blockModelGenerators, OCObjects.APPLE.get());
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
	}

	public final void registerObeseTopModel(@NotNull BlockModelGenerators blockStateModelGenerator, Block obeseCarrot, TextureMapping textureMap) {
		ResourceLocation id = ModelTemplates.CUBE.create(obeseCarrot, textureMap, blockStateModelGenerator.modelOutput);
		blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(obeseCarrot, Variant.variant().with(VariantProperties.MODEL, id)));
		blockStateModelGenerator.delegateItemModel(obeseCarrot, id);
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
