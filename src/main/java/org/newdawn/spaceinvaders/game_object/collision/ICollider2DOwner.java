package org.newdawn.spaceinvaders.game_object.collision;

public interface ICollider2DOwner {
    void collidedWith(ICollider2DOwner collider);
}
