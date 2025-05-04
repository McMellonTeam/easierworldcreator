package fr.rodofire.ewc.mixin.world.structure;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

/**
 * mixin used for nbt file generation
 */
@Mixin(StructureTemplate.class)
@SuppressWarnings("unused")
public interface StructureTemplateMixin {
    @Accessor("entityInfoList")
    void setBlockInfoLists(List<StructureTemplate.Palette> blockInfoLists);

    /**
     * getter mixin for nbt generation files
     * @return the {@code List<StructureTemplate.PalettedBlockInfoList>} of the class
     */
    @Accessor
    List<StructureTemplate.Palette> getPalettes();

    /**
     * mixin for nbt generation files to combine and sort th parameters
     * @return the {@code List<StructureTemplate.PalettedBlockInfoList>} combined and sorted
     */
    @Invoker("buildInfoList")
    static List<StructureTemplate.StructureBlockInfo> invokeCombineSorted(List<StructureTemplate.StructureBlockInfo> fullBlocks,
                                                                          List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
                                                                          List<StructureTemplate.StructureBlockInfo> otherBlocks) {
        throw new AssertionError();
    }
}
