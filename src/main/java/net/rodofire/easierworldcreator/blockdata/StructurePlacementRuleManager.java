package net.rodofire.easierworldcreator.blockdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.tag.TagUtil;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to manage if blocks can be placed or not
 */
@SuppressWarnings("unused")
public class StructurePlacementRuleManager {
    public static final Codec<StructurePlacementRuleManager> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.fieldOf("force").forGetter(ruler -> ruler.force),
            Codec.list(Block.CODEC.codec()).fieldOf("overridens_blocks").forGetter(ruler -> ruler.overriddenBlocks.stream().toList())
    ).apply(instance, StructurePlacementRuleManager::new));

    /**
     * Define if the blocks can be forced. Ex: determine if it can replace a stone block. If true, the stone block will be replaced, else not.
     */
    private boolean force = false;

    /**
     * Define which blocks can deviate from the rule defined by {@code force}
     * <p>
     * meaning that if a block is not supposed to be replaced,
     * but it is present in the Set, it will be replaced anyway.
     * <p>
     * Work the other way around: if a block is supposed to be replaced because {@code force = true},
     * but it is present in the Set, it will not be replaced.
     */
    private Set<Block> overriddenBlocks = new HashSet<>();

    public StructurePlacementRuleManager() {
    }

    public StructurePlacementRuleManager(Set<Block> overridenBlocks) {
        this.overriddenBlocks = new HashSet<>(overridenBlocks);
    }

    public StructurePlacementRuleManager(Set<Block> overriddenBlocks, Set<TagKey<Block>> overriddenTags) {
        this.overriddenBlocks = new HashSet<>(overriddenBlocks);
        addTagKeys(overriddenTags);
    }

    public StructurePlacementRuleManager(boolean force) {
        this.force = force;
    }

    public StructurePlacementRuleManager(boolean force, Set<Block> overriddenBlocks) {
        this.force = force;
        this.overriddenBlocks = new HashSet<>(overriddenBlocks);
    }

    public StructurePlacementRuleManager(boolean force, Set<Block> overriddenBlocks, Set<TagKey<Block>> overriddenTags) {
        this.force = force;
        this.overriddenBlocks = new HashSet<>(overriddenBlocks);
        addTagKeys(overriddenTags);
    }

    public StructurePlacementRuleManager(Boolean aBoolean, List<Block> blocks) {
        this.force = aBoolean;
        this.overriddenBlocks = new HashSet<>(blocks);
    }

    public Set<Block> getOverriddenBlocks() {
        return overriddenBlocks;
    }

    public void setOverriddenBlocks(Set<Block> overriddenBlocks) {
        this.overriddenBlocks = new HashSet<>(overriddenBlocks);
    }

    public void setOverriddenTags(Set<TagKey<Block>> overriddenTags) {
        this.overriddenBlocks = TagUtil.BlockTags.convertBlockTagToBlockSet(overriddenTags);
    }

    public void addOverrideBlock(Block block) {
        overriddenBlocks.add(block);
    }

    public void addTagKey(TagKey<Block> tagKey) {
        this.overriddenBlocks.addAll(TagUtil.BlockTags.convertBlockTagToBlockSet(tagKey));
    }

    public void addOverrideBlocks(Set<Block> overriddenBlocks) {
        this.overriddenBlocks.addAll(overriddenBlocks);
    }

    public void addTagKeys(Set<TagKey<Block>> tagKeys) {
        this.overriddenBlocks.addAll(TagUtil.BlockTags.convertBlockTagToBlockSet(tagKeys));
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean canPlace(StructureWorldAccess worldAccess, BlockPos pos) {
        BlockState state = worldAccess.getBlockState(pos);
        return canPlace(state);
    }

    public boolean canPlace(StructureWorldAccess worldAccess, long pos) {
        BlockState state = worldAccess.getBlockState(LongPosHelper.decodeBlockPos(pos));
        return canPlace(state);
    }

    /**
     * method to know if a {@link BlockState} is allowed to be replaced
     * @param state the state that will be tested
     * @return true if it is possible, false else
     */
    public boolean canPlace(BlockState state) {
        if (force) {
            return !overriddenBlocks.contains(state.getBlock());
        }
        return state.isAir() || overriddenBlocks.contains(state.getBlock());
    }


    @Override
    public String toString() {
        return "StructurePlacementRuleManager{" +
                "force=" + force +
                ", overriddenBlocks=" + overriddenBlocks.parallelStream()
                .map(Block::toString)
                .collect(Collectors.joining(", ", "[", "]")) +
                '}';
    }
}
