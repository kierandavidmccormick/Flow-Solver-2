package sample;

import java.util.*;

/**
 * Created by kieranmccormick on 6/1/18.
 */
public class FlowBoard {
	public Node[][] nodes;
	
	public FlowBoard(int... locArgs){
		nodes = new Node[Main.DIM][Main.DIM];
		for(int i = 0; i < Main.DIM; i++){
			for (int j = 0; j < Main.DIM; j++){
				Coordinate c = new Coordinate(i, j);
				int borderedNodes = 4 - c.getEdgeStatus();
				nodes[i][j] = new Node(borderedNodes, false, -1, c);    //initialize all to blank
			}
		}
		for (int i = 0; i < locArgs.length; i+=4){
			Coordinate c1 = new Coordinate(locArgs[i], locArgs[i+1]);
			Coordinate c2 = new Coordinate(locArgs[i+2], locArgs[i+3]);
			Node n1 = new Node(4 - c1.getEdgeStatus(), true, i/4, c1);
			Node n2 = new Node(4 - c1.getEdgeStatus(), true, i/4, c2);
			nodes[c1.x][c1.y] = n1;
			nodes[c2.x][c2.y] = n2;
		}
		connectAll();
		checkAll();
	}
	
	public FlowBoard(FlowBoard f){
		nodes = new Node[Main.DIM][Main.DIM];
		for (int i = 0; i < f.nodes.length; i++){
			for (int j = 0; j < f.nodes[0].length; j++){
				nodes[i][j] = new Node(f.nodes[i][j]);
			}
		}
		for (int i = 0; i < f.nodes.length; i++) {
			for (int j = 0; j < f.nodes[0].length; j++) {
				for (Node n : f.nodes[i][j].actualConnections){
					nodes[i][j].actualConnections.add(nodes[n.location.x][n.location.y]);
				}
				for (Node n : f.nodes[i][j].potentialConnections){
					nodes[i][j].potentialConnections.add(nodes[n.location.x][n.location.y]);
				}
			}
		}
	}
	
	public void connectAll(){
		for (int i = 0; i < Main.DIM; i++){
			for (int j = 0; j < Main.DIM; j++){
				if (i < Main.DIM - 1) {
					nodes[i][j].connect(nodes[i + 1][j]);
				}
				if (j < Main.DIM - 1) {
					nodes[i][j].connect(nodes[i][j + 1]);
				}
			}
		}
	}
	
	public boolean hasUBend(){
		for (int i = 0; i < Main.DIM - 1; i++){
			for (int j = 0; j < Main.DIM - 1; j++){
				if (nodes[i][j].getUbendDirection(this) != null){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean fatalError(){
		return hasUBend();
	}
	
	public void addLBends(){
		while (addLBend()){}
	}
	
	public boolean addLBend(){
		for (Node n : getAllNodes()){
			if (n.addLBend(this)){
				return true;
			}
		}
		return false;
	}
	
	public void checkAll(){
		for (Node n : getAllNodes()){
			n.checkConnections(true);
		}
	}
	
	public Collection<Node> getAllNodes(){
		ArrayList<Node> nodesList = new ArrayList<>(nodes.length * nodes[0].length);
		for (int i = 0; i < Main.DIM; i++) {
			nodesList.addAll(Arrays.asList(nodes[i]).subList(0, Main.DIM));
		}
		return nodesList;
	}
	
	public Collection<FlowBoard> getApplicableChildren(){
		HashSet<FlowBoard> newBoards = new HashSet<>(4);
		boolean oneChild = false;
		for (Node n : getAllNodes()){
			if (n.potentialConnections.size() >= 2) {
				LinkedList<FlowBoard> newBoardsTemp = new LinkedList<>(n.getBoardChildren(this));
				if (newBoardsTemp.size() == 0) {
					return new HashSet<>(0);
				} else if (newBoardsTemp.size() == 1 && !oneChild) {
					newBoards.clear();
					newBoards.addAll(newBoardsTemp);
					oneChild = true;
				} else if (!oneChild) {
					newBoards.addAll(newBoardsTemp);
				}
			}
		}
		return newBoards;
	}
	
	public void addAllCertainMoves(){       //inevitably going to be replaced with proper tree implementation
		LinkedList<FlowBoard> newBoards = new LinkedList<>(getApplicableChildren());
		while (newBoards.size() == 1){
			this.nodes = newBoards.get(0).nodes;        //painful
			newBoards = new LinkedList<>(getApplicableChildren());
		}
	}
}
