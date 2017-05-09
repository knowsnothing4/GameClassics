package GCFrameWork;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/*
 * Pretty powerful class that stores images (transparency supported) as well
 * their sizes and positions.
 * 
 * Works as a linked list and can detect collision as well as click events;
 * 
 * TODO: 
 *  - Make this object draw itself
 *  - check for collision with other objects of the same type
 *  - change the actionlistener dynamically ?
 */

import java.awt.image.BufferedImage;

public class SceneObject {

	private static long imageId = 0; // unique "hash" for every image-object
										// created
	private long imageZOrder;

	private BufferedImage image; // the image itself
	private boolean visible, needUpdate; // should this image be drawn ?

	private int xpos, ypos; // (x,y) where this image should be rendered
	private int tag;
	private int alphaMask;

	public SceneObject(BufferedImage image, int x, int y) {

		this.imageZOrder = imageId++;
		this.xpos = x;
		this.ypos = y;
		this.image = image;
		this.visible = true;
		this.needUpdate = true; // this will be set to true the first time.
		this.alphaMask = 0xFF000000;
		this.tag = -1;
	}

	public SceneObject(String text, Font font, Color color, int x, int y)
	{
		this(null, x, y);
		 
		FontRenderContext frc = new FontRenderContext(	null,
													RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
													RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		Rectangle2D box = font.getStringBounds(text, frc);
		final int padding = 2;
		BufferedImage textBox = new BufferedImage(	(int) box.getWidth() + padding*2,
													(int) box.getHeight() + padding*2,
													BufferedImage.TYPE_INT_ARGB);
		//BufferedImage textBox = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = textBox.createGraphics();
		g.setFont(font);
		g.setColor(color);
		
		// FIXME: I don't think 0 0 will work
		g.drawString(text, padding, ((int) box.getHeight()) -padding*2);
		
		// debug
		//g.drawRect(0, 0, textBox.getWidth()-1, textBox.getHeight()-1);
		g.dispose();
		
		this.image = textBox;
	}

	public float getAlpha() {
		int alpha = (alphaMask >> 24) & 0xFF;
		return ((float) alpha) / 0xFF;
		//return alpha / 255;
		//System.out.print("\nMASK>>24 = " + ((alphaMask>>24) & 0xff) );
		//return ((float) alphaMask) / 0xff000000;
		
	}

	public void setAlpha(float alpha) 
	{
		/*
		 * @alpha: Value between 0.0 and 1.0 that represents
		 * a percentage of transparency. 
		 * 
		 * FIXME: This function will destroy the original alpha values
		 * of the image so it might be redesigned in the future.
		 */
		if (alpha < 0 || alpha > 1)
			return;

		// convert [0.0, 1.0] into [0, 255] and compute mask
		// based on ARGB signed integer format
		alphaMask = ((int) (alpha * 255)) << 24;

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				// erase old mask
				int newPixel = image.getRGB(i, j);

				// apply new mask
				if (newPixel != 0)
					image.setRGB(i, j, newPixel & 0x00FFFFFF | alphaMask);
			}
		}
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTag() {
		return this.tag;
	}

	// scene objects won't be drawn twice if they don't update
	// by changing their internal image or moving.
	public boolean needUpdate() {
		return needUpdate;
	}

	public void update() {
		needUpdate = !needUpdate;
	}

	public boolean hit(int x, int y) {
		// FIXME: Can we hit hidden objects ?

		// calculate relative coordinates
		x -= xpos;
		y -= ypos;

		// check if the coordinates are inside the image
		if (x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight())
			return false;
		
		int pixel = 0;
		// check if this point is transparent
		try {
			pixel = image.getRGB(x, y);	
		} catch (Exception e) {
			System.err.println("(X: "+xpos+", Y: "+ ypos +") failed to find object");
		}
		
		return ((pixel & 0xFF000000) != 0);

	}

	public boolean collide(final SceneObject sObj) {
		// FIXME: Should it check whether both objects are visible ?

		// bounding box intersection check
		Rectangle r1 = new Rectangle(this.xpos, this.ypos, this.image.getWidth(), this.image.getHeight());

		Rectangle r2 = new Rectangle(sObj.xpos, sObj.ypos, sObj.image.getWidth(), sObj.image.getHeight());
		// bounding box collision check
		if (!r1.intersects(r2))
			return false;

		// transparency intersection check
		Rectangle region = r1.intersection(r2);

		for (int i = region.x; i < region.x + region.width; i++) {
			for (int j = region.y; j < region.y + region.height; j++) {
				int r1i = i - this.xpos, r1j = j - this.ypos;
				int r2i = i - sObj.xpos, r2j = j - sObj.ypos;

				int pixel1 = this.image.getRGB(r1i, r1j) & 0xFF000000;
				int pixel2 = sObj.image.getRGB(r2i, r2j) & 0xFF000000;

				// if (pixel1 == 0xFF000000 && pixel2 == 0xFF000000) {
				if (pixel1 != 0 && pixel2 != 0) {
					return true;
				}
			}
		}

		return false;

	}

	public void moveTo(int x, int y) {
		// FIXME: Is the consistency check necessary ?
		// might lead hard-to-track bugs.
		if (x < 0 || y < 0)
			return;

		this.xpos = x;
		this.ypos = y;

		needUpdate = true;
	}

	public void moveBy(int dx, int dy) {
		this.xpos += dx;
		this.ypos += dy;

		needUpdate = true;
	}

	public int getX() {
		return this.xpos;
	}

	public int getY() {
		return this.ypos;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public BufferedImage getImage() {
		return this.image;
	}

	// in case you need to keep track of this specific image.
	public final long getZIndex() {
		return this.imageZOrder;
	}

	// visibility control allows methods to decide when to draw this object
	public void hide() {
		visible = false;
		needUpdate = false; // FIXME: Is it really false ?
	}

	public void show() {
		visible = true;
		needUpdate = true;
	}

	public final boolean isVisible() {
		return visible;
	}

}
