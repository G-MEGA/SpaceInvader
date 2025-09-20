package org.newdawn.spaceinvaders.sprite;

import org.newdawn.spaceinvaders.fixed_point.FixedPointUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A sprite to be displayed on the screen. Note that a sprite
 * contains no state information, i.e. its just the image and 
 * not the location. This allows us to use a single sprite in
 * lots of different places without having to store multiple 
 * copies of the image.
 * 
 * @author Kevin Glass
 */
public class Sprite {
	/** The image to be drawn for this sprite */
	private Image image;
    private long pivotX, pivotY;
    private AffineTransform transform = new AffineTransform();
	
	/**
	 * Create a new sprite based on an image
	 * 
	 * @param image The image that is this sprite
	 */
	public Sprite(Image image, long pivotX, long pivotY) {
        this.image = image;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.transform.setToTranslation(
                FixedPointUtil.toDouble(-pivotX), FixedPointUtil.toDouble(-pivotY));
	}
	
	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

    public long getPivotX() {
        return pivotX;
    }
    public long getPivotY() {
        return pivotY;
    }

//	/**
//	 * Draw the sprite onto the graphics context provided
//	 *
//	 * @param g The graphics context on which to draw the sprite
//	 * @param x The x location at which to draw the sprite
//	 * @param y The y location at which to draw the sprite
//	 */
//	public void draw(Graphics2D g, int x, int y) {
//		g.drawImage(image,x,y,null);
//	}
    private AffineTransform tempTransform = new AffineTransform();
    public void draw(Graphics2D g, AffineTransform xform) {
        tempTransform.setTransform(xform);
        tempTransform.concatenate(transform);
        g.drawImage(image, tempTransform, null);
    }
}