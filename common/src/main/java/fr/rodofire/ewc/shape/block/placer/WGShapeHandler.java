package fr.rodofire.ewc.shape.block.placer;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.rodofire.ewc.util.file.EwcFolderData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * For multi-chunk features, we create a file that refers every piece of shapes related to a chunkPos.
 * It allows defining at which moment of the chunk generation should the piece be placed.
 */
public class WGShapeHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static void encodeInformations(WorldGenLevel worldAccess, Set<ChunkPos> posSet, WGShapeData placer, ChunkPos posOffset) {
        for (ChunkPos pos : posSet) {
            ChunkPos newPos = new ChunkPos(pos.x + posOffset.x, pos.z + posOffset.z);
            encodeInformation(worldAccess, newPos, placer);
        }
    }

    /**
     * store the information about when should the piece should be placed.
     */
    public static void encodeInformation(WorldGenLevel worldAccess, ChunkPos pos, WGShapeData placer) {
        List<WGShapeData> data = loadData(worldAccess, pos);
        data.add(
                new WGShapeData(
                        placer.getName(),
                        placer.getFeatureShift().isPresent() ? placer.getFeatureShift().get().getSecond() : null,
                        placer.getFeatureShift().isPresent() ? placer.getFeatureShift().get().getFirst() : null,
                        placer.getStep().orElse(null))
        );
        saveData(worldAccess, pos, data);
    }

    /**
     * Décode les informations d'un chunk pour savoir quels morceaux doivent être placés.
     */
    public static WGShapePlacerManager decodeInformation(WorldGenLevel worldAccess, ChunkPos pos) {
        List<WGShapeData> data = loadData(worldAccess, pos);

        if (data.isEmpty()) {
            return null;
        }
        WGShapePlacerManager manager = new WGShapePlacerManager(pos, data.size());
        for (WGShapeData shapeData : data) {
            manager.put(shapeData);
        }
        return manager;
    }

    /**
     * Charge les données JSON.
     */
    private static List<WGShapeData> loadData(WorldGenLevel worldAccess, ChunkPos pos) {
        Path path = EwcFolderData.getStructureReference(worldAccess, pos);
        if (path == null) return new ArrayList<>();
        File file = path.toFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<WGShapeData>>() {
            }.getType();
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            e.fillInStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Sauvegarde les données JSON.
     */
    private static void saveData(WorldGenLevel worldAccess, ChunkPos pos, List<WGShapeData> data) {
        Path path = EwcFolderData.getStructureReference(worldAccess, pos);
        File file = path.toFile();
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
