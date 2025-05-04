package fr.rodofire.ewc.blockdata.sorter;

import fr.rodofire.ewc.blockdata.blocklist.BlockList;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.OrderedBlockListManager;
import fr.rodofire.ewc.util.ListUtil;
import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.WorldGenUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongShortImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * method to sort BlockPos depending on a parameter
 */
@SuppressWarnings("unused")
public class BlockSorter {
    private BlockSorterType type;

    private BlockPos centerPoint = new BlockPos(0, 0, 0);
    private Vec3 axisDirection = new Vec3(-1, -1, 0);

    /**
     * Constructor of the object
     *
     * @param type the type enum that decide how the List will get sorted
     */
    public BlockSorter(BlockSorterType type) {
        this.type = type;
    }

    /**
     * Returns the central point of the structure.
     *
     * @return the center point as a BlockPos object.
     */
    public BlockPos getCenterPoint() {
        return centerPoint;
    }

    /**
     * Sets the central point of the structure.
     *
     * @param centerPoint the center point as a BlockPos object.
     */
    public void setCenterPoint(BlockPos centerPoint) {
        this.centerPoint = centerPoint;
    }

    /**
     * Returns the axis direction used in the animation.
     *
     * @return the axis direction as a Vec3 object.
     */
    public Vec3 getAxisDirection() {
        return axisDirection;
    }

    /**
     * Sets the axis direction used in the animation.
     *
     * @param axisDirection the axis direction as a Vec3 object.
     */
    public void setAxisDirection(Vec3 axisDirection) {
        this.axisDirection = axisDirection;
    }

    /**
     * method to get the type of sorter
     *
     * @return the type enum of the sorter
     */
    public BlockSorterType getType() {
        return type;
    }

    /**
     * Method to set the type of the sorter
     *
     * @param type the type of the sorter
     */
    public void setType(BlockSorterType type) {
        this.type = type;
    }

    /**
     * method to set a random sorter type
     */
    public void setRandomSorter() {
        this.type = BlockSorterType.values()[RandomSource.create().nextInt(7)];
        if (this.type == BlockSorterType.ALONG_AXIS) {
            this.axisDirection = new Vec3(2 * RandomSource.create().nextFloat() - 1, 2 * RandomSource.create().nextFloat() - 1, 2 * RandomSource.create().nextFloat() - 1);
        }
    }

