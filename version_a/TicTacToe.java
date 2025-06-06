/**
 * 
 */
package edu.rpi.csci2600.u25.kuzmik2;

import java.util.*;

/**
 * @author Konstantin Kuzmin
 *
 */
// requires:
// modifies:
// effects:
// throws:
// returns:
enum TicTacToeWinner {INIT, IN_PROGRESS, PLAYER1_WON, PLAYER2_WON, DRAW};

class TicTacToe {
	// requires: true
	// modifies: this
	// effects: configures a new TicTacToe game with a grid of rows by cols cells 
	// throws: IllegalArgumentException if rows < 3 || cols < 3 || rows != cols || player1 == null || player2 == null || 
	//    !(player1.toLowerCase().equals("x") || player1.toLowerCase().equals("o")) ||
	//    !(player2.toLowerCase().equals("x") || player2.toLowerCase().equals("o")) || player1.toLowerCase().equals(player2.toLowerCase())
	// returns: none 
	public TicTacToe(String player1, String player2, int rows, int cols) {
		if (rows < 3 || cols < 3 || rows != cols || player1 == null || player2 == null || 
				!(player1.toLowerCase().equals("x") || player1.toLowerCase().equals("o")) ||
				!(player2.toLowerCase().equals("x") || player2.toLowerCase().equals("o")) || player1.toLowerCase().equals(player2.toLowerCase())) {
			throw new IllegalArgumentException("Invalid arguments passed to <init>()");
		}
		this.rows = rows;
		this.cols = cols;
		reset();
		this.player1 = player1;
		this.player2 = player2;
	}

