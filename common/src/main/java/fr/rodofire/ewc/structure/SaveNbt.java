package fr.rodofire.ewc.structure;

import com.google.common.collect.Lists;
import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.blocklist.BlockList;
import fr.rodofire.ewc.mixin.world.structure.PalettedBlockInfoListMixin;
import fr.rodofire.ewc.mixin.world.structure.StructureTemplateMixin;
import fr.rodofire.ewc.util.LongPosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SaveNbt {

    /**
     * This method converts the List of BlockList to a nbt file.
     * <p>
     * That is a way to generate a really large structure.
     * Since that the nbt is saved during world gen, no block entity should be present.
     * </p>
     * <p>
     * The methods receive a list of blockList.
     * The list represents all the blocks of the generated structure.
     * For every BlockPos and BlockStates, the method verify the chunk it belongs to and add it to the Map chunkBlockInfoMap.
     * This divides the structure into chunks that will be saved just after converting the first list into a {@link StructureTemplate.Palette}
     * The Structure will be located in the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long]
     * </p>
     *
     * @param defaultBlockLists a list of BlockList to save it into the nbt file
     */
    @SuppressWarnings("UnreachableCode")
    public static void saveNbtDuringWorldGen(WorldGenLevel world, List<BlockList> defaultBlockLists, String featureName) {
        Map<ChunkPos, List<StructureTemplate.StructureBlockInfo>> chunkBlockInfoMap = new HashMap<>();

        List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> list2 = Lists.newArrayList();
        List<StructureTemplate.StructureBlockInfo> list3 = Lists.newArrayList();

        for (BlockList blocks : defaultBlockLists) {
            BlockState blockState = blocks.getState();
            for (long pos : blocks.getPosList()) {
                StructureTemplate.StructureBlockInfo structureBlockInfo = new StructureTemplate.StructureBlockInfo(LongPosHelper.decodeBlockPos(pos), blockState, null);
                categorize(structureBlockInfo, list, list2, list3);

                ChunkPos chunkPos = new ChunkPos(pos);

                chunkBlockInfoMap.computeIfAbsent(chunkPos, k -> new ArrayList<>()).add(structureBlockInfo);
            }
        }
        MinecraftServer server = world.getServer();
        if (server == null) return;

        StructureTemplateManager structureTemplateManager = server.getStructureManager();

        for (Map.Entry<ChunkPos, List<StructureTemplate.StructureBlockInfo>> entry : chunkBlockInfoMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            List<StructureTemplate.StructureBlockInfo> blockInfos = entry.getValue();

            List<StructureTemplate.StructureBlockInfo> combinedList = StructureTemplateMixin.invokeCombineSorted(list, list2, list3);

            List<StructureTemplate.Palette> blockInfoLists = new ArrayList<>();
            StructureTemplate.Palette palettedBlockInfoList = createPalettedBlockInfoList(combinedList);
            blockInfoLists.add(palettedBlockInfoList);


            ResourceLocation templateName = ResourceLocation.fromNamespaceAndPath(EwcConstants.MOD_ID, chunkPos.x + "-" + chunkPos.z + "/" + featureName);

            StructureTemplate structureTemplate;

            try {
                structureTemplate = structureTemplateManager.getOrCreate(templateName);
                ((StructureTemplateMixin) structureTemplate).getPalettes().clear();
                ((StructureTemplateMixin) structureTemplate).getPalettes().addAll(blockInfoLists);

                structureTemplateManager.save(templateName);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }


    private static void categorize(
            StructureTemplate.StructureBlockInfo blockInfo,
            List<StructureTemplate.StructureBlockInfo> fullBlocks,
            List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
            List<StructureTemplate.StructureBlockInfo> otherBlocks
    ) {
        if (blockInfo.nbt() != null) {
            blocksWithNbt.add(blockInfo);
        } else if (!blockInfo.state().getBlock().hasDynamicShape() && blockInfo.state().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            fullBlocks.add(blockInfo);
        } else {
            otherBlocks.add(blockInfo);
        }
    }


    /**
     * when you want, you can remove the file
     *
     * @param nbtList the list of the ResourceLocation related to every structure that has to be removed
     */
    public static void removeNbtFiles(List<ResourceLocation> nbtList) {
        for (ResourceLocation nbt : nbtList) {
            try {
                Path filePath = Path.of(nbt.getPath());
                Files.delete(filePath);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }


    public static List<ResourceLocation> loadNBTFiles(ChunkAccess chunk) {
        return loadNBTFiles(chunk.getPos());
    }

    /**
     * gives you a list of "path" of nbt files related to a chunkPos
     *
     * @param chunk the chunk of the nbt
     * @return the list of "path"
     */
    public static List<ResourceLocation> loadNBTFiles(ChunkPos chunk) {
        List<ResourceLocation> nbtList = new ArrayList<>();

        String chunkFolderPath = ResourceLocation.fromNamespaceAndPath(EwcConstants.MOD_ID, "generated/structures/" + chunk.x + "_" + chunk.z + "/").getPath();

        try {
            Path path = Path.of(chunkFolderPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                try (Stream<Path> files = Files.list(path)) {
                    files.forEach(filePath -> {
                        if (filePath.toString().endsWith(".nbt")) {

                            nbtList.add(ResourceLocation.fromNamespaceAndPath(EwcConstants.MOD_ID, chunk.x + "_" + chunk.z + "/" + filePath.getFileName().toString()));

                        }
                    });
                }
            }

        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return nbtList;
    }

    /**
     * avoid errors when using the mixin
     *
     * @param combinedList the list that will be converted to {@link StructureTemplate.Palette}
     * @return the {@link StructureTemplate.Palette} related to the list
     */
    @Unique
    public static StructureTemplate.Palette createPalettedBlockInfoList(List<StructureTemplate.StructureBlockInfo> combinedList) {
        return PalettedBlockInfoListMixin.invokeConstructor(combinedList);
    }

}
