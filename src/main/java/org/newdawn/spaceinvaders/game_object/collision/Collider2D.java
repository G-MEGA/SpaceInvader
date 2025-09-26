package org.newdawn.spaceinvaders.game_object.collision;

import org.newdawn.spaceinvaders.fixed_point.FixedPointAffineTransform;
import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Collider2D extends GameObject2D {
    private ICollider2DOwner owner;
    private boolean drawBounds = false;
    public void setDrawBounds(boolean drawBounds) {
        this.drawBounds = drawBounds;
    }

    public long boundsPosX = 0;
    public long boundsPosY = 0;
    public long boundsWidth = 0;
    public long boundsHeight = 0;

    // 0 posX
    // 1 posY
    // 2 width
    // 3 height
    long[] globalBounds = new long[4];

    public Collider2D(Loop loop, ICollider2DOwner owner) {
        super(loop);
        this.owner = owner;
    }
    public void onDestroy() {
        super.onDestroy();
        this.owner = null;
    }

    public ICollider2DOwner getOwner() {
        return owner;
    }
    public long[] getGlobalBounds() {
        FixedPointAffineTransform globalTransform = getGlobalTransform();

        globalBounds[0] = globalTransform.getPosX() + boundsPosX;
        globalBounds[1] =  globalTransform.getPosY() + boundsPosY;
        globalBounds[2] = boundsWidth;
        globalBounds[3] =  boundsHeight;

        return globalBounds;
    }

    @Override
    protected void onInLoopUpdated(boolean value) {
        super.onInLoopUpdated(value);

        if (value) {
            loop.addCollider(this);
        }
        else{
            loop.removeCollider(this);
        }
    }

    AffineTransform tempTransformToDraw = new AffineTransform();
    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if(drawBounds){
            long[] globalBounds = getGlobalBounds();

            AffineTransform saveTransform = g2d.getTransform();

            tempTransformToDraw.setTransform(g2d.getTransform());
            tempTransformToDraw.rotate(
                    getGlobalTransform().getRotationInRadians(),
                    getGlobalTransform().getAffineTransform().getTranslateX(),
                    getGlobalTransform().getAffineTransform().getTranslateY());
            g2d.setTransform(tempTransformToDraw);
            g2d.setColor(Color.RED);
            g2d.drawRect(
                    (int) FixedPointUtil.toDouble(globalBounds[0]),
                    (int) FixedPointUtil.toDouble(globalBounds[1]),
                    (int) FixedPointUtil.toDouble(globalBounds[2]),
                    (int) FixedPointUtil.toDouble(globalBounds[3])
            );

            g2d.setTransform(saveTransform);
        }
    }

    public boolean collidesWith(Collider2D other) {
        long[] myBounds = getGlobalBounds();
        long[] otherBounds = other.getGlobalBounds();

        long myStartX = myBounds[0];
        long myStartY = myBounds[1];
        long myEndX = myBounds[0] + myBounds[2];
        long myEndY = myBounds[1] + myBounds[3];

        long otherStartX = otherBounds[0];
        long otherStartY = otherBounds[1];
        long otherEndX = otherBounds[0] + otherBounds[2];
        long otherEndY = otherBounds[1] + otherBounds[3];

        return myStartX <= otherEndX && myEndX >= otherStartX && myStartY <= otherEndY && myEndY >= otherStartY;
    }
}
