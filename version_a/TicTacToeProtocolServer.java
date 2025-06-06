/**
 * 
 */
package edu.rpi.csci2600.u25.kuzmik2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.nio.charset.*;

/**
 * @author Konstantin Kuzmin
 *
 */
public class TicTacToeProtocolServer implements Connectable {
	public TicTacToeProtocolServer() throws IOException {
		this(DEFAULT_PORT);
	}
	
	public TicTacToeProtocolServer(int port) throws IOException {
		this.log = Logger.getLogger("global");
		
		this.port = port;
		this.servSocket = new ServerSocket(this.port);
		log.info(String.format("Server socket was created on port %d.\n", port));
	}
	
	@Override
	public void connect() throws IOException {
		this.socket = this.servSocket.accept();
		log.info(String.format("Incoming connection from a client at %s accepted.\n", this.socket.getRemoteSocketAddress().toString()));
		this.inStream =  this.socket.getInputStream();
		this.outStream = this.socket.getOutputStream();
		this.in = new Scanner(this.inStream);
		this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /*autoFlush */);		
	}
	
	@Override
	public void send(String message) {
		this.out.println(message);
		log.info(String.format("Message %s sent.\n", message));
	}
	
	@Override
	public String receive() {
		String message = this.in.nextLine();
		log.info(String.format("Message %s received.\n", message));
		return message;
	}
	
	public int getPort() {
		return this.port;
	}

	public static final int DEFAULT_PORT = 8189;
	
	private int port;
	private Socket socket;
	private ServerSocket servSocket;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;
}

class TicTacToeProtocolServerDemo {
	/*
	    To test:
	    1. java edu.rpi.csci2600.u25.kuzmik2.TicTacToeProtocolServerDemo 8189
	    2. cmd
	    3. telnet
	    4. open localhost 8189
	    5. conf X 5 5
	    6. mark X 0 0
	    7. mark O 0 1
	    8. mark X 1 1
	    9. mark O 0 2
	    10. mark X 2 2
	    11.mark O 0 3
	    12.mark X 3 3
	    13.mark O 0 4
	    14.mark X 4 4
	 */
	public static void main(String[] args) throws IOException{
		Logger log = Logger.getLogger("global");
		StreamHandler handler;
		try {
			handler = new FileHandler();
		}
		catch (Exception e) {
			log.warning(String.format("Unable to create global logger file handler: %s", e.getMessage()));
			throw e;
		} 
		handler.setFormatter(new SimpleFormatter());
		log.addHandler(handler);
		
		TicTacToeProtocolServer gameComm = null;
		try {
			if (args.length > 0) {
				gameComm = new TicTacToeProtocolServer(Integer.parseInt(args[0]));
			}
			else {
				gameComm = new TicTacToeProtocolServer();
			}
		}
		catch (NumberFormatException e) {
			if (gameComm != null ) {
				try {
					gameComm = new TicTacToeProtocolServer();
				}
				catch (IOException e1) {
					log.severe(String.format("Unable to create server socket: %s", e1.getMessage()));
					throw e1;
				}
				log.warning(String.format("Invalid port number %s, using the default value of %d", args[0], gameComm.getPort()));
			}
		}
		catch (IOException e) {
			log.severe(String.format("Unable to create server socket: %s", e.getMessage()));
			throw e;
		}
		
		try {
			gameComm.connect();
		}
		catch (IOException e) {
			log.severe(String.format("Accepting connection from a client failed: %s", e.getMessage()));
			throw e;
		}
		
		String command, response;
		TicTacToe game = new TicTacToe("X", "O", 3, 3);
		TicTacToeProtocol prot = new TicTacToeProtocol(game);
		try {
			while (prot.getState() != TicTacToeStates.FINISHED)
			{
				command = gameComm.receive();
				log.info(String.format("Received command: %s", command));
				response = prot.process(command);
				gameComm.send(response);
				log.info(String.format("Sent response: %s", response));
			}
		}
		catch (NoSuchElementException e) {
			log.info(String.format("Unable to read next command: %s", e.getMessage()));
		}
	}
}
