package GCFrameWork;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.RepaintManager;

public abstract class GameClassic {

	protected static final int defaultGameWidth = 800, defaultGameHeight = 600;
	private static final String cfgFilePath = "GameClassics.cfg";
	private static final int baseFrameRate = 100;
	
	protected String name;
	protected int maxWidth, maxHeight;

	private int frameRate, frameCounter; 
	protected BufferedImage screen;
	protected Graphics screenGraphics;
	protected Queue<SceneObject> sceneObjects;
	protected GameIODevice ioDevice;
	private SceneObject pauseScreen;
	
	protected long score;
	private boolean running;
	protected static final Random rng = new Random();	
	
	public GameClassic(String name, int width, int height) {

		this.name = name;
		this.score = 0;
		this.running = false;
		
		this.maxWidth = width;
		this.maxHeight = height;
		
		this.frameCounter = 0;
		this.frameRate = baseFrameRate;
		this.screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.screenGraphics = screen.createGraphics();
		sceneObjects =  new LinkedList<SceneObject>();
		
		// check for configuration file
		File cfgFile = new File(cfgFilePath);
		
		if (!cfgFile.exists())
			try {
				
				cfgFile.createNewFile();
				JOptionPane.showMessageDialog(null, "\""+ cfgFilePath +"\" successfuly created!");
				
			} catch (IOException e) {
				
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Impossible to create: \"" + cfgFilePath +"\"");
			}
		
		// Pause screen
		BufferedImage ps = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bsg = ps.createGraphics();
		bsg.setColor(new Color(10, 10, 10, 110));
		bsg.fillRect(0, 0, maxWidth, maxHeight);
		
		Font f = new Font("Helvetica", Font.BOLD, 42);
		Color c = new Color(250, 250, 250);
		SceneObject paused = new SceneObject("Paused", f, c, 0, 0);
		int x = (maxWidth - paused.getWidth()) / 2;
		int y = (maxHeight - paused.getHeight()) / 2;
		bsg.drawImage(paused.getImage(), x, y, null);
		bsg.dispose();
		pauseScreen = new SceneObject(ps, 0, 0);
	
	}
	
	/*
	 * Reads the configuration file, useful for all the GameClassic objects
	 * as well other classes that need to list the scores
	 */
	public static List<String> readGameConfigurationFile() throws IOException
	{

		// Charset.forName("UTF-8")
		// FIXME: Safe ?
		return Files.readAllLines(Paths.get(cfgFilePath),Charset.forName("UTF-8"));
		
	}

	protected int rand(int min, int max)
	{
		return rng.nextInt(max - min) + min;
	}
	
	public void setIODevice(GameIODevice gs)
	{
		this.ioDevice = gs;
		sceneObjects.clear();
	}
	
	// A weird way of doing things.
	protected void setFrameRate(int fps)
	{
		if (fps > 0) this.frameRate = fps;
	}
	
	protected SceneObject getAffectedObject(int x, int y)
	{
		// TODO: Implement efficient DYNAMIC space BSP-Tree
		debug("("+ x +","+ y +")");
		
		// search for objects
		long maxZ = 0;
		SceneObject affectedObject = null;
		for (SceneObject sObj : sceneObjects) {
			if (sObj.isVisible() && sObj.hit(x, y) && sObj.getZIndex() >= maxZ) {
			//if (sObj.hit(x, y) && sObj.getZIndex() >= maxZ) {
				affectedObject = sObj;
				maxZ = sObj.getZIndex();
			}
		}
	
		return affectedObject;
	}
	
	public long getScore()
	{
		return this.score;
	}
	
	protected void loadScores() throws Exception {

		List<String> lines = readGameConfigurationFile();
		
		for (String line : lines) {
			if (line.toLowerCase().contains(this.name.toLowerCase())){
				String[] fields = line.split(":");
				try {
					this.score = new Integer(fields[1].trim());	
				} catch(Exception e) {
					// array out of index ?
					// integer conversion error ?
				}
				break;
			}
		}
			}

	protected void saveScores() throws Exception 
	{

		List<String> lines = readGameConfigurationFile();
		
		FileWriter cfgFile = new FileWriter(cfgFilePath);
		
		for (String line : lines) {
			
			String[] fields = line.split(":");
			if (line.toLowerCase().contains(this.name.toLowerCase())){
				fields[1] = score + "";
			}
			
			cfgFile.write(fields[0].trim() +" : "+ fields[1].trim() + "\n");
		}
		
		cfgFile.close();
	}
	
	public BufferedImage readScreen() {
		
		// compose the image
		// TODO: Make it more efficient by skipping this process when not necessary.
		for (SceneObject sObj : sceneObjects)
		{
			//if (sObj.needUpdate() && sObj.isVisible()) {
			if (sObj.isVisible()) {
				screenGraphics.drawImage(sObj.getImage(), sObj.getX(), sObj.getY(), null);
				//sObj.update(); 
			}
		}

		return screen;
	}

	public void run()
	{
		frameCounter += frameRate;
		if (frameCounter >= baseFrameRate) {
			frameCounter -= baseFrameRate ;
			if (running) update();
		}
	}
	
	protected abstract void update();
	
	protected abstract void start();
	
	public void initialize() throws Exception {
		
		//BufferedImage blankScreen = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
		//Graphics2D bsg = blankScreen.createGraphics();
		screenGraphics.setColor(Color.WHITE);
		screenGraphics.fillRect(0, 0, maxWidth, maxHeight);
		
		Font f = new Font("Helvetica", Font.BOLD, 42);
		Color c = new Color(0, 0, 250);
		SceneObject loading = new SceneObject("Loading ...", f, c, 0, 0);
		int x = (maxWidth - loading.getWidth()) / 2;
		int y = (maxHeight - loading.getHeight()) / 2;
		loading.moveTo(x, y);
		sceneObjects.add(loading);
		
		loadScores();
		start();
		sceneObjects.remove(loading);
		running = true;
		debug("Running!");
	}

	public void pause() {
		
		if (running) {
			sceneObjects.add(pauseScreen);
			//update();
			debug("PAUSED");	
		} else {
			sceneObjects.remove(pauseScreen);
			debug("UNPAUSED");	
		}
		
		running = !running;	// FIXME: Save scores everytime the game pouses ?
	}

	public void stop() throws Exception {
		running = false;
		saveScores();
	}
	
	public void debug(String msg)
	{
		System.out.print("\n"+ this.name +": "+ msg);
	}

}
