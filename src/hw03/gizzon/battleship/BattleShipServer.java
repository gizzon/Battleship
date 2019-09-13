package hw03.gizzon.battleship;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.nio.charset.*;

public class BattleShipServer implements Connectable{
	private int port;
	private Socket socket;
	private ServerSocket servSocket;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;
	public static final int DEFAULT_PORT = 8189;
	

	/**
    *The battleshipShipServer() constructor which will establish the server and the port
    * @throws IOException if the server is not created properly
    *
    */
	public BattleShipServer() throws IOException{
		this(DEFAULT_PORT);
	}
	
	public BattleShipServer(int port) throws IOException{
		this.log = Logger.getLogger("global");
		
		this.port = port;
		this.servSocket = new ServerSocket(this.port);
		log.info(String.format("Server socket was created on port %d.\n", port));
	}

	/**
    *The connect() function estbalished a connected for the users to the server. It
    * will set up the I/O streams so the client and server may communicate.
    * @throws IOException if the connection cannot be made
    *
    */
	@Override
	public void connect() throws IOException {
		this.socket = this.servSocket.accept();
		log.info(String.format("Incoming connection from a client at %s accepted.\n", this.socket.getRemoteSocketAddress().toString()));
		this.inStream =  this.socket.getInputStream();
		this.outStream = this.socket.getOutputStream();
		this.in = new Scanner(this.inStream);
		this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /*autoFlush */);		
	}
	
	/**
    *The send() function will send a message to the server
    * @param message the string that will be sent
    *
    */
	@Override
	public void send(String message) {
		this.out.println(message);
		log.info(String.format("Message %s sent.\n", message));
	}
	
	/**
    *The recieve() function will recieve messages sent to the server
    * @return Returns the message sent to the server
    *
    */
	@Override
	public String receive() {
		String message = this.in.nextLine();
		log.info(String.format("Message %s received.\n", message));
		return message;
	}
	
	/**
    *The getPort() function will return the server's port
    * @return Server's port
    *
    */
	public int getPort() {
		return this.port;
	}


}