    /**
     * Method to sort the list depending on the {@code animatorType}.
     *
     * @param posList the list of BlockPos that will be sorted
     * @return the sorted list
     */
    public List<BlockPos> sortBlockPos(List<BlockPos> posList) {
        return switch (this.type) {
            case FROM_POINT -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos))).toList();
            }
            case FROM_POINT_INVERTED -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos))).toList();
            }
            case FROM_RANDOM_POINT -> {
                this.centerPoint = ListUtil.getRandomElement(posList);
                yield posList.parallelStream().sorted(
                        Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos)
                        )).toList();
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                this.centerPoint = ListUtil.getRandomElement(posList);
                yield posList.parallelStream().sorted(
                        Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos)
                        )).toList();
            }
            case RANDOM -> {
                List<BlockPos> copy = new ArrayList<>(posList);
                Collections.shuffle(copy);
                yield copy;
            }
            case ALONG_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                Vec3 direction = this.axisDirection.normalize();
                yield posList.parallelStream().sorted(Comparator.comparingDouble(
                        (pos) -> pos.getX() * direction.x + pos.getY() * direction.y + pos.getZ() * direction.z)).toList();
            }
            case RADIAL_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3 axisPoint = this.centerPoint.getBottomCenter();
                    Vec3 axisDirection = this.axisDirection.normalize();
                    Vec3 blockPosition = new Vec3(pos.getX(), pos.getY(), pos.getZ());

                    Vec3 pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dot(axisDirection);
                    Vec3 closestPointOnAxis = axisPoint.add(axisDirection.scale(projectionLength));
                    return -WorldGenUtil.getDistance(BlockPos.containing(closestPointOnAxis), BlockPos.containing(axisPoint));
                })).toList();
            }
            case FROM_LIST -> posList;
            case INVERSE -> {
                List<BlockPos> copy = new ArrayList<>(posList);
                Collections.reverse(copy);
                yield copy;
            }

            case FROM_PLANE -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3 blockVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return -Math.abs(distance);
                })).toList();
            }
            case FROM_PLANE_INVERTED -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield posList.parallelStream().sorted(Comparator.comparingDouble((pos) -> {
                    Vec3 blockVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return Math.abs(distance);
                })).toList();
            }
        };
    }

    /**
     * Method to sort the list depending on the {@code animatorType}.
     *
     * @param posList the list of BlockPos that will be sorted
     * @return the sorted list
     */
    public LongArrayList sortBlockPos(LongArrayList posList) {
        return switch (this.type) {
            case FROM_POINT -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                int[] centerPoint = LongPosHelper.convert2Array(this.centerPoint);
                yield modifyPos(posList, Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos.getValue())));
            }
            case FROM_POINT_INVERTED -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                int[] centerPoint = LongPosHelper.convert2Array(this.centerPoint);
                yield modifyPos(posList, Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos.getValue())));
            }
            case FROM_RANDOM_POINT -> {
                int[] centerPoint = LongPosHelper.decodeBlockPos2Array(ListUtil.getRandomElement(posList));
                yield modifyPos(posList,
                        Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(centerPoint, pos.getValue())
                        ));
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                int[] centerPoint = LongPosHelper.decodeBlockPos2Array(ListUtil.getRandomElement(posList));
                yield modifyPos(posList,
                        Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(centerPoint, pos.getValue())
                        ));
            }
            case RANDOM -> {
                LongArrayList copy = posList.clone();
                Collections.shuffle(copy);
                yield copy;
            }
            case ALONG_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                Vec3 direction = this.axisDirection.normalize();
                yield modifyPos(posList, Comparator.comparingDouble(
                        (pos) -> pos.getValue()[0] * direction.x + pos.getValue()[1] * direction.y + pos.getValue()[2] * direction.z));
            }
            case RADIAL_AXIS -> {
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield modifyPos(posList, Comparator.comparingDouble((pos) -> {
                    Vec3 axisPoint = this.centerPoint.getCenter();
                    Vec3 axisDirection = this.axisDirection.normalize();
                    Vec3 blockPosition = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                    Vec3 pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dot(axisDirection);
                    Vec3 closestPointOnAxis = axisPoint.add(axisDirection.scale(projectionLength));
                    return -WorldGenUtil.getDistance(BlockPos.containing(closestPointOnAxis), BlockPos.containing(axisPoint));
                }));
            }
            case FROM_LIST -> posList;
            case INVERSE -> {
                LongArrayList copy = posList.clone();
                Collections.reverse(copy);
                yield copy;
            }

            case FROM_PLANE -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield modifyPos(posList, Comparator.comparingDouble((pos) -> {
                    Vec3 blockVec = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return -Math.abs(distance);
                }));
            }
            case FROM_PLANE_INVERTED -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                if (posList == null || posList.isEmpty() || posList.size() == 1)
                    yield posList;
                yield modifyPos(posList, Comparator.comparingDouble((pos) -> {
                    Vec3 blockVec = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return Math.abs(distance);
                }));
            }
        };
    }


    /**
     * Method to sort the list depending on the {@code animatorType}.
     * The method will only modify each {@code List<BLockPos>} in every BlockList.
     * If you want to force the method so that every blockPos getSorted, see {@code sortInsideBlockList()}
     *
     * @param manager the manager that will sort the BlockPos of the blockList
     */
    public void sortInsideBlockList(BlockListManager manager) {
        switch (this.type) {
            case FROM_POINT -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    int[] center = LongPosHelper.convert2Array(this.centerPoint);
                    modifyPos(blockList, Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(center, pos.getValue())));
                }
            }
            case FROM_POINT_INVERTED -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    int[] center = LongPosHelper.convert2Array(this.centerPoint);
                    modifyPos(blockList, Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(center, pos.getValue())));
                }
            }
            case FROM_RANDOM_POINT -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    this.centerPoint = blockList.getRandomPos();
                    int[] center = LongPosHelper.convert2Array(this.centerPoint);
                    modifyPos(blockList,
                            Comparator.comparingDouble((pos) -> -WorldGenUtil.getDistance(center, pos.getValue()))
                    );
                }
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    this.centerPoint = blockList.getRandomPos();
                    int[] center = LongPosHelper.convert2Array(this.centerPoint);
                    modifyPos(blockList,
                            Comparator.comparingDouble((pos) -> WorldGenUtil.getDistance(center, pos.getValue()))
                    );
                }
            }
            case RANDOM -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    Collections.shuffle(blockList.getPosList());
                }
            }
            case ALONG_AXIS -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    Vec3 direction = this.axisDirection.normalize();
                    modifyPos(blockList, Comparator.comparingDouble(
                            (pos) -> pos.getValue()[0] * direction.x + pos.getValue()[1] * direction.y + pos.getValue()[2] * direction.z)

                    );
                }
            }
            case RADIAL_AXIS -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    modifyPos(blockList, Comparator.comparingDouble((pos) -> {
                                Vec3 axisPoint = this.centerPoint.getCenter();
                                Vec3 axisDirection = this.axisDirection.normalize();
                                Vec3 blockPosition = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                                Vec3 pointToBlock = blockPosition.subtract(axisPoint);
                                double projectionLength = pointToBlock.dot(axisDirection);
                                Vec3 closestPointOnAxis = axisPoint.add(axisDirection.scale(projectionLength));
                                return -WorldGenUtil.getDistance(BlockPos.containing(closestPointOnAxis), BlockPos.containing(axisPoint));
                            })
                    );
                }
            }
            case FROM_LIST -> {
            }
            case INVERSE -> {
                for (BlockList blockList : manager.getAllBlockList()) {
                    Collections.reverse(blockList.getPosList());
                }
            }
            case FROM_PLANE -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                for (BlockList blockList : manager.getAllBlockList()) {
                    modifyPos(blockList, Comparator.comparingDouble((pos) -> {
                                Vec3 blockVec = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                                double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                                return -Math.abs(distance);
                            })
                    );
                }
            }
            case FROM_PLANE_INVERTED -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                for (BlockList blockList : manager.getAllBlockList()) {
                    modifyPos(blockList, Comparator.comparingDouble((pos) -> {
                        Vec3 blockVec = new Vec3(pos.getValue()[0], pos.getValue()[1], pos.getValue()[2]);

                        double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                        return Math.abs(distance);
                    }));
                }
            }
            default -> throw new IllegalStateException("Unexpected sorter value: " + this.type);
        }
    }

    /**
     * Method to sort the list depending on the {@code animatorType}. The method will sort every blockPos of the BlockList and will return the related class to
     *
     * @param orderedBlockList the list of BlockList that will be sorted
     */
    public OrderedBlockListManager sortOrderedBlockList(OrderedBlockListManager orderedBlockList) {
        return switch (this.type) {
            case ALONG_AXIS -> {
                Vec3 direction = this.axisDirection.normalize();
                modifyPos(orderedBlockList, Comparator.comparingDouble(entry -> {
                    int[] pos = LongPosHelper.decodeBlockPos2Array(entry.leftLong());
                    return pos[0] * direction.x + pos[2] * direction.y + pos[1] * direction.z;
                }));
                yield orderedBlockList;
            }
            case RADIAL_AXIS -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                modifyPos(orderedBlockList, Comparator.comparingDouble((entry) -> {
                    int[] pos = LongPosHelper.decodeBlockPos2Array(entry.leftLong());
                    Vec3 blockPosition = new Vec3(pos[0], pos[1], pos[2]);

                    Vec3 pointToBlock = blockPosition.subtract(axisPoint);
                    double projectionLength = pointToBlock.dot(axisDirection);
                    Vec3 closestPointOnAxis = axisPoint.add(axisDirection.scale(projectionLength));
                    return -WorldGenUtil.getDistance(BlockPos.containing(closestPointOnAxis), BlockPos.containing(axisPoint));
                }));
                yield orderedBlockList;
            }
            case FROM_POINT -> {
                int[] center = LongPosHelper.convert2Array(this.centerPoint);
                modifyPos(orderedBlockList, Comparator.comparingDouble((entry) ->
                        -WorldGenUtil.getDistance(center, LongPosHelper.decodeBlockPos2Array(entry.leftLong())))
                );
                yield orderedBlockList;
            }
            case FROM_POINT_INVERTED -> {
                int[] center = LongPosHelper.convert2Array(this.centerPoint);
                modifyPos(orderedBlockList, Comparator.comparingDouble((entry) -> WorldGenUtil.getDistance(center, LongPosHelper.decodeBlockPos2Array(entry.leftLong())))
                );
                yield orderedBlockList;
            }
            case FROM_RANDOM_POINT -> {
                int[] center = LongPosHelper.decodeBlockPos2Array(orderedBlockList.getRandomBlockPos());
                modifyPos(orderedBlockList, Comparator.comparingDouble((entry) -> -WorldGenUtil.getDistance(center, LongPosHelper.decodeBlockPos2Array(entry.leftLong())))
                );
                yield orderedBlockList;
            }
            case FROM_RANDOM_POINT_INVERTED -> {
                int[] center = LongPosHelper.decodeBlockPos2Array(orderedBlockList.getRandomBlockPos());
                modifyPos(orderedBlockList, Comparator.comparingDouble((entry) -> WorldGenUtil.getDistance(center, LongPosHelper.decodeBlockPos2Array(entry.leftLong())))
                );
                yield orderedBlockList;
            }
            case FROM_PLANE -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                modifyPos(orderedBlockList, Comparator.comparingDouble(entry -> {
                    int[] pos = LongPosHelper.decodeBlockPos2Array(entry.leftLong());
                    Vec3 blockVec = new Vec3(pos[0], pos[1], pos[2]);

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return -Math.abs(distance);
                }));
                yield orderedBlockList;
            }
            case FROM_PLANE_INVERTED -> {
                Vec3 axisPoint = this.centerPoint.getCenter();
                Vec3 axisDirection = this.axisDirection.normalize();
                modifyPos(orderedBlockList, Comparator.comparingDouble(entry -> {
                    int[] pos = LongPosHelper.decodeBlockPos2Array(entry.leftLong());
                    Vec3 blockVec = new Vec3(pos[0], pos[1], pos[2]);

                    double distance = blockVec.subtract(axisPoint).dot(axisDirection);

                    return Math.abs(distance);
                }));
                yield orderedBlockList;

            }
            case RANDOM -> {
                Collections.shuffle(orderedBlockList.getPosList());
                yield orderedBlockList;
            }
            case FROM_LIST -> orderedBlockList;
            case INVERSE -> {
                Collections.reverse(orderedBlockList.getPosList());
                yield orderedBlockList;
            }
        };
    }

    private static void modifyPos(OrderedBlockListManager orderedBlockList, @NotNull Comparator<LongShortImmutablePair> comparator) {
        orderedBlockList.setPosList(
                orderedBlockList.getPosList()
                        .stream()
                        .parallel()
                        .sorted(comparator)
                        .collect(Collectors.toList())
        );
    }

    private static LongArrayList modifyPos(LongArrayList list, @NotNull Comparator<AbstractMap.SimpleEntry<Long, int[]>> comparator) {
        return list.longStream()
                .parallel()
                .mapToObj(encodedLong -> new AbstractMap.SimpleEntry<>(encodedLong, LongPosHelper.decodeBlockPos2Array(encodedLong)))
                .sorted(comparator)
                .mapToLong(Map.Entry::getKey)
                .collect(LongArrayList::new, LongArrayList::addLast, LongArrayList::addAll);
    }

    private static void modifyPos(BlockList blockList, @NotNull Comparator<AbstractMap.SimpleEntry<Long, int[]>> comparator) {
        blockList.setPosList(
                blockList.getPosList()
                        .longStream()
                        .parallel()
                        .mapToObj(encodedLong -> new AbstractMap.SimpleEntry<>(encodedLong, LongPosHelper.decodeBlockPos2Array(encodedLong)))
                        .sorted(comparator)
                        .mapToLong(Map.Entry::getKey)
                        .collect(LongArrayList::new, LongArrayList::addLast, LongArrayList::addAll)
        );
    }

    /**
     * enum to decide how the order of the blocks
     */
    public enum BlockSorterType {
        /**
         * Will place the blocks on an orthogonal plan to an axis. To use, it, use the method {@code setAxisDirection()}
         */
        ALONG_AXIS,
        /**
         * will place the blocks closer to an axis first
         */
        RADIAL_AXIS,
        /**
         * Will place the blocks from the closer to a blockPos to the further.
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_POINT,
        /**
         * will place the blocks from the further to a blockPos to the closer
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_POINT_INVERTED,
        /**
         * will take a random point of the shape and will place the blocks from the closer to the further
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_RANDOM_POINT,
        /**
         * will take a random point of the shape and will place the blocks from the further to the closer
         * You need to set the centerPoint from where the calculations will be done : {@code setCenterPoint()}
         */
        FROM_RANDOM_POINT_INVERTED,
        /**
         * Will sort the BlockPos depending on the distance between the plane and the BlockPos.
         * The closer BlockPos will be first while the further will be last
         * To create a plane, you have to use {@code setCenterPoint()} as well as {@code setAxisDirection()}.
         */
        FROM_PLANE,
        /**
         * Will sort the BlockPos depending on the distance between the plane and the BlockPos.
         * The further BlockPos will be first while the closer will be last
         * To create a plane, you have to use {@code setCenterPoint()} as well as {@code setAxisDirection()}.
         */
        FROM_PLANE_INVERTED,
        /**
         * will place the blocks in a random order
         */
        RANDOM,
        /**
         * Invert the BlockPos List
         */
        INVERSE,
        /**
         * will place the blocks depending on your input
         */
        FROM_LIST
    }
}