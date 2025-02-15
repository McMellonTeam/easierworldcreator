package net.rodofire.easierworldcreator.placer.blocks.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.OrderedBlockListManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Util class related to BlockStates
 */
@SuppressWarnings("unused")
public class BlockStateUtil {
    static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * method to get all blockStates from the world from a list of BlockPos
     *
     * @param posList       the list of blockPos that will be used to get the blockStates
     * @param blockStateMap the map that will be modified
     * @param world         the world where the blockStates will be collected
     */
    public static void getBlockStatesFromWorld(List<Set<BlockPos>> posList, Map<BlockPos, BlockState> blockStateMap, StructureWorldAccess world) {
        Map<BlockPos, BlockState> concurrentMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (Set<BlockPos> set : posList) {
            //executorService.submit(() -> {
            for (BlockPos blockPos : set) {
                blockStateMap.put(blockPos, world.getBlockState(blockPos));
            }
            //});
        }

        executorService.shutdown();
        //blockStateMap.putAll(concurrentMap);
    }

    public static BlockListManager getCompoundBlockStatesFromWorld(List<Set<BlockPos>> posList, StructureWorldAccess world) {
        BlockListManager comparator = new BlockListManager();
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                BlockState blockState = world.getBlockState(blockPos);
                BlockEntity entity = world.getBlockEntity(blockPos);
                if (entity != null) {
                    DynamicRegistryManager registry = world.getRegistryManager();
                    NbtCompound nbtCompound = entity.createNbtWithIdentifyingData(registry);
                    comparator.put(blockState, nbtCompound, blockPos);
                } else {
                    comparator.put(blockState, null, blockPos);
                }
            }
        }
        return comparator;
    }

    public static OrderedBlockListManager getBlockStatesFromWorld(List<Set<BlockPos>> posList, StructureWorldAccess world) {
        OrderedBlockListManager comparator = new OrderedBlockListManager();
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                BlockState blockState = world.getBlockState(blockPos);
                comparator.put(blockState, blockPos);
            }
        }
        return comparator;
    }
}
