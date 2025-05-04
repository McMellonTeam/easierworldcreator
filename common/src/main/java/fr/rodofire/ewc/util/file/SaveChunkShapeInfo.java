package fr.rodofire.ewc.util.file;


import fr.rodofire.ewc.EwcConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * <p>
 * That is a way to generate a really large structure.
 * Since that the nbt is saved during world gen, no block entity should be present.
 * </p>
 * <p>This class is used to create JSON files.
 * <p>It is used by this mod to divide a structure into chunks.
 * <p>If the structure is larger than a chunk, it will save the structure into chunks that will be saved into JSON files.
 * <p>The JSON files will then be saved into the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long].
 * <p>It will then be read by the following class {@link LoadChunkShapeInfo}
 * </p>
 * <p>
 * Since that to generate large structures, it requires to write and read the json file, be careful to don't have a too big structure.
 * It may use a lot of performance during the write of every files
 * </p>
 */
@SuppressWarnings("unused")
public class SaveChunkShapeInfo {

    //determines the number of threads that the class can use
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * this method allows the creation of the generated and related folders
     *
     * @param path the base path
     * @return the generated Path
     */
    public static Path createFolders(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        path = path.resolve(EwcConstants.MOD_ID);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        path = path.resolve("structures");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public static Path getMultiChunkPath(WorldGenLevel worldAccess, ChunkPos chunkPos) {
        MinecraftServer server = worldAccess.getServer();
        Path var;
        if(server != null) {
            Path generatedPath = server.getWorldPath(LevelResource.GENERATED_DIR).normalize();
            var = createFolders(generatedPath).resolve("chunk_" + chunkPos.x + "_" + chunkPos.z);
            try {
                Files.createDirectories(var);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return var;
        }
        return null;
    }


}
