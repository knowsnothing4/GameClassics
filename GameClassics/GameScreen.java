package GameClassics;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GameScreen extends JPanel implements MouseListener {

	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MouseEvent mouse;

	private GameClassic screenController;

	private int xOffset, yOffset;
	
	public GameScreen(Dimension resolution) {
		super();
		this.addMouseListener(this);
		
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
		super.paintComponent(g);
		
		if (this.screenController != null) {
			BufferedImage screen = screenController.readScreen();
			g.drawImage(screen , 0, 0,this);
			//g.drawImage(screen , xOffset, yOffset,this);
		}	
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

	

}
