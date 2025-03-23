package net.rodofire.ewc_test.shape.block.expected_shapes.rotations;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class to manage rotations on BlockList. It allows to convert blockPos to their rotated variants
 */
public class ExpectedShapeRotator {
    private final ExpectedRotator rotator;

    public ExpectedShapeRotator(ExpectedRotator rotator) {
        this.rotator = rotator;
    }

    public BlockPos[] get(BlockPos[] posList) {
        for (int i = 0; i < posList.length; i++) {
            posList[i] = rotator.getBlockPos(posList[i]);
        }
        return posList;
    }

    public List<BlockPos> get(List<BlockPos> posList) {
        posList.replaceAll(rotator::getBlockPos);
        return posList;
    }

    public long[] get(long[] posList) {
        for (int i = 0; i < posList.length; i++) {
            posList[i] = rotator.get(posList[i]);
        }
        return posList;
    }

    public LongArrayList get(LongArrayList posList) {
        for (int i = 0; i < posList.size(); i++) {
            posList.set(i, rotator.get(posList.getLong(i)));
        }
        return posList;
    }

    public LongOpenHashSet get(LongOpenHashSet posList) {
        LongOpenHashSet ret = new LongOpenHashSet();
        for (long pos : posList) {
            ret.add(rotator.get(pos));
        }
        return posList;
    }

    public BlockListManager get(BlockListManager comparator) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (BlockList blockList : comparator.getAllBlockList()) {
            futures.add(CompletableFuture.runAsync(() -> {
                LongArrayList rotatedPositions = new LongArrayList(blockList.size());
                for (int i = 0; i < blockList.size(); i++) {
                    rotatedPositions.add(rotator.get(blockList.getLongPos(i)));
                }
                synchronized (blockList) {
                    blockList.setPosList(rotatedPositions);
                }
            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return comparator;
    }

    public DividedBlockListManager getDivided(DividedBlockListManager comparator) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (BlockListManager manager : comparator.getAllManager()) {
            for (BlockList blockList : manager.getAllBlockList()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    LongArrayList rotatedPositions = new LongArrayList(blockList.size());
                    for (int i = 0; i < blockList.size(); i++) {
                        rotatedPositions.add(rotator.get(blockList.getLongPos(i)));
                    }
                    synchronized (blockList) {
                        blockList.setPosList(rotatedPositions);
                    }
                }));
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return comparator;
    }
}
