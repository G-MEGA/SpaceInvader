package org.newdawn.spaceinvaders.game_object.collision;

import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;

import java.awt.geom.AffineTransform;
import java.awt.Rectangle;

public class Collider2D extends GameObject2D {
    private ICollider2DOwner owner;

    public Rectangle bounds =  new Rectangle();   // 글로벌 포지션 기준임
    public Rectangle globalBounds =  new Rectangle();

    public Collider2D(Loop loop, ICollider2DOwner owner) {
        super(loop);
        this.owner = owner;
        this.loop.addCollider(this);
    }
    public void onDestroy() {
        super.onDestroy();
        this.owner = null;
        loop.removeCollider(this);
    }

    public ICollider2DOwner getOwner() {
        return owner;
    }
    public Rectangle getGlobalBounds() {
        AffineTransform gt = getGlobalTransform();

        globalBounds.setRect(
                bounds.getX() + gt.getTranslateX(),
                bounds.getY() + gt.getTranslateY(),
                bounds.getWidth(), bounds.getHeight());

        return globalBounds;
    }

    public boolean collidesWith(Collider2D other) {
        return getGlobalBounds().intersects(other.getGlobalBounds());
    }
    public void collidedWith(ICollider2DOwner collider){
        owner.collidedWith(collider);
    }
}
