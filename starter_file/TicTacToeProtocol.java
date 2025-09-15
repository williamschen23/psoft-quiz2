package edu.rpi.csci2600.f25.gildem4

enum TicTacToeStates {INIT, PLAYER1, PLAYER2, FINISHED};

public class TicTacToeProtocol {
	// requires: true
	// modifies: this
	// effects: configures a new TicTacToeProtocol object with the specified TicTacToe game object
	// throws: IllegalArgumentException if game state is not INIT
	// returns: none 	
	public TicTacToeProtocol(TicTacToe game) {
		if (game.getState() != TicTacToeWinner.INIT) {
			throw new IllegalArgumentException("Invalid arguments passed to <init>()");
		}		
		this.state = TicTacToeStates.INIT;
		this.game = game;
	}
	
	public String process(String command) {
		String response = "";
		String[] commands = parseCommand(command.toLowerCase());
		switch (state) {
			// E.g., "conf X 3 3"
			case INIT : if (commands[0].toLowerCase().equals("conf")) {
							try {
								this.game.resize(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
							}
							catch (IllegalArgumentException e) {
								response = "conf invalid_arguments";
								return response;
							}
							if (commands.length != 4) {
								response = "conf invalid_arguments";
								return response;
							}
							if (!(commands[1].toLowerCase().equals(this.game.getPlayer1().toLowerCase()) || 
									commands[1].toLowerCase().equals(this.game.getPlayer2().toLowerCase()))) {
								response = "conf invalid_arguments";
								return response;
							}
							if (commands[1].toLowerCase().equals(this.game.getPlayer1().toLowerCase())) { 
								this.state = TicTacToeStates.PLAYER1;
								response = "conf OK Player(" + this.game.getPlayer1() +")";
							}
							else if (commands[1].toLowerCase().equals(game.getPlayer2().toLowerCase())) { 
								this.state = TicTacToeStates.PLAYER2;
								response = "conf OK Player(" + this.game.getPlayer2() +")";
							}
							else {
								assert false : "The player parameter of the conf command matches none of the players";
							}
			            }
						else {
							response = "" + command + " not_valid_for_game_state " + this.state.toString();				
						}
			            break;
			// E.g., "mark X 0 2"
			case PLAYER1 :  if (commands[0].toLowerCase().equals("mark")) {
								if (commands.length != 4) {
									response = "mark invalid_arguments";
									return response;
								}
								if (!commands[1].toLowerCase().equals(this.game.getPlayer1().toLowerCase())) {
									response = "mark wrong_player";
									return response;
								}
								try {
									this.game.setCell(this.game.getPlayer1(), Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
								}
								catch (IllegalArgumentException e) {
									response = "mark invalid_arguments";
									return response;
								}
								catch (IllegalStateException e) {
									response = "mark not_valid_for_game_state";
									return response;
								}
								if (game.getState() != TicTacToeWinner.IN_PROGRESS) {
									assert game.getState() == TicTacToeWinner.PLAYER1_WON || game.getState() == TicTacToeWinner.PLAYER2_WON || game.getState() == TicTacToeWinner.DRAW;
									this.state = TicTacToeStates.FINISHED;
									if (game.getState() == TicTacToeWinner.PLAYER1_WON) {
										response = "mark OK Player(" + this.game.getPlayer1() +") wins";
									}
									else if (game.getState() == TicTacToeWinner.PLAYER2_WON) {
										response = "mark OK Player(" + this.game.getPlayer2() +") wins";
									}
									else if (game.getState() == TicTacToeWinner.DRAW) {
										response = "mark OK draw";
									}
									else {
										assert false;
									}
									return response;				
								}
								this.state = TicTacToeStates.PLAYER2;
								response = "mark OK Player(" + this.game.getPlayer2() +") next";
							}
							else {
								response = "" + command + " not_valid_for_game_state " + this.state.toString();				
							}
							break;
			// E.g., "mark O 1 2"
			case PLAYER2 :  if (commands[0].toLowerCase().equals("mark")) {
								if (commands.length != 4) {
									response = "mark invalid_arguments";
									return response;
								}
								if (!commands[1].toLowerCase().equals(this.game.getPlayer2().toLowerCase())) {
									response = "mark wrong_player";
									return response;
								}
								try {
									this.game.setCell(this.game.getPlayer2(), Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
								}
								catch (IllegalArgumentException e) {
									response = "mark invalid_arguments";
									return response;
								}
								catch (IllegalStateException e) {
									response = "mark not_valid_for_game_state";
									return response;
								}
								if (game.getState() != TicTacToeWinner.IN_PROGRESS) {
									assert game.getState() == TicTacToeWinner.PLAYER1_WON || game.getState() == TicTacToeWinner.PLAYER2_WON || game.getState() == TicTacToeWinner.DRAW;
									this.state = TicTacToeStates.FINISHED;
									if (game.getState() == TicTacToeWinner.PLAYER1_WON) {
										response = "mark OK Player(" + this.game.getPlayer1() +") wins";
									}
									else if (game.getState() == TicTacToeWinner.PLAYER2_WON) {
										response = "mark OK Player(" + this.game.getPlayer2() +") wins";
									}
									else if (game.getState() == TicTacToeWinner.DRAW) {
										response = "mark OK Players draw";
									}
									else {
										assert false;
									}
									return response;				
								}
								this.state = TicTacToeStates.PLAYER1;
								response = "mark OK Player(" + this.game.getPlayer1() +") next";
							}
							else {
								response = "" + command + " not_valid_for_game_state " + this.state.toString();				
							}
							break;
			case FINISHED : response = "" + command + " not_valid_for_game_state " + this.state.toString();
							break;
			default : break;
		}
		assert !response.isEmpty();
		return response;
	}
	
	// requires: true
	// modifies: none
	// effects: none
	// throws: none
	// returns:	one of enumeration values that corresponds to the state of the protocol engine (INIT, PLAYER1, PLAYER2, FINISHED).
	public TicTacToeStates getState() {
		return this.state;
	}	
	
	private String[] parseCommand(String command) {
		return command.split("\\s");
	}
	
	private TicTacToe game;
	private TicTacToeStates state;
}

class TicTacToeProtocolDemo {
	public static void main(String[] args) {
		String command;
		TicTacToe game = new TicTacToe("X", "O", 3, 3);
		TicTacToeProtocol prot = new TicTacToeProtocol(game);
		System.out.println(game);
		
		command = "conf X 4 4";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "conf X 3 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 1 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 2 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		System.out.println("--------------------------------------");
		
		game = new TicTacToe("X", "O", 3, 3);
		prot = new TicTacToeProtocol(game);
		System.out.println(game);
		
		command = "conf X 4 4";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 1 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 2 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 3 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 3 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 2 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 3 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 2 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		System.out.println("--------------------------------------");
		
		game = new TicTacToe("O", "X", 3, 3);
		prot = new TicTacToeProtocol(game);
		System.out.println(game);
		
		command = "conf X 5 5";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);

		command = "mark O 0 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 3 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 4";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 4 4";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 1 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		System.out.println("--------------------------------------");
		
		game = new TicTacToe("O", "X", 3, 3);
		prot = new TicTacToeProtocol(game);
		System.out.println(game);
		
		command = "conf O 3 3";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 1 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 2";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark O 0 0";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
		command = "mark X 2 1";
		System.out.println(command);
		System.out.println(prot.process(command));
		System.out.println(game);
		
	}
}
