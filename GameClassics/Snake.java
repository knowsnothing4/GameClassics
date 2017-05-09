package GameClassics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GCFrameWork.*;;

public class Snake extends GameClassic {

	private ArrayList<SceneObject> snake;
	private SceneObject candy;
	private final Color greenish = new Color(0x00, 0xAA, 0x00);
	private final static int snakeRenderSize = 24;
	private final static int candySize = snakeRenderSize -3;
	private final static int snakeSpeed = snakeRenderSize -2;
	private final int margin = 12; 
	private final int gameWidth = maxWidth - margin;
	private final int gameHeight = maxHeight - margin;
	private Point direction;

	public Snake() {
		super("Snake", 800, 600);
	}

	private BufferedImage renderSnakeSegment()
	{ 
		BufferedImage snakeSegment = new BufferedImage(snakeRenderSize, snakeRenderSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = snakeSegment.createGraphics();
		sg.setColor(greenish);
		//sg.fillRect(0, 0, snakeSegment.getWidth(), snakeSegment.getHeight());
		//sg.drawOval(0, 0, snakeSegment.getWidth(), snakeSegment.getHeight());
		sg.fillOval(0, 0, snakeSegment.getWidth(), snakeSegment.getHeight());
		sg.dispose();
		
		return snakeSegment;
	}
	
	private BufferedImage renderCandy(Color color)
	{
		BufferedImage candyImage = new BufferedImage(candySize, candySize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D  bg2d = candyImage.createGraphics();
		bg2d.setColor(color);
		bg2d.fillOval(0, 0, candyImage.getWidth(), candyImage.getHeight());
		bg2d.dispose();
		
		return candyImage;
	}
	
	@Override
	protected void start() {

		BufferedImage img = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

		// background
		Graphics2D bg2d = img.createGraphics();
		bg2d.setColor(greenish);
		bg2d.fillRect(0, 0, img.getWidth(), img.getHeight());

		final int tw = maxWidth - gameWidth;
		final int th = maxHeight - gameHeight;
		bg2d.setColor(new Color(10, 10, 20));
		bg2d.fillRect(tw, th, gameWidth - tw, gameHeight - th);
		bg2d.dispose();

		sceneObjects.add(new SceneObject(img, 0, 0));

		img = renderSnakeSegment();
		int xc = (maxWidth - img.getWidth()) / 2;
		int yc = (maxHeight - img.getHeight()) / 2;

		snake = new ArrayList<SceneObject>();
		snake.add( new SceneObject(img, xc, yc) );
		sceneObjects.add(snake.get(0));
		
		spawnCandy();
		
		direction = new Point(0, 0);
		setFrameRate(25);
		
	}

	private void spawnCandy()
	{	
		// FIXME: This should check if the candy is in a valid spot
		// and doesn't collide with the snake.
		int x = rand(margin, gameWidth - 2*margin);
		int y = rand(margin, gameHeight - 2*margin);
		debug("X: "+ x + " Y: "+ y);
		
		Color c = new Color(rand(127, 255), rand(127, 255), rand(127, 255));
		
		sceneObjects.remove(candy);
		candy = new SceneObject(renderCandy(c), x, y);
		sceneObjects.add(candy);
	}

	private void eat()
	{
		// calculate new coordinates because the snake might not be
		// perfectly aligned with the candy
		SceneObject head = snake.get(0);
		int x = head.getX() + direction.x;
		int y = head.getY() + direction.y;
		SceneObject newSegment = new SceneObject(head.getImage(), x, y);
		snake.add(0, newSegment);
		sceneObjects.add(newSegment);

	}
	
	@Override
	protected void update() 
	{

		// FIXME: check collision with self and borders
		SceneObject head = snake.get(0);
		
		// move snake segments
		for (int i = snake.size() -1; i > 0; i--) {
			int x = snake.get(i-1).getX();
			int y = snake.get(i-1).getY();
			snake.get(i).moveTo(x, y);
			
		}
		
		head.moveBy(direction.x, direction.y);
		
		for (int i = 2; i < snake.size(); i++) {
			
			SceneObject segment = snake.get(i); 
			int xc = (segment.getX() - segment.getWidth()) /2;
			int yc = (segment.getY() - segment.getHeight()) /2;
			
			// FIXME: Not good for curves.
			if (head.collide(segment)) stop();
			//if (head.hit(xc, yc)) start();
		}
		
		// eat delicious candy and grow
		if (head.collide(candy)) {	
			eat();
			spawnCandy();
		}
		
		KeyEvent pressedKey = ioDevice.getKey();
				
		if (pressedKey == null) return;
		//debug("K: "+ pressedKey + " T:"+ KeyEvent.VK_LEFT);
		
		Point newDirection = new Point(0, 0);
		
		switch (pressedKey.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			newDirection = new Point(-snakeSpeed, 0);
			break;
		case KeyEvent.VK_UP:
			newDirection = new Point(0, -snakeSpeed);
			break;
		case KeyEvent.VK_RIGHT:
			newDirection = new Point(snakeSpeed, 0);
			break;
		case KeyEvent.VK_DOWN:
			newDirection = new Point(0, snakeSpeed);
			break;
		}
		
		// can't change to opposite direction
		int xTest = direction.x + newDirection.x;
		int yTest = direction.y + newDirection.y;
		if (xTest != 0 || yTest != 0) direction = newDirection;

	}

	@Override
	protected void stop() {
		running = false;
	}

}
