package GameClassics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import GCFrameWork.GameClassic;
import GCFrameWork.SceneObject;

/*
 * A game that tests the framework's ability to handle mouse clicks, 
 * object inclusion, removal and tracking.
 * 
 * How to play:
 * -----------
 * 
 * 	1. Click the left mouse button to add bubbles
 * 	2. Click the right mouse button to remove bubbles
 * 	3. When the bubble popper hits your bubbles you lose 10 points
 * 	4. when your bubbles make it to end you get 10 points.
 * 
 */

public class TestFrameWork extends GameClassic {

	private SceneObject background, bubblePopper, scoreDisplay;
	private int velocity;
	private static final int bubbleSize = 35;
	private static final int REMOVABLE = 1;
	
	public TestFrameWork() {
		super("Test FrameWork", 800, 600);
		
		background = null;
		bubblePopper = null;
	}

	/////////////////// object rendering section 
	private BufferedImage renderBubble(int x, int y)
	{
		int size = bubbleSize;
		BufferedImage bubble = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bubble.createGraphics();
		
		// shadow
		g.setColor(new Color(0x00, 0x00, 0x00, 0x2F));
		g.fillOval(0, 0, size, size);
		size -= 2;

		// body
		g.setColor(new Color(x & 0xFF, y & 0xFF, size & 0xFF, 0x7F));
		g.fillOval(0, 0, size, size);
		g.setColor(new Color(x & 0xFF, y & 0xFF, size & 0xFF));
		g.fillOval(0, 0, size-1, size-1);
		
		// light
		g.setColor(new Color(255, 255, 255, 64));
		g.fillOval(3, 3, size/2, size/2);
		g.dispose();

		return bubble;
	}
	
	private BufferedImage renderBubblePopper(int w, int h)
	{
		BufferedImage imgCatcher = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = imgCatcher.createGraphics();
			
		// body
		g.setColor(new Color(0xAF, 0xFF, 0xAA, 0xFF));
		g.fillRoundRect(0, 0, w, h, 8, 8);
		
		// splitter
		g.setColor(new Color(0x00, 0x00, 0x55, 0xFF));
		g.drawLine(0, h /2, w, h / 2);
		
		return imgCatcher;
		
	}
	
	private BufferedImage renderBackground()
	{
		BufferedImage bg = new BufferedImage(this.maxWidth, this.maxHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics bgGraphics = bg.createGraphics();
		
		// fill the bg
		bgGraphics.setColor(new Color(10, 10, 200, 127));
		bgGraphics.fillRect(0, 0, bg.getWidth(), bg.getHeight());
		
		// do patterns
		bgGraphics.setColor(new Color(100, 200, 255, 65));
		int spacing = bg.getWidth() / 100;
		for (int i = 0; i < bg.getWidth(); i += spacing) {
			
			bgGraphics.drawRect(i, i, bg.getWidth() -2*i, bg.getHeight() -2*i);
		}
		
		bgGraphics.dispose();
		return bg;
		
	}
		
	private void markTarget(SceneObject target)
	{	
		Graphics g = target.getImage().createGraphics();
		g.setColor(new Color(0xff, 0xff, 0xff));
		g.drawOval(5, 5, 20, 20);
		g.drawLine(8, 8, 22, 22);
		g.drawLine(8, 22, 22, 8);
		g.dispose();
	}

	
	// Keeps the score valus consistent and prints it on screen 
	private void score(int ds)
	{
		this.score += ds;
		if (score < 0) score = 0;
		
		BufferedImage imgScore = scoreDisplay.getImage(); 
		Graphics2D s = imgScore.createGraphics();
		
		s.setBackground(new Color(0, 0 ,0 ,0));
		s.clearRect(0, 0, imgScore.getWidth(), imgScore.getHeight());
		if (ds >= 0)
			s.setColor(new Color(0xA0, 0xCF ,0x10));
		else
			s.setColor(new Color(0xFF, 0x10 ,0));
		
		String padding = "000";
		if (score > 9) padding = "00";
		if (score > 99) padding = "0";
		
		s.setFont(new Font("Times New Roman", Font.BOLD, 28));
		s.drawString(padding + score,imgScore.getWidth()/2, imgScore.getHeight()/2);
		s.dispose();
		
	}
	
	@Override
	public void start() throws Exception
	{
		super.start();
		
		// create background image
		background = new SceneObject(renderBackground(), 0, 0);
		sceneObjects.add(background);
		
		///// Creating the bubble popper object
		int bubblePopperW = 110, bubblePopperH = 14;
		int x = (maxWidth - bubblePopperW) / 2;
		int y = 0;
		BufferedImage imgCatcher = renderBubblePopper(bubblePopperW, bubblePopperH);
		
		bubblePopper = new SceneObject(imgCatcher, x, y);
		sceneObjects.add(bubblePopper);
		
		///// Creates the score at the bottom of the screen
		int scoreBox = 150;
		scoreDisplay = new SceneObject(new BufferedImage(scoreBox, scoreBox, BufferedImage.TYPE_INT_ARGB),
										maxWidth - scoreBox, maxHeight - scoreBox);
		sceneObjects.add(scoreDisplay);
		score(0);
	}	

	// game AI
	private void bubblePopperAI()
	{
		// don't move if we can't see any bubbles
		if (sceneObjects.size() <= 3) return;
		
		int minDistance = this.maxHeight;
		SceneObject target = null;
		
		// find the object closest to the border
		for (SceneObject bubble: sceneObjects) {
			int distance = bubble.getY();

			if (bubble.getTag() == REMOVABLE && distance < minDistance)
			{
				minDistance = distance;
				target = bubble;
			}
		}
		
		// marks this target bubble with an 'X'
		markTarget(target);
	
		// movement calculation
		int bw = bubblePopper.getImage().getWidth()/2;
		int tw = target.getImage().getWidth()/2;
		int acceleration = (target.getX() + tw) - (bubblePopper.getX() + bw);
		velocity += acceleration;
		velocity %= 10 + sceneObjects.size();
		bubblePopper.moveBy(velocity, 0);
			
		// collision detection
		if (minDistance < 5 && bubblePopper.collide(target)) {
			sceneObjects.remove(target);
			score(-10);
		}
	
	}

	@Override
	public boolean update() {
		
		// is the game running ?
		if (!super.update()) return false;
		
		for (SceneObject bubble: sceneObjects)
		{
			// move everything up except for the popper and background
			if (bubble.getTag() == REMOVABLE) {
				bubble.moveBy(0, -1);

				// tracks points
				if (bubble.getY() < 0) {
					sceneObjects.remove(bubble);
					score(10);
					break;
				}
			}
				
		}
		// run the A.I.
		bubblePopperAI();
		
		// process mouse events
		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null) return false;
		int x = mouse.getX();
		int y = mouse.getY();
		
		switch (mouse.getButton())
		{
		case 1:
		
			// Adds new bubbles
			x -= bubbleSize /2;
			y -= bubbleSize /2;
			SceneObject newObj = new SceneObject(renderBubble(x, y), x, y);
			newObj.setTag(REMOVABLE);
			
			sceneObjects.add(newObj);
			break;
			
		case 3:
			
			// Removes bubbles
			SceneObject target = getAffectedObject(x, y);
			
			if (target != null && target.getTag() == REMOVABLE)
				sceneObjects.remove(target);
			break;
		}
		debug("#NUM_OBJ: "+ sceneObjects.size());
		return true;
	}

}
