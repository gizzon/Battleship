package hw03.gizzon.battleship;

import java.awt.Color;
import java.util.ArrayList;

public class BoardInfo {
	private static int row = 10;
	private static int col = 10;
	private static Color uiColor = new Color(0,50,200);
	private static int[][] currentBoard = null;
	private static Color shipColor = new Color(0,50,200);
	private static Color waterColor = new Color(0,50,200);
	private static Color hitColor = new Color(0,50,200);
	private static String username = "";
	private static int timer = 30;
	private static String ip = "127.0.0.1";
	private static int port = 44444;
	private static String selectedSize = "10x10";
	private static String opponent = "Opponent";
	private static int score = 0;
	private static int oScore = 0;
	private static String boardFile = "";
	private static ArrayList<Ship> ships = new ArrayList<Ship> ();
	private static int numShips = 0;
	private static boolean showBoard = true;
	private static ArrayList< ArrayList<Integer>> my_hit_ships = new ArrayList<ArrayList<Integer>>();
	private static ArrayList< ArrayList<Integer>> my_moves = new ArrayList<ArrayList<Integer>>();
	private static ArrayList< ArrayList<Integer>> opp_moves = new ArrayList<ArrayList<Integer>>();
	
	
	/**
    *The getRow() function will get the user's size of the board
    * @return the number of rows in the baord
    *
    */
	public static int getRow() {
		return row;
	}

	/**
    *The setRow() function will set the user's size of the board
    * @param row the number of rows in the baord
    *
    */
	public static void setRow(int row) {
		BoardInfo.row = row;
	}

	/**
    *The getCol() function will get the user's size of the board
    * @return the number of columns in the baord
    *
    */
	public static int getCol() {
		return col;
	}
	/**
    *The setCol() function will set the user's size of the board
    * @param col the number of cols in the baord
    *
    */
	public static void setCol(int col) {
		BoardInfo.col = col;
	}

	/**
    *The getUiCOlor() function will get the user's UI COlor
    * @return the UI COlor
    *
    */
	public static Color getUiColor() {
		return uiColor;
	}

	/**
    *The setUiCOlor() function will sert the user's UI COlor
    * @param uiColor the UI Color
    *
    */
	public static void setUiColor(Color uiColor) {
		BoardInfo.uiColor = uiColor;
	}
	public static int[][] getCurrentBoard() {
		return currentBoard;
	}
	public static void setCurrentBoard(int[][] currentBoard) {
		BoardInfo.currentBoard = currentBoard;
	}
	public static void setCurrentBoardIndex(int row, int col, int val) {
		BoardInfo.currentBoard[row][col] = val;
	}
	public static Color getShipColor() {
		return shipColor;
	}
	public static void setShipColor(Color shipColor) {
		BoardInfo.shipColor = shipColor;
	}
	public static Color getWaterColor() {
		return waterColor;
	}
	public static void setWaterColor(Color waterColor) {
		BoardInfo.waterColor = waterColor;
	}
	public static Color getHitColor() {
		return hitColor;
	}
	public static void setHitColor(Color hitColor) {
		BoardInfo.hitColor = hitColor;
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		BoardInfo.username = username;
	}
	public static int getTimer() {
		return timer;
	}
	public static void setTimer(int timer) {
		BoardInfo.timer = timer;
	}
	public static String getIp() {
		return ip;
	}
	public static void setIp(String ip) {
		BoardInfo.ip = ip;
	}
	public static int getPort() {
		return port;
	}
	public static void setPort(int port) {
		BoardInfo.port = port;
	}
	public static String getSelectedSize() {
		return selectedSize;
	}
	public static void setSelectedSize(String selectedSize) {
		BoardInfo.selectedSize = selectedSize;
	}
	public static String getOpponent() {
		return opponent;
	}
	public static void setOpponent(String opponent) {
		BoardInfo.opponent = opponent;
	}
	public static int getScore() {
		return score;
	}
	public static void setScore(int score) {
		BoardInfo.score = score;
	}
	public static int getoScore() {
		return oScore;
	}
	public static void setoScore(int oScore) {
		BoardInfo.oScore = oScore;
	}
	public static String getBoardFile() {
		return boardFile;
	}
	public static void setBoardFile(String boardFile) {
		BoardInfo.boardFile = boardFile;
	}
	public static ArrayList<Ship> getShips() {
		return ships;
	}
	public static void setShips(ArrayList<Ship> ships) {
		BoardInfo.ships = ships;
	}
	public static int getNumShips() {
		return numShips;
	}
	public static void setNumShips(int numShips) {
		BoardInfo.numShips = numShips;
	}
	public static ArrayList< ArrayList<Integer>> getMy_hit_ships() {
		return my_hit_ships;
	}
	public static void setMy_hit_ships(ArrayList< ArrayList<Integer>> my_hit_ships) {
		BoardInfo.my_hit_ships = my_hit_ships;
	}
	public static ArrayList< ArrayList<Integer>> getMy_moves() {
		return my_moves;
	}
	public static void setMy_moves(ArrayList< ArrayList<Integer>> my_moves) {
		BoardInfo.my_moves = my_moves;
	}
    public static boolean isShowBoard() {
        return showBoard;
    }
    public static void setShowBoard(boolean showBoard) {
        BoardInfo.showBoard = showBoard;
    }
    public static ArrayList< ArrayList<Integer>> getOpp_moves() {
        return opp_moves;
    }
    public static void setOpp_moves(ArrayList< ArrayList<Integer>> opp_moves) {
        BoardInfo.opp_moves = opp_moves;
    }
	
}

