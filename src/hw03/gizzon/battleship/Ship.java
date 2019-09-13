package hw03.gizzon.battleship;

import java.awt.Color;


/**
* The Ship class will create instances of ships. Ship objects will contain
* the type of ship, which is dependent on their size, their starting x
* and y coordinate, the orientation and the color of their ship. The class
* is created to simplify the process of adding ships to the board.
*
* @author NicoleGizzo
*/
public class Ship {
	private int shipSize;
	private int startX;
	private int startY;
	private String orientation;
	private Color color;
	

	/**
	* The Ship constructor is used to created an instance of the Ship class.
	* The constructor will take all of the necessary parameter in order to create
	* a ship.
	*
	* @param size the size of the ship
	* @param x the starting x coordinate of the ship
	* @param y the starting y coordinate of the ship
	* @param o the orientation of the ship
	* @param c the color of the ship.
	*/
	public Ship(int size, int x, int y, String o, Color c)
	{
		this.shipSize = size;
		this.startX = x;
		this.startY = y;
		this.orientation = o;
		this.setColor(c);
	}
	

	/**
	* The getShipSize() function will return the size of the ship.
	*
	* @return the size of the ship 
	*/
	public int getShipSize() {
		return shipSize;
	}

	/**
	* The setShipSize() function will change the size of the ship.
	*
	* @param shipSize the size of the ship 
	*/
	public void setShipSize(int shipSize) {
		this.shipSize = shipSize;
	}

	/**
	* The getStartX() function will return the starting x coordinate
	*
	* @return the starting x coordinate
	*/
	public int getStartX() {
		return startX;
	}

	/**
	* The setStartX() function will change the starting x coordinate
	*
	* @param startX the starting x coordinate
	*/
	public void setStartX(int startX) {
		this.startX = startX;
	}

	/**
	* The getStartY() function will return the starting Y coordinate
	*
	* @return the starting Y coordinate
	*/
	public int getStartY() {
		return startY;
	}

	/**
	* The setStartY() function will change the starting Y coordinate
	*
	* @param startY the starting y coordinate
	*/
	public void setStartY(int startY) {
		this.startY = startY;
	}

	/**
	* The getOrientation() function will return the orientation fo the board
	*
	* @return the orientation
	*/
	public String getOrientation() {
		return orientation;
	}

	/**
	* The setOrientation() function will change the orientation fo the board
	*
	* @param orientation the orientation
	*/
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	* The getColor() function will return the color of the ship
	*
	* @return the color of the ship
	*/
	public Color getColor() {
		return color;
	}

	/**
	* The setColor() function will change the color of the ship
	*
	* @param color the color of the ship
	*/
	public void setColor(Color color) {
		this.color = color;
	}

}
