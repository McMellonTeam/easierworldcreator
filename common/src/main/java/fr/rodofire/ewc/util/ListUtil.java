package fr.rodofire.ewc.util;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.util.RandomSource;

import java.util.List;

public class ListUtil {
    public static <T> T getRandomElement(final List<T> list) {
        return list.get(RandomSource.create().nextIntBetweenInclusive(0, list.size() - 1));
    }

    public static long getRandomElement(final LongArrayList list) {
        return list.getLong(RandomSource.create().nextIntBetweenInclusive(0, list.size() - 1));
    }
}
