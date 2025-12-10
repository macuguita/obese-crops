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

package com.macuguita.obese_crops.common.resourcereloader;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class ObeseDropsMapResourceReloadListener implements SimpleSynchronousResourceReloadListener {

	public static final ResourceLocation ID = ObeseCrops.id("obese_drops_block_map_reload_listener");
	private static final ResourceLocation OBESE_DROPS_MAP_DIR = ObeseCrops.id("obese_drops_map");

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		ObeseCrops.OBESE_DROPS.clear();

		manager.listResourceStacks(OBESE_DROPS_MAP_DIR.getPath(), path ->
				path.getNamespace().equals(ObeseCrops.MOD_ID) && path.getPath().endsWith(".json")
		).forEach((identifier, resources) -> {

			for (var resource : resources) {
				try (InputStream stream = resource.open()) {

					JsonElement element = JsonParser.parseReader(
							new JsonReader(new InputStreamReader(stream))
					);

					ResourceLocation obeseBlockId = ResourceLocation.parse(
							identifier.getPath()
									.substring(identifier.getPath().indexOf("/") + 1, identifier.getPath().length() - 5)
									.replace("/", ":")
					);

					Block obeseBlock = BuiltInRegistries.BLOCK.get(obeseBlockId);
					if (obeseBlock == BuiltInRegistries.BLOCK.get(BuiltInRegistries.BLOCK.getDefaultKey()) && !obeseBlockId.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
						continue;
					}

					DataResult<ResourceLocation> result =
							ResourceLocation.CODEC.parse(JsonOps.INSTANCE, element);

					result.resultOrPartial(msg ->
							ObeseCrops.LOGGER.error("Failed to parse drop for '{}' in '{}': {}", obeseBlockId, identifier, msg)
					).ifPresent(itemId -> {
						Item item = BuiltInRegistries.ITEM.get(itemId);
						if (item == Items.AIR) {
							ObeseCrops.LOGGER.error("Unknown item '{}' in {}", itemId, identifier);
						} else {
							ObeseCrops.OBESE_DROPS.put(obeseBlock, item);
						}
					});

				} catch (Exception e) {
					ObeseCrops.LOGGER.error("Error reading {}: {}", identifier, e.getMessage());
				}
			}
		});

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}
}
