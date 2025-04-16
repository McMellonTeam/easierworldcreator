package net.rodofire.easierworldcreator.blockdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * class to store objects that represents a block
 */
public class BlockDataKey {
    public static final Codec<BlockDataKey> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockState.CODEC.fieldOf("state").forGetter(blockDataKey -> blockDataKey.state),
                    NbtCompound.CODEC.fieldOf("tag").forGetter(blockDataKey -> blockDataKey.tag)
            ).apply(instance, instance.stable(BlockDataKey::new))
    );

    private BlockState state;
    private NbtCompound tag;

    public BlockDataKey(BlockState state) {
        this(state, null);
    }

    public BlockDataKey(BlockState state, @Nullable NbtCompound tag) {
        this.state = state;
        this.tag = tag;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public NbtCompound getTag() {
        return tag;
    }

    public void setTag(NbtCompound tag) {
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
