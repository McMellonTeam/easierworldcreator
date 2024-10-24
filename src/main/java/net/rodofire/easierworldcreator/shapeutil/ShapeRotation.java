package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ShapeRotation extends ShapeLayer {

    //These are rotations in degrees (0-360).
    //These 3 are used to represent every rotation possible in a 3d world
    private int yRotation = 0;
    private int zRotation = 0;
    private int secondYRotation = 0;

    //precalculated cos and sin table for every rotation
    private double cosX2 = 1;
    private double cosX = 1;
    private double cosY = 1;
    private double sinX = 0;
    private double sinX2 = 0;
    private double sinY = 0;

    /**
     * init the ShapeRotation
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param yRotation       first rotation around the y-axis
     * @param zRotation       second rotation around the z-axis
     * @param secondYRotation last rotation around the y-axis
     */
    public ShapeRotation(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int yRotation, int zRotation, int secondYRotation) {
        super(world, pos, placeMoment, layerPlace, layersType);
        getRotations(yRotation, zRotation, secondYRotation);
    }

    /**
     * init the ShapeRotation
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public ShapeRotation(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
        getRotations(0, 0, 0);
    }


    /*---------- Rotation related ----------*/
    public int getYRotation() {
        return yRotation;
    }

    public void setXRotation(int yRotation) {
        this.yRotation = yRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public int getZRotation() {
        return zRotation;
    }

    public void setYRotation(int yRotation) {
        this.zRotation = yRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public int getSecondYRotation() {
        return secondYRotation;
    }

    public void setSecondYRotation(int secondYRotation) {
        this.secondYRotation = secondYRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public void addXRotation(int YRotation) {
        this.yRotation += YRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public void addYRotation(int yRotation) {
        this.zRotation += yRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public void addSecondXRotation(int secondYRotation) {
        this.secondYRotation += secondYRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    /**
     * precompute the cosines and the sinus of the rotations for better performance
     *
     * @param yRotation       the first rotation around the y-axis
     * @param zRotation       the second rotation the z-axis
     * @param secondYRotation the last rotation around the y-axis
     */
    private void getRotations(int yRotation, int zRotation, int secondYRotation) {
        this.yRotation = yRotation;
        this.zRotation = zRotation;
        this.secondYRotation = secondYRotation;
        this.cosX = FastMaths.getFastCos(yRotation);
        this.cosY = FastMaths.getFastCos(zRotation);
        this.sinX = FastMaths.getFastSin(yRotation);
        this.sinY = FastMaths.getFastSin(zRotation);
        this.cosX2 = FastMaths.getFastCos(secondYRotation);
        this.sinX2 = FastMaths.getFastSin(secondYRotation);
        this.setRadialCenterPos(this.getPos());
        this.setRadialCenterVec3d(this.getPos().toCenterPos());
    }

    /**
     * method to get the rotation of Vec3d depending on the different rotations determined before
     *
     * @param pos       BlockPos that has to be rotated
     * @param centerPos The center of the rotation
     * @return a list of BlockPos related to the rotation
     */
    public BlockPos getCoordinatesRotation(Vec3d pos, BlockPos centerPos) {
        return getCoordinatesRotation((float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), centerPos);
    }

    /**
     * method to get the rotation depending on the different rotations determined before
     *
     * @param x         the distance on the x-axis from the BlockPos
     * @param y         the distance on the y-axis from the BlockPos
     * @param z         the distance on the z-axis from the BlockPos
     * @param centerPos the center of the rotation
     * @return the BlockPos related to the rotation
     */
    public BlockPos getCoordinatesRotation(float x, float y, float z, BlockPos centerPos) {
        // first x rotation
        float y_rot1 = (float) (y * cosX - z * sinX);
        float z_rot1 = (float) (y * sinX + z * cosX);

        // y rotation
        float x_rot_z = (float) (x * cosY - y_rot1 * sinY);
        float y_rot_z = (float) (x * sinY + y_rot1 * cosY);

        // second x rotation
        float y_rot2 = (float) (y_rot_z * cosX2 - z_rot1 * sinX2);
        float z_rot2 = (float) (y_rot_z * sinX2 + z_rot1 * cosX2);

        return new BlockPos(new BlockPos.Mutable().set(centerPos, (int) x_rot_z, (int) y_rot2, (int) z_rot2));
    }

    /**
     * method to get the rotation of a list depending on the different rotations determined before
     *
     * @param posList   the list of coordinates that has to be rotated
     * @param centerPos The center of the rotation
     * @return a list of BlockPos related to the rotation
     */
    public List<BlockPos> getCoordinatesRotationList(List<Vec3d> posList, BlockPos centerPos) {
        List<BlockPos> newposlist = new ArrayList<>();
        for (Vec3d pos : posList) {
            newposlist.add(this.getCoordinatesRotation(pos, centerPos));
        }
        return newposlist;
    }

}
