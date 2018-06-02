package sample;

/**
 * Created by kieranmccormick on 6/1/18.
 */
public class Coordinate extends Object {
	public byte x;
	public byte y;
	
	public Coordinate(byte x, byte y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(int x, int y) {
		this.x = (byte) x;
		this.y = (byte) y;
	}
	
	int getEdgeStatus(){    //returns 0 if not on edge, 1 if on edge, 2 if on corner
		if ((x == 0 || x == Main.DIM - 1) && (y == 0 || y == Main.DIM - 1)){
			return 2;
		} else if ((x == 0 || x == Main.DIM - 1) || (y == 0 || y == Main.DIM - 1)){
			return 1;
		}
		return 0;
	}
}
