package fr.rodofire.ewc.mixin.world.gen;

import com.llamalad7.mixinextras.sugar.Local;
import fr.rodofire.ewc.blockdata.blocklist.BlockListHelper;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.shape.block.placer.WGShapeHandler;
import fr.rodofire.ewc.shape.block.placer.WGShapePlacerManager;
import fr.rodofire.ewc.util.file.EwcFolderData;
import fr.rodofire.ewc.util.file.FileUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * mixin to change how are the chunk generated to include multi-chunk features
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Unique
    WGShapePlacerManager[] ewc$placerManagers;

    @Unique
    PlacedFeature ewc$old;

    /**
     * We initialize the placer manager that will define how each piece should be placed
     */
    @Inject(method = "applyBiomeDecoration", at = @At(value = "HEAD"))
    private void initShapeHandler(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        Set<WGShapePlacerManager> shapePlacer = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChunkPos pos = new ChunkPos(chunk.getPos().x + i, chunk.getPos().z + j);
                shapePlacer.add(WGShapeHandler.decodeInformation(level, pos));
            }
        }

        ewc$placerManagers = shapePlacer.toArray(new WGShapePlacerManager[0]);
    }


    @Inject(method = "applyBiomeDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/StructureManager;shouldGenerateStructures()Z"))
    private void onGenerationStep(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci, @Local(ordinal = 2) int k) {
        for (WGShapePlacerManager placerManager : ewc$placerManagers) {
            if (placerManager == null) return;

            if (k >= GenerationStep.Decoration.values().length) {
                return;
            }

            Path[] paths = placerManager.getToPlace(level, GenerationStep.Decoration.values()[k]);
            for (Path path : paths) {
                level.setCurrentlyGenerating(() ->
                        "\n\t-ewc multi-chunk feature generating: " +
                                "\n\t\t- " + path.getFileName() +
                                "\n\t\t- mixin step : generation step");
                BlockListManager manager = BlockListHelper.fromJsonPath(level, path);
                if (manager == null) continue;
                manager.placeAllNDelete(level);
                FileUtil.removeFile(path);
            }
        }
    }


    /**
     * <p>The method verifies
     * if there are some files under {@code [save name]/generated/easierworldcreator/structures/chunk_[chunk.x]_[chunk.z].}
     * <p>If yes, for every file, it will get every BlockList of the JSON file.
     * Then it will place every block of the BlockList and will then remove the file.
     * When everything is done, it will remove the chunk folder
     * <p>Else, it will continue the normal generation.
     *
     * @param level             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureManager unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     */
    @Inject(method = "applyBiomeDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setCurrentlyGenerating(Ljava/util/function/Supplier;)V", ordinal = 1))
    private void onFeatureGenerated(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci, @Local PlacedFeature placedFeature) {
        for (WGShapePlacerManager placerManager : ewc$placerManagers) {
            if (placerManager == null) return;
            Path[] paths = placerManager.getToPlace(level, ewc$old, placedFeature);
            for (Path path : paths) {
                level.setCurrentlyGenerating(() ->
                        "\n\t-ewc multi-chunk feature generating: " +
                                "\n\t\t- " + path.getFileName() +
                                "\n\t\t- mixin step : feature");
                BlockListManager comparator = BlockListHelper.fromJsonPath(level, path);
                if (comparator == null) {
                    continue;
                }
                comparator.placeAllNDelete(level);
                FileUtil.removeFile(path);
            }
        }

        ewc$old = placedFeature;
    }

    /**
     * At then end, with the fact that multi chunk are multithreaded, some files might not have been placed,
     * that's why we add this mixin at the end of the method to make sure that all the files were placed.
     * We also remove all files related to this chunk generation
     *
     * @param level             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureManager unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     */
    @Inject(method = "applyBiomeDecoration", at = @At(value = "TAIL"))
    private void endGeneration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChunkPos pos = new ChunkPos(chunk.getPos().x + i, chunk.getPos().z + j);
                Path basePaths = EwcFolderData.getStructureDataDir(level, pos);
                try (Stream<Path> paths = Files.list(basePaths)) {
                    paths.forEach(path -> {
                        if (path.toString().endsWith(".json")) {
                            BlockListManager manager = BlockListHelper.fromJsonPath(level, path);
                            if (manager == null) return;

                            level.setCurrentlyGenerating(() ->
                                    "\n| ewc multi-chunk feature generating: "
                                            + "\n\t- mixin step: end generation"
                                            + "\n\t- center chunkPos: " + chunk.getPos()
                                            + "\n\t- parent directory: " + path.getParent().getFileName().toString()
                                            + "\n\t- generating: " + path.getFileName()
                            );
                            manager.placeAllNDelete(level);
                            FileUtil.removeFile(path);
                        }
                    });
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
                try {
                    Files.delete(basePaths);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }

        Path managerPath = EwcFolderData.getStructureReference(level, chunk.getPos());
        try {
            Files.delete(managerPath);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
