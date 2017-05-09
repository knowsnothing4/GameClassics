package GameClassics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JOptionPane;

import GCFrameWork.GameClassic;
import GCFrameWork.GameList;
import GCFrameWork.SceneObject;

public class TicTacToe extends GameClassic {

	private static final int GRID = 1; // Tag for click detectors
	private long baseIndex; // maps clicks to board matric position
	private BufferedImage xMark, oMark; // X and O's textures
	private int[][] map; // Game's logic map
	private SceneObject titleDisplay;

	private static final String[] blockMessages = {
			"Nope.",
			"Get blocked!",
			"Hmmm... no.",
			"Try again!",
			"Nice try.",
			"Not this way.",
			"Not so easy, huh?",
			"Not so fast!",
			"What?",
			"Ha ha ha!",
			"Think again!",
			"How am I playing ?",
			"Oh oh...",
			":-)",
	};
	

	public TicTacToe() {
		// super("Tic Tac Toe", 600, 480);
		super("Tic Tac Toe", 800, 600);
	}

	private BufferedImage createBackground() {
		BufferedImage bg = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D bg2d = bg.createGraphics();

		// black background
		bg2d.setBackground(new Color(0xff, 0xff, 0xff));
		bg2d.clearRect(0, 0, maxWidth, maxWidth);

		// outline
		int margin = 8;
		bg2d.setColor(new Color(0x10, 0x00, 0x00));
		bg2d.drawRoundRect(margin, margin, maxWidth - 2 * margin, maxHeight - 2 * margin, margin, margin);

		// "Grid"
		int offset = maxWidth / 8;
		bg2d.setColor(new Color(0x10, 0x10, 0xff));
		bg2d.fillRect(offset, offset, maxWidth - 2 * offset, maxHeight - 2 * offset);
		bg2d.dispose();
		
		return bg;
	}

	private BufferedImage gridObject(int w, int h) {
		BufferedImage gObj = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D bg2d = gObj.createGraphics();

		// black background
		bg2d.setBackground(new Color(0xff, 0xff, 0xff));
		bg2d.clearRect(0, 0, gObj.getWidth(), gObj.getHeight());

		return gObj;
	}

	private void drawXO(int w, int h) {
		xMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		oMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D markg = xMark.createGraphics();

		markg.setColor(new Color(0xff, 0xff, 0xff));
		markg.fillRect(0, 0, w, h);

		// Draw X
		int thickness = 5;
		int dirFix = 30;
		markg.setColor(new Color(0xff, 0, 0));
		markg.rotate(Math.PI / 4, w / 2, h / 2);
		markg.fillRect(dirFix, h / 2 - thickness, w - dirFix * 2, 2 * thickness);
		markg.fillRect(w / 2 - thickness, 0, 2 * thickness, h);
		markg.dispose();

		markg = oMark.createGraphics();
		markg.setColor(new Color(0xff, 0xff, 0xff));
		markg.fillRect(0, 0, w, h);

		// Draw O
		markg.setColor(new Color(0, 0xff, 0));
		dirFix /= 2;
		markg.fillOval(dirFix, dirFix, w - dirFix * 2, h - dirFix * 2);
		int a = dirFix + thickness;
		markg.setColor(new Color(0xff, 0xff, 0xff));
		markg.fillOval(a, a, w - 2 * a, h - 2 * a);
		markg.dispose();

	}

	private void createGrid() {
		final int xOffset = maxWidth / 8, yOffset = xOffset;
		final int spacing = 10;
		final int wDim = (maxWidth - xOffset * 2) / 3 - spacing / 2;
		final int hDim = (maxHeight - yOffset * 2) / 3 - spacing / 2;

		// create a 3x3 clicking area
		baseIndex = -1;
		for (int i = 0; i < 3; i++) {

			int y = yOffset + i * (hDim + spacing);
			for (int j = 0; j < 3; j++) {
				int x = xOffset + j * (wDim + spacing);

				SceneObject gridItem = new SceneObject(gridObject(wDim, hDim), x, y);

				if (baseIndex < 0)
					baseIndex = gridItem.getZIndex();

				gridItem.setTag(GRID);
				sceneObjects.add(gridItem);
			}
		}

		// Create X and O with the grid size
		drawXO(wDim, hDim);
	}

