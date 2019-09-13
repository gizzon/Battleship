package hw03.gizzon.battleship;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;


/**
 * The Battleship class is the the focal point of this porgram. It will order the main window to popup 
 * upon the application being started. This class will implement the user interface, and will set up
 * the connect between the two users playing this game. It is also responsible for adding functionality
 * to buttons on the interface to provide the user some accessibility.
 * 
 * @author NicoleGizzo
 */
public class Battleship{
	private Socket socket;
	private String server;
	private int port;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;

	enum mode {
		CLIENT, SERVER
	}

	public static Window frame;
	
	public static BattleshipProtocol protocol;

	public String localCommand;

	public String localResponse;

	public String remoteResponse;
	
	public static boolean clicked = false;
	
	public static int clickedRow;
	
	public static int clickedCol;
	
	public static mode mode1 = null;
	public static Connectable peer = null;
	public static boolean start = false;
	public static boolean madeShot = false;
	public static boolean timerEmpty = false;
	public static boolean timerClick = false;
	public static int p1Wins = 0;
	public static int p2Wins = 0;
	
	public static ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * Responsible for sending a command to the server and receiving a response
     * so the state can get updated accordingly.
     * 
     * @param peer Connectable connection to the server
     * @param prot BattleshipProtocol communication protocol
     * @param localCommand String command 
     * @return the response that will be sent
     */
	public static String sendRecv(Connectable peer, BattleshipProtocol prot, String localCommand) {
		String localResponse, remoteResponse;
		peer.send(localCommand);
		localResponse = prot.process(localCommand);
		remoteResponse = peer.receive();
		return remoteResponse;
	}
    /**
     * Responsible for receiving a command/response from the server and processing 
     * it locally
     * 
     * @param peer Connectable connection to the server
     * @param prot BattleshipProtocol communication protocol
     * @return the response from the server
     */
	public static String recvSend(Connectable peer, BattleshipProtocol prot) {
		String remoteCommand = peer.receive();
		String localResponse = prot.process(remoteCommand);
		peer.send(localResponse);
		return localResponse;
	}

