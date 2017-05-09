package GCFrameWork;

import GameClassics.*;

public enum GameList {
	
	TestFramework	("Framework Tester", new TestFrameWork()),
	TicTacToe 		("Tic Tac Toe", new TicTacToe()),
	Snake			("Snake", new Snake()),
	Tetris			("Tetris", null),
	MineSweeper		("Mine Sweeper", null),
	BreakOut		("Break Out", null),
	GoogleDinossaur	("Google Dinoussaur", null),
	Ludo 			("Ludo", null),
	Reversi	 		("Reversi", null),
	Connect4 		("Connect Four", null),
	Pacman			("Pacman", null),
	Monopoly 		("Monopoly", null),
	PacmeInvaders 	("Space Invaders", null),
	Game2048 		("2048", null),
	Sudoku	 		("Sudoku", null),
	Poker	 		("Texas Hold'em", null),
	FreeCell	 	("Free Cell", null);
	
	
	private String name;
	private GameClassic game;
	
	GameList(String name, GameClassic game)
	{
		this.name = name;
		this.game = game;
	}
	
	public final String getName()
	{
		return this.name;
	}
	
	public GameClassic getGame()
	{
		return this.game;
	}
}
