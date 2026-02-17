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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.macuguita.lib.platform.registry.GuitaRegistries;
import com.macuguita.lib.platform.registry.GuitaRegistry;
import com.macuguita.lib.platform.registry.GuitaRegistryEntry;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.block.AppleBlock;
import com.macuguita.obese_crops.common.block.FloweringOakSaplingBlock;
import com.macuguita.obese_crops.common.block.ObeseCropBlock;
import com.macuguita.obese_crops.common.block.ObeseCropFoliageBlock;
import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.item.ScytheItem;

import net.minecraft.client.gui.Gui;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class OCObjects {

	public static final GuitaRegistry<Block> BLOCKS = GuitaRegistries.create(BuiltInRegistries.BLOCK, ObeseCrops.MOD_ID);
	public static final GuitaRegistry<Item> ITEMS = GuitaRegistries.create(BuiltInRegistries.ITEM, ObeseCrops.MOD_ID);

	public static final GuitaRegistry<Item> SCYTHE_ITEMS = GuitaRegistries.create(ITEMS);

	public static final GuitaRegistryEntry<Item> WOODEN_SCYTHE = SCYTHE_ITEMS.register("wooden_scythe", () -> new ScytheItem(Tiers.WOOD, 5, -2.8f, 0.1f, new Item.Properties()));
	public static final GuitaRegistryEntry<Item> STONE_SCYTHE = SCYTHE_ITEMS.register("stone_scythe", () -> new ScytheItem(Tiers.STONE, 5, -2.8f, 0.15f, new Item.Properties()));
	public static final GuitaRegistryEntry<Item> IRON_SCYTHE = SCYTHE_ITEMS.register("iron_scythe", () -> new ScytheItem(Tiers.IRON, 5, -2.75f, 0.22f, new Item.Properties()));
	public static final GuitaRegistryEntry<Item> GOLDEN_SCYTHE = SCYTHE_ITEMS.register("golden_scythe", () -> new ScytheItem(Tiers.GOLD, 5, -2.7f, 0.5f, new Item.Properties()));
	public static final GuitaRegistryEntry<Item> DIAMOND_SCYTHE = SCYTHE_ITEMS.register("diamond_scythe", () -> new ScytheItem(Tiers.DIAMOND, 5, -2.7f, 0.25f, new Item.Properties()));
	public static final GuitaRegistryEntry<Item> NETHERITE_SCYTHE = SCYTHE_ITEMS.register("netherite_scythe", () -> new ScytheItem(Tiers.NETHERITE, 5, -2.7f, 0.3f, new Item.Properties().fireResistant()));

	public static final GuitaRegistryEntry<Block> OBESE_APPLE = registerObeseCrop("obese_apple", ObeseCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED));
	public static final GuitaRegistryEntry<Block> OBESE_BEETROOT = registerObeseCrop("obese_beetroot", ObeseCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA));
	public static final GuitaRegistryEntry<Block> OBESE_CARROT = registerObeseCrop("obese_carrot", ObeseCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE));
	public static final GuitaRegistryEntry<Block> OBESE_POISONOUS_POTATO = registerObeseCrop("obese_poisonous_potato", ObeseCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN));
	public static final GuitaRegistryEntry<Block> OBESE_POTATO = registerObeseCrop("obese_potato", ObeseCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW));

	public static final GuitaRegistryEntry<Block> OBESE_BEETROOT_FOLIAGE = registerFoliage("obese_beetroot_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());
	public static final GuitaRegistryEntry<Block> OBESE_CARROT_FOLIAGE = registerFoliage("obese_carrot_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());
	public static final GuitaRegistryEntry<Block> OBESE_POTATO_FOLIAGE = registerFoliage("obese_potato_foliage", ObeseCropFoliageBlock::new, BlockBehaviour.Properties.of());

	public static final GuitaRegistryEntry<Block> FLOWERING_OAK_LOG = registerWithItem("flowering_oak_log", () -> new ThinLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(Blocks.OAK_LOG.defaultMapColor()), true));
	public static final GuitaRegistryEntry<Block> STRIPPED_FLOWERING_OAK_LOG = registerWithItem("stripped_flowering_oak_log", () -> new ThinLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(Blocks.OAK_LOG.defaultMapColor())));
	public static final GuitaRegistryEntry<Block> FLOWERING_OAK_LEAVES = registerWithItem("flowering_oak_leaves", () -> Blocks.leaves(SoundType.GRASS));
	public static final GuitaRegistryEntry<Block> FLOWERING_OAK_SAPLING = registerWithItem("flowering_oak_sapling", () -> new FloweringOakSaplingBlock(
			new TreeGrower(
					"flowering_oak",
					Optional.empty(),
					Optional.of(OCWorldgen.FLOWERING_OAK_CONFIGURED),
					Optional.of(OCWorldgen.FLOWERING_OAK_BEES_CONFIGURED)
			), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
	public static final GuitaRegistryEntry<Block> POTTED_FLOWERING_OAK_SAPLING = BLOCKS.register("potted_flowering_oak_sapling", () -> Blocks.flowerPot(FLOWERING_OAK_SAPLING.get()));

	public static final GuitaRegistryEntry<Block> APPLE = BLOCKS.register("apple", () -> new AppleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA).mapColor(MapColor.COLOR_RED)));
	public static final GuitaRegistryEntry<Item> APPLE_SEED = ITEMS.register("apple_seed", () -> new BlockItem(APPLE.get(), new Item.Properties()));

	private static <T extends Block> GuitaRegistryEntry<T> registerFoliage(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
		return BLOCKS.register(name, () -> blockFactory.apply(properties
				.pushReaction(PushReaction.DESTROY)
				.noCollission()
				.instabreak()
				.sound(SoundType.GRASS)
				.ignitedByLava()));
	}

	private static <T extends Block> GuitaRegistryEntry<T> registerObeseCrop(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
		return registerWithItem(name, () -> blockFactory.apply(properties
				.instrument(NoteBlockInstrument.DIDGERIDOO)
				.strength(0.75F)
				.sound(SoundType.WOOD)
				.pushReaction(PushReaction.DESTROY)));
	}

	private static <T extends Block> GuitaRegistryEntry<T> registerWithItem(String name, Supplier<T> block) {
		GuitaRegistryEntry<T> toReturn = BLOCKS.register(name, block);
		ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
		return toReturn;
	}

	public static void init() {
		ITEMS.init();
		BLOCKS.init();
	}
}
