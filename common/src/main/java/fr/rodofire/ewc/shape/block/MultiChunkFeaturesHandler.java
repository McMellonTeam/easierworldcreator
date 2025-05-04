package fr.rodofire.ewc.shape.block;

import com.google.gson.*;
import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.util.file.EwcFolderData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class MultiChunkFeaturesHandler {
    private static final Map<ResourceKey<Level>, Map<ResourceLocation, Set<ChunkPos>>> generated = new HashMap<>();

    private static final ReentrantLock fileLock = new ReentrantLock();

    public static void init(ServerLevel world) {
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

            generated.computeIfAbsent(world.dimension(), k -> new HashMap<>());
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonArray chunkArray = entry.getValue().getAsJsonArray();
                Set<ChunkPos> chunks = new HashSet<>();

                for (JsonElement element : chunkArray.asList()) {
                    String[] parts = element.getAsString().split(",");
                    int x = Integer.parseInt(parts[0]);
                    int z = Integer.parseInt(parts[1]);
                    ChunkPos chunkPos = new ChunkPos(x, z);
                    chunks.add(chunkPos);
                }

                generated.get(world.registryAccess()).put(ResourceLocation.parse(entry.getKey()), chunks);

            }


            try (FileWriter writer = new FileWriter(generatedFeaturesFile)) {
                gson.toJson(jsonObject, writer);
            }

        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            fileLock.unlock();
        }

    }

    public static boolean isMultiChunkFeaturesGenerated(WorldGenLevel world, ResourceLocation featureName) {
        return generated.computeIfAbsent(world.getLevel().dimension(), (o) -> new HashMap<>()).containsKey(featureName);
    }

    public static void add(WorldGenLevel world, Set<ChunkPos> chunkPosSet, ResourceLocation featureName) {
        try {
            generated.computeIfAbsent(world.getLevel().dimension(), (o) -> new HashMap<>()).put(featureName, chunkPosSet);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public static void save(WorldGenLevel world) {
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

            for (Map.Entry<ResourceLocation, Set<ChunkPos>> entry : generated.computeIfAbsent(world.getLevel().dimension(), (o) -> new HashMap<>()).entrySet()) {
                if (jsonObject.has(entry.getKey().toString())) continue;
                for (ChunkPos pos : entry.getValue()) {
                    chunkArray.add(pos.x + "," + pos.z);
                }
                jsonObject.add(entry.getKey().toString(), chunkArray);
            }

            try (FileWriter writer = new FileWriter(generatedFeaturesFile)) {
                gson.toJson(jsonObject, writer);
            }

        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            fileLock.unlock();
        }
    }

    public static void cleanEntries(ServerLevel world) {
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
                    e.fillInStackTrace();
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

                    ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
                    if (chunk == null) {
                        shouldRemove = false;
                    }
                }

                if (shouldRemove) {
                    toRemove.add(entry.getKey());
                }
            }

            EwcConstants.LOGGER.info("cleaning {} entries", toRemove.size());
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
