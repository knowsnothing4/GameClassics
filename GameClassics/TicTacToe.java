package GameClassics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import GCFrameWork.GameClassic;
import GCFrameWork.GameList;
import GCFrameWork.SceneObject;

public class TicTacToe extends GameClassic {

	private static final int GRID = 1; // Tag for click detectors
	private long baseIndex; // maps clicks to board matric position
	private BufferedImage xMark, oMark; // X and O's textures
	private int[][] map; // Game's logic map

	private enum tttLogic {
		row, column, diagonal, win, lose, draw, any
	}

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
		int margin = 4;
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
		// markg.drawLine(0, 0, w, h);
		// markg.drawLine(0, h, w, 0);
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
				// JOptionPane.showMessageDialog(null, "INDEX "+ (i*3 +j));

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

	private int markFreeSpot(int line, tttLogic type) {
		int i = -1;
		int j = -1;

		for (int k = 0; k < 3 && i == -1; k++) {
			switch (type) {
			case row:
				debug("Blocking Row " + line);
				if (map[line][k] == 0) {
					i = line;
					j = k;
				}
				break;
			case column:
				debug("Blocking Column " + line);
				if (map[k][line] == 0) {
					i = k;
					j = line;
				}
				break;
			case diagonal:
				debug("Blocking Dig " + line);
				if (line == 0 && map[k][k] == 0) {
					i = k;
					j = k;
				}
				if (line == 2 && map[2 - k][k] == 0) {
					i = 2 - k;
					j = k;
				}
				break;
			case any:
				debug("Filling any spot");
				if (map[1][1] == 0) {
					i = j = 1;
				} else {
					for (int q = 0; q < 3; q++) {
						if (map[k][q] == 0) {
							i = k;
							j = q;
							break;
						}
					}
				}
				break;
			}
		}
		
		if (i < 0) return -1;
		map[i][j] = -1;

		// debug
		showMap();
		
		// do graphical stuff with the matrix
		long index = i * 3 + j + baseIndex;

		for (SceneObject target : sceneObjects) {
			if (target.getZIndex() == index) {
				int x = target.getX();
				int y = target.getY();
				sceneObjects.add(new SceneObject(oMark, x, y));
				break;
			}
		}
		return 0;
	}

	private int tttAI() {

		for (int i = 0; i < 3; i++) {

			// diaSum += map[i][i];
			int diaSum = 0;
			int rowSum = 0;
			int colSum = 0;
			for (int j = 0; j < 3; j++) {
				rowSum += map[i][j];
				colSum += map[j][i];
				if (i != 1) {
					
					// This equation works for both diagonals
					// 00 11 22
					// 20 11 02
					int z = i + (1 - i) * j;
					diaSum += map[z][j];
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

			// prioritize wins
			if (diaSum == -2)
				return markFreeSpot(i, tttLogic.diagonal);
			if (rowSum == -2)
				return markFreeSpot(i, tttLogic.row);
			if (colSum == -2)
				return markFreeSpot(i, tttLogic.column);

			// watch for blocks
			if (diaSum == 2)
				return markFreeSpot(i, tttLogic.diagonal);
			if (rowSum == 2)
				return markFreeSpot(i, tttLogic.row);
			if (colSum == 2)
				return markFreeSpot(i, tttLogic.column);
		}
		// just find an empty spot and play
		return markFreeSpot(0, tttLogic.any);

	}

	@Override
	public void start() throws Exception {
		super.start();

		sceneObjects.add(new SceneObject(createBackground(), 0, 0));
		createGrid();

		resetMap();
	}

	@Override
	public boolean update() {
		if (!super.update())
			return false;

		MouseEvent mouse = ioDevice.getMouse();
		if (mouse == null)
			return false;

		int x = mouse.getX();// - (GameClassic.defaultGameWidth - maxWidth)/2;
		int y = mouse.getY();// - (GameClassic.defaultGameHeight -maxHeight)/2;

		SceneObject cell = getAffectedObject(x, y);

		// cells can't be clicked twice since a new object is placed on top of them.
		if (cell != null && cell.getTag() == GRID) {
			
			sceneObjects.add(new SceneObject(xMark, cell.getX(), cell.getY()));	
			int index = (int) (cell.getZIndex() - baseIndex);

			map[index / 3][index % 3] = 1;
			tttAI();
		}

		return true;
	}
}
