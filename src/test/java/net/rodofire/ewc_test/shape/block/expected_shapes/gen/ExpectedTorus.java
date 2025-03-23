package net.rodofire.ewc_test.shape.block.expected_shapes.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.ewc_test.shape.block.expected_shapes.instanciator.ExpectedAbstractFillableBlockShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/*

                                         =======%
                                 %%%#+*%%%%%%@%%%%%%%%%%%%
                            %%%%%%=*%@@%%%%%%%%%%%%@ %%%%%@%@%
                        %@%%%%%%+#%%%%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%
                     %%%@%%%%%*+%%%%%%@%%%%%%%%%@%%%%%%%%%%%%%@%@%%%%@
                   %%%%%%%%%%#=%%%%%%%%%%@@%%%%%%%%%%%@%%%%%%%@%%%%%%%%%
                 %%%%%%@%%@%%=%%%%%%@%%%%%%@%%%%%%%@%% %%%%%%% % % @%%@%%%
               %%%%%%%%%%%%%%=%%%@%%%%%%%%%%@%% %%%%%%%% %%%%%%@%@%%%%%%%%%@
             %%%%%%%%%%=+==++===+=+=+%%%%@%%%%@%%%%% %%%%%%%@%%%%%%%@%%%%%%%%%
            %%%%%%%%==*#%%%%%=%@%%%%%#==*%%%%%%%%%%%%%@%%%%%% @%%%%@%%%%%%%%%%%
           %%%@%%==%%%%%%%%%%+=@%@%%%%%%%== % %%%%%% %% % %@%% %% % @%%%@%@@%%%%@
          %%%%%==%%%%%%%%%%@%%*=%@%%%%%%%@%+=%%%%@%%%%%%%%%@%%%%@@ %%%% %%%@%%%%%
        @@%%%+=%@%%%%%%%@%%%%%%%+*%%%%%%%@%%%++%%%%%%%% %%%% % %%@%@%%@%%%%%@%%%%%%
       %%%@#==%%%%%%@%%%%@%%%%%%%%**%%%%%%%%%@*=%%%%%%@% %@%%%%%%@ % %%%%%%%%%%%%%%%
      %%%%*=%% %%@%%%%@@%%%%%%%%%%%%%#+*%%%%%%%%=+%%% %@% % @ @%%%  @ % %%@@%@%%%%%%%
     @%%%#=@%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%=+%%%%%%%%%@% %% %@%@%%%%%%%%%%%%%%%%
     %%%*=%%%@%%%%%%%%%@%@%%%%%%%%%%%%%%%%%%% %%%%+*%%%%@@%%%@%@%% % % %@%%@%@%%@@%@@%
    @%%*=%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%     @%%=*%%%%%%@%  %%%%%%%%@@ %%%@%%% @%%%%
   %%%%=#@%%@%%%%%%%%%@%%@%%%%%%@%%%%%%%%%       %@%=%@@@@%%%% %%%@ % %@%%%%%%%%%%%%@%%%
   %%%=+%%%%%%@%%%% %%%%%@@%%%%%@%@@%%%%%         @%%=%%% %%%@%@%%% %% % % %  %@%% %@%%%
  %@%*=%%%%%%@@%%%@%@%%%@%@%%%%%%%%@%%%%          %%%#=%%%%@% % %@@%@%%%%%%%%%@%%%%%%%@%%
  %%%=%%%@%@%%@%%%%%@%%%%%%%%@@%%%%%%%%%           %%%=#%%%%%%%%%% % @% %%% % %@% @%@%%%%
 %%%#+%%%%%%%%%%%%%%%% % %@%%%%%%%%%%%%             %%**%%%% @ %%%%%%%% % %%%@%%%%@%%%@%%%
 %%%= %%%%%@ % @%%%%%%%  %@% @@%% %@%%               @#+%%%@%@ @%%  % % %@@%%%%%%%%%%@%%%%
 %%%=%@%%%%%%% %%%%%%%%@%%%%%@%%%%%%%%               %@=*%%%%%@%%%% % %  %@@ % %@% %% %%%%
 %@++@%%%@%%%@@%%%%@%%%%%%%%%%%%%%%%%%               @%++%%%@ %%%@%%@%%%%%%%%%%%%%%@%%%%%%
 %%+%%%%@ % %%@@%%%%%%%%%%%%@%%%%%%%%%               %%#+%%%%%%%@ % %%%@ % % %%%@%%%%%%%%%
 %%= %@%% % % %%%%%%%%%%%%%%%%%@%%%%%%                %%=%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 %@=#%%%@ %%%%%%%%%%%%@%%%%%%%%%%%%%%@                %%=% %% % %%% @@ % % %  %@% %%%% %%%@
 %%=%%%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%%@                %%=%%%%%%%%%%%%%%%%%%%@@%%%%%%%%%%%%@
 %%+%@%%%%%%%@%@%@%%@%%%@%%%%%%%%@%%%@                %@=@%%%%%@%%%%%%%%@% %  %%@ %%%@ %%%%
 %%= %%%%@%%%%%%%%@%@@ % @%% %%%% %%%%                %%=% %% % %%% %%%% % %  %%% %%%%@%%%@
 %%= %@%%%%%%@%%%%%% %%@ @%%%@@%@%%%%@                %*=% %%%%%%%%%%%%@%%%@@%%%%@%@%%%%%%@
 %%+*%@%%%@%%%@%%%@%%%%@%%%%%@@@@%%@%%               @%+*%%%%%%%% % %%%  % % @ %@% %%%%@%%
 %%%=@%%%%%@%%@%@%%@%@%%%@%%%%%%%%%%%%               @%+#%%%%%%%%%@%%%%%%%%%%%%%%%%%%%%%@%
 @%%=%%%%%@%%%%%%%% % %@@%%%%%%%%%%%%%               %%=# %%%%%%%%% % % @%%@ %@@ % @%%@%%%
 @%%==%%%%%%%@%%%%% @%@%%%%%%%%%%%%%%%%             %%*=%%@@@% % % %%%% % %%%%@%%%%@%% %%%
  %%%=%%%%@@@@%%%%@%%%%%%%%@%%%%%%%%%@%@           %%%=#%@%%%@ @%%@%@%%%%%%%% %%% %%%%%%%
  @%%#=%%%@@%%@%@%%%%%%@%%%%%%%%%@%%%%%%           %%#+%% @%%%%%%% @ % % %@%@%%%%%%%%%%%%
   %%%=*%%%%%%@@%%%%%%%@@%%%%%%%%@%%%%%%%         %%%=%%%%%%% %%% % %%%%%%%@%@@%%%%%%%%%
   %@%*=% @%%%%%%@ @%%% %%%% %%%%%%%%%%%%@       %%%=+%%%% % %%%%%% % %  % % @ % %%%@%%%
    %%%+=%%%%% %%%%%%%%%%%%@%%%%@%%%%%%%%%%     %%%+=%%@% %%%%%% @ @%@%%%%%%%%%%%%%%%%%
     %%%==%%%%%%%%%%%%%@%%%%%%%%%%%%%%%%%@%%% %%@%*=%%@@%%%%% %%%%%%%%%%%%% % @%@%%%%%
     @%%%+=%%%%%%%%%%%%%%%%%%%@%%%@%%%%%%%%%%%%@%*=% %%%@% %%@%@% % @% %%%@@@%%%%@%%%%
      %%@%#=%%%% %% %%%% %%%@%%%%@%%%%%%%%%%%%%@==%%%%% % %%%% % % %%%%%%%%%%@%%%%%%%
       %%%%#=#@%%%%%%@%%@%%%%%%%%%%%%%%%%%%%%%%=#%%%@@ %@%@%% %%%%%%%%@@%%%%%%%%%%%%
        %%%@%=+%%%%%%%%%%@%%%%%%%%%%%%%%%%%%%+=%%%%%%%%%@ %%%%%%@ % @%%%%%%%%%%%%%%
         @%%%%%==%%%%%%%%%%%%%%%%%@%%%%%%%@+=*%%%%%%%%%%%%%%% %%%@%%%%%%%@%%%%%%%%
          %%%%%%%=+@%%% @%%%@%%%% %%%%%%%==%%%%%%%%%  %@%  % %%%%@% @% % %%%@%%%%
            %%%%%%%+=+*%@%@%%%%%%%%%%+#=+%%%%%%%@%% %%%%%%%%@%@% % %%%%%%%%%%%%
             %%%%%%%@%#+=+==+=++=+===*%%%%%%@%@@%@%%%%% %%%%%%% %%%%%%%%%%%%%%
               %%%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@% %%%%%%%@%%%%@%%%%%%%%%
                 %@%%%@@ %%%%@%@%%%%% %%%%@%%%%% %@ %%%%@ %%%@%%%%%%%@%%%%
                   %%%%%%%@@%%%%@%%%%% %%@%%%%%%%@%%%%%@%%%@%@%%%%%%%%%@
                     %%%%%%%@@%%%%%%%@%@%%@%%@%@%%%%%%%@%%%%%%%%%%%%%%
                        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@%@%%%%%%%%%%
                           %%%%%%%%%%%%%%%%%%@%%%%@%%@%%%%%%%%%
                                 %%%%%%%%%%%%%%%%%%%%%%%%%
                                        @@%%%%%%%@                                         */

