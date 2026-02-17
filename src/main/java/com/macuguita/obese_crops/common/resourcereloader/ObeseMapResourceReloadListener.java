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
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.world.level.block.Blocks;

public class ObeseMapResourceReloadListener implements SimpleSynchronousResourceReloadListener {

	public static final ResourceLocation ID = ObeseCrops.id("obese_block_map_reload_listener");
	private static final ResourceLocation OBESE_MAP_DIR = ObeseCrops.id("obese_map");

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		ObeseCrops.SOURCE_TO_OBESE_DATA.clear();
		ObeseCrops.OBESE_TO_ENTRY.clear();

		manager.listResourceStacks(OBESE_MAP_DIR.getPath(), path ->
				path.getNamespace().equals(ObeseCrops.MOD_ID) && path.getPath().endsWith(".json")
		).forEach((identifier, resources) -> {
			for (var resource : resources) {
				try (InputStream stream = resource.open()) {
					JsonObject object = JsonParser.parseReader(
							new JsonReader(new InputStreamReader(stream))
					).getAsJsonObject();

					ResourceLocation cropBlockId = ResourceLocation.parse(
							identifier.getPath()
									.substring(identifier.getPath().indexOf("/") + 1, identifier.getPath().length() - 5)
									.replace("/", ":")
					);

					Source source;
					if (BuiltInRegistries.BLOCK.containsKey(cropBlockId)) {
						source = new Source.BlockSource(BuiltInRegistries.BLOCK.get(cropBlockId));
					} else if (BuiltInRegistries.ITEM.containsKey(cropBlockId)) {
						source = new Source.ItemSource(BuiltInRegistries.ITEM.get(cropBlockId));
					} else {
						ObeseCrops.LOGGER.error(
								"Unknown source '{}' in obese map file '{}'",
								cropBlockId, identifier
						);
						continue;
					}

					ObeseBlockData.CODEC
							.parse(JsonOps.INSTANCE, object)
							.resultOrPartial(error ->
									ObeseCrops.LOGGER.error(
											"Failed to parse obese crop '{}' in file '{}'",
											cropBlockId, error
									)
							)
							.ifPresent(data -> {
								ObeseCrops.SOURCE_TO_OBESE_DATA.put(source, data);

								registerEntry(data.primary());

								for (var secondary : data.secondaries()) {
									registerEntry(secondary);
								}
							});

				} catch (Exception exception) {
					ObeseCrops.LOGGER.error("{} in file '{}'",
							exception.getLocalizedMessage(), identifier);
				}
			}
		});
	}

	private static void registerEntry(ObeseBlockData.Entry entry) {
		Block obese = entry.obese();

		ObeseBlockData.Entry previous = ObeseCrops.OBESE_TO_ENTRY.put(obese, entry);

		if (previous != null) {
			ObeseCrops.LOGGER.warn(
					"Duplicate obese block '{}' detected; overriding previous entry",
					BuiltInRegistries.BLOCK.getKey(obese)
			);
		}
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public record ObeseBlockData(Entry primary, List<Entry> secondaries) {

		private static final Codec<ObeseBlockData> CODEC = RecordCodecBuilder.create(i -> i.group(
				Entry.CODEC.fieldOf("primary").forGetter(ObeseBlockData::primary),
				Entry.CODEC.listOf().optionalFieldOf("secondaries", List.of()).forGetter(ObeseBlockData::secondaries)
		).apply(i, ObeseBlockData::new));

		public record Entry(Block obese, Block foliage, Item drop, int chance) {

			public static final Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(
					BuiltInRegistries.BLOCK::get,
					BuiltInRegistries.BLOCK::getKey
			);

			public static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC.xmap(
					BuiltInRegistries.ITEM::get,
					BuiltInRegistries.ITEM::getKey
			);

			public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
					BLOCK_CODEC.fieldOf("obese").forGetter(Entry::obese),
					BLOCK_CODEC.optionalFieldOf("foliage", Blocks.AIR).forGetter(Entry::foliage),
					ITEM_CODEC.fieldOf("drop").forGetter(Entry::drop),
					Codec.INT.optionalFieldOf("chance", 5).forGetter(Entry::chance)
			).apply(i, Entry::new));
		}
	}
}
