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
	public static Main main;
	Group circles = new Group();
	Group lines = new Group();
	Group root = new Group(circles, lines);
	Circle[][] circleArray = new Circle[DIM][DIM];
	LineGroup[][] lineGroupArray = new LineGroup[DIM][DIM];

    @Override
    public void start(Stage primaryStage) throws Exception{
    	Main.main = this;
	    for (int i = 0; i < DIM; i++){
		    for (int j = 0; j < DIM; j++){
			    circleArray[i][j] = new Circle(i * 50 + 25, j * 50 + 25, 7, Color.GRAY);
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
	    //FlowBoard f = new FlowBoard(0,0,4,4, 1,1,2,4, 1,2,2,7, 2,1,5,6, 3,1,2,6, 4,7,7,2);       //10x10 c: nontrivial but pretty good n: trivial
	    FlowBoard f = new FlowBoard(0,0,2,6, 2,2,3,1, 1,3,9,6, 3,2,8,4, 2,3,6,3, 1,4,6,8, 5,0,9,0, 3,3,6,4, 1,7,8,5, 1,8,4,9, 5,6,8,8, 0,7,1,9);        //10x10 c: trivial n: trivial
	    //FlowBoard f = new FlowBoard(0,0,10,0, /*white*/ 1,2,2,11, /*pink*/ 3,1,10,7, /*red*/3,2,12,1, /*blue*/ 4,4,10,1, /*brown*/ 6,3,11,10, /*purple*/ 7,3,11,9, /*orange*/ 12,3,0,9, /*lgreen*/ 2,4,3,7, /*gray*/ 1,6,4,9, /*green*/ 4,8,1,11, /*yellow*/ 7,11,11,11 /*lblue*/);     //14x14 c:nontrivial(hard) n:nontrivial(hard) human:nontrivial(hard)
	    //FlowBoard f = new FlowBoard(7,0,13,0, /*lgreen*/ 7,1,11,12, /*grey*/ 4,2,9,11, /*beige*/ 7,2,0,9, /*yellow*/ 4,3,10,10, /*teal*/ 7,3,8,6, /*blue*/ 9,3,7,8, /*pink*/ 4,4,4,6, /*white*/ 7,4,13,12, /*lblue*/ 9,4,9,7, /*dblue*/ 10,4,1,13, /*red*/ 4,8,7,11, /*green*/ 2,9,1,12, /*purple*/ 3,10,2,13, /*orange*/ 9,12,3,13 /*brown*/);     //14x14 c:nontrivial(hard) n:nontrivial
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
    
	public void setBoard(FlowBoard f){
    	setGUIElements(f);
    	createGUIElements(circleArray);
	}
    
    public void setGUIElements(FlowBoard f){
    	for (int i = 0; i < DIM; i++){
    		for (int j = 0; j < DIM; j++){
    			if (f.nodes[i][j].color != -1){
				    circleArray[i][j].setFill(ColorSet.colorArray[f.nodes[i][j].color]);
			    }
			    if (f.nodes[i][j].isEnd){
				    circleArray[i][j].setRadius(15);
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
