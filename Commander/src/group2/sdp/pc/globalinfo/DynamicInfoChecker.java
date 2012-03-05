package group2.sdp.pc.globalinfo;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.*;

/**
 * Contains various functions all performed on DynamicInfo
 *
 */
public class DynamicInfoChecker {

	private DynamicInfo dynamicInfo;
	private DynamicRobotInfo alfieInfo;
	private DynamicRobotInfo opponentInfo;
	private DynamicBallInfo ballInfo;
	private GlobalInfo globalInfo;

	public DynamicInfoChecker(GlobalInfo globalInfo, DynamicInfo dynamicInfo) {
		this.globalInfo = globalInfo;
		this.dynamicInfo = dynamicInfo;
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

		int threshold = 30;

		Point2D robotPos = robot.getPosition(); 
		double facing = robot.getFacingDirection();

		//threshold is the give we set in checking if the robot has the ball
		if(robotPos.distance(ball)<=threshold){
			//this is the angle from the origin

			int threshold2 = 10;

			double angle = Math.abs(getAngleToBall(ball, robotPos, facing));

			if(angle<=threshold2){
				return true;
			}else{
				return false;
			}
		}else{
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
		float y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();

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
}
