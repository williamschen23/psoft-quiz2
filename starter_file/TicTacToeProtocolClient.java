/**
 * 
 */
package edu.rpi.csci2600.f25.gildem4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * @author gildem4
 *
 */
public class TicTacToeProtocolClient implements Connectable {
	public TicTacToeProtocolClient(String server) {
		this(server, DEFAULT_PORT);		
	}
	
	public TicTacToeProtocolClient(String server, int port) {
		this.log = Logger.getLogger("global");
		this.server = server;
		this.port = port;
	}
	
	@Override
	public void connect() throws IOException {
		this.socket = new Socket(this.server, this.port);
		
		log.info(String.format("Connection to server %s established at port %d.\n", server, port));
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
	
	public String getServer() {
		return this.server;
	}
	
	public boolean isConnectionClosed() {
		return this.socket.isClosed();
	}
	
	public static final int DEFAULT_PORT = 8189;
	
	private Socket socket;
	private String server;
	private int port;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;	
}

class TicTacToeProtocolClientDemo {
	/*
    To test:
    1. java edu.rpi.csci2600.u25.kuzmik2.TicTacToeProtocolServerTest 8189
    2. java edu.rpi.csci2600.u25.kuzmik2.TicTacToeProtocolClientTest localhost 8189
    3. conf X 5 5
    4. mark X 0 0
    5. mark O 0 1
    6. mark X 1 1
    7. mark O 0 2
    8. mark X 2 2
    9.mark O 0 3
    10.mark X 3 3
    11.mark O 0 4
    12.mark X 4 4
 */
	public static void main(String[] args) throws IOException, UnknownHostException {
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
		
		TicTacToeProtocolClient gameComm = null;
		try {
			if (args.length == 0) {
				log.severe("Remote server name has to be specified in the command line arguments");
				return;
			}
			else if (args.length > 1) {
				gameComm = new TicTacToeProtocolClient(args[0], Integer.parseInt(args[1]));
			}
			else if (args.length > 0) {
				gameComm = new TicTacToeProtocolClient(args[0]);
			}
		}
		catch (NumberFormatException e) {
			if (gameComm != null ) {
				gameComm = new TicTacToeProtocolClient(args[0]);	
				log.warning(String.format("Invalid port number %s, using the default value of %d", args[1], gameComm.getPort()));
			}
		}
		try {
			gameComm.connect();
		}
		catch (UnknownHostException e) {
			log.severe(String.format("Unknown host: %s.\n", e.getMessage()));
			throw e;
		} 
		catch (IOException e) {
			log.severe(String.format("Unable to create socket: %s", e.getMessage()));
			throw e;
		}
		
		String command, response = "";
		try (Scanner in = new Scanner(System.in);) {
			while (!gameComm.isConnectionClosed() && response.indexOf("wins") == -1 && response.indexOf("draw") == -1)
			{
				System.out.println("Enter your command:");
				command = in.nextLine();
				gameComm.send(command);
				log.info(String.format("Command: %s sent.", command));
				response = gameComm.receive();
				log.info(String.format("Response: %s received.", response));
			}
		}
		catch (NoSuchElementException e) {
			log.info(String.format("Unable to read next line from the input: %s", e.getMessage()));
		}
	}
}
