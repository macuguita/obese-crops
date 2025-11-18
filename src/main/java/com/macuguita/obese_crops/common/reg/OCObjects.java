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

import java.util.function.Function;

import com.macuguita.lib.platform.registry.GuitaRegistries;
import com.macuguita.lib.platform.registry.GuitaRegistry;
import com.macuguita.lib.platform.registry.GuitaRegistryEntry;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.block.ObeseCropBlock;
import com.macuguita.obese_crops.common.block.ObeseCropFoliageBlock;
import com.macuguita.obese_crops.common.item.ScytheItem;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface OCObjects {

	GuitaRegistry<Block> BLOCKS = GuitaRegistries.create(BuiltInRegistries.BLOCK, ObeseCrops.MOD_ID);
	GuitaRegistry<Item> ITEMS = GuitaRegistries.create(BuiltInRegistries.ITEM, ObeseCrops.MOD_ID);

	GuitaRegistry<Item> SCYTHE_ITEMS = GuitaRegistries.create(ITEMS);

	GuitaRegistryEntry<ScytheItem> WOODEN_SCYTHE = SCYTHE_ITEMS.register("wooden_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.WOOD, 5, -2.8f, 0.1f).setId(keyOfItem("wooden_scythe"))));
	GuitaRegistryEntry<ScytheItem> STONE_SCYTHE = SCYTHE_ITEMS.register("stone_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.STONE, 5, -2.8f, 0.15f).setId(keyOfItem("stone_scythe"))));
	GuitaRegistryEntry<ScytheItem> IRON_SCYTHE = SCYTHE_ITEMS.register("iron_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.IRON, 5, -2.75f, 0.22f).setId(keyOfItem("iron_scythe"))));
	GuitaRegistryEntry<ScytheItem> GOLDEN_SCYTHE = SCYTHE_ITEMS.register("golden_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.GOLD, 5, -2.7f, 0.5f).setId(keyOfItem("golden_scythe"))));
	GuitaRegistryEntry<ScytheItem> DIAMOND_SCYTHE = SCYTHE_ITEMS.register("diamond_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.DIAMOND, 5, -2.7f, 0.25f).setId(keyOfItem("diamond_scythe"))));
	GuitaRegistryEntry<ScytheItem> NETHERITE_SCYTHE = SCYTHE_ITEMS.register("netherite_scythe", () -> new ScytheItem(scythe(new Item.Properties(), ToolMaterial.NETHERITE, 5, -2.7f, 0.3f).setId(keyOfItem("netherite_scythe"))));

	GuitaRegistryEntry<ObeseCropBlock> OBESE_APPLE = registerBlockWithItem("obese_apple", ObeseCropBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropBlock> OBESE_BEETROOT = registerBlockWithItem("obese_beetroot", ObeseCropBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropBlock> OBESE_CARROT = registerBlockWithItem("obese_carrot", ObeseCropBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropBlock> OBESE_POISONOUS_POTATO = registerBlockWithItem("obese_poisonous_potato", ObeseCropBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropBlock> OBESE_POTATO = registerBlockWithItem("obese_potato", ObeseCropBlock::new, BlockBehaviour.Properties.of());

	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_BEETROOT_FOLIAGE = registerBlock("obese_beetroot_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_CARROT_FOLIAGE = registerBlock("obese_carrot_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());
	GuitaRegistryEntry<ObeseCropFoliageBlock> OBESE_POTATO_FOLIAGE = registerBlock("obese_potato_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());

	static <T extends Block> GuitaRegistryEntry<T> registerBlockWithItem(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
		GuitaRegistryEntry<T> toReturn = registerBlock(name, blockFactory, settings);
		registerItem(name, (properties) -> new BlockItem(toReturn.get(), properties), new Item.Properties().setId(keyOfItem(name)).useBlockDescriptionPrefix());
		return toReturn;
	}

	static <T extends Block> GuitaRegistryEntry<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
		return BLOCKS.register(name, () -> blockFactory.apply(settings.setId(keyOfBlock(name))));
	}

	static <T extends Item> GuitaRegistryEntry<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		return ITEMS.register(name, () -> itemFactory.apply(settings.setId(keyOfItem(name))));
	}

	static Item.@NotNull Properties scythe(Item.Properties properties, @NotNull ToolMaterial material, float attackDamage, float attackSpeed, float pullingSpeed) {
		return material.applySwordProperties(properties, attackDamage, attackSpeed)
				.tool(material, OCBlockTags.SCYTHE_MINABLE, attackDamage, attackSpeed, 0.0F)
				.component(OCComponents.PULLING_SPEED.get(), pullingSpeed);
	}

	private static @NotNull ResourceKey<Block> keyOfBlock(String name) {
		return ResourceKey.create(Registries.BLOCK, ObeseCrops.id(name));
	}

	private static @NotNull ResourceKey<Item> keyOfItem(String name) {
		return ResourceKey.create(Registries.ITEM, ObeseCrops.id(name));
	}

	static void init() {
		ITEMS.init();
		BLOCKS.init();
	}
}
