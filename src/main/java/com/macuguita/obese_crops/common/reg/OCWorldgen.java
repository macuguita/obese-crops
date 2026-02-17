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

import com.macuguita.lib.platform.registry.GuitaRegistries;
import com.macuguita.lib.platform.registry.GuitaRegistry;
import com.macuguita.lib.platform.registry.GuitaRegistryEntry;
import com.macuguita.obese_crops.common.ObeseCrops;
import com.macuguita.obese_crops.common.block.AppleBlock;
import com.macuguita.obese_crops.common.block.ThinLogBlock;
import com.macuguita.obese_crops.common.tree.SingleObeseAppleTreeDecorator;
import com.macuguita.obese_crops.common.tree.ThinTrunkPlacer;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;

public final class OCWorldgen {

	public static final GuitaRegistry<TrunkPlacerType<?>> TRUNK_PLACER = GuitaRegistries.create(BuiltInRegistries.TRUNK_PLACER_TYPE, ObeseCrops.MOD_ID);

	public static final GuitaRegistryEntry<TrunkPlacerType<?>> THIN_TRUNK_PLACER = TRUNK_PLACER.register("thin_trunk_placer", () -> new TrunkPlacerType<>(ThinTrunkPlacer.CODEC));

	public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERING_OAK_CONFIGURED = registerConfiguredFeature("flowering_oak");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERING_OAK_BEES_CONFIGURED = registerConfiguredFeature("flowering_oak_bees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OBESE_FLOWERING_OAK_CONFIGURED = registerConfiguredFeature("obese_flowering_oak");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OBESE_FLOWERING_OAK_BEES_CONFIGURED = registerConfiguredFeature("obese_flowering_oak_bees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERING_OAKS_CONFIGURED = registerConfiguredFeature("flowering_oaks");

	public static final ResourceKey<PlacedFeature> FLOWERING_OAK_PLACED = registerPlacedFeature("flowering_oak");
	public static final ResourceKey<PlacedFeature> FLOWERING_OAK_BEES_PLACED = registerPlacedFeature("flowering_oak_bees");
	public static final ResourceKey<PlacedFeature> OBESE_FLOWERING_OAK_PLACED = registerPlacedFeature("obese_flowering_oak");
	public static final ResourceKey<PlacedFeature> OBESE_FLOWERING_OAK_BEES_PLACED = registerPlacedFeature("obese_flowering_oak_bees");
	public static final ResourceKey<PlacedFeature> FLOWERING_OAKS_PLACED = registerPlacedFeature("flowering_oaks");

	public static final TreeDecoratorType<SingleObeseAppleTreeDecorator> APPLE_DECORATOR = Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, ObeseCrops.id("single_obese_apple_decorator"), new TreeDecoratorType<>(SingleObeseAppleTreeDecorator.CODEC));

	private static ResourceKey<ConfiguredFeature<?, ?>> registerConfiguredFeature(String id) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, ObeseCrops.id(id));
	}

	private static ResourceKey<PlacedFeature> registerPlacedFeature(String id) {
		return ResourceKey.create(Registries.PLACED_FEATURE, ObeseCrops.id(id));
	}

	public static void init() {
		BiomeModifications.create(ObeseCrops.id("obese_apple_biome_modifications"))
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.tag(OCBiomeTags.FLOWERING_OAK_TREE),
						context -> context.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FLOWERING_OAKS_PLACED)
				);
	}

	public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> registerable) {
		HolderGetter<PlacedFeature> placedFeatures = registerable.lookup(Registries.PLACED_FEATURE);

		SingleObeseAppleTreeDecorator appleDecorator = new SingleObeseAppleTreeDecorator();
		BeehiveDecorator beehiveTreeDecorator = new BeehiveDecorator(0.05F);
		AttachedToLeavesDecorator attachedToLeavesDecorator = new AttachedToLeavesDecorator(0.14f, 1, 0,
				new RandomizedIntStateProvider(
						BlockStateProvider.simple(OCObjects.APPLE.get().defaultBlockState()),
						AppleBlock.AGE,
						UniformInt.of(0, AppleBlock.MAX_AGE)
				),
				2,
				List.of(Direction.DOWN));

		FeatureUtils.register(registerable, FLOWERING_OAK_CONFIGURED, Feature.TREE,
				floweringOak().decorators(List.of(attachedToLeavesDecorator)).build());
		FeatureUtils.register(registerable, FLOWERING_OAK_BEES_CONFIGURED, Feature.TREE,
				floweringOak().decorators(List.of(attachedToLeavesDecorator, beehiveTreeDecorator)).build());
		FeatureUtils.register(registerable, OBESE_FLOWERING_OAK_CONFIGURED, Feature.TREE,
				floweringOak().decorators(List.of(attachedToLeavesDecorator, appleDecorator)).build());
		FeatureUtils.register(registerable, OBESE_FLOWERING_OAK_BEES_CONFIGURED, Feature.TREE,
				floweringOak().decorators(List.of(attachedToLeavesDecorator, beehiveTreeDecorator, appleDecorator)).build());

		FeatureUtils.register(registerable, FLOWERING_OAKS_CONFIGURED, Feature.RANDOM_SELECTOR,
				new RandomFeatureConfiguration(
						List.of(
								new WeightedPlacedFeature(placedFeatures.getOrThrow(FLOWERING_OAK_BEES_PLACED), 0.2F),
								new WeightedPlacedFeature(placedFeatures.getOrThrow(OBESE_FLOWERING_OAK_PLACED), 0.05F),
								new WeightedPlacedFeature(placedFeatures.getOrThrow(OBESE_FLOWERING_OAK_BEES_PLACED), 0.025F)
						), placedFeatures.getOrThrow(FLOWERING_OAK_PLACED)
				));
	}

	public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> registerable) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = registerable.lookup(Registries.CONFIGURED_FEATURE);

		PlacementUtils.register(registerable, FLOWERING_OAK_PLACED, configuredFeatures.getOrThrow(FLOWERING_OAK_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, FLOWERING_OAK_BEES_PLACED, configuredFeatures.getOrThrow(FLOWERING_OAK_BEES_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, OBESE_FLOWERING_OAK_PLACED, configuredFeatures.getOrThrow(OBESE_FLOWERING_OAK_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
		PlacementUtils.register(registerable, OBESE_FLOWERING_OAK_BEES_PLACED, configuredFeatures.getOrThrow(OBESE_FLOWERING_OAK_BEES_CONFIGURED), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));

		PlacementUtils.register(registerable, FLOWERING_OAKS_PLACED, configuredFeatures.getOrThrow(FLOWERING_OAKS_CONFIGURED),
				RarityFilter.onAverageOnceEvery(40), SurfaceWaterDepthFilter.forMaxDepth(0),
				PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
	}

	private static TreeConfiguration.TreeConfigurationBuilder floweringOak() {
		return new TreeConfiguration.TreeConfigurationBuilder(
				BlockStateProvider.simple(OCObjects.FLOWERING_OAK_LOG.get().defaultBlockState()
						.setValue(ThinLogBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), true)
						.setValue(ThinLogBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), true)),
				new ThinTrunkPlacer(3, 11, 0, 0.618, 1.382, 0.381, 0.328),
				BlockStateProvider.simple(OCObjects.FLOWERING_OAK_LEAVES.get()),
				new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
				new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
		)
				.ignoreVines()
				.dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK.defaultBlockState()));
	}
}