	// requires: true
	// modifies: this
	// effects: grid_after[i][j].equals("") for all 0 <= i < rows, 0 <= j < cols; state is set to TicTacToeWinner.INIT
	// throws: none
	// returns:	none
	public void reset() {
		this.grid = new String[this.rows][this.cols];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.grid[i][j] = "";
			}
		}
		this.state = TicTacToeWinner.INIT;		
	}

	// requires: true
	// modifies: this
	// effects: sets this.rows to rows and this.cols to cols and then resets this using reset()
	// throws: IllegalArgumentException if rows < 3 || cols < 3 || rows != cols
	// returns:	none
	public void resize(int rows, int cols) {
		if (rows < 3 || cols < 3 || rows != cols) {
			throw new IllegalArgumentException("Invalid arguments passed to resize()");
		}
		this.rows = rows;
		this.cols = cols;
		reset();
	}
	
	// requires: true
	// modifies: none
	// effects: none
	// throws: IllegalArgumentException if rows < 0 || cols < 0 || row >= grid.length || col >= grid[].length
	// returns:	grid[row][col]
	public String getCell(int row, int col) {
		if (row < 0 || col < 0 || row >= this.grid.length || col >= this.grid[row].length) {
			throw new IllegalArgumentException("Invalid arguments passed to getCell()");
		}
		return this.grid[row][col];
	}

	// requires: true
	// modifies: this
	// effects: if grid_before[row][col].equals("") then grid_after[row][col] = player
	// throws: IllegalArgumentException if row < 0 || col < 0 || row >= this.grid.length || col >= this.grid[row].length || player == null || 
	//   !(player.toLowerCase().equals("x") || player.toLowerCase().equals("o"))
	//   IllegalStateException if the game has already ended (!(this.getState() == TicTacToeWinner.INIT || this.getState() == TicTacToeWinner.IN_PROGRESS))
	//   or !grid[row][col].equals("")
	// returns:	none
	public void setCell(String player, int row, int col) {
		if (row < 0 || col < 0 || row >= this.grid.length || col >= this.grid[row].length || player == null || 
				!(player.toLowerCase().equals("x") || player.toLowerCase().equals("o"))) {
			throw new IllegalArgumentException("Invalid arguments passed to setCell()");
		}
		if (!(this.getState() == TicTacToeWinner.INIT || this.getState() == TicTacToeWinner.IN_PROGRESS) || !grid[row][col].equals("")) {
			throw new IllegalStateException("Illegal state of TicTacToe object in the setCell() call");
		}
		if (getUnmarkedCellsCount() == this.rows * this.cols) {
			assert this.state == TicTacToeWinner.INIT;
			this.state = TicTacToeWinner.IN_PROGRESS;
		}
		this.grid[row][col] = player;
		if (isWinner(this.getPlayer1())) {
			this.state = TicTacToeWinner.PLAYER1_WON;
		}
		else if (isWinner(this.player2)) {
			this.state = TicTacToeWinner.PLAYER2_WON;
		}
		else if (getUnmarkedCellsCount() == 0) {
			this.state = TicTacToeWinner.DRAW;
		}		
	}
	
	// requires: true
	// modifies: none
	// effects: none
	// throws: none
	// returns:	one of enumeration values that corresponds to the state of the game (IN_PROGRESS, PLAYER1_WON, PLAYER2_WON, or DRAW).
	public TicTacToeWinner getState() {
		return this.state;
	}
	
	/**
	 * @return the player1
	 */
	public String getPlayer1() {
		return this.player1;
	}
	
	/**
	 * @return the player2
	 */
	public String getPlayer2() {
		return this.player2;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\u250c");
		for (int i = 0; i < this.cols - 1; i++) {
			str.append("\u2500\u2500\u2500\u252c");
		}
		str.append("\u2500\u2500\u2500\u2510\n");
		
		String[] row;
		for (int r = 0; r < this.rows; r++) {
			row = this.grid[r];
			for (String cell : row) {
				str.append("\u2502 " + (cell.equals("") ? " " : cell) + " ");
			}
			str.append("\u2502\n");
			if (r != this.rows - 1) {
				str.append("\u251c");
				for (int c = 0; c < this.cols - 1; c++) {
					str.append("\u2500\u2500\u2500\u253c");
				}
				str.append("\u2500\u2500\u2500\u2524\n");
			}
		}
		str.append("\u2514");
		for (int i = 0; i < this.cols - 1; i++) {
			str.append("\u2500\u2500\u2500\u2534");
		}
		str.append("\u2500\u2500\u2500\u2518\n");
		
		return str.toString();
	}
	
	// requires: true
	// modifies: none
	// effects: none
	// throws: IllegalArgumentException if player == null || !(player.toLowerCase().equals("x") || player.toLowerCase().equals("o"))
	// returns:	true if player is a winner, false otherwise
	private boolean isWinner(String player) {
		boolean horizontal, vertical, diagonal;

		if (player == null || !(player.toLowerCase().equals("x") || player.toLowerCase().equals("o"))) {
			throw new IllegalArgumentException("Invalid arguments passed to isWinner()");
		}
		
		for (String[] row : this.grid) {
			horizontal = true;
			for (String cell : row) {
				if (!cell.toLowerCase().equals(player.toLowerCase())) {
					horizontal = false;
					break;
				}
			}
			if (horizontal) {
				return true;
			}
		}
		
		for (int j = 0; j < this.cols; j++) {
			vertical = true;
			for (int i = 0; i < this.rows; i++) {
				if (!this.grid[i][j].toLowerCase().equals(player.toLowerCase())) {
					vertical = false;
					break;
				}
			}
			if (vertical) {
				return true;
			}
		}

		diagonal = true;
		for (int d = 0; d < this.rows; d++) {
			if (!this.grid[d][d].toLowerCase().equals(player.toLowerCase())) {
				diagonal = false;
				break;
			}
		}
		if (diagonal) {
			return true;
		}		
		
		diagonal = true;		
		for (int d = 0; d < this.rows; d++) {
			if (!this.grid[d][this.cols - 1 - d].toLowerCase().equals(player.toLowerCase())) {
				diagonal = false;
				break;
			}
		}
		if (diagonal) {
			return true;
		}		
		
		return false;
	}
	
	// requires: true
	// modifies: none
	// effects: none
	// throws: none
	// returns:	the number of cells in the grid which have "" as the value
	private int getUnmarkedCellsCount() {
		int count = 0;
		for (String[] row : this.grid) {
			for (String cell : row) {
				if (cell.equals("")) {
					count++;
				}
			}
		}
		return count;
		
	}	
	
	private String[][] grid;
	private int rows, cols;
	private String player1;
	private String player2;
	private TicTacToeWinner state;
}

class TicTacToeDemo {
	public static void main(String[] args) {
		int row, col;
		TicTacToe game = new TicTacToe("X", "O", 3, 3);
		System.out.printf("Starting the game of TictacToe. Player 1 is %s; Player 2 is %s\n", game.getPlayer1(), game.getPlayer2());
		System.out.println(game);
		try (Scanner in = new Scanner(System.in);) {
		game:	do {
				for (int turn = 1; turn <=2; turn++) {
					System.out.printf("Enter the cell for Player %d to mark \"row col\": ", turn);
					row = in.nextInt();
					col = in.nextInt();
					if (turn == 1) {
						game.setCell(game.getPlayer1(), row, col); 
					}
					else {
						game.setCell(game.getPlayer2(), row, col);
					}
					System.out.println(game);
					switch (game.getState()) {
						case PLAYER1_WON :  System.out.printf("Player 1 wins!\n");
										break game;
						case PLAYER2_WON :  System.out.printf("Player 2 wins!\n");
										break game;
						case DRAW :     System.out.printf("It's a draw!\n");
										break game;
						default :       break;
					}
				}
				
			} while (game.getState() == TicTacToeWinner.IN_PROGRESS);
		}
	}
}