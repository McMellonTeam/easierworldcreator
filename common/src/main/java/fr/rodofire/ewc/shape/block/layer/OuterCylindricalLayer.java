package fr.rodofire.ewc.shape.block.layer;


import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.util.WorldGenUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class OuterCylindricalLayer extends AbstractOuterLayer {
    OuterCylindricalLayer(BlockLayerManager blockLayer, Vec3 center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    @Override
    protected int findLayerIndex(int[] layerDistance, float distance) {
        int left = 0, right = layerDistance.length-1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (layerDistance[mid] == distance) {
                return mid;
            } else if (layerDistance[mid] < distance) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    @Override
    protected float getDistance(int[] pos) {
        return maxDistance - WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3(pos[0], pos[1], pos[2]));
    }

    @Override
    protected float getDistance(int posX, int posY, int posZ) {
        return maxDistance - WorldGenUtil.getDistanceToAxis(this.centerPos, this.directionVector, new Vec3(posX, posY, posZ));
    }


}
