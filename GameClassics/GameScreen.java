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
	
	private BufferedImage screen;
	private int frameWidth, frameHeight;
	
	private Queue<SceneObject> sceneObjects;
	private SceneObject affectedObject;
	private MouseEvent mouse;
	
	public GameScreen(Dimension resolution) {
		//super();
		this.addMouseListener(this);
		
		frameWidth = resolution.width;
		frameHeight = resolution.height;
		
		// list of objects to be drawn
		sceneObjects =  new LinkedList<SceneObject>(); //new HashMap<Image, Point>();
		
	}

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
	
	@Override
	protected void paintComponent(Graphics g) {
		// FIXME: is this line necessary ?
		super.paintComponent(g);
		
		for (SceneObject sObj : sceneObjects)
		{
			//if (sObj.needUpdate() && sObj.isVisible()) {
			if (sObj.isVisible()) {
				g.drawImage(sObj.getImage(), sObj.getX(),sObj.getY(),this);
				//sObj.update();
				
				//System.out.print("\nID: "+ sObj.getZIndex() +" X:"+ sObj.getX() +" Y: "+ sObj.getY()); 
			}
		}
		
		this.mouse = null;
		this.affectedObject = null;
		//System.out.print("\nTotal Objs: "+ sceneObjects.size());
		
	}
	
	public MouseEvent getMouse()
	{
		return mouse;
	}
	
	public SceneObject getTargetObject()
	{
		return affectedObject;
	}
	
	@Override
	public void mousePressed(MouseEvent mouse) {

		int x = mouse.getX();
		int y = mouse.getY();
		this.mouse = mouse;

		long maxZ = 0;
		affectedObject = null;
		
		// search for objects
		for (SceneObject sObj : sceneObjects) {
			if (sObj.hit(x, y) && sObj.getZIndex() >= maxZ) {
				affectedObject = sObj;
				maxZ = sObj.getZIndex();
			}
		}
		
		if (affectedObject != null) {
			System.out.print("\nOBJ: " + affectedObject.getZIndex());
		}
		//repaint();

		/*
		int x = mouse.getX();
		int y = mouse.getY();
		
		System.out.print("\nClick: " + mouse.getButton());
		
		////////////RIGHT MOUSE BUTTON BEHAVIOR
		if (mouse.getButton() == MouseEvent.BUTTON3) {
			
			long maxZ = 0;
			SceneObject del = null;
			for (SceneObject o : sceneObjects) {
				if (o.hit(x, y) && o.getZIndex() >= maxZ) {
					del = o;
					maxZ = o.getZIndex();
					//break;
				}
			}
			
			if (del != null) {
				System.out.print("\nDEL: " + del.getZIndex());
				sceneObjects.remove(del);
			}
			repaint();
			return;
			
		}
		
		//////////////// LEFT MOUSE BUTTON BEHAVIOR
		int size = 35;
		
		BufferedImage img2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		
		//Color c = new Color();
		Graphics g = img2.createGraphics();
		g.setColor(new Color(x & 0xFF, y & 0xFF, size & 0xFF));
		g.fillOval(0, 0, size, size);
		g.setColor(new Color(255, 255, 255, 64));
		g.fillOval(3, 3, size/2, size/2);
		
		g.dispose();
		
		addSceneObject(new SceneObject(img2, x-size/2, y-size/2));
		this.repaint();
		
		System.out.print("\nX: "+x + ", Y:"+ y);
		*/
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
