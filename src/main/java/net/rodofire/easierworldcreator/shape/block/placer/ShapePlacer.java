package net.rodofire.easierworldcreator.shape.block.placer;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.GenerationStep;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListHelper;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.shape.block.MultiChunkFeaturesHandler;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.util.file.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.world.chunk.ChunkPosManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * Class that allow you to place a structure based on a coordinate map or a {@link BlockListManager}.
 * It takes {@link PlaceMoment} argument which define when should the structure be placed.
 * Different behaviors will happen depending on it.
 * Since that placement is different depending on the moment where the shape is being placed, we have to use the enum.
 */
@SuppressWarnings("unused")
public class ShapePlacer {
    StructureWorldAccess world;
    BlockPos center;

    /**
     * for better debugging, the identifier is used to know which feature is being generated
     */
    private final Identifier featureName;
    private final PlaceMoment placeMoment;
    private StructurePlaceAnimator animator;

    /**
     * define the step at which should the multi-chunk feature be placed.
     */
    private WGShapeData shapeData;

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, BlockPos center) {
        this(world, placeMoment, center, Identifier.of("unknown_mod:custom_shape" + world.getRandom().nextLong()));
    }

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, BlockPos center, Identifier featureName) {
        this.featureName = featureName.withPath(featureName.getPath() + "_" + world.getRandom().nextLong());
        this.world = world;
        this.placeMoment = placeMoment;
        this.center = center;
    }

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, WGShapeData shapeData, BlockPos center) {
        this(world, placeMoment, shapeData, center, Identifier.of("unknown_mod:custom_shape" + world.getRandom().nextLong()));
    }

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, WGShapeData shapeData, BlockPos center, Identifier featureName) {
        this.featureName = featureName.withPath(featureName.getPath() + "_" + world.getRandom().nextLong());
        this.world = world;
        this.placeMoment = placeMoment;
        this.shapeData = shapeData;
        this.center = center;
    }


    public Identifier getFeatureName() {
        return featureName;
    }


    public PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    public void setShapeData(WGShapeData shapeData) {
        this.shapeData = shapeData;
    }

    /**
     * Place the shape from a blockListManager. The place moment {@link PlaceMoment#WORLD_GEN} should not be used. Use instead {@link ShapePlacer#place(DividedBlockListManager)}.
     *
     * @param defaultManager the manager that will be placed
     */
    public void place(BlockListManager defaultManager) {
        switch (placeMoment) {
            case WORLD_GEN -> {
                Ewc.LOGGER.error("cannot place structure during world generation using BlockListManager. Please use DividedBlockListManager.");
                IllegalStateException exception = new IllegalStateException();
                exception.fillInStackTrace();
                throw exception;
                //Ewc.LOGGER.info(ExceptionUtils.getStackTrace(exception));
            }
            case ANIMATED_OTHER -> {
                if (animator == null) {
                    animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
                }
                animator.place(defaultManager);
            }
            case OTHER -> defaultManager.placeAll(world);
        }
    }

    public void place(Map<ChunkPos, LongOpenHashSet> posLit, LayerManager manager) {
        ChunkPosManager chunkPosManager = new ChunkPosManager(world);
        if (placeMoment == PlaceMoment.WORLD_GEN &&
                chunkPosManager.isMultiChunk(posLit.keySet(), center)) {

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

            if (!chunkPosManager.canPlaceMultiChunk(posLit.keySet(), 8))
                return;

            if (shapeData == null)
                shapeData = WGShapeData.ofStep(GenerationStep.Feature.VEGETAL_DECORATION, this.featureName.getNamespace() + "-" + this.featureName.getPath());

            WGShapeHandler.encodeInformations(world, posLit.keySet(), shapeData, (chunkPosManager.getOffset()));

            for (Map.Entry<ChunkPos, LongOpenHashSet> posSet : posLit.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> manager.get(posSet.getValue())
                        .placeJson(world, posSet.getKey(), chunkPosManager.getOffset(), this.featureName.getNamespace() + "-" + this.featureName.getPath()), pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            placeWorldGenFiles();

            MultiChunkFeaturesHandler.add(world, posLit.keySet(), this.featureName);

        } else if (placeMoment == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            }
            animator.place(manager.get(posLit));
        } else {
            manager.get(posLit).placeAll(world);
        }
    }


    public void place(DividedBlockListManager manager) {
        ChunkPosManager chunkPosManager = new ChunkPosManager(world);
        if (placeMoment == PlaceMoment.WORLD_GEN &&
                chunkPosManager.isMultiChunk(manager.getChunkPos(), center)) {


            if (!chunkPosManager.canPlaceMultiChunk(manager.getChunkPos(), 8)) {
                return;
            }

            if (shapeData == null)
                shapeData = WGShapeData.ofStep(GenerationStep.Feature.VEGETAL_DECORATION, this.featureName.getNamespace() + "-" + this.featureName.getPath());

            WGShapeHandler.encodeInformations(world, manager.getChunkPos(), shapeData, chunkPosManager.getOffset());

            manager.placeJson(world, this.featureName.getNamespace() + "-" + this.featureName.getPath(), chunkPosManager.getOffset());

            placeWorldGenFiles();

            MultiChunkFeaturesHandler.add(world, manager.getChunkPos(), this.featureName);

        } else if (placeMoment == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            }
            animator.place(manager.getOrdered());
        } else {
            manager.placeAll(world);
        }
    }

    private void placeWorldGenFiles() {
        List<Path> path = LoadChunkShapeInfo.getWorldGenFiles(world, this.center);
        for (Path path1 : path) {
            world.setCurrentlyGeneratingStructureName(() -> "ewc multi-chunk feature generating: " + path1.getFileName());
            BlockListManager manager = BlockListHelper.fromJsonPath(world, path1);
            if (manager != null)
                manager.placeAllNDelete(world);
        }
    }

    /**
     * Enum that define the moment of structure placing.
     * You have to be sure that what you choose is good, or you might run into issues
     */
    public enum PlaceMoment {
        /**
         * Place the structure during world generation.
         * If the structure is bigger than 3x3 chunks,
         * the structure will be cut into chunks and will use multi-chunk features.
         */
        WORLD_GEN,
        /**
         * animate the shape placement
         */
        ANIMATED_OTHER,
        /**
         * Will place all the structure at once. This should not be used during world gen.
         */
        OTHER
    }
}
