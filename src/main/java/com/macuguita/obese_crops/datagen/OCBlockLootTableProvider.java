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

package com.macuguita.obese_crops.datagen;

import java.util.concurrent.CompletableFuture;

import com.macuguita.obese_crops.common.block.ObeseCropBlock;
import com.macuguita.obese_crops.common.reg.OCObjects;
import com.macuguita.obese_crops.mixin.BlockLootSubProviderAccessor;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class OCBlockLootTableProvider extends FabricBlockLootTableProvider {

	protected OCBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		add(OCObjects.OBESE_APPLE.get(), block -> createObeseBlockDrop(block, Items.APPLE));
		add(OCObjects.OBESE_BEETROOT.get(), block -> createObeseBlockDrop(block, Items.BEETROOT));
		add(OCObjects.OBESE_CARROT.get(), block -> createObeseBlockDrop(block, Items.CARROT));
		add(OCObjects.OBESE_POISONOUS_POTATO.get(), block -> createObeseBlockDrop(block, Items.POISONOUS_POTATO));
		add(OCObjects.OBESE_POTATO.get(), block -> createObeseBlockDrop(block, Items.POTATO));

		dropSelf(OCObjects.FLOWERING_OAK_LOG.get());
		dropSelf(OCObjects.STRIPPED_FLOWERING_OAK_LOG.get());
		createOakLeavesDrops(OCObjects.FLOWERING_OAK_LEAVES.get(), OCObjects.FLOWERING_OAK_SAPLING.get(), BlockLootSubProviderAccessor.obese_crops$getNormalLeavesSaplingChances());
		dropSelf(OCObjects.FLOWERING_OAK_SAPLING.get());
		dropPottedContents(OCObjects.POTTED_FLOWERING_OAK_SAPLING.get());
		add(OCObjects.APPLE.get(), this::createOakLeavesDrops);
	}

	private LootTable.Builder createOakLeavesDrops(Block block) {
		return LootTable.lootTable().withPool(
				LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(this.applyExplosionDecay(
								block,
								LootItem.lootTableItem(OCObjects.APPLE_SEED.get())
										.apply(
												SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))
														.when(
																LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
																		.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CocoaBlock.AGE, 2))
														)
										)
						)));
	}

	private LootTable.Builder createObeseBlockDrop(Block block, Item drop) {
		return this.applyExplosionDecay(
				block,
				LootTable.lootTable().withPool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1.0F))
								.add(
										AlternativesEntry.alternatives(
												ObeseCropBlock.CARVED.getPossibleValues(),
												integer -> {
													var builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
															.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ObeseCropBlock.CARVED, integer));

													return integer == 0
															? LootItem.lootTableItem(block).when(builder)
															: LootItem.lootTableItem(drop).when(builder).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)));
												}
										)
								)
				)
		);
	}
}
