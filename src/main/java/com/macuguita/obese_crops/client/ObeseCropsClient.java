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

import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.ItemDisplayContext;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;

public class ObeseCropsClient implements ClientModInitializer {
	public static ItemDisplayContext mode = ItemDisplayContext.NONE;

	@Override
	public void onInitializeClient() {

		BlockRenderLayerMap.putBlock(OCObjects.OBESE_BEETROOT_FOLIAGE.get(), ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(OCObjects.OBESE_CARROT_FOLIAGE.get(), ChunkSectionLayer.CUTOUT);
		BlockRenderLayerMap.putBlock(OCObjects.OBESE_POTATO_FOLIAGE.get(), ChunkSectionLayer.CUTOUT);
	}
}
