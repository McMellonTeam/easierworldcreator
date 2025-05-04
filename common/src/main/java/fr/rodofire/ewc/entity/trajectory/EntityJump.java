package fr.rodofire.ewc.entity.trajectory;

import fr.rodofire.ewc.entity.EntityUtil;
import jdk.jfr.Experimental;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * based on equations given by Minecraft Parkour Wiki:
 * <p> <a href="https://www.mcpk.wiki/wiki/Horizontal_Movement_Formulas">horizontal-movement</a>
 * <p> <a href="https://www.mcpk.wiki/wiki/Vertical_Movement_Formulas">vertical-movement</a>
 * <p> licensed under <a href="https://creativecommons.org/licenses/by-sa/4.0/">CC BY-SA 4.0</a>
 */
@SuppressWarnings("unused")
@Experimental
public class EntityJump {

    float overshoot;

    int tick;

    BlockPos startPos;
    BlockPos endPos;

    /**
     * init the class
     *
     * @param startPos  the BlockPos at which the entity will start the jump
     * @param endPos    the BlockPos at which the entity should finish
     * @param overshoot the maximum height at which the entity can go above the highest point between startPos and endPos
     */
    public EntityJump(BlockPos startPos, BlockPos endPos, float overshoot) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.overshoot = overshoot;
    }

    public void setEndPos(BlockPos endPos) {
        this.endPos = endPos;
    }

    public int getTotalTime(){
        return tick;
    }

    public Vec3 calculateInitialJump() {
        if(startPos.equals(endPos)) {
            return new Vec3(0, 0, 0);
        }
        float maxHeight = Math.max(endPos.getY(), startPos.getY()) + this.overshoot;

        double heightDiff = maxHeight - startPos.getY();

        // Calculate time to reach maximum height
        double timeToMax = Math.sqrt(2 * heightDiff / EntityUtil.GRAVITY);

        // Calculate initial vertical velocity needed
        double v0y = EntityUtil.GRAVITY * timeToMax;

        // Calculate the time of fall from max height to target
        double heightDrop = maxHeight - endPos.getY();
        double timeToFall = Math.sqrt(2 * heightDrop / EntityUtil.GRAVITY);

        double totalTime = timeToMax + timeToFall;
        this.tick = (int) Math.ceil(totalTime);

        // Calculate horizontal distances
        double dx = endPos.getX() - startPos.getX();
        double dz = endPos.getZ() - startPos.getZ();

        // Calculate required horizontal velocities accounting for drag
        double dragFactor = (1 - EntityUtil.HORIZONTAL_DRAG) / (1 - Math.pow(EntityUtil.HORIZONTAL_DRAG, totalTime));
        double v0x = dx * dragFactor;
        double v0z = dz * dragFactor;

        return new Vec3(v0x, v0y, v0z);
    }
}