	///////////// Some A.I.
	private void resetMap() {
		map = new int[3][3];
	}

	private void showMap() {
		for (int i = 0; i < 3; i++) {
			System.out.print("\n");
			for (int j = 0; j < 3; j++) {
				System.out.print(map[i][j] + " ");
			}
		}
	}

	// When any of the rows/columns/diagonals sums up to |2|
	// it means we have a strategic decision to make.
	// 0 0 0 = 0
	// 0 0 1 = 1
	// 0 0 -1 = -1
	// 0 1 0 = 1
	// 0 1 1 = 2 -> Must block
	// 0 1 -1 = 0
	// 0 -1 0 = -1
	// 0 -1 1 = 0
	// 0 -1 -1 = -2 -> Must win
	
	// 1 0 0 = 1
	// 1 0 1 = 2 -> Must block
	// 1 0 -1 = 0
	// 1 1 0 = 2 -> Must block
	// 1 1 1 = 3 -> Player wins
	// 1 1 -1 = 1
	// 1 -1 0 = 0
	// 1 -1 1 = 1
	// 1 -1 -1 = -1
	
	// -1 0 0 = -1
	// -1 0 1 = 0
	// -1 0 -1 = -2 -> Must win
	// -1 1 0 = 0
	// -1 1 1 = 1
	// -1 1 -1 = -1
	// -1 -1 0 = -2 -> Must win
	// -1 -1 1 = -1
	// -1 -1 -1 = -3 -> A.I. wins
	private void ticTacToeAI()
	{
		final int[][][] sequences = {
				{{0,0}, {1,1}, {2,2}},	// diagonals	
				{{2,0}, {1,1}, {0,2}},
				{{0,0}, {0,1}, {0,2}},	// rows
				{{1,0}, {1,1}, {1,2}},
				{{2,0}, {2,1}, {2,2}},
				{{0,0}, {1,0}, {2,0}},	// columns
				{{0,1}, {1,1}, {2,1}},
				{{0,2}, {1,2}, {2,2}}//,
				//{{2,1}, {2,2}, {1,2}}	// a possible exception (FIXME).
		};
		
		int[] bestSpot = null;
		if (map[1][1] == 0) bestSpot = new int[]{1, 1};
		
		// debug
		showMap();
		
		// Test the sequences
		for (int s = 0; s < sequences.length; s++) {
			
			int sum = 0;
			int[] blankSpot = null;
			for (int k = 0; k < 3; k++) {
				int i = sequences[s][k][0];
				int j = sequences[s][k][1];
				
				// found a blank spot ?
				if (map[i][j] == 0) {
					blankSpot = sequences[s][k];
					if (bestSpot == null) bestSpot = blankSpot;
				} else {
					sum += map[i][j];
				}
			}
			
			// prioritize winning
			if (sum == -2) {
				displayTitle("You lose", Color.gray, 0.9f);
				bestSpot = blankSpot;
				this.stop();
				break;
			}
			
			// need to block ?
			if (sum == 2) {
				int msg = rand(0, blockMessages.length);
				displayTitle(blockMessages[msg], Color.ORANGE, 0.9f);
				bestSpot = blankSpot;
			}
			
		}
		if (bestSpot == null) return; 
		int i = bestSpot[0];
		int j = bestSpot[1];
		map[i][j] = -1;
	
		// do graphical stuff with the matrix
		SceneObject cell = getCell(i, j);
		
		int x = cell.getX();
		int y = cell.getY();
		sceneObjects.add(new SceneObject(oMark, x, y));
			
	}
	
