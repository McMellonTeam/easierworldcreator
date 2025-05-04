package fr.rodofire.ewc.shape.block.placer;


import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

/**
 * Class to define how multi-chunk features should be placed.
 * For example, for terrain modification,
 * it is better to place the feature before {@link GenerationStep.Decoration#VEGETAL_DECORATION} decorations.
 * This class allows you to manage this and define when is the good moment to place your structure.
 */
@SuppressWarnings("unused")
public class WGShapeData {
    private final String name;

    private final Pair<PlacedFeature, PlacementShift> featureShift;
    private final GenerationStep.Decoration step;

    // private constructor to centralize the creation of the object
    WGShapeData(String name, PlacementShift shift, PlacedFeature feature, GenerationStep.Decoration step) {
        this.name = name;
        this.featureShift = feature == null ? null : new Pair<>(feature, shift);
        this.step = step;
    }

    public static WGShapeData ofStep(GenerationStep.Decoration step, String name) {
        return new WGShapeData(name, null, null, step);
    }

    public static WGShapeData ofShift(PlacementShift shift, PlacedFeature feature, String name) {
        return new WGShapeData(name, shift, feature, null);
    }

    public String getName() {
        return name;
    }

    public Optional<Pair<PlacedFeature, PlacementShift>> getFeatureShift() {
        return Optional.ofNullable(featureShift);
    }

    public Optional<GenerationStep.Decoration> getStep() {
        return Optional.ofNullable(step);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WGShapeData [name=").append(name);
        if (featureShift != null) {
            builder.append(", placementShift=").append(featureShift.getSecond());
            builder.append(", feature=").append(featureShift.getSecond());
        }

        if (step != null) {
            builder.append(", step=").append(step);
        }
        builder.append("]");
        return builder.toString();
    }

    public enum PlacementShift {
        BEFORE,
        AFTER
    }
}
