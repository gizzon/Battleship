package hw03.gizzon.battleship;
import java.io.IOException;


/**
 *THis interface is created so "Connectable" objectss can communicate
 * with the server and clients
 * 
 * @author NicoleGizzo
 *
 */
public interface Connectable {

	 /**
	 * connect() functions connects the connectable to the server
	 * 
	 * @throws IOException if there is an issue connecting
	 */
	public void connect() throws IOException;

	/**
     * send() function is used to send the server messages
     *
     * @param message A string that will be sent to the server\
     */
	public void send(String message);	

	/**
     * recieve() function is recieve the server's response
     *
     * @return The server's response
     */
	public String receive();

	/**
     * getPort() gets the server's port
     *
     * @return the server's port
     */
	public int getPort();
}