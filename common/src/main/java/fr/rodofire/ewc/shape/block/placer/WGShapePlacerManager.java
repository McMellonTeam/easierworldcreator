package fr.rodofire.ewc.shape.block.placer;

import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.mixin.world.gen.ChunkGeneratorMixin;
import fr.rodofire.ewc.util.file.EwcFolderData;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.nio.file.Path;
import java.util.*;

/**
 * Class to manager all the multi-chunk features of a chunk.
 * This an object of this class is created for each chunk in {@link ChunkGeneratorMixin} when the chunk get generated.
 * The class gives the JSON files path of the moment being generated.
 */
public class WGShapePlacerManager {

    private final Map<PlacedFeature, ShortSet> beforeFeatures = new HashMap<>();
    private final Map<PlacedFeature, ShortSet> afterFeatures = new HashMap<>();

    private final Map<GenerationStep.Decoration, ShortSet> steps = new HashMap<>();

    ChunkPos pos;

    /**
     * represents the name of the features.
     */
    private final String[] references;
    private final Set<String> placed = new HashSet<>();

    short putReferences = 0;

    public WGShapePlacerManager(ChunkPos pos, int referenceSize) {
        references = new String[referenceSize];
        this.pos = pos;
    }

    public void put(WGShapeData placer) {
        String name = placer.getName();
        references[putReferences] = name;

        if (placer.getFeatureShift().isPresent()) {
            Pair<PlacedFeature, WGShapeData.PlacementShift> feature = placer.getFeatureShift().get();

            if (feature.getSecond() == WGShapeData.PlacementShift.AFTER) {
                afterFeatures.computeIfAbsent(feature.getFirst(), (k) -> new ShortOpenHashSet()).add(putReferences);
            } else {
                beforeFeatures.computeIfAbsent(feature.getFirst(), (k) -> new ShortOpenHashSet()).add(putReferences);
            }


        } else if (placer.getStep().isPresent()) {
            steps.computeIfAbsent(placer.getStep().get(), (k) -> new ShortOpenHashSet()).add(putReferences);
        }
        putReferences++;
    }

    public void putAll(List<WGShapeData> placers) {
        for (WGShapeData placer : placers) {
            put(placer);
        }
    }

    public Path[] getToPlace(WorldGenLevel worldAccess, PlacedFeature beforeFeature, PlacedFeature featureAfter) {
        ShortSet set = new ShortOpenHashSet();
        if (beforeFeature != null) {
            ShortSet result = beforeFeatures.get(beforeFeature);
            if (result != null) {
                set.addAll(beforeFeatures.get(beforeFeature));
            }
        }

        ShortSet result = afterFeatures.get(featureAfter);
        if (result != null) {
            set.addAll(afterFeatures.get(featureAfter));
        }

        if (set.isEmpty()) {
            return new Path[0];
        }

        return getPath(worldAccess, set);
    }

    public Path[] getToPlace(WorldGenLevel worldAccess, GenerationStep.Decoration feature) {
        ShortSet set = steps.get(feature);
        if (set == null) {
            return new Path[0];
        }
        return getPath(worldAccess, set);
    }

    private Path[] getPath(WorldGenLevel worldAccess, ShortSet set) {
        Path basePath = EwcFolderData.getStructureDataDir(worldAccess, pos);
        Path[] paths = new Path[set.size()];

        int i = 0;
        for (short index : set) {
            paths[i] = basePath.resolve(references[index] + ".json");
            placed.add(references[index]);
            i++;
        }
        return paths;
    }

    public Path[] getLeft(WorldGenLevel worldAccess) {
        Path basePath = EwcFolderData.getStructureDataDir(worldAccess, pos);
        Set<Path> paths = new HashSet<>();
        for (String string : references) {
            if (!placed.contains(string)) {
                paths.add(basePath.resolve(string + ".json"));
            }
        }
        return paths.toArray(new Path[0]);
    }
}
