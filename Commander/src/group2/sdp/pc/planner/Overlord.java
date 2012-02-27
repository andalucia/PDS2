package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.strategy.Strategy;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * The skeleton of an Overlord class. Depending on the DynamicPitchInfo, 
 * decides on the high-level strategy to employ: Offensive, Defensive, Penalty
 * Defend, or Penalty Take. 
 */
public class Overlord implements DynamicInfoConsumer {
	
	/**
	 * Indicates if the overlord is running or not.
	 */
	protected boolean running = false;
	
	/**
	 * The FieldMarshal that will be executing the strategies that the overlord
	 * comes up with.
	 */
	protected FieldMarshal fieldMarshal;
	/**
	 * The current strategy that is being executed.
	 */
	protected Strategy currentStrategy;
	
	public Overlord(FieldMarshal fieldMarshal) {
		this.fieldMarshal = fieldMarshal;
	}
	
	/**
	 * When this method is invoked the Overlord starts computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void start() {
		running = true;
	}
	/**
	 * When this method is invoked the Overlord stops computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * When running, computes the strategy that should be employed depending 
	 * on the current pitch status and passes the information to the 
	 * FieldMarshal.
	 */
	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		if (running) {
			Strategy strategy = computeStrategy(dpi);
			if (strategy != currentStrategy) {
				fieldMarshal.setStrategy(strategy);
			}
			fieldMarshal.consumeInfo(dpi);
		}
	}

	/**
	 * Most important method of the class. Computes the high-level strategy 
	 * that should be employed, depending on the current dynamic pitch 
	 * information.
	 * @param dpi The DynamicPitchInfo to use when deciding what strategy 
	 * should be employed.
	 * @return The strategy that should be currently employed.
	 */
	protected Strategy computeStrategy(DynamicPitchInfo dpi) {
		DynamicRobotInfo alfieInfo = dpi.getAlfieInfo();
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		
		Point2D ball = ballInfo.getPosition();  
		
		if(hasBall(opponentInfo, ball) && correctSide(opponentInfo, ball)){
			return Strategy.DEFENSIVE;
		}else{
			return Strategy.OFFENSIVE;
		}
	}
	
	
	/**
	 * 
	 * just realised angle checking could have been done more elegantly
	 * using a similar function to our angle checking from pathFinder
	 * 
	 * checks to see it the robot is with a certain distance of the ball,
	 * if it is then this robot check to see is the robot is facing the ball.
	 * this is done by checking the angle to the ball with the angle facing.
	 * there is a problem with how the angle resets around the 360 mark so
	 * any angles close to this must be handled with a different check
	 * @param robot is the Dynamic pitch info of robot were checking
	 * @param ball postion of the ball
	 * @return boolean
	 */
	public static boolean hasBall(DynamicRobotInfo robot, Point2D ball){
		
		
		Point2D robotPos = robot.getPosition(); 
		double facing = robot.getFacingDirection();
		double maxAngle = 0;
		double minAngle = 0;
		
		//threshold is the give we set in checking if the robot has the ball
		int threshold = 5;
		
		//this boolean tells us if the ball is at an extreme angle (close to 360 or 0)
		boolean isExtreme = true;
		
		if(robotPos.distance(ball)<=40){
			//this is the angle from the origin
			double angleBall = FieldMarshal.getAngleFromOrigin(robotPos, ball);
			
			
			//is the angle to the ball small
			if(angleBall<threshold){
				 maxAngle = angleBall+threshold;
				 minAngle = 360 -(threshold-angleBall);
			}else{
				//is the angle to the ball large
				if(angleBall>=(360 - threshold)){
					maxAngle = threshold -(360 - angleBall);
					minAngle = angleBall - threshold;
				}else{
					//all other angles
					maxAngle = angleBall+threshold;
					minAngle = angleBall - threshold;
					isExtreme = false;
				}
			}
			
			
			if(isExtreme){
				if(facing <= maxAngle||facing>=minAngle){
					return true;
				}else{
					return false;
				}
			}else{
				if(facing <= maxAngle&&facing>=minAngle){
					return true;
				}else{
					return false;
				}
			}
		}else{
				return false;
			}
	}
	
	
	/**
	 * checks to see if the robot is on the right side of the ball, does this by
	 * comparing the distance along x axis of the robot to its goal line and distance of ball to goal line.
	 * if the robot is closer its on the right/correct side so return true 
	 * @param robotInfo
	 * @param ballPos
	 * @return
	 */
	public boolean correctSide(DynamicRobotInfo robotInfo, Point2D ballPos){
		Point2D TopGoal = robotInfo.getTopGoalPost();
		double goalLine = TopGoal.getX();
		
		Point2D robotPos = robotInfo.getPosition();
		double robot = robotPos.getX();
		double ball = ballPos.getX();
		
		double robotDis = Math.abs(goalLine - robot);
		double ballDis = Math.abs(goalLine - ball);
		
		if(robotDis < ballDis ){
			return true;
		}else{
			return false;
		}
	}
	
	
}
