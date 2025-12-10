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

package com.macuguita.obese_crops.common.reg;

import com.macuguita.obese_crops.common.ObeseCrops;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface OCItemTags {

	TagKey<Item> SCYTHES = createTag("scythes");
	TagKey<Item> SCYTHE_ENCHANTABLE = createTag("scythe_enchantable");
	TagKey<Item> THIN_LOGS = createTag("thin_logs");
	TagKey<Item> FLOWERING_OAK_LOGS = createTag("flowering_oak_logs");
	TagKey<Item> FLOWERING_LEAVES = createTag("flowering_leaves");
	TagKey<Item> SHARP_TOOLS = createTag("sharp_tools");

	private static TagKey<Item> createTag(String name) {
		return TagKey.create(Registries.ITEM, ObeseCrops.id(name));
	}
}
