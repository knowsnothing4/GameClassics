package GCFrameWork;
import java.awt.Graphics;
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

public abstract class GameClassic {

	protected static final int defaultGameWidth = 800, defaultGameHeight = 600;
	private static final String cfgFilePath = "GameClassics.cfg";
	
	protected String name;
	protected int maxWidth, maxHeight;

	protected BufferedImage screen;
	protected Graphics screenGraphics;
	protected Queue<SceneObject> sceneObjects;
	protected GameIODevice ioDevice;
	
	protected long score;
	private boolean running;
	protected static final Random rng = new Random();	
	
	public GameClassic(String name, int width, int height) {

		this.name = name;
		this.score = 0;
		this.running = false;
		
		this.maxWidth = width;
		this.maxHeight = height;
		
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
	
	protected SceneObject getAffectedObject(int x, int y)
	{
		// TODO: Implement efficient space BSP-Tree
		debug("("+ x +","+ y +")");
		
		// search for objects
		long maxZ = 0;
		SceneObject affectedObject = null;
		for (SceneObject sObj : sceneObjects) {
			if (sObj.hit(x, y) && sObj.getZIndex() >= maxZ) {
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
			
			cfgFile.write(fields[0].trim() +" : "+ fields[1] + "\n");
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

	public boolean update()
	{
		return running;
	}
	
	public void start() throws Exception {
		loadScores();
		running = true;		// FIXME : Possibly not thread safe.
		debug("Running!");
	}

	public void pause() {
		running = false;	// FIXME: Save scores everytime the game pouses ?

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
