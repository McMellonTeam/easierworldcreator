package fr.rodofire.ewc.blockdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * class to store objects that represents a block
 */
public class BlockDataKey {
    public static final Codec<BlockDataKey> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockState.CODEC.fieldOf("state").forGetter(bdk -> bdk.state),
                    CompoundTag.CODEC.optionalFieldOf("tag").forGetter(bdk -> Optional.ofNullable(bdk.tag))
            ).apply(instance, (state, tagOpt) -> new BlockDataKey(state, tagOpt.orElse(null)))
    );

    private BlockState state;
    private CompoundTag tag;

    public BlockDataKey(BlockState state) {
        this(state, null);
    }

    public BlockDataKey(BlockState state, @Nullable CompoundTag tag) {
        this.state = state;
        this.tag = tag;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BlockDataKey that = (BlockDataKey) obj;
        return Objects.equals(state, that.state) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, tag);
    }
}
