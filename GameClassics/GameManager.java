package GameClassics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import GCFrameWork.GameClassic;
import GCFrameWork.GameList;
import GCFrameWork.GameIODevice;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class GameManager extends JFrame implements Runnable, ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String GAME_VERSION = "Game Classics - Alpha 0.3.0";
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	private Dimension windowDimension;
	
	private JMenuBar menuBar;
	
	private static GameIODevice screen;
	private static GameClassic activeGame;
	
	public int updateDelay;
	
	private void createMenu()
	{
		
		////////////// Game list handler
		menuBar = new JMenuBar();
		JMenu mnuGames = new JMenu("Games");
		
		menuBar.add(mnuGames);
		
		// adds all the games in the list
		for (GameList gl : GameList.values())
		{
			// TODO: Add actionListeners and Icons!
			JMenuItem mnuItem = new JMenuItem( gl.getName() );
			mnuItem.addActionListener(this);
			
			// disables non-implemented games
			mnuItem.setEnabled( gl.getGame() != null);
			
			mnuGames.add(mnuItem);
		}
		
		
		////////////// Options handler
		JMenu mnuOptions = new JMenu("Options");
		menuBar.add(mnuOptions);
		
		///////////// Network handler
		// TODO: attach network and profile options
		JMenu mnuNetwork = new JMenu("Network");
		menuBar.add(mnuNetwork);
		
		this.add(menuBar, BorderLayout.NORTH);
	}
	
	public GameManager(int width, int height)
	{	
		windowDimension = new Dimension(width, height);
		setPreferredSize(windowDimension);
		setSize(width, height);
		setResizable(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(GAME_VERSION);
		setLayout(new BorderLayout());
		
		createMenu();
		
		setFPS(25);
		screen = new GameIODevice(windowDimension);
		screen.setFocusable(true);
		add(screen, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	// sets the update fps to approximate fps
	public void setFPS(int fps)
	{
		this.updateDelay = 1 + (1000 / (fps + 1));
	}
	
	public static void main(String[] args)
	{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		GameManager gameManager = new GameManager(WIDTH, HEIGHT);
		
		Timer timer = new Timer(gameManager.updateDelay, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent timer) {
				
				if (activeGame == null) return;
		
				activeGame.run();
				screen.repaint();	
				
			}
		});
		
		timer.start();
	
	}
	
	@Override
	public void actionPerformed(ActionEvent selectedMenuItem) {
		
		JMenuItem item = (JMenuItem) selectedMenuItem.getSource();
		
		for (GameList title: GameList.values()) {
			if (title.getName().equals(item.getActionCommand())) {
	
				if (activeGame != null) {
					try {
						activeGame.stop();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				activeGame = title.getGame();
				screen.setScreenController(activeGame);
				
				try {
					activeGame.start();
					this.setTitle("ACTIVE:"+ title.getName() +" | SCORE: " +activeGame.getScore());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
		
				break;
			}
		}
			
	}
	
	///////////// UNUSED FUNCTIONS
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
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
