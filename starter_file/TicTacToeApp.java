/**
 * 
 */
package edu.rpi.csci2600.f25.gildem4;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.io.*;

/**
 * @author gildem4
 *
 */
enum Mode { CLIENT, SERVER };

public class TicTacToeApp {
	
	public static void main(String[] args) {
		Mode mode;
		Connectable peer;
		
		Logger log = Logger.getLogger("TicTacToeApp");
		StreamHandler handler;
		try {
			handler = new FileHandler();
		}
		catch (Exception e) {
			log.warning(String.format("Unable to create global logger file handler: %s", e.getMessage()));
			return;
		} 
		handler.setFormatter(new SimpleFormatter());
		log.addHandler(handler);
		
		String peerName = "";
		int port = TicTacToeApp.USE_DEFAULT_PORT;
		
		System.out.print("Do you want to Connect to the remote system or Wait for an incoming connection (enter C or W)? ");
		try (Scanner in = new Scanner(System.in);) {
			String choice = in.nextLine();
			if (choice.toLowerCase().equals("c")) {
				mode = Mode.CLIENT;
				if (args.length == 0) {
					log.severe("Remote server name has to be specified in the command line arguments");
					return;
				}
				else {
					peerName = args[0];
					if (args.length > 1) {
						try {
							port = Integer.parseInt(args[1]);
						}
						catch (NumberFormatException e) {
							log.warning(String.format("Invalid port number %s, using the default value of %d", args[1], TicTacToeProtocolClient.DEFAULT_PORT));
						}
					}
				}
			}
			else if (choice.toLowerCase().equals("w")) {
				mode = Mode.SERVER;
				if (args.length > 0) {
					try {
						port = Integer.parseInt(args[0]);
					}
					catch (NumberFormatException e) {
						log.warning(String.format("Invalid port number %s, using the default value of %d", args[0], TicTacToeProtocolServer.DEFAULT_PORT));
					}
				}
			}
			else {
				System.out.printf("Invalid mode requested: %s\n", choice);
				return;
			}

			if (mode == Mode.CLIENT) {
				if (port != USE_DEFAULT_PORT) {
					peer = new TicTacToeProtocolClient(peerName, port);
				}
				else {
					peer = new TicTacToeProtocolClient(peerName);
				}
			}
			else {
				try {
					if (port != USE_DEFAULT_PORT) {
						peer = new TicTacToeProtocolServer(port);
					}
					else {
						peer = new TicTacToeProtocolServer();
					}
				}
				catch (IOException e) {
					log.severe(String.format("Unable to create server socket: %s", e.getMessage()));
					return;
				}
			}
			
			try {
				peer.connect();
			}
			catch (IOException e) {
				log.severe(String.format("Connecting to the peer failed: %s", e.getMessage()));
			}

			String character, remoteChar;
			String localCommand, localResponse, remoteResponse;
			int rows, cols;
			
			TicTacToe game = new TicTacToe("X", "O", 3, 3);
			TicTacToeProtocol prot = new TicTacToeProtocol(game);

			if (mode == Mode.CLIENT) {
				do {
					System.out.print("Choose your character (enter X or O): ");
					character = in.nextLine();
					System.out.print("Choose your board size (enter \"rows columns\"): ");
					rows = in.nextInt();
					cols = in.nextInt();
					localCommand = String.format("conf %s %d %d", character, rows, cols);
					remoteResponse = sendRecv(peer, prot, localCommand); 
				} while (!remoteResponse.contains("conf OK"));
			}
			else {
				localResponse = recvSend(peer, prot);
				if (prot.getState() == TicTacToeStates.PLAYER1) {
					remoteChar = game.getPlayer1();
					character = game.getPlayer2();
				}
				else {
					remoteChar = game.getPlayer2();
					character = game.getPlayer1();
				}
				log.info(String.format("Client character is %s, local character is %s", remoteChar, character));
			}
			
			int row, col;
			while (prot.getState() != TicTacToeStates.FINISHED) {
				if (mode == Mode.CLIENT) {
					do {
						System.out.printf("Enter row and column (\"row column\"): ");
						row = in.nextInt();
						col = in.nextInt();
						localCommand = String.format("mark %s %d %d", character, row, col);
						remoteResponse = sendRecv(peer, prot, localCommand); 
						if (!remoteResponse.contains("mark OK")) {
							System.out.println("Invalid row and column specified.");
						}
					} while (!remoteResponse.contains("mark OK") && prot.getState() != TicTacToeStates.FINISHED);
				}
				else {
					do {
						localResponse = recvSend(peer, prot);
					} while (!localResponse.contains("mark OK") && prot.getState() != TicTacToeStates.FINISHED);
				}
				
				System.out.println(game);
				
				if (prot.getState() == TicTacToeStates.FINISHED) {
					if (game.getState() == TicTacToeWinner.DRAW) {
						System.out.println("It's a draw!");
					}
					else if (mode == Mode.CLIENT) {
						System.out.println("You won!");
					}
					else {
						System.out.println("The other player won!");
					}
					continue;
				}
				
				if (mode == Mode.CLIENT) {
					do {
						localResponse = recvSend(peer, prot);
					} while (!localResponse.contains("mark OK") && prot.getState() != TicTacToeStates.FINISHED);
				}
				else {
					do {
						System.out.printf("Enter row and column (\"row column\"): ");
						row = in.nextInt();
						col = in.nextInt();
						localCommand = String.format("mark %s %d %d", character, row, col);
						remoteResponse = sendRecv(peer, prot, localCommand); 
						if (!remoteResponse.contains("mark OK")) {
							System.out.println("Invalid row and column specified.");
						}
					} while (!remoteResponse.contains("mark OK") && prot.getState() != TicTacToeStates.FINISHED);
				}
				
				System.out.println(game);
				
				if (game.getState() == TicTacToeWinner.DRAW) {
					System.out.println("It's a draw!");
				}
				else if (prot.getState() == TicTacToeStates.FINISHED) {
					if (mode == Mode.CLIENT) {
						System.out.println("The other player won!");
					}
					else {
						System.out.println("You won!");
					}
				}			
			}
		}
	}
	public static final int USE_DEFAULT_PORT = -1;
	
	private static String sendRecv(Connectable peer, TicTacToeProtocol prot, String localCommand) {
		String localResponse, remoteResponse;
		peer.send(localCommand);
		localResponse = prot.process(localCommand);
		remoteResponse = peer.receive();
		if (!localResponse.equals(remoteResponse)) {
			throw new IllegalStateException(
					String.format("Command %s processing returned %s from the local protocol and %s from the remote one",
							localCommand, localResponse, remoteResponse));
		}
		return remoteResponse;
	}
	
	private static String recvSend(Connectable peer, TicTacToeProtocol prot) {
		String remoteCommand = peer.receive();
		String localResponse = prot.process(remoteCommand);
		peer.send(localResponse);
		return localResponse;
	}
}
