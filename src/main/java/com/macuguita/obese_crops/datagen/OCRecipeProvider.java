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

import com.macuguita.obese_crops.common.item.ScytheItem;
import com.macuguita.obese_crops.common.reg.OCObjects;
import com.macuguita.obese_crops.mixin.IngredientAccessor;
import org.jetbrains.annotations.NotNull;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

public class OCRecipeProvider extends FabricRecipeProvider {

	public OCRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void buildRecipes(RecipeOutput recipeOutput) {
		OCObjects.SCYTHE_ITEMS.getEntries().forEach(entry -> {
			ScytheItem scythe = (ScytheItem) entry.get();
			if (scythe == OCObjects.NETHERITE_SCYTHE.get()) return;
			Ingredient repairIngredient = scythe.getTier().getRepairIngredient();

			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, scythe)
					.pattern("##|")
					.pattern(" |#")
					.pattern("|  ")
					.define('#', repairIngredient)
					.define('|', Items.STICK)
					.unlockedBy("has_repair_material", getIngredientCriterion(repairIngredient))
					.unlockedBy(getHasName(Items.STICK), has(Items.STICK))
					.save(recipeOutput);
		});
		netheriteSmithing(recipeOutput, OCObjects.DIAMOND_SCYTHE.get(), RecipeCategory.TOOLS, OCObjects.NETHERITE_SCYTHE.get());
		generateObeseBlockDeconstruction(recipeOutput, OCObjects.OBESE_APPLE.get(), Items.APPLE);
		generateObeseBlockDeconstruction(recipeOutput, OCObjects.OBESE_BEETROOT.get(), Items.BEETROOT);
		generateObeseBlockDeconstruction(recipeOutput, OCObjects.OBESE_CARROT.get(), Items.CARROT);
		generateObeseBlockDeconstruction(recipeOutput, OCObjects.OBESE_POISONOUS_POTATO.get(), Items.POISONOUS_POTATO);
		generateObeseBlockDeconstruction(recipeOutput, OCObjects.OBESE_POTATO.get(), Items.POTATO);

		planksFromLog(recipeOutput, Blocks.OAK_PLANKS, OCItemTags.FLOWERING_OAK_LOGS, 4);
	}

	private @NotNull Criterion getIngredientCriterion(Ingredient ingredient) {
		for (Ingredient.Value value : ((IngredientAccessor) ingredient).obese_crops$getValues()) {
			if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) {
				return has(tag);
			}
			if (value instanceof Ingredient.ItemValue(ItemStack item)) {
				return has(item.getItem());
			}
		}

		throw new IllegalStateException("Ingredient has no values");
	}

	private void generateObeseBlockDeconstruction(RecipeOutput recipeOutput, ItemLike obeseBlock, ItemLike ozempicCrop) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ozempicCrop, 9)
				.requires(obeseBlock)
				.group("obese_crop")
				.unlockedBy(getHasName(obeseBlock), has(obeseBlock))
				.save(recipeOutput);
	}
}
