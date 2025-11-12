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
	public void generateBlockStateModels(@NotNull BlockModelGenerators blockStateModelGenerator) {
		blockStateModelGenerator.family(OCObjects.OBESE_BEETROOT.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POISONOUS_POTATO.get());
		blockStateModelGenerator.family(OCObjects.OBESE_POTATO.get());

		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_APPLE.get(), makeTopMap(OCObjects.OBESE_APPLE.get()));
		registerObeseTopModel(blockStateModelGenerator, OCObjects.OBESE_CARROT.get(), makeTopMap(OCObjects.OBESE_CARROT.get()));

		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_BEETROOT_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_CARROT_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
		blockStateModelGenerator.createCrossBlock(OCObjects.OBESE_POTATO_FOLIAGE.get(), BlockModelGenerators.TintState.NOT_TINTED);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
	}

	public final void registerObeseTopModel(@NotNull BlockModelGenerators blockStateModelGenerator, Block obeseCarrot, TextureMapping textureMap) {
		ResourceLocation id = ModelTemplates.CUBE.create(obeseCarrot, textureMap, blockStateModelGenerator.modelOutput);
		blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(obeseCarrot, Variant.variant().with(VariantProperties.MODEL, id)));
		blockStateModelGenerator.delegateItemModel(obeseCarrot, id);
	}

	private @NotNull TextureMapping makeTopMap(Block block) {
		return new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block)).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.EAST, TextureMapping.getBlockTexture(block)).put(TextureSlot.WEST, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top"));
	}
}
