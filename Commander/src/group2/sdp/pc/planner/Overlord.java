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
	
	
	public static final int RweClose = 20;
	
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
		
		if((hasBall(opponentInfo, ball) && correctSide(opponentInfo, ball)) || !correctSide(alfieInfo,ball)){
			return Strategy.DEFENSIVE;
		}else{
			//System.out.println("OFFENSIVE");
			return Strategy.OFFENSIVE;
		}
	}
	
	
	/**
	 * 
	 * added elegant version of angle check, awkward version is commented out
	 * 
	 * checks to see it the robot is with a certain distance of the ball,
	 * if it is then this robot check to see is the robot is facing the ball.
	 * this is done by checking the angle to the ball with the angle facing.
	 * if the angle to the ball is within the threshold its facing it
	 * @param robot is the Dynamic pitch info of robot were checking
	 * @param ball position of the ball
	 * @return boolean
	 */
	public static boolean hasBall(DynamicRobotInfo robot, Point2D ball){
		
		
		Point2D robotPos = robot.getPosition(); 
		double facing = robot.getFacingDirection();
		
		//threshold is the give we set in checking if the robot has the ball
		if(robotPos.distance(ball)<=RweClose){
			//this is the angle from the origin

			int threshold = 10;
		
			double angle = Math.abs(PathFinder.getAngleToTarget(ball, robotPos, facing));
		
			if(angle<=threshold){
				return true;
			}else{
				return false;
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
	public static boolean correctSide(DynamicRobotInfo robotInfo, Point2D ballPos){
		Point2D TopGoal = robotInfo.getTopGoalPost();
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
	
	
}
