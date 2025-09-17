package org.newdawn.spaceinvaders.sprite;

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
    private AffineTransform transform = new AffineTransform();
	
	/**
	 * Create a new sprite based on an image
	 * 
	 * @param image The image that is this sprite
	 */
	public Sprite(Image image, double pivotX, double pivotY) {
        this.image = image;
        this.transform.setToTranslation(-pivotX, -pivotY);
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

    public double getPivotX() {
        return -transform.getTranslateX();
    }
    public double getPivotY() {
        return -transform.getTranslateY();
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