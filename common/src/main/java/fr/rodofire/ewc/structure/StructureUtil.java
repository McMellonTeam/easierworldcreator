package fr.rodofire.ewc.structure;

import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.blockdata.blocklist.BlockList;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.mixin.world.structure.StructureTemplateMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.*;

@SuppressWarnings("unused")
public class StructureUtil {
    public static void convertNbtToManager(StructureTemplate structureTemplate, BlockListManager manager, StructurePlaceSettings data, WorldGenLevel world, BlockPos pos) {
        List<StructureTemplate.StructureBlockInfo> list = data.getRandomPalette(((StructureTemplateMixin) structureTemplate).getPalettes(), pos).blocks();
        List<StructureTemplate.StructureBlockInfo> blockInfoList = StructureTemplate.processBlockInfos(world, pos, new BlockPos(0, 0, 0), data, list);

        Map<Pair<BlockState, CompoundTag>, List<BlockPos>> blockStateToPositionsMap = new HashMap<>();
        for (StructureTemplate.StructureBlockInfo blockInfo : blockInfoList) {
            BlockState blockState = blockInfo.state();
            BlockPos blockPos = blockInfo.pos();
            CompoundTag tag = blockInfo.nbt();

            Pair<BlockState, CompoundTag> stateAndTagPair = new Pair<>(blockState, tag);

            blockStateToPositionsMap.computeIfAbsent(stateAndTagPair, k -> new ArrayList<>()).add(blockPos);
        }

        for (Map.Entry<Pair<BlockState, CompoundTag>, List<BlockPos>> entry : blockStateToPositionsMap.entrySet()) {
            BlockState blockState = entry.getKey().getFirst();
            CompoundTag tag = entry.getKey().getSecond();
            List<BlockPos> positions = entry.getValue();

            manager.put(new BlockList(blockState, tag, positions));
        }
    }

}
