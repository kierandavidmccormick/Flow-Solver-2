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
	
	Coordinate add(Coordinate c){
		return new Coordinate(x + c.x, y + c.y);
	}
	
	Coordinate sub(Coordinate c){
		return new Coordinate(x - c.x, y - c.y);
	}
	
	public boolean equals(Object o) {
		if (o == this){
			return true;
		} else if (!(o instanceof Coordinate)){
			return false;
		}
		return x == ((Coordinate)o).x && y == ((Coordinate)o).y;
	}
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + x;
		result = 31 * result + y;
		return result;
	}
	
	public void print(){
		System.out.println(x + " " + y);
	}
	
	public String toString(){
		return x + " " + y;
	}
	
	public Boolean isInBounds(){
		return x >= 0 && x < Main.DIM && y >= 0 && y < Main.DIM;
	}
	
	public Boolean isEmpty(FlowBoard f){
		return f.nodes[x][y] == null || f.nodes[x][y].color == -1;
	}
	
}
