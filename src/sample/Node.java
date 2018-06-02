package sample;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by kieranmccormick on 6/1/18.
 */
public class Node {
	ArrayList<Node> actualConnections;
	ArrayList<Node> potentialConnections;
	byte color;
	boolean isEnd;
	Coordinate location;
	
	public Node(int borderedNodes, boolean isEnd, byte color, Coordinate location){
		potentialConnections = new ArrayList<>(borderedNodes);
		if (isEnd){
			actualConnections = new ArrayList<>(1);
		} else {
			actualConnections = new ArrayList<>(2);
		}
		this.color = color;
		this.isEnd = isEnd;
		this.location = location;
	}
	
	public Node(int borderedNodes, boolean isEnd, int color, Coordinate location){
		this(borderedNodes, isEnd, (byte)color, location);
	}
	
	public Node(Node n){
		actualConnections = new ArrayList<>(n.actualConnections);
		potentialConnections = new ArrayList<>(n.potentialConnections);
		color = n.color;
		isEnd = n.isEnd;
		location = new Coordinate(n.location.x, n.location.y);
	}
	
	public void connect(Node n){
		if (validConnection(n)) {
			potentialConnections.add(n);
			n.potentialConnections.add(this);
		}
	}
	
	public boolean validConnection(Node n){         //Checks color
		if (this.color == n.color || this.color == -1 || n.color == -1){
			return true;
		}
		return false;
	}
	
	public void actualizeConnection(Node n, boolean check){
		actualConnections.add(n);
		potentialConnections.remove(n);
		n.actualConnections.add(this);
		n.potentialConnections.remove(this);
		if (check){
			checkConnections(true);
		}
	}
	
	public void disConnect(Node n, boolean check){
		actualConnections.remove(n);
		potentialConnections.remove(n);
		n.actualConnections.remove(this);
		n.potentialConnections.remove(this);
		if (check){
			checkConnections(true);
		}
	}
	
	public void checkConnections(boolean spread){
		LinkedList<Node> modified = new LinkedList<>();
		LinkedList<Node> toActualize = new LinkedList<>();
		if (potentialConnections.size() + actualConnections.size() == (isEnd ? 1 : 2)){
			for (Node n : potentialConnections){
				toActualize.add(n);
				modified.add(n);
			}
		}
		for (Node n : toActualize){
			actualizeConnection(n, false);
		}
		LinkedList<Node> toDisConnect = new LinkedList<>();
		if (actualConnections.size() == (isEnd ? 1 : 2)){
			for (Node n : potentialConnections){
				toDisConnect.add(n);
				modified.add(n);
			}
		}
		for (Node n : toDisConnect){
			disConnect(n, false);
		}
		if (spread){
			for (Node n : modified){
				n.checkConnections(true);
			}
		}
	}
	
	//TODO: checking of connections, addition of actualConnections and subtraction of potentialConnections
}
