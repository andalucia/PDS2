package group2.sdp.pc.planner.algorithms;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class AStar {
	
	protected GlobalInfo globalInfo;
	protected DynamicInfo dpi;
	protected Point destination;
	
	public AStar(GlobalInfo globalInfo, DynamicInfo dpi, Point destination){
		this.globalInfo = globalInfo;
		this.dpi = dpi;
		this.destination = destination;
		ArrayList<Rectangle2D> grid = createGrid();
	}
	
	private final int numberSquaresWidth = 50;
	private final int numberSquaresHeight = 25;
	private int[] indexRobotPosition = new int[2];
	private int[] indexDestinationPosition = new int[2];
	
	private ArrayList<Rectangle2D> createGrid(){
		ArrayList<Rectangle2D> rectangles = new ArrayList<Rectangle2D>();
		Point topLeft = new Point();
		Point bottomRight = new Point();
		topLeft.x = (int)Math.ceil(globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX());
		topLeft.y = (int)Math.floor(globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxY());
		bottomRight.x = (int) Math.floor(globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX());
		bottomRight.y = (int) Math.ceil(globalInfo.getPitch().getMinimumEnclosingRectangle().getMinY()); 
		double numbeSquaresWidth = (globalInfo.getPitch().getMinimumEnclosingRectangle().getWidth())/numberSquaresWidth;
		double numbeSquaresHeight = (globalInfo.getPitch().getMinimumEnclosingRectangle().getHeight())/numberSquaresHeight;

		for (int  i = topLeft.x; i < bottomRight.x; i += numbeSquaresWidth){
			for (int j = topLeft.y; j < bottomRight.y; j += numbeSquaresHeight){
				Rectangle2D rectangle = new Rectangle2D.Double(i, j, numbeSquaresWidth, numbeSquaresHeight);
				rectangles.add(rectangle);
				if (rectangle.contains(dpi.getAlfieInfo().getPosition())){
					indexRobotPosition[0] = i;
					indexRobotPosition[1] = j;
					}
				if (rectangle.contains(destination)){
					indexDestinationPosition[0] = i;
					indexDestinationPosition[1] = j;
					}
				}
			}
		return rectangles;
	} 


	
	public Rectangle2D AStarAlgorithm(Point goal){
	
	
		
    ArrayList<Rectangle2D> closedSet = new ArrayList<Rectangle2D>();     // The set of nodes already evaluated.
    ArrayList<int[]> openSet = new ArrayList<int[]>();
    openSet.add(indexRobotPosition);
    
//    // The set of tentative nodes to be evaluated, initially containing the start node
//    came_from := the empty map    // The map of navigated nodes.
//
//    g_score[start] := 0    // Cost from start along best known path.
//    h_score[start] := heuristic_cost_estimate(start, goal)
//    f_score[start] := g_score[start] + h_score[start]    // Estimated total cost from start to goal through y.
//
//    while openset is not empty
//        current := the node in openset having the lowest f_score[] value
//        if current = goal
//            return reconstruct_path(came_from, came_from[goal])
//
//        remove current from openset
//        add current to closedset
//        foreach neighbor in neighbor_nodes(current)
//            if<Intege neighbor in closedset
//                continue
//            tentative_g_score := g_score[current] + dist_between(current,neighbor)
//
//            if neighbor not in openset
//                add neighbor to openset
//                h_score[neighbor] := heuristic_cost_estimate(neighbor, goal)
//                tentative_is_better := true
//            else if tentative_g_score < g_score[neighbor]
//                tentative_is_better := true
//            else
//                tentative_is_better := false
//
//            if tentative_is_better = true
//                came_from[neighbor] := current
//                g_score[neighbor] := tentative_g_score
//                f_score[neighbor] := g_score[neighbor] + h_score[neighbor]
//
//    return failure
//
//function reconstruct_path(came_from, current_node)
//    if came_from[current_node] is set
//        p := reconstruct_path(came_from, came_from[current_node])
//        return (p + current_node)
//    else
//        return current_node
//	
    return null;
	}
}
