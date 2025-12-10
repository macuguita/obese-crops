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

package com.macuguita.obese_crops.common;

import java.util.Map;
import java.util.Optional;

import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.item.ScytheItem;
import com.macuguita.obese_crops.common.reg.OCComponents;
import com.macuguita.obese_crops.common.reg.OCCreativeTabs;
import com.macuguita.obese_crops.common.reg.OCEnchantmentComponents;
import com.macuguita.obese_crops.common.reg.OCEnchantmentTags;
import com.macuguita.obese_crops.common.reg.OCEnchantments;
import com.macuguita.obese_crops.common.reg.OCObjects;
import com.macuguita.obese_crops.common.reg.OCWorldgen;
import com.macuguita.obese_crops.common.resourcereloader.ObeseDropsMapResourceReloadListener;
import com.macuguita.obese_crops.common.resourcereloader.ObeseMapResourceReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.util.TriState;

public class ObeseCrops implements ModInitializer {

	public static final String MOD_ID = "obese_crops";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<Block, ObeseMapResourceReloadListener.ObeseBlockData> OBESE_MAP = new Object2ObjectOpenHashMap<>();
	public static final Map<Block, Item> OBESE_DROPS = new Object2ObjectOpenHashMap<>();

	public static Optional<ObeseMapResourceReloadListener.ObeseBlockData> getObeseBlockData(Block block) {
		return Optional.ofNullable(OBESE_MAP.get(block));
	}

	public static Optional<Item> getObeseDrops(Block block) {
		return Optional.ofNullable(OBESE_DROPS.get(block));
	}

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		initRegistries();
		initEvents();
		floweringOakLogSetup();
		ResourceManagerHelper.get(PackType.SERVER_DATA)
				.registerReloadListener(new ObeseMapResourceReloadListener());
		ResourceManagerHelper.get(PackType.SERVER_DATA)
				.registerReloadListener(new ObeseDropsMapResourceReloadListener());
		FuelRegistry.INSTANCE.add(OCObjects.WOODEN_SCYTHE.get(), 10);
	}

	private void initRegistries() {
		OCObjects.init();
		OCComponents.init();
		OCCreativeTabs.init();
		OCWorldgen.init();
		OCEnchantments.init();
		OCEnchantmentComponents.init();
	}

	private void initEvents() {
		//this doesn't show up in recipe viewers but whatever
		EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
			if (target.getItem() instanceof ScytheItem && enchantment.is(OCEnchantmentTags.SCYTHE_ALLOWED)) {
				return TriState.TRUE;
			}
			return TriState.DEFAULT;
		});
	}

	private void floweringOakLogSetup() {
		FlammableBlockRegistry.getDefaultInstance().add(OCObjects.FLOWERING_OAK_LEAVES.get(), 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(OCObjects.FLOWERING_OAK_LOG.get(), 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(OCObjects.STRIPPED_FLOWERING_OAK_LOG.get(), 5, 5);

		FuelRegistry.INSTANCE.add(OCObjects.FLOWERING_OAK_LOG.get(), 300);
		FuelRegistry.INSTANCE.add(OCObjects.STRIPPED_FLOWERING_OAK_LOG.get(), 300);

		ThinLogBlock.STRIPPED_THIN_LOGS.put(OCObjects.FLOWERING_OAK_LOG.get(), OCObjects.STRIPPED_FLOWERING_OAK_LOG.get());
	}
}
