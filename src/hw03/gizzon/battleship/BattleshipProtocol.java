package hw03.gizzon.battleship;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JOptionPane;


/**
 * The BattleshipProtocol class is used to translate messages that are sent between the users and
 * the server and implement the appropriate actions
 * 
 * @author NicoleGizzo
 *
 */
public class BattleshipProtocol {
	private GameSim game;
	private BattleshipStates state;
	private boolean p1Ready = false;
	private boolean p2Ready = false;
	private int timer = 0;
	
    /**
     * The BattleshipProtocol constructor is used to establish the protocol state to the
     * intial state(INIT) and vreate a new game.
     *
     */
	public BattleshipProtocol() {
		this.setState(BattleshipStates.INIT);
		this.game = new GameSim("", "", BoardInfo.getRow(), BoardInfo.getCol());
	}

    /**
     * The getState() function is used to return the current state of the game
     *
     * @return Returns the current game state
     *
     */
	public BattleshipStates getState() {
		return state;
	}

    /**
     * The setStates function is used to set the current state of the game
     *
     * @param state The state that the game will be set to
     *
     */
	public void setState(BattleshipStates state) {
		this.state = state;
	}
	
    /**
     * The getPlayerName function is used return the appropriate player's name
     *
     * @param player A string that will be used to distinguished between player 1 and 2
     * @return Returns the name of the desired player
     *
     */
	public String getPlayerName(String player)
	{
		if( player.equals("Player1"))
		{
			return game.getPlayer1();
		}
		else
		{
			return game.getPlayer2();
		}
	}

    /**
     * The stringToArray function is used to convert a string message that represents
     * a board into an actual 2d array. 
     *
     * @param str A string that represents a battleship board
     * @return An int[][] containing the information in the string in a proper format
     *
     */  
	public int[][] stringToArray(String str)
	{
		int[][] res = new int[BoardInfo.getRow()][BoardInfo.getRow()];
		String[] temp = str.split(":");
		for (int i = 0; i < BoardInfo.getRow(); i++)
		{
			String[] temp_line = temp[i].split(",");
			for(int j = 0; j < BoardInfo.getRow(); j++)
			{
				res[i][j] = Integer.parseInt(temp_line[j]);
			}
		}
		return res;
	}
	
