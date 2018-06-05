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
	
	public LinkedList<Coordinate> getConnectedCoordinates(Coordinate start){
		boolean[][] inValidNodes = new boolean[nodes.length][nodes[0].length];
		LinkedList<Coordinate> edgeCoordinates = new LinkedList<>();
		edgeCoordinates.add(start);
		while(edgeCoordinates.size() > 0){
			Coordinate c = edgeCoordinates.get(0);
			edgeCoordinates.remove(0);
			for (Direction d : Direction.directions) {
				Coordinate c2 = c.add(d.toCoordinate());
				if (c2.isInBounds() && !inValidNodes[c2.x][c2.y]){      //new
					inValidNodes[c2.x][c2.y] = true;
					if (c2.isEmpty(this)){
						edgeCoordinates.add(c2);
					}
				}
			}
			
		}
		LinkedList<Coordinate> connectedCoordinates = new LinkedList<>();
		for (int i = 0; i < inValidNodes.length; i++){
			for (int j = 0; j < inValidNodes[0].length; j++){
				if (inValidNodes[i][j]){
					connectedCoordinates.add(new Coordinate(i, j));
				}
			}
		}
		return connectedCoordinates;
	}
	
	public Boolean connected(Coordinate start, Coordinate end){
		boolean[][] inValidNodes = new boolean[nodes.length][nodes[0].length];
		LinkedList<Coordinate> edgeCoordinates = new LinkedList<>();
		edgeCoordinates.add(start);
		while(edgeCoordinates.size() > 0){
			Coordinate c = edgeCoordinates.get(0);
			edgeCoordinates.remove(0);
			for (Direction d : Direction.directions) {
				Coordinate c2 = c.add(d.toCoordinate());
				if (c2.equals(end)){
					return true;
				} else if (c2.isInBounds() && c2.isEmpty(this) && !inValidNodes[c2.x][c2.y]) {
					edgeCoordinates.add(c2);
					inValidNodes[c2.x][c2.y] = true;
				}
			}
		}
		return false;
	}
	/*      //TODO: adapt this to a lack of flows, workingNode tracking, etc.
	public Boolean allNodesReachable(){
		boolean[][][] inValidNodes = new boolean[nodes.length][nodes[0].length][2];
		LinkedList<Coordinate> connectedCoordinates = new LinkedList<>();
		for (Flow f : flows){
			if (f.workingNodes.size() > 0){
				connectedCoordinates.addAll(getConnectedCoordinates(f.workingNodes.get(0).loc));
				break;
			}
		}
		for (Coordinate coordinate : connectedCoordinates) {
			inValidNodes[coordinate.x][coordinate.y][0] = true;
		}
		connectedCoordinates.clear();
		for (Flow f : flows){
			if (f.workingNodes.size() > 1){
				connectedCoordinates.addAll(getConnectedCoordinates(f.workingNodes.get(1).loc));
				break;
			}
		}
		for (Coordinate coordinate : connectedCoordinates) {
			inValidNodes[coordinate.x][coordinate.y][1] = true;
		}
		LinkedList<Coordinate> unConnectedCoordinates = new LinkedList<>();
		for (int i = 0; i < inValidNodes.length; i++) {
			for (int j = 0; j < inValidNodes[0].length; j++) {
				if (!(inValidNodes[i][j][0] && inValidNodes[i][j][1]) && !nodes[i][j].isSolved) {        //new
					unConnectedCoordinates.add(new Coordinate(i, j));
				}
			}
		}
		for (int i = 0; i < unConnectedCoordinates.size(); i++){
			boolean isConnected = false;
			for (Flow flow : flows){
				if (!flow.isSolved){
					if (connected(flow.workingNodes.get(0).loc, unConnectedCoordinates.get(0)) && connected(flow.workingNodes.get(1).loc, unConnectedCoordinates.get(0))) {
						unConnectedCoordinates.removeAll(getConnectedCoordinates(unConnectedCoordinates.get(0)));
						if (unConnectedCoordinates.size() == 0){
							return true;
						}
						unConnectedCoordinates.remove(0);
						i = 0;
						isConnected = true;
						break;
					}
				}
			}
			if (!isConnected){
				return false;
			}
		}
		return true;
	}
	*/
	
	public ArrayList<Node[]> getWorkingNodes(){
		ArrayList<Node[]> workingNodes = new ArrayList<>();
		for (Node n : getAllNodes()){
			if (((n.isEnd && n.actualConnections.size() == 0) || (!n.isEnd && n.actualConnections.size() == 1)) && n.color != -1){
				boolean inArray = false;
				for (Node[] ar : workingNodes){
					if (ar[0] != null && ar[0].color == n.color){
						ar[1] = n;
						inArray = true;
						break;
					}
				}
				if (!inArray) {
					Node[] no = {n, null};
					workingNodes.add(no);
				}
			}
		}
		return workingNodes;
	}
	
	public ArrayList<ArrayList<Node>> getConnectedAreas(){
		ArrayList<ArrayList<Node>> connectedAreas = new ArrayList<>();
		ArrayList<Node> unconnectedNodes = new ArrayList<>(getAllNodes());
		LinkedList<Node> nodesToRemove = new LinkedList<>();
		for (Node n : unconnectedNodes){
			if (n.color != -1){
				nodesToRemove.add(n);
			}
		}
		unconnectedNodes.removeAll(nodesToRemove);
		while (unconnectedNodes.size() > 0){
			LinkedList<Node> connectedNodes = new LinkedList<>();
			for (Coordinate c : getConnectedCoordinates(unconnectedNodes.get(0).location)){
				connectedNodes.add(nodes[c.x][c.y]);
			}
			connectedAreas.add(new ArrayList<>(connectedNodes));
			unconnectedNodes.removeAll(connectedNodes);
		}
		return connectedAreas;
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
	
	public Collection<FlowBoard> getApplicableChildren(LinkedList<Node> connectionsToDelete1, LinkedList<Node> connectionsToDelete2){
		HashSet<FlowBoard> newBoards = new HashSet<>();
		boolean oneChild = false;
		for (Node n : getAllNodes()){
			if (n.potentialConnections.size() >= 2) {
				LinkedList<Node> connectionsToDeleteTemp = new LinkedList<>();
				LinkedList<FlowBoard> newBoardsTemp = new LinkedList<>(n.getBoardChildren(this, connectionsToDeleteTemp));
				for (Node toDelete : connectionsToDeleteTemp){
					connectionsToDelete1.add(n);
					connectionsToDelete2.add(toDelete);
				}
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
		LinkedList<Node> connectionsToDelete1 = new LinkedList<>();
		LinkedList<Node> connectionsToDelete2 = new LinkedList<>();
		LinkedList<FlowBoard> newBoards = new LinkedList<>(getApplicableChildren(connectionsToDelete1, connectionsToDelete2));
		while (newBoards.size() == 1){
			nodes = newBoards.get(0).nodes;        //painful
			for (int i = 0; i < connectionsToDelete1.size(); i++){
				Node n = connectionsToDelete1.get(i);
				Node m = connectionsToDelete2.get(i);
				nodes[n.location.x][n.location.y].disConnect(nodes[m.location.x][m.location.y], true);      //could cause problems with deletion of connections invalid in original board, but fine in modified board, questionable whether that can exist or not, and if so, what to do about it
			}
			connectionsToDelete1.clear();
			connectionsToDelete2.clear();
			newBoards = new LinkedList<>(getApplicableChildren(connectionsToDelete1, connectionsToDelete2));
		}
	}
}
