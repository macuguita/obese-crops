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

import java.util.List;
import java.util.OptionalInt;

import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.treedecorator.SingleObeseAppleTreeDecorator;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;

public interface OCWorldgen {

	ResourceKey<ConfiguredFeature<?, ?>> APPLE_OAK_CONFIGURED = registerConfiguredFeature("apple_oak");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_DARK_OAK_CONFIGURED = registerConfiguredFeature("apple_dark_oak");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_FANCY_OAK_CONFIGURED = registerConfiguredFeature("apple_fancy_oak");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_OAK_BEES_CONFIGURED = registerConfiguredFeature("apple_oak_bees");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_FANCY_OAK_BEES_CONFIGURED = registerConfiguredFeature("apple_fancy_oak_bees");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREES_CONFIGURED = registerConfiguredFeature("apple_trees");
	ResourceKey<ConfiguredFeature<?, ?>> APPLE_DARK_TREES_CONFIGURED = registerConfiguredFeature("apple_dark_trees");

	ResourceKey<PlacedFeature> APPLE_OAK_CHECKED_PLACED = registerPlacedFeature("apple_oak_checked");
	ResourceKey<PlacedFeature> APPLE_DARK_OAK_CHECKED_PLACED = registerPlacedFeature("apple_dark_oak_checked");
	ResourceKey<PlacedFeature> APPLE_FANCY_OAK_CHECKED_PLACED = registerPlacedFeature("apple_fancy_oak_checked");
	ResourceKey<PlacedFeature> APPLE_OAK_BEES_PLACED = registerPlacedFeature("apple_oak_bees");
	ResourceKey<PlacedFeature> APPLE_FANCY_OAK_BEES_PLACED = registerPlacedFeature("apple_fancy_oak_bees");
	ResourceKey<PlacedFeature> APPLE_TREES_PLACED = registerPlacedFeature("apple_trees");
	ResourceKey<PlacedFeature> APPLE_DARK_TREES_PLACED = registerPlacedFeature("apple_dark_trees");