/**
 * Class to generate torus related shapes
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
public class ExpectedTorus extends ExpectedAbstractFillableBlockShape {
    private int innerRadiusX;
    private int outerRadiusX;
    private int innerRadiusZ;
    private int outerRadiusZ;

    @Override
    public LongOpenHashSet getCoveredChunks() {
        return new LongOpenHashSet();
    }

    //set the shape of the torus
    private TorusType torusType = TorusType.FULL;
    //float that determines how much of the structure is filled along the y-axis
    private float verticalTorus = 1f;
    //float that determines how much of the structure is filled along the x-axis
    private float horizontalTorus = 1f;

    private float outerSquared;
    float outerXSquared;
    float outerZSquared;
    float innerSquared;
    float innerXSquared;
    float innerZSquared;

    /**
     * init the Torus Shape
     *
     * @param pos          the center of the spiral
     * @param innerRadiusX the radius of the inner circle on the x-axis
     * @param outerRadiusX the radius of the outer circle on the x-axis
     * @param innerRadiusZ the radius of the inner circle on the z-axis
     * @param outerRadiusZ the radius of the outer circle on the z-axis
     */
    public ExpectedTorus(@NotNull BlockPos pos, Rotator rotator, int innerRadiusX, int outerRadiusX, int innerRadiusZ, int outerRadiusZ) {
        super(pos, rotator);
        this.innerRadiusX = innerRadiusX;
        this.outerRadiusX = outerRadiusX;
        this.innerRadiusZ = innerRadiusZ;
        this.outerRadiusZ = outerRadiusZ;
        init();
    }

    /**
     * init the Torus Shape
     *
     * @param pos         the center of the spiral
     * @param innerRadius the radius of the inner circle
     * @param outerRadius the radius of the outer circle
     */
    public ExpectedTorus(@NotNull BlockPos pos, int innerRadius, int outerRadius) {
        super(pos);
        this.innerRadiusX = innerRadius;
        this.outerRadiusX = outerRadius;
        this.innerRadiusZ = innerRadius;
        this.outerRadiusZ = outerRadius;
        init();
    }

    private void init() {
        outerSquared = outerRadiusX * outerRadiusZ;
        outerXSquared = outerRadiusX * outerRadiusX;
        outerZSquared = outerRadiusZ * outerRadiusZ;
        innerSquared = innerRadiusX * innerRadiusZ;
        innerXSquared = innerRadiusX * innerRadiusX;
        innerZSquared = innerRadiusZ * innerRadiusZ;
    }

    /**
     * Gets the inner radius of the torus along the X-axis.
     *
     * @return the inner radius along the X-axis.
     */
    public int getInnerRadiusX() {
        return innerRadiusX;
    }

    /**
     * Sets the inner radius of the torus along the X-axis.
     *
     * @param innerRadiusX the inner radius to set along the X-axis.
     */
    public void setInnerRadiusX(int innerRadiusX) {
        this.innerRadiusX = innerRadiusX;
        init();
    }

    /**
     * Gets the outer radius of the torus along the X-axis.
     *
     * @return the outer radius along the X-axis.
     */
    public int getOuterRadiusX() {
        return outerRadiusX;
    }

    /**
     * Sets the outer radius of the torus along the X-axis.
     *
     * @param outerRadiusX the outer radius to set along the X-axis.
     */
    public void setOuterRadiusX(int outerRadiusX) {
        this.outerRadiusX = outerRadiusX;
        init();
    }

    /**
     * Gets the inner radius of the torus along the Z-axis.
     *
     * @return the inner radius along the Z-axis.
     */
    public int getInnerRadiusZ() {
        return innerRadiusZ;
    }

    /**
     * Sets the inner radius of the torus along the Z-axis.
     *
     * @param innerRadiusZ the inner radius to set along the Z-axis.
     */
    public void setInnerRadiusZ(int innerRadiusZ) {
        this.innerRadiusZ = innerRadiusZ;
        init();
    }

    /**
     * Gets the outer radius of the torus along the Z-axis.
     *
     * @return the outer radius along the Z-axis.
     */
    public int getOuterRadiusZ() {
        return outerRadiusZ;
    }

    /**
     * Sets the outer radius of the torus along the Z-axis.
     *
     * @param outerRadiusZ the outer radius to set along the Z-axis.
     */
    public void setOuterRadiusZ(int outerRadiusZ) {
        this.outerRadiusZ = outerRadiusZ;
        init();
    }

    /**
     * Gets the type of the torus.
     *
     * <p>The torus type determines the configuration of the torus, such as whether it is hollow, solid, or has other specific properties.</p>
     *
     * @return the {@link TorusType} of the torus.
     */
    public TorusType getTorusType() {
        return torusType;
    }

    /**
     * Sets the type of the torus.
     *
     * <p>The torus type determines the configuration of the torus, such as whether it is hollow, solid, or has other specific properties.</p>
     *
     * @param torusType the {@link TorusType} to set.
     */
    public void setTorusType(TorusType torusType) {
        this.torusType = torusType;
    }

    /**
     * Gets the vertical torus ratio.
     *
     * <p>The vertical torus determines the percentage of the torus's height relative to its overall dimensions.</p>
     *
     * @return the vertical torus ratio as a float.
     */
    public float getVerticalTorus() {
        return verticalTorus;
    }

    /**
     * Sets the vertical torus ratio.
     *
     * <p>The vertical torus determines the percentage of the torus's height relative to its overall dimensions.</p>
     *
     * @param verticalTorus the vertical torus ratio to set, as a float.
     */
    public void setVerticalTorus(float verticalTorus) {
        this.verticalTorus = verticalTorus;
    }

    /**
     * Gets the horizontal torus ratio.
     *
     * <p>The horizontal torus determines the percentage of the torus's width relative to its overall dimensions.</p>
     *
     * @return the horizontal torus ratio as a float.
     */
    public float getHorizontalTorus() {
        return horizontalTorus;
    }

    /**
     * Sets the horizontal torus ratio.
     *
     * <p>The horizontal torus determines the percentage of the torus's width relative to its overall dimensions.</p>
     *
     * @param horizontalTorus the horizontal torus ratio to set, as a float.
     */
    public void setHorizontalTorus(float horizontalTorus) {
        this.horizontalTorus = horizontalTorus;
    }

    /**
     * Method to get all the BlockPos needed to place the shape
     *
     * @return a list that contain divided BlockPos depending in the chunkPos
     */
    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        setTorusFill();
        if (this.getFillingType() == Type.EMPTY) {
            this.generateEmptyTore();
        } else {
            this.generateFullTore();
        }
        return chunkMap;
    }


    /**
     * generates a full torus / tore with custom filling
     * the shape with the torus might be different from the empty one if you're using custom torus filling
     */
    public void generateFullTore() {
        this.setFill();

        //TODO fix
        //float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        //float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;


        int maxInnerRadiusX = Math.max(innerRadiusX, innerRadiusZ);
        int maxOuterRadiusX = Math.max(outerRadiusX, outerRadiusZ);

        int b = maxOuterRadiusX + maxInnerRadiusX;

        if (rotator == null) {
            for (int x = (-b); x <= 2 * b * this.horizontalTorus - b; x++) {
                int xSquared = x * x;
                for (int z = -b; z <= b; z++) {

                    int zSquared = z * z;
                    float angle = (float) Math.toDegrees(Math.atan2(z, x));
                    double outerRadius = getOuterRadius(angle);
                    double innerRadius = getInnerRadius(angle);
                    int squaredSum = xSquared + zSquared;
                    int outerRadiusSquared = (int) (outerRadius * outerRadius);
                    int innerRadiusSquared = (int) (innerRadius * innerRadius);
                    int a1 = squaredSum + outerRadiusSquared - innerRadiusSquared;

                    int e = 4 * outerRadiusSquared * (squaredSum);

                    for (int y = -b; y <= 2 * b * this.verticalTorus - b; y++) {
                        int ySquared = y * y;


                        int a = a1 + ySquared;


                        if ((a * a) - e <= 0) {
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) {
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                modifyChunkMap(LongPosHelper.encodeBlockPos(x + centerX, y + centerY, z + centerZ));
                            }
                        }

                    }
                }
            }
        } else {
            float max = 2 * b * this.verticalTorus - b;
            for (float x = -b; x <= max; x += 0.5f) {
                float xSquared = x * x;
                for (float z = -b; z <= b; z += 0.5f) {
                    float zSquared = z * z;

                    float angle = (float) Math.toDegrees(Math.atan2(z, x));
                    double outerRadius = getOuterRadius(angle);
                    double innerRadius = getInnerRadius(angle);

                    int outerRadiusSquared = (int) (outerRadius * outerRadius);
                    int innerRadiusSquared = (int) (innerRadius * innerRadius);
                    float squaredSum = xSquared + zSquared;

                    float a1 = squaredSum + outerRadiusSquared - innerRadiusSquared;

                    float e = 4 * outerRadiusSquared * (squaredSum);
                    for (float y = -b; y <= max; y += 0.5f) {
                        float ySquared = y * y;

                        float a = a1 + ySquared;

                        if ((a * a) - e <= 0) {
                            //TODO
                            //implement fully the torus interior filling
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) {
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                modifyChunkMap(rotator.get(x, y, z));
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * generates an empty torus
     * the shape with the torus might be different from the full one if you're using custom torus filling
     */
    public void generateEmptyTore() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();

        int maxOuterRadius = Math.max(outerRadiusX, outerRadiusZ);
        int maxInnerRadius = Math.max(innerRadiusX, innerRadiusZ);
        //many if statement to avoid doing multiple if in the loops
        if (rotator == null) {
            for (int u = 0; u <= this.verticalTorus * 360; u += 40 / maxOuterRadius) {
                for (int v = 0; v <= this.horizontalTorus * 360; v += 45 / maxInnerRadius) {
                    float[] vec = this.getEllipsoidalToreCoordinates(u, v);
                    modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + vec[0]), (int) (centerY + vec[1]), (int) (centerZ + vec[3])));
                }
            }
        } else {
            float maxOuter = (float) 40 / (maxOuterRadius * 1.3f);
            float maxInner = (float) 45 / (maxInnerRadius * 1.3f);
            for (float u = 0; u <= 360 * this.verticalTorus; u += maxOuter) {
                for (float v = 0; v <= 360 * this.horizontalTorus; v += maxInner) {
                    double[] vec = this.getPreciseToreCoordinates(u, v);
                    modifyChunkMap(rotator.get(vec[0], vec[1], vec[2]));
                }
            }
        }
    }

    private void setTorusFill() {
        switch (this.torusType) {
            case FULL:
                this.verticalTorus = 1f;
                this.horizontalTorus = 1f;
                break;
            case HORIZONTAL_HALF:
                this.horizontalTorus = 0.5f;
                this.verticalTorus = 1f;
                break;
            case VERTICAL_HALF:
                this.verticalTorus = 0.5f;
                this.horizontalTorus = 1f;
                break;
            case HORIZONTAL_CUSTOM:
                this.verticalTorus = 1f;
                break;
            case VERTICAL_CUSTOM:
                this.horizontalTorus = 1f;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.torusType);
        }
    }


    private double[] getPreciseToreCoordinates(float u, float v) {
        double sinU = FastMaths.getPreciseSin(u);
        // Interpolating the radii based on the angle
        double R = outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(sinU);
        double r = innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(sinU);

        double a = R + r * FastMaths.getPreciseCos(v);
        double x = (a * FastMaths.getPreciseCos(u));
        double z = (a * sinU);
        double y = (r * FastMaths.getPreciseSin(v));

        return new double[]{x, y, z};
    }

    public float[] getEllipsoidalToreCoordinates(int u, int v) {

        // Interpolating the radii based on the angle
        double R = outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(FastMaths.getFastSin(u));
        double r = innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(FastMaths.getFastSin(u));

        double a = R + r * FastMaths.getFastCos(v);
        float x = (float) (a * FastMaths.getFastCos(u));
        float z = (float) (a * FastMaths.getFastSin(u));
        float y = (float) (r * FastMaths.getFastSin(v));

        return new float[]{x, y, z};
    }

    //    public double getInnerRadius(int angle) {
