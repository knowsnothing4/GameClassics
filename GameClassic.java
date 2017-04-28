package GameClassics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.JOptionPane;

public abstract class GameClassic {

	protected static final int defaultGameWidth = 800, defaultGameHeight = 600;
	private static final String cfgFilePath = "GameClassics.cfg";
	//protected File cfgFile;
	
	protected String name;
	protected int maxWidth, maxHeight;

	protected BufferedImage screen;
	protected GameScreen outputDevice;
	
	protected long score;
	private boolean running;
	;
	
	public GameClassic(String name, int width, int height) {

		this.name = name;
		this.score = 0;
		this.running = false;
		
		this.maxWidth = width;
		this.maxHeight = height;
		
		this.screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
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
	public static String[] readGameConfigurationFile() throws IOException
	{

		Scanner cfgScanner = new Scanner(Paths.get(cfgFilePath),"UTF-8");
		
		//cfgScanner.useDelimiter("\n");
		
		// FIXME : Stupid way of counting lines ...
		int numberOfLines = 0;
		while (cfgScanner.nextLine() != null) numberOfLines++;
			
		String[] lines = new String[numberOfLines]; 
		
		cfgScanner.reset();
		
		for (int l = 0; l < numberOfLines; l++) {
			lines[l] = cfgScanner.nextLine();
		}
		cfgScanner.close();
		
		return lines;

	}

	public void setOutputDevice(GameScreen gs)
	{
		this.outputDevice = gs;
		gs.clear();
	}
	
	
	public long getScore()
	{
		return this.score;
	}
	
	protected void loadScores() throws Exception {

		String[] lines = readGameConfigurationFile();
		
		for (String line : lines) {
			if (line.contains(this.name)){
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
		/*
		Scanner cfgScanner = new Scanner(Paths.get(cfgFilePath),"UTF-8");
		
		//cfgScanner.useDelimiter("\n");
		
		while (cfgScanner.hasNextLine()) {
		
			 // find strings of the form "Game name : 0000..."
			 //  where 0000.. represents the previous score.
			 
			String[] line = cfgScanner.nextLine().split(":");
			
			if (line[0].contains(this.name)){
				this.score = new Integer(line[1].trim());
				break;
			}
		}
		
		cfgScanner.close();
		*/
	}

	protected void saveScores() throws Exception 
	{
		
		String[] lines = readGameConfigurationFile();
		
		FileWriter cfgFile = new FileWriter(cfgFilePath);
		
		String[] fields = {"",""};
		for (String line : lines) {
			if (line.contains(this.name)){
				fields = line.split(":");
				fields[1] = new Long(score).toString();
			}
			
			cfgFile.write(fields[0] +" : "+ fields[1] + "\n");
		}
		
		cfgFile.close();
	}

	public BufferedImage readScreen() {
		return screen;
	}

	public boolean update()
	{
		return running;
	}
	
	public void start() throws Exception {
		//loadScores();
		running = true;		// FIXME : Possibly not thread safe.
		debug("Running!");
	}

	public void pause() {
		running = false;
		// TODO Auto-generated method stub

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
