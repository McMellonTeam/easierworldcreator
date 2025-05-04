package fr.rodofire.ewc.shape.block.layer;

import fr.rodofire.ewc.blockdata.WorldStateCollector;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayer;
import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.util.LongPosHelper;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

//TODO test to see if everything works
public abstract class AbstractRadialLikeLayer extends AbstractLayer {
    AbstractRadialLikeLayer(BlockLayerManager blockLayer, Vec3 center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    AbstractRadialLikeLayer(BlockLayerManager blockLayer, Vec3 center) {
        super(blockLayer, center);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        for (LongOpenHashSet set : posMap.values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set) {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                    manager1.put(state, pos, layer.getRuler());
                }
                synchronized (manager) {
                    manager.put(manager1);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public void place(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
            }
        }
    }

    @Override
    public BlockListManager getVerified(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            for (LongOpenHashSet posList : posMap.values()) {
                worldStates.collect(world1, posList);
            }
            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

            for (LongOpenHashSet set : posMap.values()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager manager1 = new BlockListManager();
                    for (long pos : set) {
                        float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                        int index = findLayerIndex(layerDistance, distance);
                        BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                        if (!layer.getRuler().canPlace(worldStates.getState(pos)))
                            continue;
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        manager1.put(state, pos);
                    }
                    synchronized (manager) {
                        manager.put(manager1);
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
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set.getValue()) {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                    manager1.put(state, pos, layer.getRuler());
                }
                synchronized (manager) {
                    manager.putWithoutVerification(set.getKey(), manager1);
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
            for (LongOpenHashSet posList : posMap.values()) {
                worldStates.collect(world1, posList);
            }

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

            for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager manager1 = new BlockListManager();
                    for (long pos : set.getValue()) {
                        float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                        int index = findLayerIndex(layerDistance, distance);
                        BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                        if (!layer.getRuler().canPlace(worldStates.getState(pos)))
                            continue;

                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        manager1.put(state, pos);
                    }
                    synchronized (manager) {
                        manager.putWithoutVerification(set.getKey(), manager1);
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
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos, layer.getRuler());
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(WorldGenLevel world, T posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (BlockPos pos : posList) {
            float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), pos, layer.getRuler());
        }
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(WorldGenLevel world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


            for (BlockPos pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos)))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                        synchronized (manager) {
                            manager.put(state, pos, layer.getRuler());
                        }
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
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos, layer.getRuler());
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


            for (BlockPos pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos)))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
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
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos, layer.getRuler());
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(WorldGenLevel world, U posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (long pos : posList) {
            float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
        }

    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(WorldGenLevel world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

            for (long pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(pos))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
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
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos, layer.getRuler());
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerLevel world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

            for (long pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(pos))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
                    }
                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();
            return manager;
        }
        return null;
    }

    protected abstract float getDistance(int[] pos);

    protected abstract float getDistance(int posX, int posY, int posZ);

    protected abstract int findLayerIndex(int[] layerDistance, float distance);

}
