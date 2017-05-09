package GameClassics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

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

	private SceneObject bubblePopper, scoreDisplay, bubbleCount;
	private int velocity, collectedBubbles, maxBubbles;
	private static final int bubbleSize = 35;
	private static final int REMOVABLE = 1;
	private static final int SCOREPOP = 2;
	private static final int BUBBLE_SPAWN_RATE = 4;	// A number between 0 and 100
	
	public TestFrameWork() {
		super("Test FrameWork", 800, 600);
		
		bubblePopper = null;
		collectedBubbles = 0;
		maxBubbles = 20;
		setFrameRate(80);
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
		
		// reflex
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

	private void scorePopEffect(int value, int x, int y)
	{
		String s= "" + value;
		if (value > 0) s += "+"; 
	
		// font setup
		Font f = new Font("Helvetica", Font.BOLD, 14);
		
		// red for negatives, green for postives
		Color c = (value < 0 ? Color.red:Color.green);
		
		SceneObject scorePop = new SceneObject(s, f, c, x, y);
		scorePop.setTag(SCOREPOP);
		sceneObjects.add(scorePop);
	}
	
	private void fadeOutPopScores()
	{
		final float decayDelta = 0.06f;
		LinkedList<SceneObject> toRemove = new LinkedList<SceneObject>();
		
		for (SceneObject ps : sceneObjects) {
			
			if (ps.getTag() == SCOREPOP) {
				
				float newAlpha = ps.getAlpha() -decayDelta;
				
				if (newAlpha < 0.0) {
					toRemove.add(ps);
					break;
				} else {
					ps.setAlpha(newAlpha);
				}
			}
		}
		
		sceneObjects.removeAll(toRemove);
	}
	
	// Keeps the score values consistent and prints it on screen 
	private void score(int ds) 
	{
		this.score += ds;
		if (score < 0) score = 0;
		
		sceneObjects.remove(scoreDisplay);
		
		String paddedScore = score + "";
		if (score < 10) paddedScore = "0" + paddedScore;
		if (score < 100) paddedScore = "00" + paddedScore;
		
		Font f = new Font("Verdana", Font.BOLD, 20);
		scoreDisplay = new SceneObject("Score:" + paddedScore, f, Color.WHITE, 0, 0);
		final int margin = 8;
		final int x = margin;
		final int y = maxHeight - scoreDisplay.getHeight()*3 -margin;
		scoreDisplay.moveTo(x, y);
		
		sceneObjects.add(scoreDisplay);
	}
	
	private boolean spawnBubble(int x, int y)
	{	
		if (y < bubblePopper.getHeight()*2) return false;

		// Adds new bubbles
		x -= bubbleSize /2;
		y -= bubbleSize /2;
		SceneObject newObj = new SceneObject(renderBubble(x, y), x, y);
		newObj.setTag(REMOVABLE);
		
		sceneObjects.add(newObj);
		
		return true;
	}
	
	@Override
	protected void start()
	{
		
		// create background image
		sceneObjects.add(new SceneObject(renderBackground(), 0, 0));
		
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
		displayBubbleCount();
	}	

	private void displayBubbleCount()
	{
		if (scoreDisplay == null) return;
		
		sceneObjects.remove(bubbleCount);
		
		String paddedCount = collectedBubbles + "";
		if (collectedBubbles < 10) paddedCount = "0" + paddedCount;
		
		Font f = new Font("Helvetica", Font.BOLD, 18);
		bubbleCount = new SceneObject("Bubbles: " + paddedCount +"/"+ maxBubbles, f, Color.GREEN, 0, 0);
		final int x = scoreDisplay.getX();
		final int y = scoreDisplay.getY() - bubbleCount.getHeight(); 
		bubbleCount.moveTo(x, y);
		bubbleCount.setAlpha(0.60f);
		
		sceneObjects.add(bubbleCount);
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
		
		// CONCURRENCY BUGS ?
		if (target == null) return;
		// marks this target bubble with an 'X'
		markTarget(target);
	
		// movement calculation
		int bw = bubblePopper.getWidth()/2;
		int tw = target.getWidth()/2;
		int acceleration = (target.getX() + tw) - (bubblePopper.getX() + bw);
		velocity += acceleration;
		velocity %= 10 + sceneObjects.size();
		bubblePopper.moveBy(velocity, 0);
			
		// collision detection
		if (minDistance < 5 && bubblePopper.collide(target)) {
			
			int x = target.getX() + target.getWidth() / 2;
			int y = target.getY() + target.getHeight() / 2;
			scorePopEffect(-10, x, y);
			sceneObjects.remove(target);
			score(-10);
		}
	
	}

	@Override
	protected void update() 
	{
		//sceneObjects.removeIf(e->e.getY() <= 0 && e.getTag()== REMOVABLE);
		
		for (SceneObject bubble: sceneObjects)
		{
			// move everything up except for the popper and background
			if (bubble.getTag() == REMOVABLE) {
				bubble.moveBy(0, -1);

				// tracks points
				if (bubble.getY() <= 0) {
					sceneObjects.remove(bubble);
					scorePopEffect(10, bubble.getX(), bubble.getHeight());
					score(10);
					break;
				}
			}
			
			if (bubble.getTag() == SCOREPOP) bubble.moveBy(0, -2); 
				
		}
		
		fadeOutPopScores();
		
		// run the A.I.
		bubblePopperAI();
		
		// spawn a random bubble
		if (rand(0, 100) < BUBBLE_SPAWN_RATE) {
			
			final int margin = bubbleSize + 5;
			final int yy = maxHeight - margin;
			int xy = rand(margin, maxWidth - 2*margin);
			
			spawnBubble(xy, yy);
		}
		
		// process mouse events
		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null) return;
		int x = mouse.getX();
		int y = mouse.getY();
		
		switch (mouse.getButton())
		{
		case 1:
		
			if (collectedBubbles > 0 && spawnBubble(x, y)) {
				--collectedBubbles;
				displayBubbleCount();	
			}
			
			break;
			
		case 3:
			
			if (collectedBubbles < maxBubbles) {
				// Removes bubbles
				SceneObject target = getAffectedObject(x, y);
				
				if (target != null && target.getTag() == REMOVABLE) {
					sceneObjects.remove(target);
					++collectedBubbles;
					displayBubbleCount();
				}		
			}
			
			break;
		}
		//debug("#NUM_OBJ: "+ sceneObjects.size());
	}

	@Override
	protected void stop() {
		
	}

}
