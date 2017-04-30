package GameClassics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class TicTacToe extends GameClassic {

	private static final String GRID = "GRID";
	private BufferedImage xMark, oMark;
	private boolean xTurn;
	private int[][] board;
	
	public TicTacToe() {
		//super("Tic Tac Toe", 600, 480);
		super("Tic Tac Toe", 800, 600);
	}
	
	private BufferedImage createBackground()
	{
		BufferedImage bg = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
				
		Graphics2D bg2d = bg.createGraphics();
		
		// black background
		bg2d.setBackground(new Color(0xff, 0xff, 0xff));
		bg2d.clearRect(0, 0, maxWidth, maxWidth);
		
		// outline
		int margin = 4;
		bg2d.setColor(new Color(0x10, 0x00, 0x00));
		bg2d.drawRoundRect(margin, margin, maxWidth-2*margin, maxHeight-2*margin, margin, margin);
		
		// "Grid"
		int offset = maxWidth / 8;
		bg2d.setColor(new Color(0x10, 0x10, 0xff));
		bg2d.fillRect(offset, offset, maxWidth-2*offset, maxHeight-2*offset);
		
		bg2d.dispose();
		
		return bg;
	}
	
	private BufferedImage gridObject(int w, int h)
	{
		BufferedImage gObj = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			
		Graphics2D bg2d = gObj.createGraphics();

		// black background
		bg2d.setBackground(new Color(0xff, 0xff, 0xff));
		bg2d.clearRect(0, 0, gObj.getWidth(), gObj.getHeight());
		
		return gObj;
	}
	
	
	private void createGrid()
	{
		final int xOffset = maxWidth / 8, yOffset = xOffset;
		final int spacing = 10;
		final int wDim = (maxWidth - xOffset*2)/3 -spacing /2;
		final int hDim = (maxHeight - yOffset*2)/3 -spacing /2;
		
		// create a 3x3 clicking area
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int x = xOffset + i*(wDim + spacing);
				int y = yOffset + j*(hDim + spacing);
				
				SceneObject gridItem = new SceneObject(gridObject(wDim, hDim), x, y);
				gridItem.setTag(GRID);
				sceneObjects.add(gridItem);
				
			}
		}
		
		// Create X and O with the grid size
		drawXO(wDim, hDim);
	}

	private void drawXO(int w, int h)
	{
		xMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		oMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D markg = xMark.createGraphics();
		
		markg.setColor(new Color(0xff, 0xff, 0xff));
		markg.fillRect(0, 0, w, h);
		
		// Draw X
		markg.setColor(new Color(0xff, 0, 0));
		markg.drawLine(0, 0, w, h);
		markg.drawLine(0, h, w, 0);
		markg.dispose();
		
		markg = oMark.createGraphics();
		markg.setColor(new Color(0xff, 0xff, 0xff));
		markg.fillRect(0, 0, w, h);
		
		// Draw O
		markg.setColor(new Color(0, 0xff, 0));
		markg.drawOval(0, 0, w, h);
		markg.dispose();
		
	}
	
	@Override
	public void start() throws Exception
	{
		super.start();
		
		sceneObjects.add(new SceneObject(createBackground(), 0, 0));
		
		createGrid();
		xTurn = true;
	}
	
	@Override
	public boolean update()
	{
		if (!super.update()) return false;
		
		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null) return false;
		
		int x = mouse.getX();// - (GameClassic.defaultGameWidth - maxWidth)/2;
		int y = mouse.getY();// - (GameClassic.defaultGameHeight -maxHeight)/2;
		
		SceneObject cell = getAffectedObject(x, y);
		
		if (cell != null && cell.tag(GRID)) {
			debug("ID: "+ cell.getZIndex());
			
			if (xTurn) {
				sceneObjects.add(new SceneObject(xMark, cell.getX(), cell.getY()));	
			} else
				sceneObjects.add(new SceneObject(oMark, cell.getX(), cell.getY()));
			
			xTurn = !xTurn;
		} 
		
		
		return true;
	}
}