	private SceneObject getCell(int i, int j)
	{
		SceneObject cell = null;
		long zIndex = i * 3 + j + baseIndex;
		for (SceneObject target : sceneObjects) {
			if (target.getZIndex() == zIndex) {
				cell = target;
				break;
			}
		}
		return cell;
	}
	
	private void strike(int direction)
	{
		// broken. Needs thinking.
		SceneObject cellA = null, cellB = null;
		
		switch(direction) {
		case 0:
			// diagonal 1
			cellA = getCell(0, 0);
			cellB = getCell(2, 2);
			break;
		case 1:
			// diagonal 2  
			cellA = getCell(2, 0);
			cellB = getCell(0, 2);
			break;
		case 2:
			// row 1
			cellA = getCell(0, 0);
			cellB = getCell(0, 2);
			break;
		case 3:
			// row 2
			cellA = getCell(1, 0);
			cellB = getCell(1, 2);
			break;
		case 4:
			// row 2
			cellA = getCell(2, 0);
			cellB = getCell(2, 2);
			break;
		case 5:
			// column 1
			cellA = getCell(0, 0);
			cellB = getCell(2, 0);
			break;
		case 6:
			// column 2
			cellA = getCell(0, 1);
			cellB = getCell(2, 1);
			break;
		case 7:
			// column 3
			cellA = getCell(0, 2);
			cellB = getCell(2, 2);
			break;
		}
		
		int x0 = cellA.getX();
		int y0 = cellA.getY();
		int x1 = cellB.getX();
		int y1 = cellB.getY();
		int w = Math.abs(x1 - x0) + 2;
		int h = Math.abs(y1 - y0) + 2;
		int xC = x0 + cellA.getWidth() / 2;
		int yC = y0 + cellA.getHeight() / 2;
		
		BufferedImage line = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lineG = line.createGraphics();
		lineG.setColor(Color.BLACK);
		lineG.drawLine(x1, y1, x0, y0);
		lineG.dispose();
		SceneObject strikeLine = new SceneObject(line, xC, yC);
		sceneObjects.add(strikeLine);
		JOptionPane.showMessageDialog(null, "X0: "+x0 +" Y0: "+y0 +" X1: "+x1 +" Y1"+y1);
	}
	
	private void displayTitle(String text, Color color, float alpha)
	{
		// clear previous title
		if (this.titleDisplay != null) {
			sceneObjects.remove(titleDisplay);
			Graphics2D t = titleDisplay.getImage().createGraphics();
			t.setColor(Color.white);
			t.drawRect(0, 0, titleDisplay.getWidth(), titleDisplay.getHeight());
		}
		
		// creates a title
		Font font = new Font("Helvetica", Font.PLAIN, 36);
		titleDisplay = new SceneObject(text, font, color, 0, 0);
		int xTitle = (maxWidth - titleDisplay.getWidth()) /2;
		titleDisplay.moveTo(xTitle, 8);
		titleDisplay.setAlpha(alpha);
		sceneObjects.add(titleDisplay);
		
	}

	@Override
	protected void start()
	{
		sceneObjects.add(new SceneObject(createBackground(), 0, 0));
		createGrid();
		
		displayTitle("Tic Tac Toe", new Color(80, 20, 0), 0.1f);

		resetMap();
	}

	@Override
	protected void update() {

		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null) return;

		int x = mouse.getX();// - (GameClassic.defaultGameWidth - maxWidth)/2;
		int y = mouse.getY();// - (GameClassic.defaultGameHeight -maxHeight)/2;

		SceneObject cell = getAffectedObject(x, y);

		// cells can't be clicked twice since a new object is placed on top of them.
		if (cell != null && cell.getTag() == GRID) {
			
			sceneObjects.add(new SceneObject(xMark, cell.getX(), cell.getY()));	
			int index = (int) (cell.getZIndex() - baseIndex);

			map[index / 3][index % 3] = 1;
			ticTacToeAI();
		}
		
	}
	
	@Override
	protected void stop() {
		running = false;
	}

}
