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

import java.util.function.Supplier;

import com.macuguita.lib.platform.registry.GuitaRegistries;
import com.macuguita.lib.platform.registry.GuitaRegistry;
import com.macuguita.lib.platform.registry.GuitaRegistryEntry;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.block.ObeseCropBlock;
import com.macuguita.obese_crops.common.block.ObeseCropFoliageBlock;
import com.macuguita.obese_crops.common.item.ScytheItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface OCObjects {

	GuitaRegistry<Block> BLOCKS = GuitaRegistries.create(BuiltInRegistries.BLOCK, ObeseCrops.MOD_ID);
	GuitaRegistry<Item> ITEMS = GuitaRegistries.create(BuiltInRegistries.ITEM, ObeseCrops.MOD_ID);

	GuitaRegistry<Item> SCYTHE_ITEMS = GuitaRegistries.create(ITEMS);

	GuitaRegistryEntry<ScytheItem> WOODEN_SCYTHE = SCYTHE_ITEMS.register("wooden_scythe", () -> new ScytheItem(Tiers.WOOD, 5, -2.8f, 0.1f, new Item.Properties()));
	GuitaRegistryEntry<ScytheItem> STONE_SCYTHE = SCYTHE_ITEMS.register("stone_scythe", () -> new ScytheItem(Tiers.STONE, 5, -2.8f, 0.15f, new Item.Properties()));
	GuitaRegistryEntry<ScytheItem> IRON_SCYTHE = SCYTHE_ITEMS.register("iron_scythe", () -> new ScytheItem(Tiers.IRON, 5, -2.75f, 0.22f, new Item.Properties()));
	GuitaRegistryEntry<ScytheItem> GOLDEN_SCYTHE = SCYTHE_ITEMS.register("golden_scythe", () -> new ScytheItem(Tiers.GOLD, 5, -2.7f, 0.5f, new Item.Properties()));
	GuitaRegistryEntry<ScytheItem> DIAMOND_SCYTHE = SCYTHE_ITEMS.register("diamond_scythe", () -> new ScytheItem(Tiers.DIAMOND, 5, -2.7f, 0.25f, new Item.Properties()));
	GuitaRegistryEntry<ScytheItem> NETHERITE_SCYTHE = SCYTHE_ITEMS.register("netherite_scythe", () -> new ScytheItem(Tiers.NETHERITE, 5, -2.7f, 0.3f, new Item.Properties()));

	GuitaRegistryEntry<ObeseCropBlock> OBESE_APPLE = registerWithItem("obese_apple", () -> new ObeseCropBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropBlock> OBESE_BEETROOT = registerWithItem("obese_beetroot", () -> new ObeseCropBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropBlock> OBESE_CARROT = registerWithItem("obese_carrot", () -> new ObeseCropBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropBlock> OBESE_POISONOUS_POTATO = registerWithItem("obese_poisonous_potato", () -> new ObeseCropBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropBlock> OBESE_POTATO = registerWithItem("obese_potato", () -> new ObeseCropBlock(BlockBehaviour.Properties.of()));

	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_BEETROOT_FOLIAGE = BLOCKS.register("obese_beetroot_foliage", () -> new ObeseCropFoliageBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_CARROT_FOLIAGE = BLOCKS.register("obese_carrot_foliage", () -> new ObeseCropFoliageBlock(BlockBehaviour.Properties.of()));
	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_POTATO_FOLIAGE = BLOCKS.register("obese_potato_foliage", () -> new ObeseCropFoliageBlock(BlockBehaviour.Properties.of()));

	private static <T extends Block> GuitaRegistryEntry<T> registerWithItem(String name, Supplier<T> block) {
		GuitaRegistryEntry<T> toReturn = BLOCKS.register(name, block);
		ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
		return toReturn;
	}

	static void init() {
		ITEMS.init();
		BLOCKS.init();
	}
}
