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
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;

public class ObeseMapResourceReloadListener implements ResourceManagerReloadListener {

	public static final ResourceLocation ID = ObeseCrops.id("obese_block_map_reload_listener");
	private static final ResourceLocation OBESE_MAP_DIR = ObeseCrops.id("obese_map");

	@Override
	public void onResourceManagerReload(@NotNull ResourceManager manager) {
		ObeseCrops.OBESE_MAP.clear();

		manager.listResourceStacks(OBESE_MAP_DIR.getPath(), path ->
				path.getNamespace().equals(ObeseCrops.MOD_ID) && path.getPath().endsWith(".json")
		).forEach((identifier, resources) -> {
			for (var resource : resources) {
				try (InputStream stream = resource.open()) {
					JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream))).getAsJsonObject();

					ResourceLocation cropBlockId = ResourceLocation.parse(
							identifier.getPath()
									.substring(identifier.getPath().indexOf("/") + 1, identifier.getPath().length() - 5)
									.replace("/", ":")
					);
					Block cropBlock = BuiltInRegistries.BLOCK.getValue(cropBlockId);
					if (cropBlock == BuiltInRegistries.BLOCK.getValue(BuiltInRegistries.BLOCK.getDefaultKey()) && !cropBlockId.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
						continue;
					}

					DataResult<ObeseBlockData> data = ObeseBlockData.CODEC.parse(JsonOps.INSTANCE, object);
					data.resultOrPartial(error -> ObeseCrops.LOGGER.error("Failed to parse obese crop '{} in file '{}'", cropBlockId, error))
							.ifPresent(obeseBlockData -> ObeseCrops.OBESE_MAP.put(cropBlock, obeseBlockData));

				} catch (Exception exception) {
					ObeseCrops.LOGGER.error("{} in file '{}'", exception.getLocalizedMessage(), identifier);
				}
			}
		});
	}

	public record ObeseBlockData(BlockAndChance primary, List<BlockAndChance> secondaries) {

		private static final Codec<ObeseBlockData> COMPLEX_CODEC = RecordCodecBuilder.create(i -> i.group(
				BlockAndChance.CODEC.fieldOf("primary").forGetter(ObeseBlockData::primary),
				BlockAndChance.CODEC.listOf().fieldOf("secondaries").forGetter(ObeseBlockData::secondaries)
		).apply(i, ObeseBlockData::new));

		private static final Codec<ObeseBlockData> SIMPLE_CODEC = RecordCodecBuilder.create(i -> i.group(
				BlockAndChance.CODEC.fieldOf("primary").forGetter(ObeseBlockData::primary)
		).apply(i, blockAndChance -> new ObeseBlockData(blockAndChance, Collections.emptyList())));

		public static final Codec<ObeseBlockData> CODEC = Codec.either(COMPLEX_CODEC, SIMPLE_CODEC)
				.xmap(
						either -> either.map(data -> data, data -> data),
						data -> data.secondaries().isEmpty()
								? Either.right(data)
								: Either.left(data)
				);

		public record BlockAndChance(Block block, Block foliage, int chance) {

			public static final Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.flatXmap(
					id -> {
						Block block = BuiltInRegistries.BLOCK.getValue(id);
						if (!BuiltInRegistries.BLOCK.containsKey(id)) {
							return DataResult.error(() -> "Unknown block: " + id);
						}
						return DataResult.success(block);
					},
					block -> DataResult.success(BuiltInRegistries.BLOCK.getKey(block))
			);

			public static final Codec<BlockAndChance> CODEC = RecordCodecBuilder.create(i -> i.group(
					BLOCK_CODEC.fieldOf("block").forGetter(BlockAndChance::block),
					BLOCK_CODEC.fieldOf("foliage").forGetter(BlockAndChance::foliage),
					Codec.INT.fieldOf("chance").forGetter(BlockAndChance::chance)
			).apply(i, BlockAndChance::new));
		}
	}
}
