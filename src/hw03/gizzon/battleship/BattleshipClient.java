package hw03.gizzon.battleship;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

/**
* The BattleshipClient class is used for the peer connection. This class will 
* control the connection with the server, dealing with commands, and I/O streams.
*
*@author NicoleGizzo
*/
public class BattleshipClient implements Connectable{
	public static final int DEFAULT_PORT = 8189;
	
	private Socket socket;
	private String server;
	private int port;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;
	

	/**
	* The battleshipclient constructor will call another constructor that takes in
	* the user's port
	*
	* @param server Server's IP Address
	*/
	public BattleshipClient(String server)
	{
		this(server, DEFAULT_PORT);
	}
	
	/**
	* The battleshipclient constructor sets the respective port and server. This constructor
	* will be used to connect the battleship user's to the server.
	*
	* @param server Server's IP Address
	* @param port Server's port
	*/
	public BattleshipClient(String server, int port)
	{
		this.log = Logger.getLogger("global");
		this.server = server;
		this.port = port;
	}

	/**
	* The connect() function establishes a connection between the client and the server. It will also 
	* establish the stream used for sending messages to/from the server
	*
	* @throws IOException an exception is thrown if a connection cannot be established
	*/
	@Override
	public void connect() throws IOException {
		this.socket = new Socket(this.server, this.port);
		
		log.info(String.format("Connection to server %s established at port %d.\n", server, port));
		this.inStream =  this.socket.getInputStream();
		this.outStream = this.socket.getOutputStream();
		this.in = new Scanner(this.inStream);
		this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /*autoFlush */);		
	}
	
	/**
	* The send() function is used to send messages to the server
	*
	* @param message A string, representing a message, that will be sent to the server
	*/
	@Override
	public void send(String message) {
		this.out.println(message);
		log.info(String.format("Message %s sent.\n", message));
	}
	
	/**
	* The receive() function is used so the server can recieve messages
	*
	* @return A String that represents the message received by the server
	*/
	@Override
	public String receive() {
		String message = this.in.nextLine();
		log.info(String.format("Message %s received.\n", message));
		return message;
	}
	
	/**
	* The getPort() function is used to return the port
	*
	* @return A string representing the port
	*/
	public int getPort() {
		return this.port;
	}
	
	/**
	* The getServer() function is used to return the server
	*
	* @return A string representing the server
	*/
	public String getServer() {
		return this.server;
	}
	
	/**
	* The connectionClosed() function is used to ensure the connection was established
	*
	* @return A boolean representing wether the connection was established
	*/
	public boolean isConnectionClosed() {
		return this.socket.isClosed();
	}

}
