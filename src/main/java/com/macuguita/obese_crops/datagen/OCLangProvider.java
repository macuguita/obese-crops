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

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.reg.OCEnchantments;
import com.macuguita.obese_crops.common.reg.OCItemTags;
import com.macuguita.obese_crops.common.reg.OCObjects;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class OCLangProvider extends FabricLanguageProvider {

	public OCLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, "en_us", registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
		generateBlockTranslations(translationBuilder, OCObjects.OBESE_APPLE.get());
		generateBlockTranslations(translationBuilder, OCObjects.OBESE_BEETROOT.get());
		generateBlockTranslations(translationBuilder, OCObjects.OBESE_CARROT.get());
		generateBlockTranslations(translationBuilder, OCObjects.OBESE_POISONOUS_POTATO.get());
		generateBlockTranslations(translationBuilder, OCObjects.OBESE_POTATO.get());
		OCObjects.SCYTHE_ITEMS.stream().forEach(item -> generateItemTranslations(translationBuilder, item.get()));
		translationBuilder.add("itemGroup." + ObeseCrops.MOD_ID + "." + ObeseCrops.MOD_ID, "Obese Crops");
		generateEnchantmentTranslations(translationBuilder, OCEnchantments.BOUNTIFUL_REAP);
		generateEnchantmentDescriptionTranslations(translationBuilder, OCEnchantments.BOUNTIFUL_REAP, "Allows you scythe to reap in a bigger area.");
		generateItemTagTranslations(translationBuilder, OCItemTags.SCYTHES);
		generateItemTagTranslations(translationBuilder, OCItemTags.SCYTHE_ENCHANTABLE);
	}

	@Contract("_ -> new")
	private @NotNull String capitalizeString(@NotNull String string) {
		char[] chars = string.toLowerCase(Locale.getDefault()).toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; ++i) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}
		return new String(chars);
	}

	private void generateBlockTranslations(@NotNull TranslationBuilder translationBuilder, Block block) {
		String temp = capitalizeString(BuiltInRegistries.BLOCK.getKey(block).getPath().replace("_", " "));
		translationBuilder.add(block, temp);
	}

	private void generateItemTranslations(@NotNull TranslationBuilder translationBuilder, Item item) {
		String temp = capitalizeString(BuiltInRegistries.ITEM.getKey(item).getPath().replace("_", " "));
		translationBuilder.add(item, temp);
	}

	private void generateEnchantmentTranslations(@NotNull TranslationBuilder translationBuilder, @NotNull ResourceKey<Enchantment> enchantment) {
		String temp = capitalizeString(enchantment.location().getPath().replace("_", " "));
		translationBuilder.add("enchantment." + enchantment.location().getNamespace() + "." + enchantment.location().getPath(), temp);
	}

	private void generateEnchantmentDescriptionTranslations(@NotNull TranslationBuilder translationBuilder, @NotNull ResourceKey<Enchantment> enchantment, String description) {
		translationBuilder.add("enchantment." + enchantment.location().getNamespace() + "." + enchantment.location().getPath() + ".desc", description);
	}

	private void generateItemTagTranslations(@NotNull TranslationBuilder translationBuilder, @NotNull TagKey<Item> itemTag) {
		String temp = capitalizeString(itemTag.location().getPath().replace("_", " "));
		translationBuilder.add(itemTag, temp);
	}
}
