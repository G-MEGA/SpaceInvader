package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.geom.AffineTransform;

public abstract class GameObject2D extends GameObject{
    long posX = 0;
    long posY = 0;
    long rotation = 0;
    long scale = 1 << 16;

    // 0 posX
    // 1 posY
    // 2 rotation
    // 3 scale
    long[] globalTransform = new long[4];
    
    public GameObject2D(Loop loop) {
        super(loop);
    }

    public long[] getGlobalTransform(){
        if (getParent() instanceof GameObject2D) {
            long[] parentGlobalTransform =  ((GameObject2D)getParent()).getGlobalTransform();

            long globalRotation = parentGlobalTransform[2] + rotation;
            long globalScale = FixedPointUtil.mul(parentGlobalTransform[3], scale);

            long newX = FixedPointUtil.mul(posX, FixedPointUtil.cos(parentGlobalTransform[2])) - FixedPointUtil.mul(posY, FixedPointUtil.sin(parentGlobalTransform[2]));
            long newY = FixedPointUtil.mul(posX, FixedPointUtil.sin(parentGlobalTransform[2])) + FixedPointUtil.mul(posY, FixedPointUtil.cos(parentGlobalTransform[2]));
            long globalPosX = parentGlobalTransform[0] + newX;
            long globalPosY =  parentGlobalTransform[1] + newY;

            globalTransform[0] = globalPosX;
            globalTransform[1] = globalPosY;
            globalTransform[2] = globalRotation;
            globalTransform[3] = globalScale;
            return globalTransform;
        }
        else{
            globalTransform[0] = posX;
            globalTransform[1] = posY;
            globalTransform[2] = rotation;
            globalTransform[3] = scale;
            return globalTransform;
        }
    }

    public long getPosX(){
        return posX;
    }
    public long getPosY(){
        return posY;
    }
    public long getRotation(){
        return rotation;
    }
    public long getScale(){
        return scale;
    }

    public void setPosX(long x){
        posX = x;
    }
    public void setPosY(long y){
        posY = y;
    }
    public void setPos(long x, long y){
        posX = x;
        posY = y;
    }
    public void setRotation(long degrees){
        while (degrees > 360 << 16) {
            degrees -= 360 << 16;
        }
        while (degrees < 0) {
            degrees += 360 << 16;
        }

        rotation = degrees;
    }
    public void setScale(long scale){
        this.scale = scale;
    }
}