    /**
     * The function below converts an array of integers to a string of integers
     * so it can be sent as a message between the server and the client.
     * 
     * @param grid int[][] the grid that will be converted
     * @param size int size of the grid
     * @return The array that was passed in but now in a string format
     */
	public static String ArrayToString(int[][] grid, int size)
	{
		int i;
		int j;
		String ret = "";
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < size - 1; j++)
			{
				ret += grid[i][j] + ",";
			}
			ret += "" + grid[i][j] + ":"; 
		}
		return ret;
	}
	
    /**
     * Constructor for the Battleship() class. This sets up the connection between the two players
     * by establishing the server and the client and will process the messages that are sent between
     * the two players prior to the game beginning. It will also prompt the user for their selected 
     * board, name, IP address, and port.
     */
	public Battleship()
	{
		
		int result = -1;
		String[] options = {"Wait for player", "Connect"};
		
		//Prompts the user to pick whether they would like to wait for a player or connect to one
		result = JOptionPane.showOptionDialog(null, "Would you like to connect to another player or wait?", "Start", JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		//Checks if there is a board loaded, if not... load the board
		if (BoardInfo.getCurrentBoard() == null)
		{
		    //Creates new instance of ExtFileSelector() which allows the user to select a board file
			ExtFileSelector resFile = new ExtFileSelector();
			SwingUtilities.invokeLater(resFile);
			//While loop waits until user selects a file
			while(resFile.getFile() == null)
			{
				try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
			}
			//Opens up the board file
			File user_input = resFile.getFile();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(user_input));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// the code below is used to process the file that was chosen
			String filename = "";
			try {
				filename = reader.readLine();
				// Reintializes the row and col values to those stated in the input file.
				String[] inp = filename.split(", ");
				int row = Integer.parseInt(inp[0]);
				int col = Integer.parseInt(inp[1]);
				BoardInfo.setRow(row);
				BoardInfo.setCol(col);

				try {
					BoardInfo.setRow(row);
					BoardInfo.setCol(col);
					BoardInfo.setCurrentBoard(new int[row][col]);
					// FUnction call below opens the file and reads the data into an int[][]

					BoardInfo.setCurrentBoard(BoardParser.readData(user_input.getPath(), BoardInfo.getShips(),
							BoardInfo.getRow(), BoardInfo.getCol()));
					Battleship.frame.repaint();
					Battleship.frame.requestFocus();
					Battleship.frame.validate();

				} catch (Exception e1) {
					e1.printStackTrace();
					//initializes an empty board if an invalid board is selected
					BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
					Battleship.frame.repaint();
					Battleship.frame.requestFocus();
					Battleship.frame.validate();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				//initializes an empty board if an invalid board is selected
				BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
				Battleship.frame.repaint();
				Battleship.frame.requestFocus();
				Battleship.frame.validate();
			}

		}
		//Establishes the connection if they user chooses to be the server
		if (result == 0)
		{
			start = true;
			mode1 = mode.SERVER;
			try {
			    //prompts the user to enter their name
				String name = JOptionPane.showInputDialog("Enter a name: ");
				BoardInfo.setUsername(name);
				int input_port = -1;
				//Waits for the user to enter a valid port
				do
				{
					try {
						input_port = Integer.parseInt(JOptionPane.showInputDialog("Enter the port:"));
					}
					catch(Exception e)
					{
						input_port = -1;
					}
				} while(input_port == -1);
				BoardInfo.setPort(input_port);
				//prompts the user to enter an IP address
				String ip = JOptionPane.showInputDialog("Enter IP Address: ");
				BoardInfo.setIp(ip);
				//Establishes connection to the server
				peer = new BattleShipServer(BoardInfo.getPort());
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
			}
		}
		//Establishes the connection if the user chooses to be a client
		else if (result == 1)
		{
			start = true;
			mode1 = mode.CLIENT;
			//prompts the user to enter their name
			String name = JOptionPane.showInputDialog("Enter a name: ");
			BoardInfo.setUsername(name);
			int input_port = -1;
			//Waits for the user to enter a valid port.
			do
			{
				try {
					input_port = Integer.parseInt(JOptionPane.showInputDialog("Enter the port:"));
				}
				catch(Exception e)
				{
					input_port = -1;
				}
			} while(input_port == -1);
			BoardInfo.setPort(input_port);
			//Prompts the user to enter the IP Address.
			String ip = JOptionPane.showInputDialog("Enter IP Address: ");
			BoardInfo.setIp(ip);
			//Establishes the connection of the client
			peer = new BattleshipClient(BoardInfo.getIp(), BoardInfo.getPort());
		}
		//If the server and client are established properly, the code below connects the two users and passes
		//information between the two user's regarding their names, boards, and timer. 
		if (start == true)
		{
		    //Connects the server and client
			try
			{
				peer.connect();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			protocol = new BattleshipProtocol();
			
			//sends the client name to the server
			if (mode1 == mode.CLIENT)
			{
				do {
					localCommand = "conf setPlayerName PLAYER2 " + BoardInfo.getUsername();
					remoteResponse = sendRecv(peer,protocol, localCommand);
					
				}while(!remoteResponse.equals("confrecv set player 2 name"));
			
			}
			//Receives the client name
			else if (mode1 == mode.SERVER)
			{
				localResponse = recvSend(peer, protocol);
			}
			
			//Server sends their name to the client
			if (mode1 == mode.SERVER)
			{
				do {
					localCommand = "conf setPlayerName PLAYER1 " + BoardInfo.getUsername();
					remoteResponse = sendRecv(peer,protocol, localCommand);
					
				}while(!remoteResponse.equals("confrecv set player 1 name"));
			
			}
			//client receives the server's name
			else if (mode1 == mode.CLIENT)
			{
				do {
					localResponse = recvSend(peer, protocol);
				}
				while(!localResponse.equals("confrecv set player 1 name"));
				
			}
			//Server sends their timer to the client
			if(mode1 == mode.SERVER)
			{
				do
				{
					localCommand = "conf setTimer " + BoardInfo.getTimer();
					remoteResponse = sendRecv(peer, protocol, localCommand);
				} while(!remoteResponse.equals("timer " + BoardInfo.getTimer()));
				
			}
			//client receives the timer
			else if (mode1 == mode.CLIENT)
			{
				localResponse = recvSend(peer, protocol);
				
			}
			
			//Server receives the client's timer
			if (mode1 == mode.SERVER)
			{
				localResponse = recvSend(peer, protocol);
			}
			
			//client sends their timer to the server. If it is not the same, the client's timer
			//is changed to be the same as the server's timer.
			else if (mode1 == mode.CLIENT)
			{
				do {
					localCommand = "conf getTimer";
					remoteResponse = sendRecv(peer, protocol, localCommand);
				}
				while(localResponse.equals(""));
				String[] inp = localResponse.split(" ");
				int time = Integer.parseInt(inp[1]);
				if(time != BoardInfo.getTimer())
				{
					BoardInfo.setTimer(time);
					protocol.setTimer(time);
				}
				
			}
			
			//Client sends their battleship board to the server
			if (mode1 == mode.CLIENT)
			{
				do
				{
					localCommand = "conf sendBoard PLAYER2 " + ArrayToString(BoardInfo.getCurrentBoard(), BoardInfo.getRow());
					remoteResponse = sendRecv(peer, protocol, localCommand);
				} while(!remoteResponse.equals("board 2 set"));
			}
			//Server receives the client's battleship board
			else if (mode1 == mode.SERVER)
			{
				localResponse = recvSend(peer, protocol);
			} 
			
			//Server sends their battleship board to the client
			if (mode1 == mode.SERVER)
			{
				do
				{
					localCommand = "conf sendBoard PLAYER1 " + ArrayToString(BoardInfo.getCurrentBoard(), BoardInfo.getRow());
					remoteResponse = sendRecv(peer, protocol, localCommand);
				} while(!remoteResponse.equals("board 1 set"));
			}
			
			//client receives the Server's battleship board
			else if (mode1 == mode.CLIENT)
			{
				localResponse = recvSend(peer, protocol);
			} 
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			// creates frame
			Battleship.frame = new Window();
			// changes background color
			Battleship.frame.setBackground(new Color(100, 97, 107));
			// makes frame visible
			Battleship.frame.setVisible(true);

			
			//Code below will read in the user's previous settings(if there are any)
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader("defaultsettings.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = "";
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] data = line.split(",");

			//Sets the user's preferred username
			if (!data[0].equals("") && BoardInfo.getUsername().equals("")) {
				BoardInfo.setUsername(data[0]);
			}
			//Sets the user's preferred board row size
			if (!data[1].equals("")) {
				BoardInfo.setRow(Integer.parseInt(data[1]));
			}
			//Sets the user's preferred column size
			if (!data[2].equals("")) {
				BoardInfo.setCol(Integer.parseInt(data[2]));
			}
			//Sets the user's preferred user interface color
			if (!data[3].equals("") && !data[4].equals("") && !data[5].equals("")) {
				int r = Integer.parseInt(data[3]);
				int g = Integer.parseInt(data[4]);
				int b = Integer.parseInt(data[5]);
				BoardInfo.setUiColor(new Color(r, g, b));
			}
			//Sets the user's preferred water color
			if (!data[6].equals("") && !data[7].equals("") && !data[8].equals("")) {
				int r = Integer.parseInt(data[6]);
				int g = Integer.parseInt(data[7]);
				int b = Integer.parseInt(data[8]);
				BoardInfo.setWaterColor(new Color(r, g, b));
			}
			//Sets the user's preferred board size
			if (!data[9].equals(""))
				BoardInfo.setSelectedSize(data[9]);

			//Sets the user's preferred timer amount
			if (!data[10].equals(""))
				BoardInfo.setTimer(Integer.parseInt(data[10]));

			//Sets the user's preferred IP Address
			if (!data[11].equals(""))
				BoardInfo.setIp(data[11]);

			//Sets the user's preferred port
			if (!data[12].equals(""))
				BoardInfo.setPort(Integer.parseInt(data[12]));

		});
		
		new Battleship();
	}

}

