package net.rodofire.ewc_test.shape.block.expected_shapes.instanciator;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.jetbrains.annotations.NotNull;

/**
 * class to change the filling of the structure
 * since that all structure may not need or can't have a custom filling like the line generation, it is not implemented in the ShapeGen class
 * <br>
 * <br>
 * Class to generate cylinder related shapes
 * <br>
 * The Main purpose of this class is to generate the coordinates based on a shape.
 * The coordinates are organized depending on a {@code Map<ChunkPos, LongOpenHashSet>}.
 * <p>It emply some things:
 * <ul>
 *     <li>The coordinates are divided in chunk</li>
 *     <li>It uses {@link LongOpenHashSet} for several reasons.
 *     <ul>
 *     <li>First, We use a set to avoid doing unnecessary calculations on the shape. It ensures that no duplicate is present.
 *     <li>Second, it compresses the BlockPos: The {@link BlockPos} are saved under long using {@link LongPosHelper}.
 *     It saves some memory since that we save four bytes of data for each {@link BlockPos},
 *     and there should not have overhead since that we use primitive data type.
 *     <li>Third, since that we use primitive data types and that they take less memory,
 *     coordinate generation, accession or deletion is much faster than using a {@code Set<BlockPos>}.
 *     Encoding and decoding blockPos and then adding it into {@link LongOpenHashSet}is extremely faster
 *     compared to only adding a {@link BlockPos}.
 *     ~60- 70% facter.
 *     </ul>
 *     </li>
 * </ul>
 * <p>Dividing Coordinates into Chunk has some advantages :
 * <ul>
 *     <li> allow a multithreaded block assignement when using {@link LayerManager}
 *     <li> allow to be used during WG, when using {@link DividedBlockListManager} or when placing using {@link ShapePlacer}
 * </ul>
 */
@SuppressWarnings("unused")
public abstract class ExpectedAbstractFillableBlockShape extends ExpectedAbstractBlockShape {
    /**
     * if ==0, there will be no circle
     * if ==1f, it will be a full circle
     * Don't need to care if
     *
     * @see ExpectedAbstractFillableBlockShape is not set on CUSTOM
     **/
    float customFill = 1f;

    /**
     * set the default filling type
     */
    Type fillingType = Type.FULL;

    /**
     * init the ShapeFilling
     *
     * @param pos the center of the spiral
     */
    public ExpectedAbstractFillableBlockShape(@NotNull BlockPos pos) {
        super(pos);
    }

    /**
     * init the ShapeFilling
     *
     * @param pos the pos of the shape (usually the center of the structure)
     * @param rotator the object that is used to rotate the structure
     */
    public ExpectedAbstractFillableBlockShape(@NotNull BlockPos pos, Rotator rotator) {
        super(pos, rotator);
    }

    /**
     * change how the structure is filled
     */
    public enum Type {
        /**
         * will only generate the outline
         */
        EMPTY,
        /**
         * will generate an outline large of 50% the radius
         */
        HALF,
        /**
         * will entirely fill the circle
         */
        FULL,
        /**
         * Set custom filling type. It must be associated with a customFill float.
         */
        CUSTOM
    }

    /*----------- FillingType Related -----------*/

    /**
     * method to get the filling Type
     *
     * @return the filling type
     */
    public Type getFillingType() {
        return fillingType;
    }

    /**
     * method to change the filling Type
     *
     * @param fillingType change the fillingType
     */
    public void setFillingType(Type fillingType) {
        this.fillingType = fillingType;
    }

    /**
     * method to get the custom fill
     *
     * @return the float of the custom fill
     */
    public float getCustomFill() {
        return customFill;
    }

    /**
     * method to set the custom fill
     *
     * @param customFill change the custom fill used to change the percentage of the radius that will be filled
     */
    public void setCustomFill(float customFill) {
        this.customFill = customFill;
    }

    /**
     * method to set the custom fill
     * set the filling value depending on the filling type
     */
    protected void setFill() {
        if (this.fillingType == Type.HALF) {
            this.customFill = 0.5f;
        }
        if (this.fillingType == Type.FULL) {
            this.customFill = 1.0f;
        }
        if (this.getCustomFill() > 1f) this.customFill = 1f;
        if (this.getCustomFill() < 0f) this.customFill = 0f;
    }
}
