package group2.sdp.pc.globalinfo;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Contains various functions all performed on DynamicInfo
 * TODO: move to util, rename to Geometry; move Tests file also
 */
public class DynamicInfoChecker {

	private GlobalInfo globalInfo;

	public DynamicInfoChecker(GlobalInfo globalInfo, DynamicInfo dynamicInfo) {
		this.globalInfo = globalInfo;
//		this.dynamicInfo = dynamicInfo;
	}

	/**
	 * This function finds the smallest angle between a robot and his target.
	 * 
	 * @param isAlfie If the robot we are getting the angle for is Alfie or not
	 * @return The angle to turn at.
	 */
	public int getAngleToBall(Point2D targetPosition, Point2D robotPosition, double facingDirection) {

		double dx = (targetPosition.getX() - robotPosition.getX());
		double dy = (targetPosition.getY() - robotPosition.getY());

		double angle = Math.toDegrees(Math.atan2(dy, dx));

		if (angle < 0) {
			angle = 360 + angle;
		}
		double result = angle - facingDirection;
		// Variables angle and facingDirection are between 0 and 360. Thus result is 
		// between -360 and 360. We need to normalize to -180 and 180. 
		if (result < -180) {
			result += 360;
		} else if (result > 180) {
			result -= 360;
		}
		return (int) result;
	}
	
	/**
	 * returns the angle from a point to another point with repect the plane of are zero angle. 
	 * @param origin 
	 * @param targetPosition position of the target we are working out the angle to the ball
	 * @return double
	 */
	public static double getAngleFromOrigin(Point2D origin, Point2D targetPosition) {
		double dx = (targetPosition.getX() - origin.getX());
		double dy = (targetPosition.getY() - origin.getY());

		double angle = Math.toDegrees(Math.atan2(dy, dx));
		if(angle<0){
			angle = 360 +angle;
		}
		return angle;
	}

	/**
	 * FIXME: Not finished, just moved into this class
	 * checks to see it the robot is with a certain distance of the ball,
	 * if it is then this robot check to see is the robot is facing the ball.
	 * this is done by checking the angle to the ball with the angle facing.
	 * if the angle to the ball is within the threshold its facing it
	 * @param robot is the Dynamic pitch info of robot were checking
	 * @param ball position of the ball
	 * @return boolean
	 */
	public boolean hasBall(DynamicRobotInfo robot, Point2D ball){
		

		int threshold = 35;

		Point2D robotPos = robot.getPosition(); 
		double facing = robot.getFacingDirection();

		//threshold is the give we set in checking if the robot has the ball
		if(robotPos.distance(ball)<=threshold){
			//this is the angle from the origin

			int threshold2 = 10;

			double angle = Math.abs(getAngleToBall(ball, robotPos, facing));

			if(angle<=threshold2){
				//System.out.println("HASBALL is TRUE");
				return true;
			}else{
				//System.out.println("HASBALL is FALSE");
				return false;
			}
		}else{
			//System.out.println("HASBALL is FALSE");
			return false;
		}
	}