    /**
    *The process() function is used to desipher commands that are sent by the server and recieved by
    * the users. This will take a message that is recieved by the server and implement the appropriate
    * action
    * 
    * @param command A string representing the command recieved by the server
    * @return the response that is followed by the command
    *
    */
	public String process(String command)
	{
		String response = "";
		String[] commands = parseCommand(command);
		if(commands[0].equals("conf"))
		{
            //This command is used to retrieve the names of the 2 players
			if(commands[1].equals("playerName"))
			{
				if (commands[2].equals("PLAYER1"))
				{
					response = "confrecv playerName " + game.getPlayer1();
				}
				else if (commands[2].equals("PLAYER2"))
				{
					response = "confrecv playerName " + game.getPlayer2();
				}
			}

            //This command is used to set the names of the two players
			else if(commands[1].equals("setPlayerName"))
			{
				if (commands[2].equals("PLAYER1"))
				{
					game.setPlayer1(commands[3]);
					response = "confrecv set player 1 name";
				}
				else if (commands[2].equals("PLAYER2"))
				{
					game.setPlayer2(commands[3]);
					response = "confrecv set player 2 name";
				}
			}

            //This command is used to get the board size of each player
			else if(commands[1].equals("boardSize"))
			{
				if (commands[2].equals("PLAYER1"))
				{
					response = "" + game.getRow();
				}
				else if (commands[2].equals("PLAYER2"))
				{
					response = "" + game.getRow();
				}
			}

            //This command is used to send the user's board between the clients and the server
			else if(commands[1].equals("sendBoard"))
			{
				if (commands[2].equals("PLAYER1"))
				{
					game.setBoardIndex(stringToArray(commands[3]), 0);
					response = "board 1 set";
				}
				else if (commands[2].equals("PLAYER2"))
				{
					game.setBoardIndex(stringToArray(commands[3]), 1);
					response = "board 2 set";
				}
			}

            //This commmand is used to set the player's timers.
			else if(commands[1].equals("setTimer"))
			{
				game.setTimer(Integer.parseInt(commands[2]));
				response = "timer " + game.getTimer();
			}

            //this command is used to retrieve the timer amount each player has selected
			else if(commands[1].equals("getTimer"))
			{
				response = "" + game.getTimer();
			}

            //this command is used to inform the server that a player has ready'd up
			else if(commands[1].equals("readyUp"))
			{
				if (commands[2].equals("PLAYER1"))
				{
					this.p1Ready = true;
					response = "ready";
				}
				else if (commands[2].equals("PLAYER2"))
				{
					this.p2Ready = true;
					response = "ready";
				}
			}

            //command doesn't match any of the above
			else
				response = "unknown";
		}

        //Used to handle when a player presses on the board to make a shot
        else if(commands[0].equals("shot"))
        {
            response = "shot ok";
            if(state == BattleshipStates.PLAYER1)
            {
                //If the player runs out of time, the game ends
                if (Battleship.timerEmpty)
                {
                    response = "win PLAYER2";
                    return response;
                }

                //Gets the coordinates for the attempted shot
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(Integer.parseInt(commands[1]));
                temp.add(Integer.parseInt(commands[2]));
                
                //Sends the shoot to shoot move and make the appropriate actions
                game.shootMove(BattleshipStates.PLAYER1, temp.get(0), temp.get(1));

                //Changes state to player2 so they can make the next shot
                state = BattleshipStates.PLAYER2;

                //Adds the shot that was just make to the list of previous shots
                if(Battleship.mode1 == Battleship.mode.SERVER)
                {
                    BoardInfo.getMy_moves().add(temp);
                }

                //If the server shot
                else
                {
                    //Adds the attempted shot to the other user's list of attempted shots
                    BoardInfo.getOpp_moves().add(temp);

                    //Runs the timer.. Will start at the timer both users have selected
                    //and decrease each second. If the time is up, the player loses.
                    Battleship.exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            int temp_timer = BoardInfo.getTimer();
                            Window.timer.setText(BoardInfo.getTimer() + " seconds remaining");
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
                                System.out.println(temp_timer);
                                if(temp_timer == 0)
                                {
                                    System.out.println("here");
                                    Battleship.timerClick = false;
                                    Battleship.timerEmpty = true;
                                    Battleship.clicked = true; 
                                }
                            }
                            catch(Exception e)
                            {
                                // do nothing :)
                                return;
                            }
                            System.out.println("hey");
                            
                        }
                    });
                }  

                //Checks if player1 won the game.
                if(game.isWinner(BattleshipStates.PLAYER1))
                {
                    //Stops the timer and sets the game state to finished
                    Battleship.timerClick = true;
                    Battleship.timerEmpty = false;
                    BoardInfo.setShowBoard(true);
                    state = BattleshipStates.FINISHED;
                    Battleship.p1Wins++;
                    response = "win PLAYER1";

                    //Displays the name of the winner
                    Window.p1.setText(Battleship.protocol.getName(1) + ": " + Battleship.p1Wins);
                    Window.p2.setText(Battleship.protocol.getName(0) + ": " + Battleship.p2Wins);
                    Window.bottombar.paint(Window.bottombar.getGraphics());
                    System.out.println("P1 wins: " + Battleship.p1Wins);
                    System.out.println("P2 wins: " + Battleship.p2Wins);
                    int result = -1;

                    //Asks if the user would like to play another game
                    String[] options = {"Yes", "No"};
                    result = JOptionPane.showOptionDialog(null, "Would you like to play another game?", "Play again?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    
                    //If yes, reinitializes the state of the game and resets all variables.
                    if(result == 0)
                    {
                        Battleship.protocol.setState(BattleshipStates.INIT);
                        BoardInfo.getOpp_moves().clear();
                        BoardInfo.getMy_moves().clear();
                        BoardInfo.setShowBoard(true);
                        Battleship.protocol.getGame().reset();
                        
                        Window.dg.repaint();
                        Window.dg.paint(Window.dg.getGraphics());
                        Window.dg.revalidate();
                        Window.dg.validate();
                        Window.dg.getIgnoreRepaint();
                    }

                    //if no, game is exited.
                    else
                    {
                        System.exit(0);
                    }
                }
                    
            }
            else if(state == BattleshipStates.PLAYER2)
            {
                //Returns that player1 won if player 2 ran out of time
                if (Battleship.timerEmpty)
                {
                    response = "win PLAYER1";
                    return response;
                }

                //Adss the attempted shot to the users list of rpevious shots
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(Integer.parseInt(commands[1]));
                temp.add(Integer.parseInt(commands[2]));
                game.shootMove(BattleshipStates.PLAYER2, temp.get(0), temp.get(1));
                
                //canges the game state to player1 so they can make the next move
                state = BattleshipStates.PLAYER1;
                if(Battleship.mode1 == Battleship.mode.CLIENT)
                {
                    
                    BoardInfo.getMy_moves().add(temp);
                }
                else
                {
                    //Adds the move to the opposite user;s list of previous shots
                    BoardInfo.getOpp_moves().add(temp);
                    //Runs the timer.. Will start at the timer both users have selected
                    //and decrease each second. If the time is up, the player loses.
                    Battleship.exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            int temp_timer = BoardInfo.getTimer();
                            Window.timer.setText(BoardInfo.getTimer() + " seconds remaining");
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
                                System.out.println(temp_timer);
                                if(temp_timer == 0)
                                {
                                    System.out.println("here");
                                    Battleship.timerClick = false;
                                    Battleship.timerEmpty = true;
                                    Battleship.clicked = true; 
                                }
                            }
                            catch(Exception e)
                            {
                                // do nothing :)
                                return;
                            }
                            System.out.println("hey");
                            
                        }
                    });
                }  

                //Checks if player2 won the game.
                if(game.isWinner(BattleshipStates.PLAYER2))
                {

                    //Stops the timer and doesnt allow new clicks
                    Battleship.timerClick = true;
                    Battleship.timerEmpty = false;
                    BoardInfo.setShowBoard(true);
                    state = BattleshipStates.FINISHED;
                    Battleship.p2Wins++;
                    //displays that player 2 won the game
                    response = "win PLAYER2";
                    Window.p1.setText(Battleship.protocol.getName(1) + ": " + Battleship.p1Wins);
                    Window.p2.setText(Battleship.protocol.getName(0) + ": " + Battleship.p2Wins);
                    Window.bottombar.paint(Window.bottombar.getGraphics());
                    System.out.println("P1 wins: " + Battleship.p1Wins);
                    System.out.println("P2 wins: " + Battleship.p2Wins);
                    int result = -1;

                    //Asks if the users would like to play another game
                    String[] options = {"Yes", "No"};
                    result = JOptionPane.showOptionDialog(null, "Would you like to play another game?", "Play again?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    //If yes, reinitializes the state of the game and resets all variables.
                    if(result == 0)
                    {
                        Battleship.protocol.setState(BattleshipStates.INIT);
                        BoardInfo.getOpp_moves().clear();
                        BoardInfo.getMy_moves().clear();
                        BoardInfo.setShowBoard(true);
                        Battleship.protocol.getGame().reset();
                        
                        Window.dg.repaint();
                        Window.dg.paint(Window.dg.getGraphics());
                        Window.dg.revalidate();
                        Window.dg.validate();
                        Window.dg.getIgnoreRepaint();
                    }
                    //if no, exit the game.
                    else
                    {
                        System.exit(0);
                    }
                }
            }
            BoardInfo.setShowBoard(!BoardInfo.isShowBoard());

            Window.dg.repaint();
            Window.dg.paint(Window.dg.getGraphics());
            Window.dg.revalidate();
            Window.dg.validate();
            Window.dg.getIgnoreRepaint();
        }

        //checks if player1 won the game
        else if (command.equals("win PLAYER1")) {

            //shows that player 1 won the game and clears the timer and does not allow ticks
            response = "win PLAYER1";
            Battleship.timerClick = true;
            Battleship.timerEmpty = false;
            BoardInfo.setShowBoard(true);
            Battleship.p1Wins++;
            Window.p1.setText(Battleship.protocol.getName(1) + ": " + Battleship.p1Wins);
            Window.p2.setText(Battleship.protocol.getName(0) + ": " + Battleship.p2Wins);

            //displays the winner on each board
            Window.bottombar.paint(Window.bottombar.getGraphics());
            System.out.println("P1 wins: " + Battleship.p1Wins);
            System.out.println("P2 wins: " + Battleship.p2Wins);
            int result = -1;

            //asks if the users want to play another game
            String[] options = {"Yes", "No"};
            result = JOptionPane.showOptionDialog(null, "Would you like to play another game?", "Play again?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            //If yes, reinitializes the state of the game and resets all variables.
            if(result == 0)
            {
                Battleship.protocol.setState(BattleshipStates.INIT);
                BoardInfo.getOpp_moves().clear();
                BoardInfo.getMy_moves().clear();
                BoardInfo.setShowBoard(true);
                Battleship.protocol.getGame().reset();
                
                Window.dg.repaint();
                Window.dg.paint(Window.dg.getGraphics());
                Window.dg.revalidate();
                Window.dg.validate();
                Window.dg.getIgnoreRepaint();
            }
            //if no, exits the game
            else
            {
                System.exit(0);
            }
            return response;
            
        }
         //checks if player2 won the game
        else if (command.equals("win PLAYER2"))
        {
            //Stops the timer and does not allow new clicks. Displays that player2 won the game
            Battleship.timerClick = true;
            Battleship.timerEmpty = false;
            BoardInfo.setShowBoard(true);
            response = "win PLAYER2";
            Battleship.p2Wins++;
            Window.p1.setText(Battleship.protocol.getName(1) + ": " + Battleship.p1Wins);
            Window.p2.setText(Battleship.protocol.getName(0) + ": " + Battleship.p2Wins);
            Window.bottombar.paint(Window.bottombar.getGraphics());
            System.out.println("P1 wins: " + Battleship.p1Wins);
            System.out.println("P2 wins: " + Battleship.p2Wins);
            int result = -1;

            //asks if the users want to play another game
            String[] options = {"Yes", "No"};
            result = JOptionPane.showOptionDialog(null, "Would you like to play another game?", "Play again?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            //If yes, reinitializes the state of the game and resets all variables.
            if(result == 0)
            {
                Battleship.protocol.setState(BattleshipStates.INIT);
                BoardInfo.getOpp_moves().clear();
                BoardInfo.getMy_moves().clear();
                BoardInfo.setShowBoard(true);
                Battleship.protocol.getGame().reset();
                
                Window.dg.repaint();
                Window.dg.paint(Window.dg.getGraphics());
                Window.dg.revalidate();
                Window.dg.validate();
                Window.dg.getIgnoreRepaint();
            }
            //if no, game is exited
            else
            {
                System.exit(0);
            }
            return response;
        }
		return response;
	}
	
    /**
    *The function parseCommand() splits up a command by the spaces
    * @param command the command that is to be split
    * @return the split string
    */
	private String[] parseCommand(String command) {
		return command.split("\\s");
	}

    /**
    *The setTimer() functions sets the timer
    * @param time the time that the timer will be equals
    */
	public void setTimer(int time)
	{
		game.setTimer(time);
	}

    /**
    *The getTimer() function will return the timer
    * @return Returns the time
    */
	public int getTimer()
	{
		return game.getTimer();
	}
	
    /**
    *The getSize() function will return the size of the baord.
    * @return the size of the board
    *
    */
	public int getSize()
	{
		return game.getRow();
	}

    /**
    *The getName() function will return the name of the user
    *
    * @param player The number of the player, 1 or 2, that the user wants to retrieve
    * @return the name of the user
    *
    */
	public String getName(int player)
	{
		if (player == 1)
			return game.getPlayer1();
		else
			return game.getPlayer2();
	}

    /**
    *The getGame() function will return the current game
    * @return the current game.
    *
    */
	public GameSim getGame()
	{
	    return game;
	}
}

