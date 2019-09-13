package hw03.gizzon.battleship;

import java.util.ArrayList;


/**
* gStates is an enum for deciding which state the game is in. Player1 is used
* when player1 is making a move, PLAYER2 is used when player2 is making a move.
* The states PLAYER1_WON and and PLAYER2_WON is used to signal when the game is
* over and who won. 
*
* @author NicoleGizzo
*/
enum gStates{
	INIT, IN_PROGESS, PLAYER1, PLAYER2, END, PLAYER1_WON, PLAYER2_WON
}

/**
* The GameSim class is used to simulate the process of BattleShip. This GameSim
* class contains the names of both players, as well as their boards and the board
* dimensions. It also contains gStates, which allows the class to change it's game
* state.
*
* @author Nicole Gizzo
*/
public class GameSim {
	private String player1;
	private String player2;
	private ArrayList<int[][]> boards;
	private int row;
	private int col;
	private gStates state;
	private int timer;

	
	/**
	* The GameSim constructor initializes the the names of the players, creates
	* an array to eventually hold the boards of the players, and will hold the 
	* dimensions of the board. The boards though are intially set to null. This
	* will create a new instance of the game.
	*
	* @param p1 Player 1's name
	* @param p2 Player 2's name
	* @param row the number of rows in the board
	* @param col the number of columns in the board
	*/
	public GameSim(String p1, String p2, int row, int col) {
		this.setPlayer1(p1);
		this.setPlayer2(p2);
		this.setRow(row);
		this.setCol(col);
		this.boards = new ArrayList<int[][]>();
		this.boards.add(null);
		this.boards.add(null);
	}
	
	/**
	* The reset() function will reinitalize and restart the game by clearing the boards
	* and change the gState to "INIT".
	*/
	public void reset() {
		this.setState(gStates.INIT);
		this.setBoards(new ArrayList<int[][]>());
		this.getBoards().add(null);
		this.getBoards().add(null);
	}
	
	/**
	* The shootMove() function will simulate a player making a shot on their opponents
	* board. This function will take the name of the player who is making the shot,
	* and the coordinates of where they are shooting on the board. The isHit() function
	* will then be called to verify wether the hit was succesfull for not. 
	*
	* @param player the name of the player shooting
	* @param row The row in which the user shot
	* @param col the column in which the user shot 
	*/
	public void shootMove(BattleshipStates player, int row, int col)
	{
		if (player == BattleshipStates.PLAYER1)
		{
			if (isHit(BattleshipStates.PLAYER1, row, col))
			    boards.get(1)[row][col] = 0;
				
		}
		else
		{
			if (isHit(BattleshipStates.PLAYER2, row, col))
			    boards.get(0)[row][col] = 0;
		}
			
		
	}
	

	/**
	* The isHit() is used to check wether a shot that a player took on
	* their oppononets board was a succesful hit or not. If the player makes
	* the shot, the function returns true, ellse.. fale.
	*
	* @param player the name of the player shooting
	* @param row The row in which the user shot
	* @param col the column in which the user shot 
	* @return A boolean indicating wether the shot was successful or not.
	*/
	public boolean isHit(BattleshipStates player, int row, int col)
	{
		if (player == BattleshipStates.PLAYER1)
		{
			int [][] temp = boards.get(1);
			if (temp[row][col] != -1)
				return true;
			else
				return false;
		}
		else
		{
			int [][] temp = boards.get(0);
			if (temp[row][col] != -1)
				return true;
			else
				return false;
		}
	}

	/**
	* The isWinner() function is used to check wether a player has
	* hit all of the opponents ships and th egame is done. This is done
	* by taking the name of the player you a trying to check if they won,
	* and then looking at the opposite players board. If their board is 
	* has nothing on their board besides empty water(-1) and hit shits(0),
	* then that player won
	*
	* @param player the name of the player shooting
	* @return A boolean whether the given player won or not.
	*/
	public boolean isWinner(BattleshipStates player)
	{
		if (player == BattleshipStates.PLAYER1)
		{
			int [][] temp = boards.get(1);
			for(int i = 0; i < row; i++)
			{
				for(int j = 0; j < col; j++)
				{
					if (temp[i][j] != -1 && temp[i][j] != 0)
						return false;
				}
			}
			this.state = gStates.PLAYER1_WON;
			return true;
		}
		else
		{
			int [][] temp = boards.get(0);
			for(int i = 0; i < row; i++)
			{
				for(int j = 0; j < col; j++)
				{
					if (temp[i][j] != -1 && temp[i][j] != 0)
						return false;
				}
			}
			this.state = gStates.PLAYER2_WON;
			return true;
		}
	}

	/**
	* The resize() function allows the size of the boards
	* to be changed in between the games when a new board is loaded.
	*
	* @param row the number of rows in the new board
	* @param col the number of columns in the new board
	*/
	public void resize(int row, int col)
	{
		this.setRow(row);
		this.setCol(col);
		reset();
	}

	/**
	* The getPlayer1 function will return the name of player 1
	*
	* @return the name of player 1
	*/
	public String getPlayer1() {
		return player1;
	}

	/**
	* The setPlayer1 function allow you to change the name of player1
	*
	* @param player1 the name of player 1
	*/
	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	/**
	* The getPlayer2 function will return the name of player 2
	*
	* @return the name of player 2
	*/
	public String getPlayer2() {
		return player2;
	}

	/**
	* The setPlayer2 function allow you to change the name of player2
	*
	* @param player2 the name of player 2
	*/
	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	/**
	* The getBoards() function will return the boards containing each players
	* board.
	* 
	* @return the arraylist of boards
	*/
	public ArrayList<int[][]> getBoards() {
		return boards;
	}

	/**
	* The setBoards() function will you to change the boards in the arraylist
	* to a new list of boards.
	*
	* @param boards a list containing two new boards
	*/
	public void setBoards(ArrayList<int[][]> boards) {
		this.boards = boards;
	}
	
	/**
	* The setBoardIndex() function will you to change a specific board in the arraylist
	* to a new noard.
	*
	* @param board the new board to be inserted
	* @param index the index where the new board will be inserted
	*/
	public void setBoardIndex(int[][] board, int index)
	{
		BoardInfo.setRow(board.length);
		BoardInfo.setCol(board.length);
		this.boards.set(index, board);
	}

	/**
	* The getRow() function will return the number of rows in each
	* board.
	* 
	* @return the number of rows
	*/
	public int getRow() {
		return row;
	}

	/**
	* The setRow() function will change the number of rows in each
	* board.
	* 
	* @param row the number of rows
	*/
	public void setRow(int row) {
		this.row = row;
	}

	/**
	* The getCol() function will return the number of cols in each
	* board.
	* 
	* @return the number of cols
	*/
	public int getCol() {
		return col;
	}

	/**
	* The setCol() function will change the number of columns in each
	* board.
	* 
	* @param col the number of columns
	*/
	public void setCol(int col) {
		this.col = col;
	}

	/**
	* The getState() function will return current state of the game
	* 
	* @return the current state of the game
	*/
	public gStates getState() {
		return state;
	}

	/**
	* The setState() function will change current state of the game
	* 
	* @param state the current state of the game
	*/
	public void setState(gStates state) {
		this.state = state;
	}


	/**
	* The getTimer() function will return the timer
	* 
	* @return the shared timer between the two players
	*/
	public int getTimer() {
		return timer;
	}

	/**
	* The setTimer() function will allow you to change the value of the timer
	* 
	* @param timer The new value the timer will hold.
	*/
	public void setTimer(int timer) {
		this.timer = timer;
	}


}
