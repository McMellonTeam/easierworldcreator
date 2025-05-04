package fr.rodofire.ewc.util;


import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.OrderedBlockListManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class BlockStateUtil {
    /**
     * method used to convert {@link String} to BlockState
     *
     * @param world       used to get the registry entry
     * @param stateString the {@link String} related to the{@link BlockState}
     * @return the {@link BlockState} converted
     */
    public static BlockState parseBlockState(WorldGenLevel world, String stateString) {
        HolderGetter<Block> blockLookup = world.holderLookup(Registries.BLOCK);

        //ResourceLocation ResourceLocation = new ResourceLocation(stateString.split("\\[")[0]);
        ResourceLocation resource = ResourceLocation.parse(extractBlockName(stateString.split("\\[")[0]));
        Optional<Holder.Reference<Block>> optional = blockLookup.get(ResourceKey.create(Registries.BLOCK, resource));
        if (optional.isEmpty()) {
            EwcConstants.LOGGER.error("error parsing BlockState: {}", stateString.split("\\[")[0]);
            return Blocks.AIR.defaultBlockState();
        }

        Block block = (optional.get()).value();
        BlockState blockState = block.defaultBlockState();

        if (stateString.contains("[")) {
            String propertiesPart = stateString.split("\\[")[1].replace("]", "");
            String[] keyValuePairs = propertiesPart.split(",");

            StateDefinition<Block, BlockState> stateManager = block.getStateDefinition();

            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split("=");
                Property<?> property = stateManager.getProperty(keyValue[0]);
                if (property != null) {
                    blockState = applyProperty(blockState, property, keyValue[1]);
                }
            }
        }

        return blockState;
    }

    /**
     * methods to apply the property to a BlockState
     *
     * @param state    the previous states of the {@link BlockState}
     * @param property the property related
     * @param value    the value of the property
     * @param <T>      the type of the property value must be Comparable
     * @return the changed {@link BlockState}
     */
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        T propertyValue = property.getValue(value).orElseThrow(() -> new IllegalArgumentException("Invalid property value"));
        return state.setValue(property, propertyValue);
    }

    /**
     * method to remove the {@code Block{}} to only get the {@link String} related to the block {@link ResourceLocation}
     *
     * @param blockString the {@link String} that needs to be separated
     * @return the String related to the {@link Block}
     */
    private static String extractBlockName(String blockString) {
        if (blockString.startsWith("Block{") && blockString.endsWith("}")) {
            return blockString.substring(6, blockString.length() - 1);
        }
        throw new IllegalArgumentException("Invalid block string format: " + blockString);
    }

    static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * method to get all blockStates from the world from a list of BlockPos
     *
     * @param posList       the list of blockPos that will be used to get the blockStates
     * @param blockStateMap the map that will be modified
     * @param world         the world where the blockStates will be collected
     */
    public static void getBlockStatesFromWorld(List<Set<BlockPos>> posList, Map<BlockPos, BlockState> blockStateMap, WorldGenLevel world) {
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

    public static Block parseBlock(WorldGenLevel world, String blockString) {
        HolderGetter<Block> blockLookup = world.holderLookup(Registries.BLOCK);

        //ResourceLocation ResourceLocation = new ResourceLocation(stateString.split("\\[")[0]);
        ResourceLocation resource = ResourceLocation.parse(extractBlockName(blockString.split("\\[")[0]));
        Optional<Holder.Reference<Block>> optional = blockLookup.get(ResourceKey.create(Registries.BLOCK, resource));
        if (optional.isEmpty()) {
            EwcConstants.LOGGER.error("error parsing BlockState: {}", blockString.split("\\[")[0]);
            return Blocks.AIR;
        }

        return optional.get().value();
    }

    public static BlockListManager getCompoundBlockStatesFromWorld(List<Set<BlockPos>> posList, WorldGenLevel world) {
        BlockListManager comparator = new BlockListManager();
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                BlockState blockState = world.getBlockState(blockPos);
                BlockEntity entity = world.getBlockEntity(blockPos);
                if (entity != null) {
                    RegistryAccess registry = world.registryAccess();
                    CompoundTag nbtCompound = entity.saveWithFullMetadata(registry);
                    comparator.put(blockState, nbtCompound, blockPos);
                } else {
                    comparator.put(blockState, null, blockPos);
                }
            }
        }
        return comparator;
    }

    public static OrderedBlockListManager getBlockStatesFromWorld(List<Set<BlockPos>> posList, WorldGenLevel world) {
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
