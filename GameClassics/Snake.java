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
	public void start() throws Exception {
		super.start();

		BufferedImage img = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

		// background
		Graphics2D bg2d = img.createGraphics();
		bg2d.setColor(greenish);
		bg2d.fillRect(0, 0, img.getWidth(), img.getHeight());

		final int tw = maxWidth - gameWidth;
		final int th = maxHeight - gameHeight;
		bg2d.setColor(new Color(10, 10, 20));
		bg2d.fillRect(tw, th, gameWidth - 2*tw, gameHeight - 6*th);
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
		setFrameRate(20);
		
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
	
	@Override
	protected void update() 
	{

		// FIXME: check collision with self and borders
		SceneObject head = snake.get(0);
		head.moveBy(direction.x, direction.y);
		
		// move snake segments
		for (int i = snake.size() -1; i > 0; i--) {
			int x = snake.get(i-1).getX();
			int y = snake.get(i-1).getY();
			snake.get(i).moveTo(x, y);
		}
		
		// eat delicious candy and grow
		if (head.collide(candy)) {
			
			// calculate new coordinates because the snake might not be
			// perfectly aligned with the candy
			int x = head.getX() + direction.x;
			int y = head.getY() + direction.y;
			SceneObject newSegment = new SceneObject(head.getImage(), x, y);
			snake.add(0, newSegment);
			sceneObjects.add(newSegment);

			spawnCandy();
		}
		
		KeyEvent pressedKey = ioDevice.getKey();
				
		if (pressedKey == null) return;
		//debug("K: "+ pressedKey + " T:"+ KeyEvent.VK_LEFT);
		
		
		switch (pressedKey.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			//snake.moveBy(-snakeSpeed, 0);
			direction = new Point(-snakeSpeed, 0);
			break;
		case KeyEvent.VK_UP:
			//snake.moveBy(0, -snakeSpeed);
			direction = new Point(0, -snakeSpeed);
			break;
		case KeyEvent.VK_RIGHT:
			//snake.moveBy(snakeSpeed, 0);
			direction = new Point(snakeSpeed, 0);
			break;
		case KeyEvent.VK_DOWN:
			//snake.moveBy(0, snakeSpeed);
			direction = new Point(0, snakeSpeed);
			break;
		}

	}

}
