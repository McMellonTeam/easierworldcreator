package fr.rodofire.ewc.util.file;


import fr.rodofire.ewc.EwcConstants;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * class that provide useful methods for files related.
 */
@SuppressWarnings("unused")
public class FileUtil {
    /**
     * <p>Method to move a file from one place to another.
     * <p>If the old path and the new path are under the same folder, this will just rename the file
     *
     * @param oldPath the path of the file that will be moved
     * @param newPath the path of the new file
     */
    public static void renameFile(Path oldPath, Path newPath) {
        if (Files.exists(oldPath)) {
            try {
                Path parentDir = newPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }

                Files.move(oldPath, newPath);

            } catch (IOException e) {
                EwcConstants.LOGGER.warn("Failed to rename file. oldPath: {} newPath: {}", oldPath, newPath, e);
            } catch (SecurityException e) {
                EwcConstants.LOGGER.warn("Insufficient permissions to rename file. oldPath: {} newPath: {}", oldPath, newPath, e);
            }
        } else {
            EwcConstants.LOGGER.warn("Renaming file failed, file doesn't exist: {}", oldPath);
        }
    }

    /**
     * method to remove a file without the risk of getting {@link java.io.FileNotFoundException}
     *
     * @param path the path of the file
     */
    public static void removeFile(Path path) {
        File file = new File(path.toString());
        if (file.exists()) {
            boolean deleted = file.delete();
        }
    }

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



    /**
     * method to remove the chunk folder under the generated folder
     *
     * @param chunk the chunk of the folder that will be removed
     * @param world the world used to get the generated folder
     */
    public static void removeGeneratedChunkDirectory(ChunkAccess chunk, WorldGenLevel world) {
        Path directoryPath = getLegacyGeneratedChunkDir(chunk, world);
        File file = new File(directoryPath.toString());
        if (file.exists() && file.isDirectory()) {
            boolean deleted = file.delete();
        }
    }

    public static Path getWorldSavePathDirectory(WorldGenLevel world, LevelResource savePath) {
        return Objects.requireNonNull(world.getServer()).getWorldPath(savePath).normalize();
    }

    public static String loadJson(Path path) {
        String jsonContent;
        try {
            jsonContent = Files.readString(path);
        } catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }
        return jsonContent;
    }
}
