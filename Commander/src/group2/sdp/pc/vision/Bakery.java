package group2.sdp.pc.vision;


import java.util.LinkedList;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.vision.skeleton.BakerySkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * Implementation of a Bakery. Check the comments in BakerySkeleton. Should
 * compute dynamic information about a pitch. Just implement the abstract 
 * compute methods in the parent. Give an implementor of the DynamicInfoConsumer
 * interface (a Planner object, most probably) as argument when constructing an object.
 * Pass the constructed object as an argument to the constructor of the 
 * ImageProcessor you want to use.  
 */
public class Bakery extends BakerySkeleton {

	public Bakery(DynamicInfoConsumer consumer) {
		super(consumer);
	}
	
	

	@Override
	protected double computeBallRollingSpeed(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		//at beginning when no history speed cannot be calculated
		if (ballHistoryInfos.size()<2){
			return 0;
		}
		int recent= ballHistoryInfos.size()-1;
		double speed=0;
		int numCalcs=0;
		
		//iterates through last (up to)5 positions and finds total of speeds 
		//w.r.t most recent	frame
		for(int i=recent-1; i>recent-5 && i>=0 ; i--){
			//gets distance between points			
			double distance=Math.abs(ballHistoryInfos.get(recent).getPosition().distance(ballHistoryInfos.get(i).getPosition()));
			//gets time difference 
			double time= ballHistoryInfos.get(recent).getTimeStamp()-ballHistoryInfos.get(i).getTimeStamp();
			//1000* converts milliseconds to seconds and calc speed in cm/s
			speed = speed+(1000*distance/time);
			//keeps track of number of points for average
			numCalcs++;			
		}
		//calculate average speed
		speed=speed/numCalcs;
		return speed;
				
	}

	
	@Override
	//remember algorithm calculates angle TO most recent point
	protected double computeBallRollingDirection(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		//at beginning when no history angle cannot be calculated
		if (ballHistoryInfos.size()<2){
			return 0;
		}
		int recent= ballHistoryInfos.size()-1;
		double angle=0;
		int numCalcs=0;
		//iterates through last (up to)5 positions and finds total of angles 
		//to most recent frame
		for(int i=recent-1; i>recent-5 && i>=0 ; i--){
			//gets difference in  y and x
			double yDiff=ballHistoryInfos.get(recent).getPosition().getY()-ballHistoryInfos.get(i).getPosition().getY();
			double xDiff=ballHistoryInfos.get(recent).getPosition().getX()-ballHistoryInfos.get(i).getPosition().getX();
			//calculates angle and adds to total
			//if y is negative mtan2 returns clockwise angle in negative degrees
			if(yDiff>=0){
				angle=angle+Math.toDegrees(Math.atan2(yDiff, xDiff));
			}else{
				angle=angle+360+Math.toDegrees(Math.atan2(yDiff, xDiff));
			}
			//keeps track of number of points for average
			numCalcs++;			
		}
		return angle/numCalcs;
	}

	@Override
	protected double computeRobotTravelSpeed(LinkedList<StaticRobotInfo> historyInfos) {
		//at beginning when no history speed cannot be calculated
		if (historyInfos.size()<2){
			return 0;
		}
		int recent= historyInfos.size()-1;
		double speed=0;
		int numCalcs=0;
		//iterates through last (up to)5 positions and finds total of speeds 
		//w.r.t most recent	frame
		for(int i=recent-1; i>recent-5 && i>=0 ; i--){
			//gets distance between points			
			double distance=Math.abs(historyInfos.get(recent).getPosition().distance(historyInfos.get(i).getPosition()));
			//gets time difference 
			double time=historyInfos.get(recent).getTimeStamp()-historyInfos.get(i).getTimeStamp();
			//1000* converts milliseconds to seconds and calc speed in cm/s
			speed = speed+(1000*distance/time);
			//keeps track of number of points for average
			numCalcs++;			
		}
		//calculate average speed
		speed=speed/numCalcs;
		return speed;
	}

	@Override
	protected double computeRobotTravelDirection(
			LinkedList<StaticRobotInfo> historyInfos) {
		//at begininng when no history angle cannot be calculated
		if (historyInfos.size()<2){
			return 0;
		}
		int recent= historyInfos.size()-1;
		double angle=0;
		int numCalcs=0;
		//iterates through last (up to)5 positions and finds total of angles 
		//to most recent frame
		for(int i=recent-1; i>recent-5 && i>=0 ; i--){
			//gets difference in  y and x			
			double yDiff=historyInfos.get(recent).getPosition().getY()-historyInfos.get(i).getPosition().getY();
			double xDiff=historyInfos.get(recent).getPosition().getX()-historyInfos.get(i).getPosition().getX();
			//calculates angle and adds to total
			//if y is negative mtan2 returns clockwise angle in negative degrees
			if(yDiff>=0){
				angle=angle+Math.toDegrees(Math.atan2(yDiff, xDiff));
			}else{
				angle=angle+360+Math.toDegrees(Math.atan2(yDiff, xDiff));
			}
			//keeps track of number of points for average
			numCalcs++;			
		}
		return angle/numCalcs;
	}
}