class settingsWindow extends JFrame {
	private static final long serialVersionUID = 1l;

	/**
	 * The function below is used to determine whether a string contains all
	 * numbers.
	 * 
	 * @param s A string that represents a users input
	 * @return A boolean value, true is the string is all numbers, false otherwise.
	 */
	public static boolean isNumeric(String s) {
		if (s == null || s.equals("")) {
			return false;
		}

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * The function below is used to create the settings window. This contains
	 * buttons and text fields that allow the user to change their default settings,
	 * regarding to the state of the game.
	 */
	public settingsWindow() {
		// Creates the window, sets it size and location.
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(540, 300));
		setDefaultLookAndFeelDecorated(true);
		setLocation(85, 100);

		JLabel nameLabel = new JLabel("Username: ");
		nameLabel.setBounds(10, 10, 100, 30);
		JTextField userName = new JTextField();
		userName.setText("" + BoardInfo.getUsername());
		userName.setBounds(100, 10, 200, 30);
		add(nameLabel);
		add(userName);

		// The code below allows the user to change the color of the cells that are
		// alive.
		JLabel uiColor = new JLabel("Interface color: ");
		uiColor.setBounds(10, 50, 200, 30);

		// Creates 3 text boxes for the rgb values... one box for red, another for blue
		// and
		// the final text box for the green value.
		JLabel rLabel = new JLabel("R: ");
		rLabel.setBounds(165, 50, 30, 30);
		JTextField RC = new JTextField();
		RC.setBounds(175, 50, 50, 30);

		JLabel gLabel = new JLabel("G: ");
		gLabel.setBounds(240, 50, 30, 30);
		JTextField GC = new JTextField();
		GC.setBounds(250, 50, 50, 30);

		JLabel bLabel = new JLabel("B: ");
		bLabel.setBounds(315, 50, 30, 30);
		JTextField BC = new JTextField();
		BC.setBounds(325, 50, 50, 30);

		// The following jpanel is created to allow the user to preview the color they
		// have
		// chosen before saving that color.
		JPanel aPreview = new JPanel();
		aPreview.setBounds(395, 50, 50, 30);
		aPreview.setBackground(BoardInfo.getUiColor());
		aPreview.setBorder(BorderFactory.createLineBorder(Color.black));

		// The show button will set the jpanel to the alive color the user selected.
		JButton aPrevButton = new JButton("Show");
		aPrevButton.setBounds(450, 50, 80, 30);
		aPrevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// code below ensures that the color they provided is valid.
				int r = 0;
				int g = 0;
				int b = 0;
				if (!RC.getText().equals("") && isNumeric(RC.getText()) && Integer.parseInt(RC.getText()) > 0
						&& Integer.parseInt(RC.getText()) < 256) {
					r = Integer.parseInt(RC.getText());
				}
				if (!GC.getText().equals("") && isNumeric(GC.getText()) && Integer.parseInt(GC.getText()) > 0
						&& Integer.parseInt(GC.getText()) < 256) {
					g = Integer.parseInt(GC.getText());
				}
				if (!BC.getText().equals("") && isNumeric(BC.getText()) && Integer.parseInt(BC.getText()) > 0
						&& Integer.parseInt(BC.getText()) < 256) {
					b = Integer.parseInt(BC.getText());
				}
				// sets the jpanel to the respective color.
				BoardInfo.setUiColor(new Color(r, g, b));
				aPreview.setBackground(new Color(r, g, b));
			}
		});
		add(uiColor);
		add(rLabel);
		add(RC);
		add(gLabel);
		add(GC);
		add(bLabel);
		add(BC);
		add(aPreview);
		add(aPrevButton);

		//Button to show a preview for the user interface color
		JLabel rules = new JLabel("(Must press \"Show\" to save colors)");
		rules.setBounds(10, 90, 300, 300);
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		pane.setFocusable(true);

		//JCombo box for the user to select their board size.
		JLabel sizeO = new JLabel("Board size: ");
		sizeO.setBounds(10, 90, 200, 30);
		String sizes[] = { "8x8", "9x9", "10x10" };
		final JComboBox cb = new JComboBox(sizes);
		cb.setSelectedItem(BoardInfo.getSelectedSize());
		cb.setBounds(90, 98, 90, 20);
		add(sizeO);
		add(cb);

		//Text field that allows the user to set the amount for their timer
		JLabel timerLabel = new JLabel("Timer: ");
		timerLabel.setBounds(10, 120, 100, 30);
		JTextField timer = new JTextField();
		timer.setText("" + BoardInfo.getTimer());
		timer.setBounds(90, 120, 50, 30);
		JLabel timerLabel2 = new JLabel("seconds");
		timerLabel2.setBounds(140, 120, 100, 30);
		add(timerLabel);
		add(timerLabel2);
		add(timer);

		//Text field that allows the user to set their IP Address
		JLabel hostLabel = new JLabel("Host IP: ");
		hostLabel.setBounds(10, 150, 100, 30);
		JTextField host = new JTextField();
		host.setText("" + BoardInfo.getIp());
		host.setBounds(90, 150, 100, 30);
		add(hostLabel);
		add(host);

		//Text field that allows the user to set their port
		JLabel portLabel = new JLabel("Port: ");
		portLabel.setBounds(10, 190, 100, 30);
		JTextField port = new JTextField();
		port.setText("" + BoardInfo.getPort());
		port.setBounds(90, 190, 100, 30);
		add(portLabel);
		add(port);

		//Button below will save all of the user's input and reflect thheir changes in their user interface and game.
		JButton saveButton = new JButton("Save");
		saveButton.setBounds(240, 220, 100, 30);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    //Saves the userna,e
				if (!userName.getText().equals("")) {
					BoardInfo.setUsername(userName.getText());
				}
				// saves the size of the board
				if (cb.getItemAt(cb.getSelectedIndex()).equals("10x10")) {
					BoardInfo.setRow(10);
					BoardInfo.setCol(10);
					BoardInfo.setSelectedSize("10x10");
				}
				// saves the size of the board

				if (cb.getItemAt(cb.getSelectedIndex()).equals("9x9")) {
					BoardInfo.setRow(9);
					BoardInfo.setCol(9);
					BoardInfo.setSelectedSize("9x9");
				}
				// saves the size of the board

				if (cb.getItemAt(cb.getSelectedIndex()).equals("8x8")) {
					BoardInfo.setRow(8);
					BoardInfo.setCol(8);
					BoardInfo.setSelectedSize("8x8");
				}
				// saves the timer
				if (!timer.getText().equals("") && isNumeric(timer.getText())) {
					BoardInfo.setTimer(Integer.parseInt(timer.getText()));
				}
				//Saves the ip address
				if (!host.getText().equals("")) {
					BoardInfo.setIp(host.getText());
				}
				//saves the port
				if (!port.getText().equals("") && isNumeric(port.getText())) {
					BoardInfo.setPort(Integer.parseInt(port.getText()));
				}

				//Code below will write out all of the user's updated settings to a text file that is
				//referenced when the game is restarted in order to keep the user's settings the same. 
				FileWriter writer = null;
				try {
					writer = new FileWriter("defaultsettings.txt");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					writer.write(BoardInfo.getUsername() + "," + BoardInfo.getRow() + "," + BoardInfo.getRow() + ",");
					writer.write(BoardInfo.getUiColor().getRed() + "," + BoardInfo.getUiColor().getGreen() + ","
							+ BoardInfo.getUiColor().getBlue() + ",");
					writer.write(BoardInfo.getWaterColor().getRed() + "," + BoardInfo.getWaterColor().getGreen() + ","
							+ BoardInfo.getWaterColor().getBlue() + ",");
					writer.write(BoardInfo.getSelectedSize() + "," + BoardInfo.getTimer() + "," + BoardInfo.getIp()
							+ "," + BoardInfo.getPort());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// draws the changes that were made to the board.
				Window.pane.add(new drawGrid());
				Window.pane.revalidate();
				Window.pane.validate();
				Window.pane.getIgnoreRepaint();
				setVisible(true);
				repaint();
			}
		});

		// The following code adds all of the buttons and textfields that were created
		// above.
		add(saveButton);
		setLayout(null);
		setVisible(true);
	}

}
/**
 * The drawGrid() class below is responsible for drawing the current grid that
 * is to be displayed on the interface. It uses information stored in the
 * BoardInfo class to determine what to draw and where to draw it. It also
 * has its own mouse listener to listen for clicks when the user is making
 * their move on the user's board.
 *
 */
