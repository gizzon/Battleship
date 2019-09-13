package hw03.gizzon.battleship;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;



/**
* The BoardParser class is used to take a file that contains the battleship board and convert
* it to the necessary board format(). It will also make all of the necessary checks to ensure the board
* is valid
*@author NicoleGizzo
*/
public class BoardParser {

	/**
	* The isNumeric function takes a string and checks wether it contains all numbers or not.
	* @param s A string
	* @return A boolean that represents true if the string is all numbers, false otherwise
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
	 * Returns String[][] that represents a grid. The function takes an input file
	 * and reads it line by line, putting the input into the grid.
	 *
	 * @param filename a string that holds the name of the input file
	 * @param ships    an arraylist that will hold the ship objects
	 * @param row      an int that is equal to the # of rows in the grid
	 * @param col      an int that is equal to the # of columns in the grid
	 * @return A grid representing the battlehsip board
	 * @throws Exception If an input does not meet the specifications, a
	 *                     RuntimeException is thrown
	 */
	public static int[][] readData(String filename, ArrayList<Ship> ships, int row, int col) throws Exception{
		// Following code opens and reads the first line of the file
		ships.clear();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String [] data;
		String [] size;
		String line = null;
		
		/*
		 * Splits the first line of the file and saves the row and col into their
		 * appropriate variables. If the input does not meet the specifications, a
		 * RuntimeException is thrown.
		 */
		line = reader.readLine();
		size = line.split(", ");
		if (size.length != 2) {
			throw new RuntimeException("Bad input file, first line does not follow specifications of \"row, column\"");
		}
		row = Integer.parseInt(size[0]);
		col = Integer.parseInt(size[1]);
		if (row != col)
		{
			throw new RuntimeException(
					"Bad grid input, row size must equal grid size");
		}
		if ((row == 10 && col == 10) || (row == 9 && col == 9) || (row == 8 && col == 8))
		{
			
		}
		else
		{
			throw new RuntimeException(
					"Bad grid input, row size must equal grid size and they must either be of size 8, 9, or 10");
		}
		
		//while loop below is used to read in each ship and perform necessary checks
		int shipcount = 0;
		while((line = reader.readLine())!= null)
		{
			data = line.split(", ");
			
			//ensures data is in correct format
			int len = data.length;
			if(len != 4)
				throw new RuntimeException(
						"Bad ship data, must be in the format of \"Size, startX, startY, orientation\"");
			
			//ensures data is in correct format
			if (!isNumeric(data[0]))
				throw new RuntimeException(
						"Bad ship data, must be in the format of \"Size, startX, startY, orientation\"");
			
			int ship_size = Integer.parseInt(data[0]);
			if(isNumeric(data[0]) && (ship_size == 4 || ship_size == 3 || ship_size == 2 || ship_size == 1))
			{
				
			}
			//ensures data is in correct format
			else {
				throw new RuntimeException("Bad ship data, ship must be of size 1, 2, 3 or 4.");
			}
			//ensures data is in correct format
			if (!isNumeric(data[1]))
				throw new RuntimeException(
						"Bad ship data, must be in the format of \"Size, startX, startY, orientation\"");
			int startX = Integer.parseInt(data[1]);
			//makes sure thr row is a valid size
			if(startX < 0 || startX >= row)
				throw new RuntimeException(
						"Bad ship data, start X index out of bounds");
			//makes sure the ships column position is valid
			if (!isNumeric(data[2]))
				throw new RuntimeException(
						"Bad ship data, must be in the format of \"Size, startX, startY, orientation\"");
			
			int startY = Integer.parseInt(data[2]);
			if(startY < 0 || startY >= col)
				throw new RuntimeException(
						"Bad ship data, start Y index out of bounds");
			String orientation = data[3];
			if (!orientation.equals("Horizontal") && !orientation.equals("Vertical"))
				throw new RuntimeException(
						"Bad ship data, orientation must either be \"Horizontal\" or \"Vertical\"");
			
			//Creates the ship depending on its sizes
			if (ship_size == 4)
			{
				ships.add(new Ship(ship_size, startX, startY, orientation, new Color(0,255, 0)));
			}
			if (ship_size == 3)
			{
				ships.add(new Ship(ship_size, startX, startY, orientation, new Color(255,0,0)));
			}
			if (ship_size == 2)
			{
				ships.add(new Ship(ship_size, startX, startY, orientation, new Color(0,0,255)));
			}
			if (ship_size == 1)
			{
				ships.add(new Ship(ship_size, startX, startY, orientation,new Color(255,255,0)));
			}
			
			
			shipcount++;
		}

		int fours = 0;
		int threes = 0;
		int twos = 0;
		int ones = 0;
		
		//for loop below ensures there are the correct number of ships depending on board size
		for(int i = 0; i < shipcount; i++)
		{
			if (ships.get(i).getShipSize() == 4)
				fours++;
			if (ships.get(i).getShipSize() == 3)
				threes++;
			if (ships.get(i).getShipSize() == 2)
				twos++;
			if (ships.get(i).getShipSize() == 1)
				ones++;
			
		}

		System.out.println("Row: " + row + " col: " + col);
		System.out.println("fours: " + fours);
		System.out.println("threes: " + threes);
		System.out.println("twos: " + twos);
		System.out.println("ones: " + ones);
		if (row == 10 && col == 10)
		{
			if (fours!= 1 || threes!= 2 || twos != 3 || ones != 4)
				throw new RuntimeException(
						"Bad ship data, invalid number of ships");
		}
		if (row == 9 && col == 9)
		{
			if (fours!= 3 || threes!= 5 || twos != 0 || ones != 0)
				throw new RuntimeException(
						"Bad ship data, invalid number of ships");
		}
		if (row == 8 && col == 8)
		{
			if (fours!= 1 || threes!= 3 || twos != 3 || ones != 0)
				throw new RuntimeException(
						"Bad ship data, invalid number of ships");
		}
		
		//Creates a new grid filled with -1, representing empty water
		int[][] grid = new int[row][col];
		for (int i = 0; i < row; i++)
		{
			for(int j = 0; j < col; j++)
			{
				grid[i][j] = -1;
			}
		}
		
		for(int i = 0; i < shipcount; i++)
		{
			int tsize = ships.get(i).getShipSize();
			String o = ships.get(i).getOrientation();
			int y = ships.get(i).getStartX();
			int x = ships.get(i).getStartY();
			int fill = 0;
			if(o.equals("Horizontal"))
			{
				for (int j = x; j < x + tsize; j++)
				{
					if (j >= col)
						throw new RuntimeException(
								"Bad ship data, ship out of bounds");
					else if (grid[y][j] == 1)
						throw new RuntimeException(
								"Bad ship data, you cannot have overlapping ships");
					else
						grid[y][j] = tsize;
				}
			}
			//Uses the previous empty baord and adds the ships to the baord
			else
			{
				for (int j = y; j < y + tsize; j++)
				{
					if (j >= row)
						throw new RuntimeException(
								"Bad ship data, ship out of bounds");
					else if (grid[j][x] == 1)
						throw new RuntimeException(
								"Bad ship data, you cannot have overlapping ships");
					else
						grid[j][x] = tsize;
					
				}
			}
		}
		//returns the grid
		return grid;
	}

}
