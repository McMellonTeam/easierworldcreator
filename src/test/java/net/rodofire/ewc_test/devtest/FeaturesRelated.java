package net.rodofire.ewc_test.devtest;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.shape.block.gen.SphereGen;
import net.rodofire.easierworldcreator.shape.block.gen.TorusGen;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.LayerPlacer;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.tag.TagUtil;
import net.rodofire.ewc_test.EWCTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FeaturesRelated {
    public static class FeatureTester extends Feature<DefaultFeatureConfig> {

        public FeatureTester(Codec<DefaultFeatureConfig> configCodec) {
            super(configCodec);
        }

        @Override
        public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
            StructureWorldAccess world = context.getWorld();
            BlockPos pos = context.getOrigin();

            long startTimeCartesian = System.nanoTime();
            //NbtPlacer placer = new NbtPlacer(world, Identifier.of("village/plains/houses/plains_accessory_1"));
                        TorusGen gen = new TorusGen(pos, 3, 30);
            gen.setOuterRadiusZ(40);
            StructurePlacementRuleManager ruler = new StructurePlacementRuleManager();
            ruler.addTagKeys(Set.of(BlockTags.REPLACEABLE_BY_TREES));

            LayerManager manager = new LayerManager(LayerManager.Type.SURFACE,
                    new BlockLayerManager(
                            new BlockLayer(new LayerPlacer(LayerPlacer.PlacingType.RANDOM),
                                    Blocks.REDSTONE_BLOCK.getDefaultState(),
                                    (short) 1, ruler),
                            new BlockLayer(new LayerPlacer(LayerPlacer.PlacingType.RANDOM),
                                    List.of(Blocks.TUFF.getDefaultState(), Blocks.DEEPSLATE.getDefaultState()),
                                    List.of((short) 1, (short) 2), 1, ruler)
                    )
            );

            StructurePlaceAnimator animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.FROM_POINT), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            animator.setTicks(260);
            animator.place(manager.get(gen.getShapeCoordinates()));


/*
            SphereGen sphereGen = new SphereGen(pos, 48);
            Map<ChunkPos, LongOpenHashSet> posSet = sphereGen.getShapeCoordinates();
            ShapePlacer placer = new ShapePlacer(world, ShapePlacer.PlaceMoment.WORLD_GEN, pos);
            StructurePlacementRuleManager ruler = new StructurePlacementRuleManager();
            ruler.addTagKeys(Set.of(BlockTags.REPLACEABLE_BY_TREES));
            placer.place(posSet, new LayerManager(
                    LayerManager.Type.SURFACE,
                    new BlockLayerManager(
                            new LayerPlacer(LayerPlacer.PlacingType.RANDOM),
                            Blocks.REDSTONE_BLOCK.getDefaultState(),
                            (short) 1, ruler)
            ));
*/

            long endTimeCartesian = (System.nanoTime());
            long durationCartesian = (endTimeCartesian - startTimeCartesian) / 1000000;
            System.out.println("duration : " + durationCartesian + " ms");
            return true;
        }
    }

    public static class ModConfiguredFeatures<FC extends FeatureConfig> {
        public static final RegistryKey<ConfiguredFeature<?, ?>> FEATURE_TESTER_KEY = registerKey("feature_teste_key");
        public static final RegistryKey<ConfiguredFeature<?, ?>> TORUS_KEY = registerKey("torus_key");

        public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
            register(context, FEATURE_TESTER_KEY, ModFeatures.FEATURE_TESTER, new DefaultFeatureConfig());
            register(context, TORUS_KEY, ModFeatures.TORUS_TESTER, new DefaultFeatureConfig());
        }

        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(EWCTest.MOD_ID, name));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                       RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
            context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
    }

    public static class ModFeatures {
        public static Feature<DefaultFeatureConfig> FEATURE_TESTER;
        public static Feature<DefaultFeatureConfig> TORUS_TESTER;

        public static void addFeatures() {
            FEATURE_TESTER = registercustomfeature("feature_tester", new FeatureTester(DefaultFeatureConfig.CODEC));
            TORUS_TESTER = registercustomfeature("torus_tester", new TorusGenTest(DefaultFeatureConfig.CODEC));
        }

        private static <C extends FeatureConfig, F extends Feature<C>> F registercustomfeature(String name, F feature) {
            return Registry.register(Registries.FEATURE, name, feature);
        }


    }

    public class ModPLacedFeatures {

        public static final RegistryKey<PlacedFeature> FEATURE_TESTER = registerKey("feature_tester");
        public static final RegistryKey<PlacedFeature> TORUS_TESTER = registerKey("torus_tester");

        public static void bootstrap(Registerable<PlacedFeature> context) {
            var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

            register(context, FEATURE_TESTER, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FEATURE_TESTER_KEY), RarityFilterPlacementModifier.of(100), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
            register(context, TORUS_TESTER, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.TORUS_KEY), RarityFilterPlacementModifier.of(100), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
        }

        public static RegistryKey<PlacedFeature> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(EWCTest.MOD_ID, name));
        }

        private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                     List<PlacementModifier> modifiers) {
            context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                       RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                       PlacementModifier... modifiers) {
            register(context, key, configuration, List.of(modifiers));
        }
    }

    public static class ModBlocks {
        public static final Block FEATURETESTER = Registry.register(Registries.BLOCK, Identifier.of(EWCTest.MOD_ID, "featuretester"), new FeatureBlock(AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), ModConfiguredFeatures.TORUS_KEY));

        public static void registerModBlocks() {
            EWCTest.LOGGER.info("Registering ModBlocks");
        }
    }

    public static class ModItems {
        public static final Item FEATURETESTER = Registry.register(Registries.ITEM, Identifier.of(EWCTest.MOD_ID, "feature_tester"), new BlockItem(ModBlocks.FEATURETESTER, new Item.Settings()));

        public static void registerModItems() {
            EWCTest.LOGGER.info("Registering ModItems");
        }

        private static RegistryKey<Item> keyOf(String id) {
            return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EWCTest.MOD_ID, id));
        }
    }

    public static class FeatureBlock extends Block implements Fertilizable {
        private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

        public FeatureBlock(Settings settings, RegistryKey<ConfiguredFeature<?, ?>> featureKey) {
            super(settings);
            this.featureKey = featureKey;
        }

        @Override
        public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
            return true;
        }

        @Override
        public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
            return true;
        }

        @Override
        public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
            Optional<? extends RegistryEntry<ConfiguredFeature<?, ?>>> optional = world.getRegistryManager()
                    .get(RegistryKeys.CONFIGURED_FEATURE)
                    .getEntry(this.featureKey);
            if (optional.isEmpty()) {
            } else {
                world.removeBlock(pos, false);
                if (((ConfiguredFeature)((RegistryEntry)optional.get()).value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos)) {
                } else {
                    world.setBlockState(pos, state, Block.NOTIFY_ALL);
                }
            }
        }
    }
}
