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
			n.checkConnections(true);
		}
	}
	
	public void disConnect(Node n, boolean check){
		actualConnections.remove(n);
		potentialConnections.remove(n);
		n.actualConnections.remove(this);
		n.potentialConnections.remove(this);
		if (check){
			checkConnections(true);
			n.checkConnections(true);
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
	
	public boolean addLBend(FlowBoard f){
		Coordinate bendC = hasLBend();
		if (bendC != null){
			Node bendN = f.nodes[bendC.x][bendC.y];
			if (!bendN.isEnd && bendN.actualConnections.size() == 0) {
				Coordinate offset = bendC.sub(location);
				//if (bendN.actualConnections.size() != 0) {
				//	System.err.println("****** ERROR: ILLEGAL CREATION OF L-BEND AT: (" + bendC.toString() + ")");
				//	return false;
				//}
				bendN.actualizeConnection(f.nodes[bendC.x + offset.x][bendC.y], true);
				bendN.actualizeConnection(f.nodes[bendC.x][bendC.y + offset.y], true);
				int i = 0;
				return true;
			}
		}
		return false;
	}
	
	public Coordinate hasLBend(){       //returns coordinate of created L-bend as a result of this node and its connections, otherwise returns null
		if (actualConnections.size() == 2){
			Coordinate c1 = getDirection(actualConnections.get(0));
			Coordinate c2 = getDirection(actualConnections.get(1));
			if (!c1.add(c2).equals(new Coordinate(0,0))){
				return location.add(c1).add(c2);
			}
		}
		return null;
	}
	
	public Coordinate getDirection(Node n){       //returns coordinate c such that this.location.add(c).equals(n.location)
		return n.location.sub(location);
	}
	
	public Coordinate getDirectionIfConnected(Node n){
		if (actualConnections.contains(n)){
			return getDirection(n);
		}
		return null;
	}
	
	public Coordinate getUbendDirection(FlowBoard f){      //returns coordinate of direction of empty part of U-bend, or null if none  EX: u bend facing upwards returns 0,-1 and facing right returns 1,0
		if (location.x < Main.DIM - 1 || location.y < Main.DIM - 1) {
			ArrayList<Coordinate> connections = new ArrayList<>(4);
			Node d1 = f.nodes[location.x][location.y + 1];          //node below this
			Node o1 = f.nodes[location.x + 1][location.y];          //node to the right of this
			Node d1o1 = f.nodes[location.x + 1][location.y + 1];    //node below and to the right of this
			connections.add(getDirectionIfConnected(d1));
			connections.add(getDirectionIfConnected(o1));
			connections.add(o1.getDirectionIfConnected(d1o1));      //get connections between nodes in square below and to the right of this one
			connections.add(d1.getDirectionIfConnected(d1o1));      //exact ordering of nodes is important for direction getting
			int nulls = 0;                                          //number of null values in connections
			for (Coordinate c : connections){
				if (c == null){
					nulls++;
				}
			}
			if (nulls == 1){
				if (connections.get(0) == null){
					return new Coordinate(-1, 0);
				} else if (connections.get(1) == null){
					return new Coordinate(0, -1);
				} else if (connections.get(2) == null){
					return new Coordinate(1, 0);
				} else if (connections.get(3) == null){
					return new Coordinate(0, 1);
				}
			}
		}
		return null;
	}
}
