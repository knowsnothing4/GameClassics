package GameClassics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Random;

public class TestFrameWork extends GameClassic {

	//private GameScreen out;
	private SceneObject background, catcher;
	private Random rng;
	
	public TestFrameWork() {
		super("Test FrameWork", 800, 600);
		
		background = null;
		catcher = null;
		rng = new Random();
	}

	private BufferedImage renderBubble(int x, int y, int size)
	{
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
	
	private BufferedImage renderCatcher(int x, int y, int w, int h)
	{
		BufferedImage imgCatcher = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = imgCatcher.createGraphics();
		
		/*
		g.setColor(new Color(0xFF, 0xFF, 0xFF));
		g.fillRect(0, 0, w, h);
		
		return imgCatcher;
		*/
		
		// body
		g.setColor(new Color(0xAF, 0xFF, 0xAA, 0xFF));
		g.fillRoundRect(0, 0, w, h, 8, 8);
		
		// splitter
		g.setColor(new Color(0x00, 0x00, 0x55, 0xFF));
		g.drawLine(0, h /2, w, h / 2);
		
		return imgCatcher;
		
	}
	
	
	@Override
	public void start() throws Exception
	{
		super.start();
		
		// create background image
		BufferedImage bg = new BufferedImage(this.maxWidth, this.maxHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics bgGraphics = bg.createGraphics();
		
		// fill the bg
		bgGraphics.setColor(new Color(10, 10, 200));
		bgGraphics.fillRect(0, 0, bg.getWidth(), bg.getHeight());
		
		bgGraphics.setColor(new Color(10, 20, 255));
		int spacing = bg.getWidth() / 100;
		for (int i = 0; i < bg.getWidth(); i += spacing) {
			
			bgGraphics.drawRect(i, i, bg.getWidth() -2*i, bg.getHeight() -2*i);
		}
		
		bgGraphics.dispose();
		
		background = new SceneObject(bg, 0, 0);
		sceneObjects.add(background);
		
		///// Creating the cather object
		
		int catcherW = 110, catcherH = 14;
		int x = (maxWidth - catcherW) / 2;
		int y = 0; //maxHeight - catherH / 2;
		BufferedImage imgCatcher = renderCatcher(x, y, catcherW, catcherH);
		
		catcher = new SceneObject(imgCatcher, x, y);
		sceneObjects.add(catcher);

	}
	
	private void moveCatcher(int amplitude)
	{
		amplitude = rng.nextInt(amplitude * 2) - amplitude;
	
		catcher.moveBy(amplitude, 0);
		
		if (catcher.getX() < 0 || catcher.getX() + catcher.getImage().getWidth() > maxWidth)
			amplitude *= -2;
	
		catcher.moveBy(amplitude, 0);
	}
	
	@Override
	public boolean update() {
		
		// is the game running ?
		if (!super.update()) return false;
		
		for (SceneObject bubble: sceneObjects)
		{

			// move everything but the catcher and background
			if (bubble.getZIndex() != background.getZIndex() &&
				bubble.getZIndex() != catcher.getZIndex())
				bubble.moveBy(0, -1);
			
			int bubbleCenterX = (bubble.getX() - bubble.getImage().getWidth()/2);
			//int bubbleCenterY = (bubble.getY() + bubble.getImage().getHeight()/2);
			int catcherBase = catcher.getY() + catcher.getImage().getHeight();
			
			if (bubble.getY() <= catcherBase &&
				bubbleCenterX > catcher.getX() &&
				bubbleCenterX < catcher.getX() + catcher.getImage().getWidth() )
			{
				sceneObjects.remove(bubble);
				score -= 10;
				debug("SCORE: " + score);
				break;
			}
			
			if (bubble.getY() < 0) {
				sceneObjects.remove(bubble);
				score += 10;
				debug("SCORE: " + score);
				break;
			}
		}
		moveCatcher(30);
		
		// process mouse events
		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null) return false;
		int x = mouse.getX();
		int y = mouse.getY();
		
		switch (mouse.getButton()) {
		case 1:
			int size = 35;
			x -= size /2;
			y -= size /2;
			
			sceneObjects.add(new SceneObject(renderBubble(x, y, size), x, y));
			break;
			
		case 3:
			debug("BUTTON: "+ mouse.getButton());
			SceneObject target = getAffectedObject(x, y);
			
			// FIXME: Should not have to check for background removal
			if (target != null && target.getZIndex() != background.getZIndex())
				sceneObjects.remove(target);
			break;
		}
		debug("#NUM_OBJ: "+ sceneObjects.size());
		return true;
	}

}
