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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.macuguita.obese_crops.common.item.ScytheItem;
import com.macuguita.obese_crops.common.reg.OCObjects;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Repairable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

public class OCRecipeProvider extends FabricRecipeProvider {

	public OCRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		return new RecipeProvider(provider, recipeOutput) {
			@Override
			public void buildRecipes() {
				OCObjects.SCYTHE_ITEMS.getEntries().forEach(entry -> {
					ScytheItem scythe = (ScytheItem) entry.get();
					if (scythe == OCObjects.NETHERITE_SCYTHE.get()) return;
					Optional<Repairable> maybeItems = Optional.ofNullable(scythe.components().get(DataComponents.REPAIRABLE));
					if (maybeItems.isEmpty()) return;
					Optional<TagKey<Item>> maybeTag = maybeItems.get().items().unwrapKey();
					if (maybeTag.isEmpty()) return;
					Ingredient repairIngredient = Ingredient.of(maybeItems.get().items());

					ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.TOOLS, scythe)
							.pattern("##|")
							.pattern(" |#")
							.pattern("|  ")
							.define('#', repairIngredient)
							.define('|', Items.STICK)
							.unlockedBy("has_repair_material", has(maybeTag.get()))
							.unlockedBy(getHasName(Items.STICK), has(Items.STICK))
							.save(recipeOutput);
				});
				netheriteSmithing(OCObjects.DIAMOND_SCYTHE.get(), RecipeCategory.TOOLS, OCObjects.NETHERITE_SCYTHE.get());
			}
		};
	}

	@Override
	public @NotNull String getName() {
		return "Obese Crops Recipe Provider";
	}
}
