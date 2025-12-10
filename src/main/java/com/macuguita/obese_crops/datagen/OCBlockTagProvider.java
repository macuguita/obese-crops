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

import java.util.concurrent.CompletableFuture;

import com.macuguita.obese_crops.common.reg.OCBlockTags;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class OCBlockTagProvider extends FabricTagProvider.BlockTagProvider {

	public OCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		getOrCreateRawBuilder(OCBlockTags.OBESE_CROP)
				.addElement(OCObjects.OBESE_BEETROOT.getId())
				.addElement(OCObjects.OBESE_CARROT.getId())
				.addElement(OCObjects.OBESE_POISONOUS_POTATO.getId())
				.addElement(OCObjects.OBESE_POTATO.getId());
		getOrCreateRawBuilder(OCBlockTags.DOUBLE_OBESE_CROP)
				.addElement(OCObjects.OBESE_CARROT.getId());

		getOrCreateRawBuilder(OCBlockTags.SCYTHE_MINABLE)
				.addElement(OCObjects.OBESE_APPLE.getId())
				.addElement(OCObjects.OBESE_BEETROOT.getId())
				.addElement(OCObjects.OBESE_CARROT.getId())
				.addElement(OCObjects.OBESE_POISONOUS_POTATO.getId())
				.addElement(OCObjects.OBESE_POTATO.getId())
				.addOptionalTag(BlockTags.MINEABLE_WITH_HOE.location());
		getOrCreateRawBuilder(OCBlockTags.SCYTHE_EFFICIENT)
				.addElement(OCObjects.OBESE_APPLE.getId())
				.addElement(OCObjects.OBESE_BEETROOT.getId())
				.addElement(OCObjects.OBESE_CARROT.getId())
				.addElement(OCObjects.OBESE_POISONOUS_POTATO.getId())
				.addElement(OCObjects.OBESE_POTATO.getId());
		getOrCreateRawBuilder(OCBlockTags.SCYTHE_WEEDS)
				.addOptionalTag(BlockTags.FLOWERS.location())
				.addElement(getRes(Blocks.SHORT_GRASS))
				.addElement(getRes(Blocks.FERN))
				.addElement(getRes(Blocks.DEAD_BUSH))
				.addElement(getRes(Blocks.VINE))
				.addElement(getRes(Blocks.GLOW_LICHEN))
				.addElement(getRes(Blocks.TALL_GRASS))
				.addElement(getRes(Blocks.LARGE_FERN))
				.addElement(getRes(Blocks.HANGING_ROOTS));

		getOrCreateRawBuilder(OCBlockTags.FLOWERING_LEAVES)
				.addElement(OCObjects.FLOWERING_OAK_LEAVES.getId());
		getOrCreateRawBuilder(OCBlockTags.FLOWERING_OAK_LOGS)
				.addElement(OCObjects.FLOWERING_OAK_LOG.getId())
				.addElement(OCObjects.STRIPPED_FLOWERING_OAK_LOG.getId());
		getOrCreateRawBuilder(OCBlockTags.THIN_LOGS)
				.addOptionalTag(OCBlockTags.FLOWERING_OAK_LOGS.location());
		getOrCreateRawBuilder(BlockTags.LOGS)
				.addOptionalTag(OCBlockTags.THIN_LOGS.location());
		getOrCreateRawBuilder(BlockTags.LEAVES)
				.addOptionalTag(OCBlockTags.FLOWERING_LEAVES.location());
		getOrCreateRawBuilder(BlockTags.SAPLINGS)
				.addElement(OCObjects.FLOWERING_OAK_SAPLING.getId());
		getOrCreateRawBuilder(BlockTags.FLOWER_POTS)
				.addElement(OCObjects.POTTED_FLOWERING_OAK_SAPLING.getId());
	}

	private ResourceLocation getRes(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}
}
