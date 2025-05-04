package fr.rodofire.ewc.shape.block.layer;


import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

abstract class AbstractLayer implements Layer {
    protected BlockLayerManager blockLayer;
    protected Vec3 centerPos = new Vec3(0,0,0);
    protected Vec3 directionVector = new Vec3(0,1,0);


    AbstractLayer(BlockLayerManager blockLayer, Vec3 centerPos, Vec3i directionVector) {
        this.blockLayer = blockLayer;
        this.centerPos = centerPos;
        this.directionVector = Vec3.atCenterOf(directionVector);
    }

    AbstractLayer(BlockLayerManager blockLayer, Vec3 centerPos) {
        this.blockLayer = blockLayer;
        this.centerPos = centerPos;
    }

    AbstractLayer(BlockLayerManager blockLayer) {
        this.blockLayer = blockLayer;
    }

    @Override
    public Vec3 getCenterPos() {
        return centerPos;
    }

    @Override
    public void setCenterPos(Vec3 centerPos) {
        this.centerPos = centerPos;
    }

    @Override
    public Vec3 getDirectionVector() {
        return directionVector;
    }

    @Override
    public void setDirectionVector(Vec3i directionVector) {
        this.directionVector = Vec3.atCenterOf(directionVector);
    }
}
