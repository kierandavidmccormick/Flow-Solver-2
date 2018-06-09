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
	
	public LinkedList<Coordinate> getConnectedCoordinates(Coordinate start){        //assumes start is empty
		LinkedList<Node> unconnectedNodes = new LinkedList<>();
		LinkedList<Node> edgeNodes = new LinkedList<>();
		LinkedList<Coordinate> connectedCoordinates = new LinkedList<>();
		edgeNodes.add(nodes[start.x][start.y]);
		while (edgeNodes.size() > 0){
			Node node = edgeNodes.get(0);
			edgeNodes.remove(node);
			connectedCoordinates.add(node.location);
			if (node.color == -1) {
				//edgeNodes.addAll(node.actualConnections);
				for (Node n : node.actualConnections){
					if (!unconnectedNodes.contains(n)){
						edgeNodes.add(n);
						unconnectedNodes.add(n);
					}
				}
				//edgeNodes.addAll(node.potentialConnections);
				for (Node n : node.potentialConnections){
					if (!unconnectedNodes.contains(n)){
						edgeNodes.add(n);
						unconnectedNodes.add(n);
					}
				}
			}
		}
		return connectedCoordinates;
	}
	
	public boolean connectionsValid(){
		//each workingNode connected to its pair, and no unconnected nodes in an area without at least one full pair of connected coordinates
		ArrayList<Node[]> workingNodes = getWorkingNodes();
		ArrayList<ArrayList<Node>> connectedAreas = getConnectedAreas();
		ArrayList<Boolean> areasHavePairs = new ArrayList<>(connectedAreas.size());
		for (Node[] ar : workingNodes){
			boolean workingNodeConnected = false;
			for (ArrayList<Node> nodeAr : connectedAreas){
				if ((nodeAr.contains(ar[0]) && nodeAr.contains(ar[1]))){
					workingNodeConnected = true;
				}
			}
			if (!workingNodeConnected){
				return false;
			}
		}
		for (ArrayList<Node> area : connectedAreas){
			boolean hasPair = false;
			for (Node[] ar : workingNodes){
				if (area.contains(ar[1]) && area.contains(ar[0])){
					hasPair = true;
					break;
				}
			}
			areasHavePairs.add(hasPair);
		}
		for (int i = 0; i < connectedAreas.size(); i++){
			if (!areasHavePairs.get(i)){
				for (Node n : connectedAreas.get(i)){
					if (n.color == -1) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int connectionsValid2(){
		//each workingNode connected to its pair, and no unconnected nodes in an area without at least one full pair of connected coordinates
		ArrayList<Node[]> workingNodes = getWorkingNodes();
		ArrayList<ArrayList<Node>> connectedAreas = getConnectedAreas();
		ArrayList<Boolean> areasHavePairs = new ArrayList<>(connectedAreas.size());
		for (Node[] ar : workingNodes){
			boolean workingNodeConnected = false;
			for (ArrayList<Node> nodeAr : connectedAreas){
				if ((nodeAr.contains(ar[0]) && nodeAr.contains(ar[1]))){
					workingNodeConnected = true;
				}
			}
			if (!workingNodeConnected){
				return ar[0].color;
			}
		}
		for (ArrayList<Node> area : connectedAreas){
			boolean hasPair = false;
			for (Node[] ar : workingNodes){
				if (area.contains(ar[1]) && area.contains(ar[0])){
					hasPair = true;
					break;
				}
			}
			areasHavePairs.add(hasPair);
		}
		for (int i = 0; i < connectedAreas.size(); i++){
			if (!areasHavePairs.get(i)){
				for (Node n : connectedAreas.get(i)){
					if (n.color == -1) {
						return -2;
					}
				}
			}
		}
		return -3;
	}
	
	public ArrayList<Node[]> getWorkingNodes(){
		ArrayList<Node[]> workingNodes = new ArrayList<>();
		//LinkedList<Node> nullWorkingNodes = new LinkedList<>();
		for (Node n : getAllNodes()){
			if (((n.isEnd && n.actualConnections.size() == 0) || (!n.isEnd && n.actualConnections.size() == 1))) {
				if (n.color != -1) {
					boolean inArray = false;
					for (Node[] ar : workingNodes) {
						if (ar[0] != null && ar[0].color == n.color) {
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
			}// else {
			//	nullWorkingNodes.add(n);
			//}
		}
		//workingNodes.add((Node[])nullWorkingNodes.toArray());
		for (Node[] nodeAr : workingNodes){
			if (nodeAr[1] == null){
				int i = 0;
			}
		}
		return workingNodes;
	}
	
	public String[] getCrudeVisual(){
		String[] strings = new String[Main.DIM];
		for (int i = 0; i < Main.DIM; i++){
			StringBuilder stringBuilder = new StringBuilder(Main.DIM);
			for (int j = 0; j < Main.DIM; j++){
				if (nodes[j][i].color == -1){
					stringBuilder.append('_');
				} else {
					String n = (nodes[j][i].color < 10 ? ((Integer)(int)nodes[j][i].color).toString() : "e");
					stringBuilder.append(n);
					if (n.length() != 1){
						strings[0] = "!!!!!!!!!! " + i  + " " + j + " " + n;
					}
				}
			}
			strings[i] = stringBuilder.toString();
		}
		return strings;
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
			if (!connectedNodes.contains(unconnectedNodes.get(0))){
				connectedNodes.add(unconnectedNodes.get(0));
			}
			connectedAreas.add(new ArrayList<>(connectedNodes));
			unconnectedNodes.removeAll(connectedNodes);
		}
		return connectedAreas;
	}
	
	public boolean fatalError(){
		return hasUBend() || !connectionsValid();
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
			n.checkConnections();
		}
	}
	public boolean isSolved(){
		return connectionsValid() && getWorkingNodes().equals(new ArrayList<Node[]>());
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
			int i = 0;
		}
	}
	
	public int hashCode(){
		int result = 17;
		result = result * 31 + Arrays.deepHashCode(nodes);
		return result;
	}
	
	public boolean equals(Object o){
		if (o == this){
			return true;
		} else if (o instanceof FlowBoard){
			return o.hashCode() == hashCode();
		}
		return false;
	}
}
