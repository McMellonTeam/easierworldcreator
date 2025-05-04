package fr.rodofire.ewc.shape.block.placer;

import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.blocklist.BlockListHelper;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.sorter.BlockSorter;
import fr.rodofire.ewc.shape.block.layer.LayerManager;
import fr.rodofire.ewc.shape.block.placer.animator.StructurePlaceAnimator;
import fr.rodofire.ewc.util.file.LoadChunkShapeInfo;
import fr.rodofire.ewc.world.chunk.ChunkPosManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.GenerationStep;

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
    WorldGenLevel world;
    BlockPos center;

    /**
     * for better debugging, the ResourceLocation is used to know which feature is being generated
     */
    private ResourceLocation featureName;
    private final PlaceMoment placeMoment;
    private StructurePlaceAnimator animator;

    /**
     * define the step at which should the multi-chunk feature be placed.
     */
    private WGShapeData shapeData;

    public ShapePlacer(WorldGenLevel world, PlaceMoment placeMoment, BlockPos center) {
        this(world, placeMoment, center, ResourceLocation.tryParse("unknown_mod:custom_shape" + world.getRandom().nextLong()));
    }

    public ShapePlacer(WorldGenLevel world, PlaceMoment placeMoment, BlockPos center, ResourceLocation featureName) {
        this.featureName = featureName.withPath(featureName.getPath() + "_" + world.getRandom().nextLong());
        this.world = world;
        this.placeMoment = placeMoment;
        this.center = center;
    }

    public ShapePlacer(WorldGenLevel world, PlaceMoment placeMoment, WGShapeData shapeData, BlockPos center) {
        this(world, placeMoment, shapeData, center, ResourceLocation.tryParse("unknown_mod:custom_shape" + world.getRandom().nextLong()));
    }

    public ShapePlacer(WorldGenLevel world, PlaceMoment placeMoment, WGShapeData shapeData, BlockPos center, ResourceLocation featureName) {
        this.featureName = featureName.withPath(featureName.getPath() + "_" + world.getRandom().nextLong());
        this.world = world;
        this.placeMoment = placeMoment;
        this.shapeData = shapeData;
        this.center = center;
    }


    public ResourceLocation getFeatureName() {
        return featureName;
    }

    public void setFeatureName(ResourceLocation featureName) {
        this.featureName = featureName;
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
                EwcConstants.LOGGER.error("cannot place structure during world generation using BlockListManager. Please use DividedBlockListManager.");
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
                shapeData = WGShapeData.ofStep(GenerationStep.Decoration.VEGETAL_DECORATION, this.featureName.getNamespace() + "-" + this.featureName.getPath());

            WGShapeHandler.encodeInformations(world, posLit.keySet(), shapeData, (chunkPosManager.getOffset()));

            for (Map.Entry<ChunkPos, LongOpenHashSet> posSet : posLit.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> manager.get(posSet.getValue())
                        .placeJson(world, posSet.getKey(), chunkPosManager.getOffset(), this.featureName.getNamespace() + "-" + this.featureName.getPath()), pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            placeWorldGenFiles();

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
                shapeData = WGShapeData.ofStep(GenerationStep.Decoration.VEGETAL_DECORATION, this.featureName.getNamespace() + "-" + this.featureName.getPath());

            WGShapeHandler.encodeInformations(world, manager.getChunkPos(), shapeData, chunkPosManager.getOffset());

            manager.placeJson(world, this.featureName.getNamespace() + "-" + this.featureName.getPath(), chunkPosManager.getOffset());

            placeWorldGenFiles();

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
            world.setCurrentlyGenerating(() -> "ewc multi-chunk feature generating: " + path1.getFileName());
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
