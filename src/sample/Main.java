package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
	
	public static int DIM = 10;
	Group circles = new Group();
	Group lines = new Group();
	Group root = new Group(circles, lines);
	Circle[][] circleArray = new Circle[DIM][DIM];
	LineGroup[][] lineGroupArray = new LineGroup[DIM][DIM];

    @Override
    public void start(Stage primaryStage) throws Exception{
	    for (int i = 0; i < DIM; i++){
		    for (int j = 0; j < DIM; j++){
			    circleArray[i][j] = new Circle(i * 50 + 25, j * 50 + 25, 5, Color.GRAY);
			    Line vertLine = j < DIM - 1 ? new Line(i * 50 + 25, j * 50 + 25, i * 50 + 25, j * 50 + 75) : null;
			    Line horizLine = i < DIM - 1 ? new Line(i * 50 + 25, j * 50 + 25, i * 50 + 75, j * 50 + 25) : null;
		        if (vertLine != null){
			    	vertLine.setFill(Color.LIGHTGREY);
			    	vertLine.setStroke(Color.LIGHTGREY);
			    	vertLine.setStrokeWidth(3);
		        }
		        if (horizLine != null){
		        	horizLine.setFill(Color.LIGHTGREY);
		        	horizLine.setStroke(Color.LIGHTGREY);
		        	horizLine.setStrokeWidth(3);
		        }
		        lineGroupArray[i][j] = new LineGroup(new Coordinate(i, j), vertLine, horizLine);
		    }
	    }
	    Rectangle background = new Rectangle(0, 0, DIM * 50, DIM * 50);
	    background.setFill(Color.BLACK);
	    root.getChildren().add(0, background);
	    circles.toFront();
	    //FlowBoard f = new FlowBoard(0,0,4,0, 0,1,6,0, 2,1,4,2, 6,1,0,4, 2,3,5,5, 0,2,4,5);          //7x7 c: trivial n: trivial
	    FlowBoard f = new FlowBoard(0,0,4,4, 1,1,2,4, 1,2,2,7, 2,1,5,6, 3,1,2,6, 4,7,7,2);       //10x10 c: nontrivial but pretty good n: nontrivial
	    //FlowBoard f = new FlowBoard(0,0,2,6, 2,2,3,1, 1,3,9,6, 3,2,8,4, 2,3,6,3, 1,4,6,8, 5,0,9,0, 3,3,6,4, 1,7,8,5, 1,8,4,9, 5,6,8,8, 0,7,1,9);        //10x10 c: trivial n:
	    // **** ADD TESTS BELOW HERE:
	    //f.addLBends();
	    f.addAllCertainMoves();
	    // **** AND ABOVE HERE
	    setGUIElements(f);
	    createGUIElements(circleArray);
        primaryStage.setScene(new Scene(root, DIM * 50, DIM * 50));
        primaryStage.show();
    }
	
    public static void main(String[] args) {
        launch(args);
    }
    
    public void setGUIElements(FlowBoard f){
    	for (int i = 0; i < DIM; i++){
    		for (int j = 0; j < DIM; j++){
    			if (f.nodes[i][j].color != -1){
    				circleArray[i][j].setFill(ColorSet.colorArray[f.nodes[i][j].color]);
			    }
			    if (f.nodes[i][j].isEnd){
				    circleArray[i][j].setRadius(10);
			    }
			    if (i < DIM - 1 && f.nodes[i][j].actualConnections.contains(f.nodes[i+1][j])){
    			    lineGroupArray[i][j].horiz.setStroke(Color.GREEN);
    			    lineGroupArray[i][j].horiz.setFill(Color.GREEN);
			    } else if (i < DIM - 1 && !f.nodes[i][j].potentialConnections.contains(f.nodes[i+1][j])){
			    	lineGroupArray[i][j].horiz.setVisible(false);
			    }
			    if (j < DIM - 1 && f.nodes[i][j].actualConnections.contains(f.nodes[i][j+1])){
				    lineGroupArray[i][j].vert.setStroke(Color.GREEN);
				    lineGroupArray[i][j].vert.setFill(Color.GREEN);
			    } else if (j < DIM - 1 && !f.nodes[i][j].potentialConnections.contains(f.nodes[i][j+1])){
				    lineGroupArray[i][j].vert.setVisible(false);
			    }
		    }
	    }
    }
    
    public void createGUIElements(Circle[][] circleArray){
	    for (int i = 0; i < DIM; i++) {
		    for (int j = 0; j < DIM; j++) {
			    if (circleArray[i][j] != null) {
				    circles.getChildren().add(circleArray[i][j]);
			    }
			    if (lineGroupArray[i][j].vert != null){
			    	lines.getChildren().add(lineGroupArray[i][j].vert);
			    }
			    if (lineGroupArray[i][j].horiz != null){
			    	lines.getChildren().add(lineGroupArray[i][j].horiz);
			    }
		    }
	    }
    }
}
