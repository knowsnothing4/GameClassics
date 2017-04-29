package GameClassics;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

public class GameScreen extends JPanel implements MouseListener {

	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private Queue<SceneObject> sceneObjects;
	private SceneObject affectedObject;
	private MouseEvent mouse;

	private GameClassic screenController;
	
	public GameScreen(Dimension resolution) {
		super();
		this.addMouseListener(this);
		
	}

	/*
	public void removeSceneObject(SceneObject sObj)
	{
		sceneObjects.remove(sObj);
	}
	
	public void addSceneObject(SceneObject sObj)
	{
		sceneObjects.add(sObj);
	}
	
	
	public void clear() {
		sceneObjects.clear();
	}
	
	
	public SceneObject[] getSceneObjects()
	{
		SceneObject[] list = new SceneObject[ sceneObjects.size() ];
		
		int k = 0;
		for(SceneObject obj : sceneObjects) {
			list[k++] = obj;
		}
		
		return list; 
		//return (SceneObject[]) sceneObjects.toArray();
	}
	*/

	// GameScreen meets GameClassic
	public void setScreenController(GameClassic game)
	{
		this.screenController = game;
		game.setIODevice(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// FIXME: is this line necessary ?
		//super.paintComponent(g);
		
		if (this.screenController != null) {
			BufferedImage screen = screenController.readScreen();
			// Center the drawing
			g.drawImage(screen , 0,0 ,this);
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
