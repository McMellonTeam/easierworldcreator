package fr.rodofire.ewc.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

/**
 * class to manage new surface rules
 */
@SuppressWarnings("unused")
public class ExtendedSurfaceRules {
    public static SurfaceRules.ConditionSource fullNoiseThresold(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold) {
        return new FullNoiseThresholdConditionSource(noise, minThreshold, maxThreshold);
    }

    /**
     * method to place a block randomly
     *
     * @param name  the name of the condition
     * @param bound the bound that determines if the block is placed. Must be between {@code 0f - 1f}.
     *              <ul>
     *              <li> 0f means that the value will always be false
     *              <li> 0.5f means that the block will be placed 50% of the time
     *              <li> 1f means that the block will always be placed
     *              </ul>
     * @return the material condition
     */
    public static SurfaceRules.ConditionSource random(ResourceLocation name, float bound) {
        return new RandomConditionSource(name, bound);
    }


    public record FullNoiseThresholdConditionSource(
            ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold,
            double maxThreshold) implements SurfaceRules.ConditionSource {

        public static final KeyDispatchDataCodec<FullNoiseThresholdConditionSource> CODEC = KeyDispatchDataCodec.of(
                RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                        ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(FullNoiseThresholdConditionSource::noise),
                                        Codec.DOUBLE.fieldOf("min_threshold").forGetter(FullNoiseThresholdConditionSource::minThreshold),
                                        Codec.DOUBLE.fieldOf("max_threshold").forGetter(FullNoiseThresholdConditionSource::maxThreshold)
                                ).apply(instance, FullNoiseThresholdConditionSource::new)
                )
        );

        @Override
        public @NotNull KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context materialRuleContext) {
            final NormalNoise doublePerlinNoiseSampler = materialRuleContext.randomState.getOrCreateNoise(this.noise);

            class NoiseThresholdPredicate extends SurfaceRules.LazyXZCondition {
                NoiseThresholdPredicate() {
                    super(materialRuleContext);
                }

                @Override
                protected boolean compute() {
                    double d = doublePerlinNoiseSampler.getValue(this.context.blockX, this.context.blockY, this.context.blockZ);
                    return d >= FullNoiseThresholdConditionSource.this.minThreshold && d <= FullNoiseThresholdConditionSource.this.maxThreshold;
                }
            }

            return new NoiseThresholdPredicate();
        }
    }

    public record RandomConditionSource(ResourceLocation name, float bound) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<RandomConditionSource> CODEC = KeyDispatchDataCodec.of(
                RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                        ResourceLocation.CODEC.fieldOf("random_name").forGetter(RandomConditionSource::name),
                                        Codec.FLOAT.fieldOf("bound").forGetter(RandomConditionSource::bound)
                                )
                                .apply(instance, RandomConditionSource::new)
                )
        );

        @Override
        public @NotNull KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRules.Condition apply(SurfaceRules.Context materialRuleContext) {
            class RandomPredicate extends SurfaceRules.LazyYCondition {
                RandomPredicate() {
                    super(materialRuleContext);
                }

                @Override
                protected boolean compute() {
                    final PositionalRandomFactory randomSplitter = materialRuleContext.randomState.getOrCreateRandomFactory(name);
                    RandomSource random = randomSplitter.at(this.context.blockX, this.context.blockY, this.context.blockZ);
                    return random.nextFloat() < bound;
                }
            }
            return new RandomPredicate();
        }
    }
}