	/**
	 * FIXME: restructure
	 * checks to see if the robot is on the right side of the ball, does this by
	 * comparing the distance along x axis of the robot to its goal line and distance of ball to goal line.
	 * if the robot is closer its on the right/correct side so return true 
	 * @param robotInfo
	 * @param ballPos
	 * @return
	 */
	public boolean correctSide(DynamicRobotInfo robotInfo, Point2D ballPos){
		float x = (float) (
				globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
						: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y = globalInfo.getPitch().getTopGoalPostYCoordinate();
		Point2D TopGoal = new Point2D.Float(x, y);
		double goalLine = TopGoal.getX();

		Point2D robotPos = robotInfo.getPosition();
		double robot = robotPos.getX();
		double ball = ballPos.getX();

		double robotDis = Math.abs(goalLine - robot);
		double ballDis = Math.abs(goalLine - ball);

		if(robotDis < ballDis) {
			return true;
		} else{ 
			return false;
		}
	}

	/**
	 * Checks if the robot is in a defensive position. If true it means the robot is closer 
	 * to the robot's goal than the ball and is not facing the robot's goal. Or it is facing 
	 * the robot's goal and is around halfway between the ball and the goal (see threshold)
	 * @param robotInfo
	 * @param ballInfo
	 * @return
	 */
	public boolean inDefensivePosition(DynamicRobotInfo robotInfo, Point2D ball) {
//		float y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();

		double goalX = 		
			globalInfo.isAttackingRight() 
			? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
					: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
					;
			double ballX = ball.getX();
			double robotX = robotInfo.getPosition().getX();
			double betweenBallAndGoalX = (goalX + ballX)/2;

			int threshold = 30;

			if (!correctSide(robotInfo, ball)) {
				return false;
			} else {
				float x = (float) (
						globalInfo.isAttackingRight() 
						? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
								: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
				);
				float y = globalInfo.getPitch().getTopGoalPostYCoordinate();
				Point2D topGoalPost = new Point2D.Float(x, y);
				int angleToGoal = getAngleToBall(topGoalPost, robotInfo.getPosition(), robotInfo.getFacingDirection());
				if (Math.abs(angleToGoal) > 90) {
					return true;
				} else {
					if (Math.abs(robotX - betweenBallAndGoalX) < threshold) {
						return true;
					} else {
						return false;
					}
				}
			}
	}
	/**
	 * Checks if the robot is blocking our path(in the current facing direction). Projects a line from our 
	 * centroid to a box drawn around the opponent and checks for intersection. 
	 * @param alfie
	 * @param opponent
	 * @return is the opponent blocking our path
	 */
	public boolean opponentBlockingPath(DynamicRobotInfo alfie, Point2D obstaclePosition){
		if(alfie.getFacingDirection()==-1){
			System.out.println("angle negative return false");
			return false;
		}
		Point2D alfiePos = alfie.getPosition();
		double x=alfiePos.getX();
		double y=alfiePos.getY();
		double angle=alfie.getFacingDirection();
		double constant;
		double slope;
		//calculating equation of the line from our centroid in facing direction
		if (angle == 90 || angle == 270){
			angle= angle+1;
		}
		slope=Math.tan(Math.toRadians(angle));
		//now work out constant for y=mx+c using c=y-mx
		constant=y-(slope*x);
		
		//increase x or y by 100 to cre			System.out.println("DEFENSIVE");ate arbitrary point for end of line segment(therefore line of minmum length 100. can be tweaked)
		Point2D.Double endP;
		//case increase y
		if (angle>=45 && angle<135){
			double yEnd=alfiePos.getY()+50;
			double xEnd=(yEnd-constant)/slope;
			endP=new Point2D.Double(xEnd, yEnd);
		}
		else if ( (angle >= 0 && angle < 45) || angle >= 315 ){									
			double xEnd = alfiePos.getX() + 50;
			double yEnd = (slope * xEnd) + constant;
			endP=new Point2D.Double(xEnd, yEnd);
		}
		else if (angle >= 135 && angle < 225){
			double xEnd = alfiePos.getX() - 50;
			double yEnd = (slope * xEnd) + constant;
			endP = new Point2D.Double(xEnd, yEnd);
		}else if (angle >= 225 && angle < 315){
			double yEnd = alfiePos.getY() - 50;
			double xEnd = (yEnd-constant) / slope;
			endP = new Point2D.Double(xEnd, yEnd);
		}else{
		
			System.out.print("angle weird return false");
			return false;
		}
		//line created
		Line2D.Double ourLine = new Line2D.Double(alfiePos, endP);
		//now create box around obstacle 21 by 21 (non rotating)
		double topLeftX=obstaclePosition.getX()-12;
		double topLeftY=obstaclePosition.getY()-12;
		Rectangle2D.Double enemyBox = new Rectangle2D.Double(topLeftX, topLeftY, 24, 24);
		//now check if the line intersects the box 
		
		
		if (enemyBox.contains(alfiePos)) {
			return isSimilarAngle(getAngleFromOrigin(alfiePos, obstaclePosition), alfie.getFacingDirection(), 30);
		} else {
			return enemyBox.intersectsLine(ourLine);
		}
		
	}

	/**
	 * Finds the position "behind" the ball. Checks which part of the pitch 
	 * the ball is in an then gets to a position based on that. If the ball 
	 * is near one of the top or bottom walls then it gets to a position so that 
	 * when it turns to face the ball it will be at a 45 degree angle and therefore 
	 * more likely to score if we kick. If we are within the y coordinates of the goal 
	 * then we get behind the ball so that when we face the ball we will face the goal.
	 * @param ballPosition
	 * @return
	 */
	public Point2D getKickingPosition(Point2D ballPosition) {
		float kickingPositionX,kickingPositionY;
		
		// distance to be away from the ball
		int distance = 25;
		// distance to be away from the ball by x and y coordinates.
		double sideDistance = Math.sqrt(2*distance*distance);
		// if the ball is within the y coordinates of the goal then get immediately behind it
		if (globalInfo.getPitch().getTopGoalPostYCoordinate() - 5 > ballPosition.getY() &&
				globalInfo.getPitch().getBottomGoalPostYCoordinate() + 5 < ballPosition.getY()) {
			
			kickingPositionX = (float) (globalInfo.isAttackingRight()
					?  ballPosition.getX() - distance
							: ballPosition.getX() + distance);
					kickingPositionY = (float) (ballPosition.getY());
		// have already checked if ball is near middle of pitch so this check is sufficient
		} else if (ballPosition.getY() > 0) {
			kickingPositionX = (float) (globalInfo.isAttackingRight()
					? ballPosition.getX() - sideDistance
							: ballPosition.getX() + sideDistance);
			kickingPositionY = (float) (ballPosition.getY() + sideDistance);
		} else {
			kickingPositionX = (float) (globalInfo.isAttackingRight()
					? ballPosition.getX() - sideDistance
							: ballPosition.getX() + sideDistance);
			kickingPositionY = (float) (ballPosition.getY() - sideDistance);
		}
		
		// check if position is within bounds
		if ((kickingPositionY > globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxY() - 13) || 
		(kickingPositionY < globalInfo.getPitch().getMinimumEnclosingRectangle().getMinY() + 13)){
			//out of bounds :(
			kickingPositionX = (float) (globalInfo.isAttackingRight()
					?  ballPosition.getX() - distance
							: ballPosition.getX() + distance);
					kickingPositionY = (float) (ballPosition.getY());
		}
		if ((kickingPositionY > globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxY() - 13) || 
				(kickingPositionY < globalInfo.getPitch().getMinimumEnclosingRectangle().getMinY() + 13)){
			kickingPositionX = (float) ballPosition.getX();
			kickingPositionY = (float) ballPosition.getY();
		}
		System.out.println("DEFENSIVE");

		Point2D kickingPosition = new Point.Float(kickingPositionX,kickingPositionY);

		
		return kickingPosition;
		
	}
	
	/**
	 * See http://en.wikipedia.org/wiki/Line-line_intersection for calculation
	 * @param robotPosition
	 * @param ballPosition
	 * @param opponentPosition
	 * @param radius
	 * @return
	 */
	public static Point2D.Double findTangentIntersect(Point2D robotPosition, Point2D ballPosition, Point2D opponentPosition, double radius) {
		
		Point2D.Double alfieDangerZoneIntersection = findCircleTangentIntersect(robotPosition, opponentPosition, radius);
		Point2D.Double ballDangerZoneIntersection = findCircleTangentIntersect(ballPosition, opponentPosition, radius);
		
		// purely for shortness and to fit with wikipedia maths
		double x1 = alfieDangerZoneIntersection.getX();
		double y1 = alfieDangerZoneIntersection.getY();
		double x2 = robotPosition.getX();
		double y2 = robotPosition.getY();
		double x3 = ballPosition.getX();
		double y3 = ballPosition.getY();
		double x4 = ballDangerZoneIntersection.getX();
		double y4 = ballDangerZoneIntersection.getY();
		
		double intersectionX = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / 
					(((x1 - x2)*(y3 - y4)) - ((y1 - y2)*(x3 - x4)));
		
		double intersectionY = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / 
		(((x1 - x2)*(y3 - y4)) - ((y1 - y2)*(x3 - x4)));
		
		return new Point2D.Double(intersectionX, intersectionY);
	}
	/**
	 * See http://paulbourke.net/geometry/2circle/
	 * @param P0 Alfie or ball (outside circle)
	 * @param P1 Opponent (circle center)
	 * @param r1 radius of danger zone
	 * @return
	 */
	public static Point2D.Double findCircleTangentIntersect(Point2D p0, Point2D p1, double r1) {
		
		double d = p1.distance(p0);
		double r0 = Math.sqrt(r1*r1 + d*d);
		double a = ((r0*r0 - r1*r1) + d*d)/(2*d);
		double h = Math.sqrt(r0*r0 - a*a);
		
		//TODO check signs
		double halfWayPointX = p0.getX() + (a*(p1.getX() - p0.getX())/d);
		double halfWayPointY = p0.getY() + (a*(p1.getY() - p0.getY())/d);

		double x1 = halfWayPointX + h*(p1.getY() - p0.getY())/d;
		double y1 = halfWayPointY - h*(p1.getX() - p0.getX())/d;
		
		double x2 = halfWayPointX - h*(p1.getY() - p0.getY())/d;
		double y2 = halfWayPointY + h*(p1.getX() - p0.getX())/d;
		
		Point2D.Double point1 = new Point.Double(x1,y1);
		Point2D.Double point2 = new Point.Double(x2,y2);
		
		if (p1.getY() > 0) {
			if (y1 > y2) {
				return point2;
			} else {
				return point1;
			}
		} else {
			if (y1 > y2) {
				return point1;
			} else {
				return point2;
			}
		}
		
	}
	
	/**
	 * Method for comparison of angles. If angle within certain threshold of each other then
	 * return true else false
	 * @param angle1 first angle for comparison
	 * @param angle2 second angle for comparison
	 * @param threshold max difference for angles to be similar
	 * @return are angles within threshold of each other
	 */
	public static boolean isSimilarAngle(double angle1, double angle2, double threshold){
		double bigAngle;
		double smallAngle;
		if (angle1==angle2){
			return true;
		}
		if (angle1>=angle2){
			bigAngle=angle1;
			smallAngle=angle2;
		}else{
			bigAngle=angle2;
			smallAngle=angle1;
		}
		if(bigAngle-smallAngle<=threshold){
			return true;
		}
		//check to solve 360-0 problem
		if(bigAngle>=(360-threshold) && smallAngle<=0+(threshold-(360-bigAngle))){
			return true;
		}
		return false;
	}
	
	/**
	 * this function will tell us if we are facing the oppositions goal
	 * it compares the angle we are facing with the angle to the extremes
	 * of the goal, it must act dif			System.out.println("DEFENSIVE");ferently for each goal as one of the goals has the zero angle in the middle 
	 * @param robotInfo robot's info
	 * @param opponentInfo opponent info used to get the points of the goal where shooting for
	 * @param ball nuff said
	 * @return boolean
	 */
	public boolean shotOnGoal(DynamicRobotInfo robotInfo, DynamicRobotInfo opponentInfo, Point2D ball){
		float x = (float) (
				!globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
		float y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
		
		
		Point2D topGoal = new Point2D.Float(x, y1);
		Point2D bottomGoal = new Point2D.Float(x, y2);
		Point2D alfiePos = robotInfo.getPosition();
		Point2D enemyPos = opponentInfo.getPosition();
		double facing = robotInfo.getFacingDirection();

		x = (float) (
				globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y = globalInfo.getPitch().getTopGoalPostYCoordinate();

		Point2D ourGoal = new Point2D.Float(x, y);
		double ourGoalLine = ourGoal.getX();
		double theirGoalLine = topGoal.getX();

		double topAngle = getAngleFromOrigin(alfiePos,topGoal);
		double bottomAngle = getAngleFromOrigin(alfiePos, bottomGoal);
		//if other robot is in the way threshold can be changed, current uses 30 degree angle and 30cm distance
		if((alfiePos.distance(enemyPos)<30)&&(isSimilarAngle(getAngleFromOrigin(alfiePos,enemyPos),robotInfo.getFacingDirection(),30))){
			System.out.println("ENEMY CLOSE NO SHOT");
			return false;
		}

		if(theirGoalLine > ourGoalLine) {
			if(facing>bottomAngle || facing<topAngle) {
				return true;
			}else{
				return false;
			}
		} else {
			if (facing<bottomAngle && facing>topAngle) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * This method was created for clarity. It just combines two other methods. 
	 * This should only be used for the opponent robot (unless you decide otherwise)
	 * @param robotInfo
	 * @param ballPosition
	 * @return True if the (opponent) robot has the ball and is on the correct side of the ball 
	 * i.e. they are in an attacking position
	 */
	public boolean isInAttackingPosition(DynamicRobotInfo robotInfo, Point2D ballPosition) {
		boolean condition1 = hasBall(robotInfo, ballPosition);
		boolean condition2 = correctSide(robotInfo, ballPosition);
		
		return condition1 && condition2;
	}
	
	/**
	 * Get the x coordinate of the defensive goal of the robot
	 * @param isAlfie whether or not we want to get the defensive 
	 * 			goal of Alfie
	 * @return x coordinate of the goal we are defending
	 */
	public int getDefensiveGoalOfRobot(boolean isAlfie){
		
		if (isAlfie){
			return globalInfo.isAttackingRight() 
			? (int)globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
			: (int)globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX();
		}
		else{
			return !globalInfo.isAttackingRight() 
			? (int)globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
					: (int)globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX();
		}
	}
}
