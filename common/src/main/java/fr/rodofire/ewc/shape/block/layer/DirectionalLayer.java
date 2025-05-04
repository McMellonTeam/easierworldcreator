package fr.rodofire.ewc.shape.block.layer;

import fr.rodofire.ewc.blockdata.WorldStateCollector;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayer;
import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.WorldGenUtil;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

//TODO need test

public class DirectionalLayer extends AbstractLayer {
    DirectionalLayer(BlockLayerManager blockLayer, Vec3 center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager threadedManager = new BlockListManager();
                for (long po : entry.getValue()) {
                    double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).getCenter()) / distanceMin;

                    BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                    threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po, layer.getRuler());
                }
                synchronized (manager) {
                    manager.put(threadedManager);
                }

            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();

        return manager;
    }

    @Override
    public void place(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {

    }

    @Override
    public BlockListManager getVerified(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            for (LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }
            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    for (long po : entry.getValue()) {
                        double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).getCenter()) / distanceMin;

                        BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                        if (layer.getRuler().canPlace(worldStates.getState(po)))
                            threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                    }
                    synchronized (manager) {
                        manager.put(threadedManager);
                    }

                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();

            return manager;
        }
        return null;
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager threadedManager = new BlockListManager();
                for (long po : entry.getValue()) {
                    double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).getCenter()) / distanceMin;

                    BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                    threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po, layer.getRuler());
                }
                synchronized (manager) {
                    manager.putWithoutVerification(threadedManager);
                }

            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();

        return manager;
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            for (LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }
            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    for (long po : entry.getValue()) {
                        double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).getCenter()) / distanceMin;

                        BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                        if (layer.getRuler().canPlace(worldStates.getState(po)))
                            threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                    }
                    synchronized (manager) {
                        manager.putWithoutVerification(threadedManager);
                    }

                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();

            return manager;
        }
        return null;
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (BlockPos pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.getCenter()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));
            manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos, layer.getRuler());
        }
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(WorldGenLevel world, T posList) {

    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(WorldGenLevel world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (BlockPos pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.getCenter()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (BlockPos pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.getCenter()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));

            manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos, layer.getRuler());
        }
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);
            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (BlockPos pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.getCenter()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (long pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).getCenter()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));
            manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos, layer.getRuler());
        }
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(WorldGenLevel world, U posList) {

    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(WorldGenLevel world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (long pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).getCenter()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (long pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).getCenter()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));

            manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos, layer.getRuler());
        }
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (long pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).getCenter()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }
            return manager;
        }
        return null;
    }

    private int @NotNull [] initDepth() {
        int[] depth = new int[blockLayer.size()];
        depth[0] = blockLayer.get(0).getDepth();

        for (int i = 1; i < blockLayer.size(); i++) {
            depth[i] = blockLayer.get(i).getDepth() + depth[i - 1];
        }
        return depth;
    }

    private int binarySearch(int[] depth, double distance) {
        int left = 0;
        int right = depth.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            double diff = Math.abs(depth[mid] - distance);

            if (diff < 1.0E-6) {
                return mid;
            } else if (depth[mid] < distance) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }
}
