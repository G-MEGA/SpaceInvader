package org.newdawn.spaceinvaders.game_object;

import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.fixed_point.FixedPointAffineTransform;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.geom.AffineTransform;

public class GameObject2D extends GameObject{
    FixedPointAffineTransform localTransform = new FixedPointAffineTransform();

    // 0 posX
    // 1 posY
    // 2 rotation
    // 3 scale
    FixedPointAffineTransform globalTransform = new FixedPointAffineTransform();

    boolean globalTransformDirty = true;

    // Kryo 역직렬화를 위한 매개변수 없는 생성자
    public GameObject2D(){
        super();
    }
    public GameObject2D(Loop loop) {
        super(loop);
    }

    @Override
    protected void onInLoopUpdated(boolean value) {
        super.onInLoopUpdated(value);

        setGlobalTransformDirty();
    }

    public FixedPointAffineTransform getGlobalTransform(){
        if(!isInLoop()){throw new RuntimeException("not in loop");}

        if(globalTransformDirty){
            if (getParent() instanceof GameObject2D) {
                FixedPointAffineTransform parentGlobalTransform =  ((GameObject2D)getParent()).getGlobalTransform();

                long globalRotation = parentGlobalTransform.getRotation() + localTransform.getRotation();
                long globalScale = FixedPointUtil.mul(parentGlobalTransform.getScale(), localTransform.getScale());

                long newX = FixedPointUtil.mul(localTransform.getPosX(), FixedPointUtil.cos(parentGlobalTransform.getRotation()))
                        - FixedPointUtil.mul(localTransform.getPosY(), FixedPointUtil.sin(parentGlobalTransform.getRotation()));
                long newY = FixedPointUtil.mul(localTransform.getPosX(), FixedPointUtil.sin(parentGlobalTransform.getRotation()))
                        + FixedPointUtil.mul(localTransform.getPosY(), FixedPointUtil.cos(parentGlobalTransform.getRotation()));
                long globalPosX = parentGlobalTransform.getPosX() + newX;
                long globalPosY =  parentGlobalTransform.getPosY() + newY;

                globalTransform.setTransform(globalPosX, globalPosY, globalRotation, globalScale);
            }
            else{
                globalTransform.setTransform(localTransform.getPosX(), localTransform.getPosY(), localTransform.getRotation(), localTransform.getScale());
            }

            globalTransformDirty = false;
        }

        return globalTransform;
    }

    public long getPosX(){
        return localTransform.getPosX();
    }
    public long getPosY(){
        return localTransform.getPosY();
    }
    public long getRotation(){
        return localTransform.getRotation();
    }
    public long getScale(){
        return localTransform.getScale();
    }

    public void setPosX(long x){
        localTransform.setPosX(x);
        setGlobalTransformDirty();
    }
    public void setPosY(long y){
        localTransform.setPosY(y);
        setGlobalTransformDirty();
    }
    public void setPos(long x, long y){
        localTransform.setPos(x, y);
        setGlobalTransformDirty();
    }
    public void setRotation(long degrees){
        localTransform.setRotation(degrees);
        setGlobalTransformDirty();
    }
    public void setPosRotation(long x, long y, long degrees){
        localTransform.setPosRotation(x, y, degrees);
        setGlobalTransformDirty();
    }
    public void setScale(long scale){
        localTransform.setScale(scale);
        setGlobalTransformDirty();
    }

    void setGlobalTransformDirty(){
        globalTransformDirty = true;

        for(GameObject gameObject : getChildren()){
            if(gameObject instanceof GameObject2D){
                ((GameObject2D)gameObject).setGlobalTransformDirty();
            }
        }
    }

    @Override
    protected void onAddedToParent() {
        super.onAddedToParent();

        setGlobalTransformDirty();
    }
    @Override
    protected void onRemovedFromParent() {
        super.onRemovedFromParent();

        setGlobalTransformDirty();
    }
}
