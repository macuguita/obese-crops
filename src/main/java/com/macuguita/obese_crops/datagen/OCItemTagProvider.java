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

import com.macuguita.obese_crops.common.reg.OCItemTags;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class OCItemTagProvider extends FabricTagProvider.ItemTagProvider {

	public OCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		OCObjects.SCYTHE_ITEMS.stream().forEach(item -> getOrCreateRawBuilder(OCItemTags.SCYTHES).addElement(item.getId()));
		getOrCreateRawBuilder(OCItemTags.SCYTHE_ENCHANTABLE)
				.addTag(OCItemTags.SCYTHES.location());
		getOrCreateRawBuilder(ItemTags.WEAPON_ENCHANTABLE)
				.addOptionalTag(OCItemTags.SCYTHES.location());
		getOrCreateRawBuilder(ItemTags.MINING_ENCHANTABLE)
				.addOptionalTag(OCItemTags.SCYTHES.location());
		getOrCreateRawBuilder(ItemTags.MINING_LOOT_ENCHANTABLE)
				.addOptionalTag(OCItemTags.SCYTHES.location());
		getOrCreateRawBuilder(ItemTags.DURABILITY_ENCHANTABLE)
				.addOptionalTag(OCItemTags.SCYTHES.location());
		getOrCreateRawBuilder(ItemTags.VANISHING_ENCHANTABLE)
				.addOptionalTag(OCItemTags.SCYTHES.location());

		getOrCreateRawBuilder(OCItemTags.FLOWERING_LEAVES)
				.addElement(OCObjects.FLOWERING_OAK_LEAVES.getId());
		getOrCreateRawBuilder(OCItemTags.FLOWERING_OAK_LOGS)
				.addElement(OCObjects.FLOWERING_OAK_LOG.getId())
				.addElement(OCObjects.STRIPPED_FLOWERING_OAK_LOG.getId());
		getOrCreateRawBuilder(OCItemTags.THIN_LOGS)
				.addOptionalTag(OCItemTags.FLOWERING_OAK_LOGS.location());
		getOrCreateRawBuilder(ItemTags.LOGS)
				.addOptionalTag(OCItemTags.THIN_LOGS.location());
		getOrCreateRawBuilder(ItemTags.LEAVES)
				.addOptionalTag(OCItemTags.FLOWERING_LEAVES.location());
		getOrCreateRawBuilder(ItemTags.SAPLINGS)
				.addElement(OCObjects.FLOWERING_OAK_SAPLING.getId());
	}
}
