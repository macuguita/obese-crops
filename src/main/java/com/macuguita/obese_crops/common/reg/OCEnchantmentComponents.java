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

import java.util.function.UnaryOperator;

import com.macuguita.obese_crops.common.ObeseCrops;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

public interface OCEnchantmentComponents {

	DataComponentType<EnchantmentValueEffect> SCYTHE_PULLING_RANGE = register(ObeseCrops.id("scythe_pulling_range"), builder -> builder.persistent(EnchantmentValueEffect.CODEC));

	private static <T> @NotNull DataComponentType<T> register(ResourceLocation id, @NotNull UnaryOperator<DataComponentType.Builder<T>> operator) {
		return Registry.register(
				BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id, ((DataComponentType.Builder) operator.apply(DataComponentType.builder())).build()
		);
	}

	static void init() {

	}
}
