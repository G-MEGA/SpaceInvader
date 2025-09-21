package org.newdawn.spaceinvaders.game_object.visual;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.sprite.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class SpriteRenderer extends GameObject2D {
    public Sprite sprite;

    public SpriteRenderer(Loop loop) {
        super(loop);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sprite = null;
    }

    protected void draw(Graphics2D g) {
        if(sprite==null) return;

        sprite.draw(g, getGlobalTransformForDraw());
    }
}
