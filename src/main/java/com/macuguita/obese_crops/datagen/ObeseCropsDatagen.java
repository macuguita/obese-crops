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

import com.macuguita.obese_crops.common.reg.OCEnchantments;
import com.macuguita.obese_crops.common.reg.OCWorldgen;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ObeseCropsDatagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(OCBiomeTagProvider::new);
		pack.addProvider(OCBlockLootTableProvider::new);
		pack.addProvider(OCBlockTagProvider::new);
		pack.addProvider(OCEnchantmentProvider::new);
		pack.addProvider(OCEnchantmentTagProvider::new);
		pack.addProvider(OCItemTagProvider::new);
		pack.addProvider(OCLangProvider::new);
		pack.addProvider(OCModelProvider::new);
		pack.addProvider(OCRecipeProvider::new);
		pack.addProvider(OCWorldProvider::new);
	}

	@Override
	public void buildRegistry(@NotNull RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, OCWorldgen::bootstrapConfiguredFeatures);
		registryBuilder.add(Registries.PLACED_FEATURE, OCWorldgen::bootstrapPlacedFeatures);
		registryBuilder.add(Registries.ENCHANTMENT, OCEnchantments::bootstrap);
	}
}
