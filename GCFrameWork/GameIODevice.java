package GCFrameWork;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/*
 *	GameIODevice: Registers input from Mouse/Keyboard as well
 *	as draws images to the screen on request. 
 */

public class GameIODevice extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private MouseEvent mouse;
	
	private KeyEvent keyboard;

	private GameClassic screenController;

	private int xOffset, yOffset;
	
	public GameIODevice(Dimension resolution) {
		super();
		this.addMouseListener(this);
		this.addKeyListener(this);
		
	}
	
	// GameScreen meets GameClassic
	public void setScreenController(GameClassic game)
	{
		this.screenController = game;
		game.setIODevice(this);
		
		BufferedImage screen = screenController.readScreen();
		
		// Center the drawing
		xOffset = (this.getWidth() - screen.getWidth()) / 2;
		yOffset = (this.getHeight() - screen.getHeight()) / 2; 
		//super.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// FIXME: is this line necessary ?
		//super.paintComponent(g);
		
		if (this.screenController != null) {
			BufferedImage screen = screenController.readScreen();
			g.drawImage(screen , 0, 0,this);
			//g.drawImage(screen , xOffset, yOffset,this);
		}	
	}
	
	public KeyEvent getKey()
	{
		KeyEvent k = this.keyboard;
		this.keyboard = null;
		return k;
	}
	
	public MouseEvent getMouse()
	{
		MouseEvent m = this.mouse;
		this.mouse = null;
		return m;
	}
	
	@Override
	public void mousePressed(MouseEvent mouse) {
		this.mouse = mouse;
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		
		if (screenController == null) return;
		
		switch (key.getKeyCode())
		{
		case KeyEvent.VK_ESCAPE:
			screenController.pause();
			repaint();
			break;
		case KeyEvent.VK_ENTER:
			screenController.restart();
			keyboard = null;
			mouse = null;
			break;
		}
			
		keyboard = key;
		
	}

	@Override
	public void mouseClicked(MouseEvent mouse) {
		
	}

	@Override
	public void mouseEntered(MouseEvent mouse) {
				
	}

	@Override
	public void mouseExited(MouseEvent mouse) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