//        return innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(FastMaths.getFastSin(angle));
//
//    }
//
//    public double getOuterRadius(int angle) {
//        return outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(FastMaths.getFastSin(angle));
//    }
 /*   public double getOuterRadius(double x, double z) {
        double Rm = (outerRadiusX + outerRadiusZ) / 2.0;
        return Rm * Math.sqrt((x * x) / (outerRadiusX * outerRadiusX) + (z * z) / (outerRadiusZ * outerRadiusZ));
    }

    public double getInnerRadius(double x, double z) {
        double Rm = (innerRadiusX + innerRadiusZ) / 2.0;
        return Rm * Math.sqrt((x * x) / (innerRadiusX * innerRadiusX) + (z * z) / (innerRadiusZ * innerRadiusZ));
    }
*/
    public double getInnerRadius(float angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        return (innerSquared) /
                FastMaths.getFastSqrt(innerXSquared * sinAngle * sinAngle +
                        innerZSquared * cosAngle * cosAngle, 0.01f);
    }

    public double getOuterRadius(float angle) {
        double cosAngle = FastMaths.getFastCos(angle);
        double sinAngle = FastMaths.getFastSin(angle);

        return (outerSquared) /
                FastMaths.getFastSqrt(outerXSquared * sinAngle * sinAngle +
                        outerZSquared * cosAngle * cosAngle, 0.01f);
    }

    /**
     * change the outside filling of the torus
     * it is different from the Filling Type
     * the torus type changes the exterior filling while the other one change the interior filling type
     */
    public enum TorusType {
        FULL,
        VERTICAL_HALF,
        HORIZONTAL_HALF,
        VERTICAL_CUSTOM,
        HORIZONTAL_CUSTOM,
        CUSTOM
    }
}
