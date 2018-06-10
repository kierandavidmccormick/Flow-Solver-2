package sample;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by kieranmccormick on 6/9/18.
 */
public class Arbor {
	public HashMap<Integer, FlowBoard> boards;
	public LinkedList<FlowBoard> mostRecentBoards;
	int iterations;
	int boardsCreated;
	
	public Arbor(FlowBoard root){
		boards = new HashMap<>();
		mostRecentBoards = new LinkedList<>();
		root.addAllCertainMoves();
		boards.put(root.hashCode(), root);
		mostRecentBoards.add(root);
		iterations = 0;
		boardsCreated = 0;
	}
	
	public boolean addToBoard(FlowBoard f){
		return boards.putIfAbsent(f.hashCode(), f) == null;
	}
	
	public boolean addStubToBoard(FlowBoard f){
		return boards.putIfAbsent(f.hashCode(), new FlowBoard()) == null;
	}
	
	public FlowBoard genNextBoards(){
		System.out.println("Iteration: " + iterations++ + ", " + "Boards: " + boards.values().size() + ", " + "Most Recent: " + mostRecentBoards.size());
		LinkedList<FlowBoard> newBoards = new LinkedList<>();
		for (FlowBoard f : mostRecentBoards){
			for (FlowBoard fl : (f.getApplicableChildren(null, null))){
				System.out.println("Generated Board: " + boardsCreated++);
				fl.addAllCertainMoves();
				newBoards.add(fl);
			}
		}
		mostRecentBoards.clear();
		for (FlowBoard f : newBoards){
			if (f.isSolved()){
				return f;
			}
			if (addStubToBoard(f)){
				mostRecentBoards.add(f);
			}
		}
		return null;
	}
}
