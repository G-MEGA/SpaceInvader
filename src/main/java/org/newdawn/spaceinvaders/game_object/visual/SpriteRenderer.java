package org.newdawn.spaceinvaders.game_object.visual;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;
import org.newdawn.spaceinvaders.game_object.GameObject2D;
import org.newdawn.spaceinvaders.loop.Loop;
import org.newdawn.spaceinvaders.sprite.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class SpriteRenderer extends GameObject2D {
    public Sprite sprite;

    AffineTransform globalTransformForDraw = new AffineTransform();
    AffineTransform tempTransform = new AffineTransform();

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

        long[] xform = getGlobalTransform();

        double x = FixedPointUtil.toDouble(xform[0]);
        double y = FixedPointUtil.toDouble(xform[1]);
        double rotation = Math.toRadians(FixedPointUtil.toDouble(xform[2]));
        double scale = FixedPointUtil.toDouble(xform[3]);

        globalTransformForDraw.setToIdentity();

        tempTransform.setToScale(scale, scale);
        globalTransformForDraw.preConcatenate(tempTransform);

        tempTransform.setToRotation(rotation);
        globalTransformForDraw.preConcatenate(tempTransform);

        tempTransform.setToTranslation(x, y);
        globalTransformForDraw.preConcatenate(tempTransform);

        sprite.draw(g, globalTransformForDraw);
    }
}
