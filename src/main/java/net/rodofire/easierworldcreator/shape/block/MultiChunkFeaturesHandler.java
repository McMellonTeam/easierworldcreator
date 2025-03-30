package net.rodofire.easierworldcreator.shape.block;

import com.google.gson.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import net.rodofire.easierworldcreator.util.file.EwcFolderData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class MultiChunkFeaturesHandler {
    private static final ReentrantLock fileLock = new ReentrantLock();

    public static boolean isMultiChunkFeaturesGenerated(StructureWorldAccess world, Identifier featureName) {
        Path referencePath = EwcFolderData.getGeneratedFeatures(world);
        try (FileReader reader = new FileReader(referencePath.toFile())) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject == null) return false;

            return jsonObject.has(featureName.toString());

        } catch (Exception e) {
            e.fillInStackTrace();
            return false;
        }
    }

    public static void add(StructureWorldAccess world, Set<ChunkPos> chunkPosSet, Identifier featureName) {
        File generatedFeaturesFile = EwcFolderData.getGeneratedFeatures(world).toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject;

        fileLock.lock();
        try {
            if (generatedFeaturesFile.exists()) {
                try (FileReader reader = new FileReader(generatedFeaturesFile)) {
                    jsonObject = gson.fromJson(reader, JsonObject.class);
                    if (jsonObject == null) {
                        jsonObject = new JsonObject();
                    }
                }
            } else {
                jsonObject = new JsonObject();
            }
            JsonArray chunkArray = new JsonArray();
            for (ChunkPos pos : chunkPosSet) {
                chunkArray.add(pos.x + "," + pos.z);
            }
            jsonObject.add(featureName.toString(), chunkArray);

            try (FileWriter writer = new FileWriter(generatedFeaturesFile)) {
                gson.toJson(jsonObject, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    public static void cleanEntries(ServerWorld world) {
        File generatedFeaturesFile = EwcFolderData.getGeneratedFeatures(world).toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject;
        fileLock.lock();
        try {
            if (generatedFeaturesFile.exists()) {
                try (FileReader reader = new FileReader(generatedFeaturesFile)) {
                    jsonObject = gson.fromJson(reader, JsonObject.class);
                    if (jsonObject == null) {
                        jsonObject = new JsonObject();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                return;
            }

            Set<String> toRemove = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonArray chunkArray = entry.getValue().getAsJsonArray();
                if (chunkArray.isEmpty()) {
                    toRemove.add(entry.getKey());
                    continue;
                }

                boolean shouldRemove = true;
                for (JsonElement element : chunkArray.asList()) {
                    String[] parts = element.getAsString().split(",");
                    int x = Integer.parseInt(parts[0]);
                    int z = Integer.parseInt(parts[1]);
                    ChunkPos chunkPos = new ChunkPos(x, z);

                    Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
                    if (chunk == null) {
                        shouldRemove = false;
                    }
                }

                if (shouldRemove) {
                    toRemove.add(entry.getKey());
                }
            }

            Ewc.LOGGER.info("cleaning {} entries", toRemove.size());
            toRemove.forEach(jsonObject::remove);


            FileWriter writer = new FileWriter(generatedFeaturesFile);
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileLock.unlock();
        }

    }
}
