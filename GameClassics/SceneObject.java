package GameClassics;

import java.awt.Rectangle;

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

	private static long imageId = 0;		// unique "hash" for every image-object created
	private long imageZOrder;					
	
	private BufferedImage image;			// the image itself
	private boolean visible, needUpdate;	// should this image be drawn ?
	
	private int xpos, ypos;					// (x,y) where this image should be rendered
	private double collisionRadius;			// outer circular collision box radius (first check)
	
	
	public SceneObject(BufferedImage image) {
		this(image, 0, 0);
	}
	
	public SceneObject(BufferedImage image, int x, int y) {
	
		this.imageZOrder = imageId++;
		this.xpos = x;
		this.ypos = y;
		this.image = image;
		this.visible = true;
		this.needUpdate = true;	// this will be set to true the first time.
		
		collisionRadius = pythagoras(image.getWidth(), image.getHeight()) * 0.5;
	}

	// scene objects won't be drawn twice if they don't update
	// by changing their internal image or moving.
	public boolean needUpdate()
	{
		return needUpdate;
	}
	
	public void update()
	{
		needUpdate = !needUpdate;
	}
	
	public boolean hit(int x, int y)
	{
		// FIXME: Can we hit hidden objects ?
		
		// calculate relative coordinates
		x -= xpos;
		y -= ypos;

		// check if the coordinates are inside the image
		if (x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight()) return false;

		// check if this point is transparent
		return ( (image.getRGB(x, y) & 0xFF000000) == 0xFF000000);

	}
	
	public boolean collide(final SceneObject sObj)
	{
		// FIXME: Should it check whether both objects are visible ?
		double distance = pythagoras(this.xpos - sObj.xpos, this.ypos - sObj.ypos);
		
		// circular radius check
		if (distance > this.collisionRadius + sObj.collisionRadius) return false;
		
		// bounding box intersection check
		Rectangle r1 = new Rectangle(	this.xpos,
										this.ypos,
										this.image.getWidth(),
										this.image.getHeight()
									);
		
		Rectangle r2 = new Rectangle(	sObj.xpos,
										sObj.ypos,
										sObj.image.getWidth(),
										sObj.image.getWidth()
									);
		
		if (!r1.intersects(r2)) return false;
		
		// transparency intersection check
		// TODO: Intersection transparency check
		
		
		
		return false;
		
	}

	public void moveTo(int x, int y)
	{
		// FIXME: Is the consistency check necessary ?
		// might lead hard-to-track bugs.
		if (x < 0 || y < 0) return;
		
		this.xpos = x;
		this.ypos = y;
		
		needUpdate = true;
	}
	
	public void moveBy(int dx, int dy)
	{
		this.xpos += dx;
		this.ypos += dy;
		needUpdate = true;
	}
	
	public int getX()
	{
		return this.xpos;
	}
	
	public int getY()
	{
		return this.ypos;
	}
	

	public BufferedImage getImage()
	{
		return this.image;
	}
	
	// in case you need to keep track of this specific image.

	public final long getZIndex()
	{
		return this.imageZOrder;
	}
	
	// visibility control allows methods to decide when to draw this object
	
	public void hide()
	{
		visible = false;
		needUpdate = false;	// FIXME: Is it really false ?
	}
	
	public void show()
	{
		visible = true;
		needUpdate = true;
	}
	
	public final boolean isVisible()
	{
		return visible;
	}

	//// AUX
	private static final double pythagoras(int b, int c)
	{
		return Math.sqrt(b*b + c*c);
	}
	
}
