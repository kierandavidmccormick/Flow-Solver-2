package sample;

import javafx.scene.shape.Line;

/**
 * Created by kieranmccormick on 6/1/18.
 */
public class LineGroup {
	Coordinate homeCoordinate;
	Line vert;
	Line horiz;
	
	public LineGroup(Coordinate homeCoordinate, Line vert, Line horiz){
		this.homeCoordinate = homeCoordinate;
		this.vert = vert;
		this.horiz = horiz;
	}
}
