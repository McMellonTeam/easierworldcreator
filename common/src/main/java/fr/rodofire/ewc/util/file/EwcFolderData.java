package fr.rodofire.ewc.util.file;


import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.platform.Services;
import fr.rodofire.ewc.shape.block.MultiChunkFeaturesHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class EwcFolderData {
    private static int tickNumber = 0;
    private static final Map<ResourceKey<Level>, Path> dimensionPath = new HashMap<>();

    public static void initFiles() {
        EwcConstants.LOGGER.info("|\t- Registering Data Folders");

        Services.PlatformEvents.SERVER.onWorldLoad(( serverWorld) -> {
            MinecraftServer minecraftServer = serverWorld.getServer();
            EwcConstants.LOGGER.info("Initializing and cleaning shape files for dimension: " + serverWorld.dimension().location());
            dimensionPath.put(serverWorld.dimension(), DimensionType.getStorageFolder(serverWorld.dimension(), minecraftServer.getWorldPath(LevelResource.ROOT)));
            createDirectories(serverWorld);

            if (!getGeneratedFeatures(serverWorld).toFile().exists()) {
                try {
                    Files.writeString(getGeneratedFeatures(serverWorld), "{}", StandardOpenOption.CREATE);
                } catch (IOException e) {
                    EwcConstants.LOGGER.error("Failed to write generated features to file, report the issue to the mod author");
                    e.fillInStackTrace();
                }
            }

            MultiChunkFeaturesHandler.cleanEntries(serverWorld);
            MultiChunkFeaturesHandler.init(serverWorld);
            EwcConstants.LOGGER.info("finished file initialize and clean");
        });

        Services.PlatformEvents.SERVER.onWorldUnload(MultiChunkFeaturesHandler::save);

        Services.PlatformEvents.WORLD.onEndWorldTick(serverWorld -> {
            if (++tickNumber % 2400 == 0) {
                MultiChunkFeaturesHandler.save(serverWorld);
            }
        });
    }

    private static void createDirectories(ServerLevel world) {
        File file = getEwcDataDirectory(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getStructuresDirectory(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getReferenceDir(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public static Path getEwcDataDirectory(WorldGenLevel world) {
        ServerLevel serverWorld = world.getLevel();
        return dimensionPath.get(serverWorld.dimension()).resolve("ewc_data");
    }

    public static Path getStructuresDirectory(WorldGenLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structures");
    }

    public static Path getStructureDataDir(WorldGenLevel world, ChunkPos chunk) {
        Path path = getStructuresDirectory(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z);
    }

    public static Path getNVerifyDataDir(WorldGenLevel world, ChunkPos chunk) {
        Path path = getStructureDataDir(world, chunk);
        if (path.toFile().exists()) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }

    public static Path getReferenceDir(WorldGenLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structure_references");
    }

    public static Path getStructureReference(WorldGenLevel world, ChunkPos chunk) {
        Path path = getReferenceDir(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z + ".json");
    }

    public static Path getGeneratedFeatures(WorldGenLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("generated_features.json");
    }

    public static Path getGeneratedFeatures(ServerLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("generated_features.json");
    }

    public static Path getEwcDataDirectory(ServerLevel world) {
        return dimensionPath.get(world.dimension()).resolve("ewc_data");
    }

    public static Path getStructuresDirectory(ServerLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structures");
    }

    public static Path getStructureDataDir(ServerLevel world, ChunkPos chunk) {
        Path path = getStructuresDirectory(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z);
    }

    public static Path getNVerifyDataDir(ServerLevel world, ChunkPos chunk) {
        Path path = getStructureDataDir(world, chunk);
        if (path.toFile().exists()) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }

    public static Path getReferenceDir(ServerLevel world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structure_references");
    }

    public static Path getStructureReference(ServerLevel world, ChunkPos chunk) {
        Path path = getReferenceDir(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z + ".json");
    }

    public static class Legacy {
        /**
         * method to get the path of the chunk under the generated folder
         *
         * @param chunk the chunk of the folder
         * @param world the world used to get the generated folder
         * @return the path
         */
        public static Path getLegacyGeneratedChunkDir(ChunkAccess chunk, WorldGenLevel world) {
            Path generatedPath = Objects.requireNonNull(world.getServer()).getWorldPath(LevelResource.GENERATED_DIR).normalize();
            String chunkDirPrefix = "chunk_" + chunk.getPos().x + "_" + chunk.getPos().z;
            return generatedPath.resolve(EwcConstants.MOD_ID).resolve("structures").resolve(chunkDirPrefix);
        }

        public static Path getLegacyStructureDir(WorldGenLevel world) {
            Path generatedPath = Objects.requireNonNull(world.getServer()).getWorldPath(LevelResource.GENERATED_DIR).normalize();
            return generatedPath.resolve(EwcConstants.MOD_ID).resolve("structures");
        }
    }

}