class drawGrid extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	public static int gridSize = 30;

	/**
	 * Class below is used to make the grid visible.
	 */
	public drawGrid() {
		// Draws grid onto the board and makes it visible
		setFocusable(true);
		requestFocus();
		repaint();
		setSize(getWidth(), getHeight());
		requestFocus();
		repaint();
		validate();
		addMouseListener(this);
		setVisible(true);
	}

	/**
	 * The function below is used to create the actual board. It draws a number of
	 * intersecting lines that represent the grid. It will also color in the boxes
	 * with their represective color, depending on when they're alive or dead.
	 * 
	 * @param g Graphics that is used to draw the grid
	 */
	@Override
	public void paint(Graphics g) {
		// ssuper.paintComponent(g);
		// Sets color of the background
		g.setColor(BoardInfo.getUiColor());
		g.fillRect(0, 0, getWidth(), getHeight());
		Graphics2D g2d = (Graphics2D) g;
		// makes the lines thicker
		g2d.setStroke(new BasicStroke(2));
		int gridSize = 30;
		g2d.setColor(new Color(0, 0, 0));
		setBackground(new Color(100, 97, 107));
		int row = BoardInfo.getRow();
		int col = BoardInfo.getCol();
		BoardInfo.getCurrentBoard();

		// For loops below fill in the squares with their respected colors
		//This print's out the user's actual board.
		if(BoardInfo.getCurrentBoard() != null && BoardInfo.isShowBoard())
		{
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if (BoardInfo.getCurrentBoard()[i][j] == -1) {
						g.setColor(new Color(0, 0, 255));
						g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
					} else {
					    //colors the box in pink if the ship is of length 4
						if (BoardInfo.getCurrentBoard()[i][j] == 4) {
							g.setColor(new Color(255, 0, 255));
							g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
						}
						//colors the box in green if the ship is of length 3
						if (BoardInfo.getCurrentBoard()[i][j] == 3) {
							g.setColor(new Color(0, 255, 0));
							g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
						}
						//colors the box in light blue if the ship is of length 2
						if (BoardInfo.getCurrentBoard()[i][j] == 2) {
							g.setColor(new Color(0, 255, 255));
							g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
						}
						//colors the box in yellow if the ship is of length 1
						if (BoardInfo.getCurrentBoard()[i][j] == 1) {
							g.setColor(new Color(255, 255, 0));
							g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
						}
					}
	
				}
			}
					    BufferedImage img = null;
            try {
                img = ImageIO.read(new File("images/splash.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //loads the images for hit ships
            BufferedImage img2 = null;
            try {
                img2 = ImageIO.read(new File("images/fire.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //For loops goes through all of the users previous moves
		    for(int i = 0; i < BoardInfo.getOpp_moves().size(); i++)
		    {
		        BattleshipStates p = (Battleship.mode1 == Battleship.mode.SERVER ? BattleshipStates.PLAYER1 : BattleshipStates.PLAYER2);
		        ArrayList<Integer> temp = BoardInfo.getOpp_moves().get(i);
		        //Checks if that move was a hit, displays the image corresponding to a hit shit grid location
		        if(Battleship.protocol.getGame().isHit(p, temp.get(0), temp.get(1)))
		        {
		            g.drawImage(img2, temp.get(1) * gridSize, temp.get(0) * gridSize, 30, 30, null);
		        }
		        //if the move was not a hit, display the "splash" image for that grid location.
		        else
		        {
		            g.drawImage(img, temp.get(1) * gridSize, temp.get(0) * gridSize, 30, 30, null);
		        }
		       
		    }

		}
		//This will print out the opponent's board, and will show the userr's previous hits and misses. 
		else
		{
		    //Draws out an empty board that is the size of the opponent's board
		    for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(j * gridSize, i * gridSize, gridSize, gridSize);
                }
            }
		    //Loads the image for missed shots
		    BufferedImage img = null;
            try {
                img = ImageIO.read(new File("images/splash.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //loads the images for hit ships
            BufferedImage img2 = null;
            try {
                img2 = ImageIO.read(new File("images/fire.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //For loops goes through all of the users previous moves
		    for(int i = 0; i < BoardInfo.getMy_moves().size(); i++)
		    {
		        BattleshipStates p = (Battleship.mode1 == Battleship.mode.SERVER ? BattleshipStates.PLAYER2 : BattleshipStates.PLAYER1);
		        ArrayList<Integer> temp = BoardInfo.getMy_moves().get(i);
		        //Checks if that move was a hit, displays the image corresponding to a hit shit grid location
		        if(Battleship.protocol.getGame().isHit(p, temp.get(0), temp.get(1)))
		        {
		            g.drawImage(img2, temp.get(1) * gridSize, temp.get(0) * gridSize, 30, 30, null);
		        }
		        //if the move was not a hit, display the "splash" image for that grid location.
		        else
		        {
		            g.drawImage(img, temp.get(1) * gridSize, temp.get(0) * gridSize, 30, 30, null);
		        }
		       
		    }
		}
		// Two for loops below are used to draw the vertical and horizontal lines
		// that make up the grid.
		g2d.setColor(new Color(0, 0, 0));
		for (int i = 0; i <= row; i++)
			g2d.drawLine(0, i * gridSize, col * gridSize, i * gridSize);

		for (int i = 0; i <= col; i++)
			g2d.drawLine(i * gridSize, 0, i * gridSize, row * gridSize);

		requestFocus();
		validate();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}
    /**
     * This function is executed whenever the user presses their mouse. The location at which they clicked
     * is sent as a message to the server to simulate a shot made on the opponent's baord.
     * 
     * @param e Mouse event
     */
	@Override
	public void mousePressed(MouseEvent e) {
	    //If the current user is the server and the battleship state is player1
	    if(Battleship.mode1 == Battleship.mode.SERVER && Battleship.protocol.getState() == BattleshipStates.PLAYER1){
	        //get the row and col location for the user's mouse event
	        Battleship.clickedRow = e.getY() / gridSize;
    		Battleship.clickedCol = e.getX() / gridSize;
    		//checks to see if the location is on the battleship board
    		if (Battleship.clickedRow < 0 || Battleship.clickedRow >= BoardInfo.getRow() ||
    		        Battleship.clickedCol < 0 || Battleship.clickedCol >= BoardInfo.getCol())
    		    return;
    		
    		//updates the board
    		Battleship.clicked = true;
    		Battleship.timerClick = true;
    		setFocusable(true);
    		requestFocus();
    		repaint();
    		setSize(getWidth(), getHeight());
    		requestFocus();
    		validate();
    		
    		setVisible(true);
	    }
	  //If the current user is the client and the battleship state is player2
	    else if(Battleship.mode1 == Battleship.mode.CLIENT && Battleship.protocol.getState() == BattleshipStates.PLAYER2){
	      //get the row and col location for the user's mouse event
            Battleship.clickedRow = e.getY() / gridSize;
            Battleship.clickedCol = e.getX() / gridSize;
          //checks to see if the location is on the battleship board
            if (Battleship.clickedRow < 0 || Battleship.clickedRow >= BoardInfo.getRow() ||
                    Battleship.clickedCol < 0 || Battleship.clickedCol >= BoardInfo.getCol())
                return;
            
            //updates the board
            Battleship.clicked = true;
            Battleship.timerClick = true;
            setFocusable(true);
            requestFocus();
            repaint();
            setSize(getWidth(), getHeight());
            requestFocus();
            validate();
    
            setVisible(true);
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}

/**
 * The Window() class below is responsible for creating the visual interface that
 * the user will use to play the Battleship game with their opponent. The window 
 * contains buttons that allows the users to complete different actions, such as 
 * loading their board, changing their settings, starting their game and exiting
 * the game. All of the buttons and pop up windows in the interfface and buttons
 * will record information and save it to the BoardInfo class.
 */
class Window extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem leave;

	public static Container pane;

	public static drawGrid dg;
	public static JLabel timer;
	public static JToolBar bottombar = new JToolBar();
	
	public static JButton p1 = new JButton();
	public static JButton p2 = new JButton();

	/**
	 * The constructor is the only function in the Window() class. It is responsible
	 * for completing everything that was mentioned in the Window() class doc.
	 */
	public Window() {
	    //sets the size of the window
		this.setPreferredSize(new Dimension(700, 700));
		this.setSize(new Dimension(700, 700));
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		//pane.setFocusable(true);
		
		JPanel p = new JPanel();
		p.setFocusable(true);
		p.setLayout(new BorderLayout());
		setJMenuBar(menuBar);

		//Menu option below is responsible for loading in the user's selected board
		JMenuItem load = new JMenuItem("Load Board");
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    //Opens up a jfilechooser window for the user where they will select a file.
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				File workingDirectory = new File(System.getProperty("user.dir"));
				jfc.setCurrentDirectory(workingDirectory);
				int returnValue = jfc.showOpenDialog(null);

				//If it is a valid choice
				if (returnValue == JFileChooser.APPROVE_OPTION) {
				    //Opens the selected file
					File user_input = jfc.getSelectedFile();
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(user_input));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// the code below is used to process the file that was chosen
					String filename = "";
					try {
					    //reads the first line of the file to save the row and column size
						filename = reader.readLine();
						// Reintializes the row and col values to those stated in the input file.
						String[] inp = filename.split(", ");
						int row = Integer.parseInt(inp[0]);
						int col = Integer.parseInt(inp[1]);
						BoardInfo.setRow(row);
						BoardInfo.setCol(col);

						try {
						    //Attempts to read in and save the board using the readData() function
							BoardInfo.setRow(row);
							BoardInfo.setCol(col);
							BoardInfo.setCurrentBoard(new int[row][col]);
							// FUnction call below opens the file and reads the data into an int[][]

							BoardInfo.setCurrentBoard(BoardParser.readData(user_input.getPath(), BoardInfo.getShips(),
									BoardInfo.getRow(), BoardInfo.getCol()));
							dg.paint(dg.getGraphics());
						//If the board cannot be read it is initialized to an empty board
						} catch (Exception e1) {
							e1.printStackTrace();
							BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
							dg.paint(dg.getGraphics());
						}
						//If the board cannot be read it is initialized to an empty board
					} catch (Exception e1) {
						e1.printStackTrace();
						BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
						dg.paint(dg.getGraphics());
					}

				}
			}
		});


		JMenu options = new JMenu("Options");
	      //JMenu options will allow the user to view and edit their settings. When this button
        //is pressed it will open a settings window by calling the settingsWindow() function.
		JMenuItem regSettings = new JMenuItem("Interface Settings");
		regSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		regSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new settingsWindow();
			}
		});
		options.add(load);
		options.add(regSettings);

		//The leave MenuItem will allow the users to quit the program by pressing this button.
		leave = new JMenuItem("Quit");
		leave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		leave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		options.add(leave);
		menuBar.add(options);

		//Initializes a new toolbar that will have buttons to complete actions
		JToolBar toolbar = new JToolBar();
		toolbar.setBackground(new Color(100, 97, 107));
		add(toolbar, BorderLayout.NORTH);

		//Menu option below is responsible for loading in the user's selected board
		JButton loadIcon = new JButton(new ImageIcon("images/newLoad.png"));
		loadIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			  //Opens up a jfilechooser window for the user where they will select a file.
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				File workingDirectory = new File(System.getProperty("user.dir"));
				jfc.setCurrentDirectory(workingDirectory);
				int returnValue = jfc.showOpenDialog(null);

				//If it is a valid choice
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    //Opens the selected file
					File user_input = jfc.getSelectedFile();
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(user_input));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// the code below is used to process the file that was chosen
					String filename = "";
					try {
						filename = reader.readLine();
						// Reintializes the row and column values to those stated in the input file.
						String[] inp = filename.split(", ");
						int row = Integer.parseInt(inp[0]);
						int col = Integer.parseInt(inp[1]);
						BoardInfo.setRow(row);
						BoardInfo.setCol(col);

						try {
							BoardInfo.setRow(row);
							BoardInfo.setCol(col);
							BoardInfo.setCurrentBoard(new int[row][col]);
							// FUnction call below opens the file and reads the data into an int[][]

							BoardInfo.setCurrentBoard(BoardParser.readData(user_input.getPath(), BoardInfo.getShips(),
									BoardInfo.getRow(), BoardInfo.getCol()));

							dg.paint(dg.getGraphics());
						//Sets the board to an empty grid if the file is invalid.
						} catch (Exception e1) {
							e1.printStackTrace();
							BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
							dg.paint(dg.getGraphics());
						}
						//Sets the board to an empty grid if the file is invalid. 
					} catch (Exception e1) {
						e1.printStackTrace();
						BoardInfo.setCurrentBoard(new int[BoardInfo.getRow()][BoardInfo.getCol()]);
						dg.paint(dg.getGraphics());
					}

				}
			}
		});

	     //The settings icon will allow the user to view and edit their settings. When this button
        //is pressed it will open a settings window by calling the settingsWindow() function.
		JButton settingsIcon = new JButton(new ImageIcon("images/settings.png"));
		settingsIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new settingsWindow();
			}
		});
	    //The leave icon will allow the users to quit the program by pressing this button.
		JButton exitIcon = new JButton(new ImageIcon("images/exit.png"));
		exitIcon.addActionListener((e) -> System.exit(0));

		toolbar.add(loadIcon);
		toolbar.add(settingsIcon);
		toolbar.add(exitIcon);

		//Initalizes the toolbar that is on the bottom of the screen
		bottombar.setBackground(new Color(100, 97, 107));
		add(bottombar, BorderLayout.SOUTH);

		//Ready button will initalize the game and allows the users to start making shots
		//on their opponent's board.
		JButton readyIcon = new JButton(new ImageIcon("images/ready.png"));
		readyIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String localCommand = "";
				String localResponse = "";
				String remoteResponse = "";
				//Sends a signal to the client that the server is ready'd up.
				if (Battleship.mode1 == Battleship.mode.SERVER)
				{
					do {
						localCommand = "conf readyUp PLAYER1 " + BoardInfo.getUsername();
						remoteResponse = Battleship.sendRecv(Battleship.peer,Battleship.protocol, localCommand);
						
					}while(!remoteResponse.equals("ready"));
					do {
						localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
					}
					while(!localResponse.equals("ready"));
					System.out.println("Player 1 ready");
				
				}
				//sends a signal to the server that the client is ready'd up
				else if (Battleship.mode1 == Battleship.mode.CLIENT)
				{
					do {
						localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
					}
					while(!localResponse.equals("ready"));
					do {
						localCommand = "conf readyUp PLAYER2 " + BoardInfo.getUsername();
						remoteResponse = Battleship.sendRecv(Battleship.peer,Battleship.protocol, localCommand);
						
					}while(!remoteResponse.equals("ready"));
					System.out.println("Player 2 ready");
					
				}
				
				//Client sends their battleship board to the server
	            if (Battleship.mode1 == Battleship.mode.CLIENT)
	            {
	                do
	                {
	                    localCommand = "conf sendBoard PLAYER2 " + Battleship.ArrayToString(BoardInfo.getCurrentBoard(), BoardInfo.getRow());
	                    remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
	                } while(!remoteResponse.equals("board 2 set"));
	            }
	            //Server receives the client's battleship board
	            else if (Battleship.mode1 == Battleship.mode.SERVER)
	            {
	                localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
	            } 
	            
	            //Server sends their battleship board to the client
	            if (Battleship.mode1 == Battleship.mode.SERVER)
	            {
	                do
	                {
	                    localCommand = "conf sendBoard PLAYER1 " + Battleship.ArrayToString(BoardInfo.getCurrentBoard(), BoardInfo.getRow());
	                    remoteResponse = Battleship.sendRecv(Battleship.peer, Battleship.protocol, localCommand);
	                } while(!remoteResponse.equals("board 1 set"));
	            }
	            
	            //client receives the Server's battleship board
	            else if (Battleship.mode1 == Battleship.mode.CLIENT)
	            {
	                localResponse = Battleship.recvSend(Battleship.peer, Battleship.protocol);
	            }
				
				//Sets the server's show board equal to false.. will display the opponents board so they can make a shot
				if(Battleship.mode1 == Battleship.mode.SERVER)
				{
				    BoardInfo.setShowBoard(false);
				    Battleship.exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            int temp_timer = BoardInfo.getTimer();
                            timer.setText(BoardInfo.getTimer() + " seconds remaining");
                            try {
                                do {
                                    if (Battleship.timerClick) {
                                        Battleship.timerClick = false;
                                        return;
                                    }
                                    
                                    temp_timer-= 1;
                                    Window.timer.setText(temp_timer + " seconds remaining");
                                    Window.bottombar.repaint();
                                    
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    
                                } while (temp_timer > 0);
                                
                                if(temp_timer == 0)
                                {
                                    System.out.println("HERE");
                                    Battleship.timerClick = false;
                                    Battleship.timerEmpty = true;
                                    Battleship.clicked = true; 
                                }
                            }
                            catch(Exception e)
                            {
                                return;
                                // do nothing :)
                            }
                        }
                    });
				}
				//Sets the opponent's show board equal to true... Will wait until the server makes their first move.
				else
				{
				    BoardInfo.setShowBoard(true);
				}
				
				Battleship.timerClick = false;
				Battleship.timerEmpty = false;
				Battleship.clicked = false;
				
				if (Battleship.mode1 == Battleship.mode.SERVER)
				    BoardInfo.setShowBoard(false);
				else
				    BoardInfo.setShowBoard(true);
				
				Window.p1.setText(Battleship.protocol.getName(1) + ": " + Battleship.p1Wins);
				Window.p2.setText(Battleship.protocol.getName(0) + ": " + Battleship.p2Wins);
				
				//Initializes the game state to player1
				Battleship.protocol.setState(BattleshipStates.PLAYER1);
				//Calls the command listener which will send messages between the server and the client about the shots
				Battleship.exec.execute(new CommandListener());
				//Updates the board interface.
	            Window.dg.repaint();
	            Window.dg.paint(Window.dg.getGraphics());
	            Window.dg.revalidate();
	            Window.dg.validate();
	            Window.dg.getIgnoreRepaint();
                
			}
		});
		//adds all of the buttons to the bottom bar. 
		bottombar.add(readyIcon);
		bottombar.addSeparator();
		setVisible(true);

		bottombar.add(p1);
		bottombar.addSeparator();
		bottombar.add(p2);
		bottombar.addSeparator();
		bottombar.addSeparator(new Dimension(220,10));
		Window.timer = new JLabel("Timer");
		bottombar.add(Window.timer);
		
		//Updates the users graphic interface.
		p.add(dg = new drawGrid(), BorderLayout.CENTER);
		pane.add(p, BorderLayout.CENTER);

	}
}
