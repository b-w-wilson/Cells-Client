package me.cells.world;

/**
 * Individual tile storage. Keeps things neat and tidy!
 * @author bruce
 *
 */
public class CellTile {
	//public ArrayList<Cell> cellsOnTile = new ArrayList<Cell>();
	public int tileX;
	public int tileY;
	public float tileDifficulty;
	public int tileColour;
	public CellTile(int tileX, int tileY, float diff, int colour) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileDifficulty = diff;
		this.tileColour = colour;
	}

}