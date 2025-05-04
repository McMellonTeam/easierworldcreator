package fr.rodofire.ewc.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.sorter.BlockSorter;
import fr.rodofire.ewc.util.BlockStateUtil;
import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.file.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class BlockListHelper {
    //TODO use divide to reign for better performance

    /**
     * method to combine a number of {@code List<BlockList>} superior to 2
     *
     * @param lists the list to combine
     * @return a {@code List<BlockList>} that correspond to the combined List
     */
    @SafeVarargs
    public static List<BlockList> combineNBlockList(List<BlockList>... lists) {
        if (lists.length == 0) {
            return new ArrayList<>();
        }
        if (lists.length == 1) {
            return new ArrayList<>(lists[0]);
        }
        return Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<BlockList> result = new ArrayList<>(list1);
                    combine2BlockList(result, list2);
                    return result;
                });
    }


    /**
     * method to sort a BlockList
     *
     * @param blockList the blockList that will be sorted
     * @param sorter    the sorter object that will be used to get the sorted BlockPos
     * @return the sorted BlockList
     */
    public static BlockListManager getSorted(List<BlockList> blockList, BlockSorter sorter) {
        BlockListManager sortedList = new BlockListManager(blockList);
        sorter.sortInsideBlockList(sortedList);
        return sortedList;
    }

    /**
     * method to combine 2 {@code List<BlockList>}
     *
     * @param defaultBlockList1 the first list that will contain the modifications
     * @param defaultBlockList2 the second list that will get merged
     */
    public static void combine2BlockList(List<BlockList> defaultBlockList1, List<BlockList> defaultBlockList2) {
        Map<BlockState, Integer> blockStateIndexMap = new HashMap<>();
        int i = 0;
        for (BlockList list : defaultBlockList1) {
            blockStateIndexMap.put(list.getState(), i++);
        }
        for (BlockList list : defaultBlockList2) {
            BlockState state = list.getState();
            if (blockStateIndexMap.containsKey(state)) {
                int index = blockStateIndexMap.get(state);
                defaultBlockList1.get(index).addAllPos(list.getPosList());
            } else {
                defaultBlockList1.add(list);
            }

        }
    }

    public static BlockListManager fromJsonPath(WorldGenLevel world, Path path) {
        String fileName = path.getParent().getFileName().toString();
        Pattern pattern = Pattern.compile("chunk_(-?\\d+)_(-?\\d+)$");
        Matcher matcher = pattern.matcher(fileName);
        int chunkX;
        int chunkZ;

        if (matcher.matches()) {
            chunkX = Integer.parseInt(matcher.group(1));
            chunkZ = Integer.parseInt(matcher.group(2));
        }
        //initialize in the else because if not, intellij cries
        else {
            chunkZ = 0;
            chunkX = 0;
        }

        if (path.toString().endsWith(".json")) {
            JsonArray jsonArray = new Gson().fromJson(FileUtil.loadJson(path), JsonArray.class);
            if(jsonArray == null)
                return null;
            return BlockListHelper.fromJson(world, jsonArray, new ChunkPos(chunkX, chunkZ));
        }
        return null;
    }

    public static BlockListManager fromJson(WorldGenLevel worldAccess, JsonArray jsonArray, ChunkPos chunkPos) {
        BlockListManager manager = new BlockListManager();
        Gson gson = new Gson();

        for (JsonElement jsonElement : jsonArray) {
            BlockList blockList = new BlockList();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            BlockState state = BlockStateUtil.parseBlockState(worldAccess, jsonObject.get("state").getAsString());

            blockList.setState(state);


            if (jsonObject.has("force")) {
                boolean force = jsonObject.get("force").getAsBoolean();
                blockList.ruler.setForce(force);
            }
            if (jsonObject.has("overriddenBlock")) {
                Set<Block> overriddenBlocks = new HashSet<>();

                JsonArray overrideArray = jsonObject.getAsJsonArray("overriddenBlock");

                for (JsonElement element : overrideArray) {
                    String blockId = element.getAsString();
                    Block block = BlockStateUtil.parseBlock(worldAccess, blockId);
                    overriddenBlocks.add(block);
                }
                blockList.ruler.setOverriddenBlocks(overriddenBlocks);
            }

            // Récupération du tag (optionnel)
            if (jsonObject.has("tag")) {
                String tagString = jsonObject.get("tag").getAsString();
                try {
                    CompoundTag tag = TagParser.parseTag(tagString);
                    blockList.setTag(tag);
                } catch (Exception e) {
                    EwcConstants.LOGGER.info("cannot parse NbtCompound");
                    e.fillInStackTrace();
                }
            }

            JsonArray positionsArray = jsonObject.getAsJsonArray("positions");
            int[] compactPositions = gson.fromJson(positionsArray, int[].class);

            int chunkMinX = chunkPos.x << 4;
            int chunkMinZ = chunkPos.z << 4;

            for (int compactPos : compactPositions) {
                int relX = ((compactPos >> 21) & 0x7FF);
                int relY = ((compactPos >> 11) & 0x3FF) - 512;
                int relZ = (compactPos & 0x7FF);

                // Ajustement pour les valeurs signées
                if (relX >= 1024) relX -= 2048;
                if (relZ >= 1024) relZ -= 2048;

                int x = relX + chunkMinX;
                int y = relY;
                int z = relZ + chunkMinZ;

                blockList.addPos(LongPosHelper.encodeBlockPos(x, y, z));
            }
            manager.put(blockList);

        }
        return manager;
    }
}