	TreeDecoratorType<SingleObeseAppleTreeDecorator> APPLE_DECORATOR = Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, ObeseCrops.id("single_obese_apple_decorator"), new TreeDecoratorType<>(SingleObeseAppleTreeDecorator.CODEC));

	private static @NotNull ResourceKey<ConfiguredFeature<?, ?>> registerConfiguredFeature(String id) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, ObeseCrops.id(id));
	}

	private static @NotNull ResourceKey<PlacedFeature> registerPlacedFeature(String id) {
		return ResourceKey.create(Registries.PLACED_FEATURE, ObeseCrops.id(id));
	}

	static void init() {
		BiomeModifications.create(ObeseCrops.id("obese_apple_biome_modifications"))
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.tag(OCBiomeTags.OBESE_APPLE),
						context -> context.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, APPLE_TREES_PLACED)
				)
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.tag(OCBiomeTags.OBESE_APPLE).and(BiomeSelectors.tag(OCBiomeTags.DARK_OBESE_APPLE)),
						context -> context.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, APPLE_DARK_TREES_PLACED)
				);
	}

	static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> registerable) {
		SingleObeseAppleTreeDecorator appleDecorator = new SingleObeseAppleTreeDecorator();
		BeehiveDecorator beehiveTreeDecorator = new BeehiveDecorator(0.05F);

		FeatureUtils.register(registerable, APPLE_OAK_CONFIGURED, Feature.TREE, oak().decorators(List.of(appleDecorator)).build());
		FeatureUtils.register(registerable, APPLE_DARK_OAK_CONFIGURED, Feature.TREE,
				new TreeConfiguration.TreeConfigurationBuilder(
						BlockStateProvider.simple(Blocks.DARK_OAK_LOG),
						new DarkOakTrunkPlacer(6, 2, 1),
						BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
						new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
				)
						.ignoreVines()
						.decorators(List.of(appleDecorator)).build());
		FeatureUtils.register(registerable, APPLE_FANCY_OAK_CONFIGURED, Feature.TREE, fancyOak().decorators(List.of(appleDecorator)).build());
		FeatureUtils.register(registerable, APPLE_OAK_BEES_CONFIGURED, Feature.TREE, oak().decorators(List.of(appleDecorator, beehiveTreeDecorator)).build());
		FeatureUtils.register(registerable, APPLE_FANCY_OAK_BEES_CONFIGURED, Feature.TREE, fancyOak().decorators(List.of(appleDecorator, beehiveTreeDecorator)).build());

		HolderGetter<PlacedFeature> placedFeatures = registerable.lookup(Registries.PLACED_FEATURE);

		FeatureUtils.register(registerable, APPLE_TREES_CONFIGURED, Feature.RANDOM_SELECTOR,
				new RandomFeatureConfiguration(
						List.of(
								new WeightedPlacedFeature(placedFeatures.getOrThrow(APPLE_OAK_CHECKED_PLACED), 0.2F),
								new WeightedPlacedFeature(placedFeatures.getOrThrow(APPLE_OAK_BEES_PLACED), 0.2F),
								new WeightedPlacedFeature(placedFeatures.getOrThrow(APPLE_FANCY_OAK_BEES_PLACED), 0.2F)
						), placedFeatures.getOrThrow(APPLE_FANCY_OAK_CHECKED_PLACED)
				));
		FeatureUtils.register(registerable, APPLE_DARK_TREES_CONFIGURED, Feature.RANDOM_SELECTOR,
				new RandomFeatureConfiguration(
						List.of(
						), placedFeatures.getOrThrow(APPLE_DARK_OAK_CHECKED_PLACED)
				));
	}

	static void bootstrapPlacedFeatures(@NotNull BootstrapContext<PlacedFeature> registerable) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = registerable.lookup(Registries.CONFIGURED_FEATURE);

		PlacementUtils.register(registerable, APPLE_OAK_CHECKED_PLACED, configuredFeatures.getOrThrow(APPLE_OAK_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, APPLE_DARK_OAK_CHECKED_PLACED, configuredFeatures.getOrThrow(APPLE_DARK_OAK_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
		PlacementUtils.register(registerable, APPLE_FANCY_OAK_CHECKED_PLACED, configuredFeatures.getOrThrow(APPLE_FANCY_OAK_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, APPLE_OAK_BEES_PLACED, configuredFeatures.getOrThrow(APPLE_OAK_BEES_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, APPLE_FANCY_OAK_BEES_PLACED, configuredFeatures.getOrThrow(APPLE_FANCY_OAK_BEES_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));

		PlacementUtils.register(registerable, APPLE_TREES_PLACED, configuredFeatures.getOrThrow(APPLE_TREES_CONFIGURED),
				RarityFilter.onAverageOnceEvery(40), SurfaceWaterDepthFilter.forMaxDepth(0),
				PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
		PlacementUtils.register(registerable, APPLE_DARK_TREES_PLACED, configuredFeatures.getOrThrow(APPLE_DARK_TREES_CONFIGURED),
				RarityFilter.onAverageOnceEvery(40), SurfaceWaterDepthFilter.forMaxDepth(0),
				PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
	}

	private static TreeConfiguration.@NotNull TreeConfigurationBuilder oak() {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(Blocks.OAK_LOG),
				new StraightTrunkPlacer(4, 2, 0),
				BlockStateProvider.simple(Blocks.OAK_LEAVES),
				new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
				new TwoLayersFeatureSize(1, 0, 1)
		).ignoreVines();
	}

	private static TreeConfiguration.@NotNull TreeConfigurationBuilder fancyOak() {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(Blocks.OAK_LOG),
				new FancyTrunkPlacer(3, 11, 0),
				BlockStateProvider.simple(Blocks.OAK_LEAVES),
				new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
				new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
		)
				.ignoreVines();
	}
}
