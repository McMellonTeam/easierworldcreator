package net.rodofire.ewc_test.shape.block.expected_shapes.rotations;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.util.LongPosHelper;

@SuppressWarnings("unused")
public class ExpectedRotator {
    private BlockPos centerPos = new BlockPos(0, 0, 0);
    //These are rotations in degrees (0-360).
    //These 3 are used to represent every rotation possible in a 3d world
    private int yRotation = 0;
    private int zRotation = 0;
    private int secondYRotation = 0;

    //precalculated cos and sin table for every rotation
    private double cosY2 = 1;
    private double cosY = 1;
    private double cosZ = 1;
    private double sinY = 0;
    private double sinY2 = 0;
    private double sinZ = 0;


    /**
     * constructor of the object
     *
     * @param centerPos       the {@link BlockPos} on which the coordinates will rotates.
     *                        This is an important parameter to define since that if it is not specified,
     *                        the rotations will be done around the point 0,0,0
     *                        <br>
     * @param yRotation       the rotations of the object.
     *                        <p> This is the first rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     * @param zRotation       the rotations of the object.
     *                        <p> This is the second rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the z-axis
     * @param secondYRotation the rotations of the object.
     *                        <p> This is the third rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     */
    public ExpectedRotator(BlockPos centerPos, int yRotation, int zRotation, int secondYRotation) {
        this.centerPos = centerPos;
        this.yRotation = yRotation;
        this.zRotation = zRotation;
        this.secondYRotation = secondYRotation;
        getRotations(yRotation, zRotation, secondYRotation);
    }

    /**
     * init the ShapeRotation
     *
     * @param yRotation       the rotations of the object.
     *                        <p> This is the first rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     * @param zRotation       the rotations of the object.
     *                        <p> This is the second rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the z-axis
     * @param secondYRotation the rotations of the object.
     *                        <p> This is the third rotation applied.
     *                        <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     */
    public ExpectedRotator(int yRotation, int zRotation, int secondYRotation) {
        getRotations(yRotation, zRotation, secondYRotation);
    }

    /**
     * constructor of the object
     *
     * @param centerPos the {@link BlockPos} on which the coordinates will rotates.
     *                  This is an important parameter to define since that if it is not specified,
     *                  the rotations will be done around the point 0,0,0
     *                  <br>
     * @param yRotation the rotations of the object.
     *                  <p> This is the first rotation applied.
     *                  <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     * @param zRotation the rotations of the object.
     *                  <p> This is the second rotation applied.
     *                  <p> The parameter will make the {@link BlockPos} rotate around the z-axis
     */
    public ExpectedRotator(BlockPos centerPos, int yRotation, int zRotation) {
        this.centerPos = centerPos;
        getRotations(yRotation, zRotation, 0);
    }

    /**
     * constructor of the object
     *
     * @param centerPos the {@link BlockPos} on which the coordinates will rotates.
     *                  This is an important parameter to define since that if it is not specified,
     *                  the rotations will be done around the point 0,0,0
     *                  <br>
     * @param yRotation the rotations of the object.
     *                  <p> This is the first rotation applied.
     *                  <p> The parameter will make the {@link BlockPos} rotate around the y-axis
     */
    public ExpectedRotator(BlockPos centerPos, int yRotation) {
        this.centerPos = centerPos;
        getRotations(yRotation, 0, 0);
    }

    /**
     * constructor of the object
     *
     * @param centerPos the {@link BlockPos} on which the coordinates will rotates.
     *                  This is an important parameter to define since that if it is not specified,
     *                  the rotations will be done around the point 0,0,0
     */
    public ExpectedRotator(BlockPos centerPos) {
        this.centerPos = centerPos;
    }

    /**
     * init the ShapeRotation
     */
    public ExpectedRotator() {
        getRotations(0, 0, 0);
    }


