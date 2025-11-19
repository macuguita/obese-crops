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

import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.Optional;

public class OCModelProvider extends FabricModelProvider {

	public OCModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators blockStateModelGenerator) {
		blockStateModelGenerator.family(OCObjects.OBESE_BEETROOT.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POISONOUS_POTATO.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POTATO.get());

		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_APPLE.get(), makeTopMap(OCObjects.OBESE_APPLE.get()));
		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_CARROT.get(), makeTopMap(OCObjects.OBESE_CARROT.get()));

		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_BEETROOT_FOLIAGE.get(), BlockModelGenerators.PlantType.NOT_TINTED);
		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_CARROT_FOLIAGE.get(), BlockModelGenerators.PlantType.NOT_TINTED);
		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_POTATO_FOLIAGE.get(), BlockModelGenerators.PlantType.NOT_TINTED);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		OCObjects.SCYTHE_ITEMS.stream().forEach((entry) -> this.generateScythe(itemModelGenerator, entry.get()));
	}

	private static final ModelTemplate SCYTHE_IN_HAND = new ModelTemplate(
			Optional.of(ObeseCrops.id("item/scythe_in_hand")),
			Optional.of("_in_hand"),
			TextureSlot.LAYER0);

	public final void registerObeseTopModel(@NotNull BlockModelGenerators blockModelGenerators, Block obeseCarrot, TextureMapping tm) {
		ResourceLocation id = ModelTemplates.CUBE.create(obeseCarrot, tm, blockModelGenerators.modelOutput);
		MultiVariant multiVariant = BlockModelGenerators.plainVariant(id);
		blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(obeseCarrot, multiVariant));
		blockModelGenerators.registerSimpleItemModel(obeseCarrot, id);
	}

	private @NotNull TextureMapping makeTopMap(Block block) {
		return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block)).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.EAST, TextureMapping.getBlockTexture(block)).put(TextureSlot.WEST, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top"));
	}

	private void generateScythe(ItemModelGenerators itemModelGenerators, Item scythe) {
		ResourceLocation id = SCYTHE_IN_HAND.create(scythe, new TextureMapping().put(TextureSlot.LAYER0, TextureMapping.getItemTexture(scythe, "_in_hand")), itemModelGenerators.modelOutput);
		ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(scythe, ModelTemplates.FLAT_ITEM));
		ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(id);
		itemModelGenerators.itemModelOutput.accept(scythe, ItemModelGenerators.createFlatModelDispatch(unbaked, unbaked2));
	}
}
