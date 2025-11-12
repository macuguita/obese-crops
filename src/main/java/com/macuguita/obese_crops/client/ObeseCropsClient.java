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

package com.macuguita.obese_crops.client;

import com.macuguita.lib.platform.registry.GuitaRegistryEntry;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

public class ObeseCropsClient implements ClientModInitializer {
	public static ItemDisplayContext mode = ItemDisplayContext.NONE;

	@Override
	public void onInitializeClient() {
		for (GuitaRegistryEntry<Item> scythe : OCObjects.SCYTHE_ITEMS.getEntries()) {
			for (ItemDisplayContext displayMode : ItemDisplayContext.values()) {
				ItemProperties.register(scythe.get(), ObeseCrops.id(displayMode.getSerializedName()), (itemStack, clientLevel, livingEntity, i) -> mode == displayMode ? 1.0F : 0.0F);
			}
			//TODO: Custom renderer for diff skins
		}

		BlockRenderLayerMap.INSTANCE.putBlock(OCObjects.OBESE_BEETROOT_FOLIAGE.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OCObjects.OBESE_CARROT_FOLIAGE.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OCObjects.OBESE_POTATO_FOLIAGE.get(), RenderType.cutout());
	}
}