    /*---------- Rotation related ----------*/
    public void setYRotation(int yRotation) {
        this.yRotation = yRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public int getYRotation() {
        return yRotation;
    }

    public int getZRotation() {
        return zRotation;
    }

    public void setZRotation(int yRotation) {
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


    public void addZRotation(int YRotation) {
        this.yRotation += YRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public void addYRotation(int yRotation) {
        this.zRotation += yRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public void addSecondZRotation(int secondYRotation) {
        this.secondYRotation += secondYRotation;
        getRotations(this.yRotation, this.zRotation, this.secondYRotation);
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }

    public ExpectedRotator setCenterPos(BlockPos centerPos) {
        this.centerPos = centerPos;
        return this;
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
        this.cosY = FastMaths.getFastCos(yRotation);
        this.sinY = FastMaths.getFastSin(yRotation);
        this.cosZ = FastMaths.getFastCos(zRotation);
        this.sinZ = FastMaths.getFastSin(zRotation);
        this.cosY2 = FastMaths.getFastCos(secondYRotation);
        this.sinY2 = FastMaths.getFastSin(secondYRotation);
    }

    public LongArrayList getAll(LongArrayList pos) {
        for (int i = 0; i < pos.size(); ++i) {
            pos.set(i, get(pos.getLong(i)));
        }
        return pos;
    }

    public long get(long pos) {
        int[] intPos = LongPosHelper.decodeBlockPos2Array(pos);
        // first y rotation
        float x_rot1 = (float) (intPos[0] * cosY - intPos[2] * sinY);
        float z_rot1 = (float) (intPos[0] * sinY + intPos[2] * cosY);
        // z rotation
        float x_rot_z = (float) (x_rot1 * cosZ - intPos[1] * sinZ);
        float y_rot_z = (float) (x_rot1 * sinZ + intPos[1] * cosZ);

        // second y rotation
        float x_final = (float) (x_rot_z * cosY2 - z_rot1 * sinY2);
        float z_final = (float) (x_rot_z * sinY2 + z_rot1 * cosY2);

        return LongPosHelper.encodeBlockPos((int) x_final + this.centerPos.getX(), (int) y_rot_z + this.centerPos.getY(), (int) z_final + this.centerPos.getZ());
    }

    public long get(double x, double y, double z) {
        // first y rotation
        double x_rot1 = (x * cosY - z * sinY);
        double z_rot1 = (x * sinY + z * cosY);
        // z rotation
        double x_rot_z = (x_rot1 * cosZ - y * sinZ);
        double y_rot_z = (x_rot1 * sinZ + y * cosZ);

        // second y rotation
        double x_final = (x_rot_z * cosY2 - z_rot1 * sinY2);
        double z_final = (x_rot_z * sinY2 + z_rot1 * cosY2);

        return LongPosHelper.encodeBlockPos((int) x_final + this.centerPos.getX(), (int) y_rot_z + this.centerPos.getY(), (int) z_final + this.centerPos.getZ());
    }

    public long getRaw(long pos) {
        int[] intPos = LongPosHelper.decodeBlockPos2Array(pos);
        // first y rotation
        float x_rot1 = (float) (intPos[0] * cosY - intPos[2] * sinY);
        float z_rot1 = (float) (intPos[0] * sinY + intPos[2] * cosY);
        // z rotation
        float x_rot_z = (float) (x_rot1 * cosZ - intPos[1] * sinZ);
        float y_rot_z = (float) (x_rot1 * sinZ + intPos[1] * cosZ);

        // second y rotation
        float x_final = (float) (x_rot_z * cosY2 - z_rot1 * sinY2);
        float z_final = (float) (x_rot_z * sinY2 + z_rot1 * cosY2);

        return LongPosHelper.encodeBlockPos((int) x_final, (int) y_rot_z, (int) z_final);
    }

    public long getRaw(double x, double y, double z) {
        // first y rotation
        double x_rot1 = (x * cosY - z * sinY);
        double z_rot1 = (x * sinY + z * cosY);
        // z rotation
        double x_rot_z = (x_rot1 * cosZ - y * sinZ);
        double y_rot_z = (x_rot1 * sinZ + y * cosZ);

        // second y rotation
        double x_final = (x_rot_z * cosY2 - z_rot1 * sinY2);
        double z_final = (x_rot_z * sinY2 + z_rot1 * cosY2);

        return LongPosHelper.encodeBlockPos((int) x_final, (int) y_rot_z, (int) z_final);
    }

    public BlockPos getBlockPos(int[] pos) {
        return getBlockPos(pos[0], pos[1], pos[2]);
    }

    public BlockPos getBlockPos(BlockPos pos) {
        return getBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * method to get the rotation of Vec3d depending on the different rotations determined before
     *
     * @param pos BlockPos that has to be rotated
     * @return a list of BlockPos related to the rotation
     */
    public BlockPos getBlockPos(Vec3d pos) {
        return getBlockPos((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
    }

    /**
     * method to get the rotation depending on the different rotations determined before
     *
     * @param x the distance on the x-axis from the BlockPos
     * @param y the distance on the y-axis from the BlockPos
     * @param z the distance on the z-axis from the BlockPos
     * @return the BlockPos related to the rotation
     */
    public BlockPos getBlockPos(float x, float y, float z) {
        // first y rotation
        float x_rot1 = (float) (x * cosY - z * sinY);
        float z_rot1 = (float) (x * sinY + z * cosY);
        // z rotation
        float x_rot_z = (float) (x_rot1 * cosZ - y * sinZ);
        float y_rot_z = (float) (x_rot1 * sinZ + y * cosZ);

        // second y rotation
        float x_final = (float) (x_rot_z * cosY2 - z_rot1 * sinY2);
        float z_final = (float) (x_rot_z * sinY2 + z_rot1 * cosY2);

        return new BlockPos((int) x_final + centerPos.getX(), (int) y_rot_z + centerPos.getY(), (int) z_final + centerPos.getZ());
    }

    /**
     * method to get the rotation without taking account of the center pos
     */
    public BlockPos getRawBlockPos(float x, float y, float z) {
        // first y rotation
        float x_rot1 = (float) (x * cosY - z * sinY);
        float z_rot1 = (float) (x * sinY + z * cosY);
        // z rotation
        float x_rot_z = (float) (x_rot1 * cosZ - y * sinZ);
        float y_rot_z = (float) (x_rot1 * sinZ + y * cosZ);

        // second y rotation
        float x_final = (float) (x_rot_z * cosY2 - z_rot1 * sinY2);
        float z_final = (float) (x_rot_z * sinY2 + z_rot1 * cosY2);
        return new BlockPos((int) x_final, (int) y_rot_z, (int) z_final);
    }
}
