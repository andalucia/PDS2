package group2.sdp.pc.strategy; 

import group2.sdp.pc.objects.pitchInfo;
import group2.sdp.pc.vision.SimpleViewer;

import java.awt.Point;



/*
 * this code is not complete the robot class that is used for Alfi and opponent
 * is not made yet needs to be implemented.
 * 
 * i have yet to correctly implement the function to make the robot move forward and turn etc
 */

public class stratMain extends Thread {

	
	private SimpleViewer viewer;
	public Point ball;
	public robot Alfi;
	public robot opponent;
	float anglePointing = 0;
	
	public boolean anti_clockwise = true;
	public boolean areWeBlue = true;
	
	public Point target = new Point();
	
	
	//to be updated to show the location on the boundaries of the pitch
	public int NorthWall = 0;
	public int southWall = 0;
	public int eastWall = 0;
	public int westWall =0;
	
	
	
	public void getReady(){
		//@todo
		//set up connection with the robot and camera or simulator
		target.x = 0;
		target.y = 0;
		
		getPitchInfo();
		start();
	}
	
	
	public void start(){
		while(true){
			
			getPitchInfo();
			
			//we are closer to the ball than other robo and the ball is not in the corner
			//need to make that if bracket a lot smaller
			if( Math.sqrt( Math.abs(Alfi.getCoors().x - ball.x) + Math.abs(Alfi.getCoors().y - ball.y)) < Math.sqrt(Math.abs(opponent.getCoors().x-ball.x) + Math.abs(opponent.getCoors().x-ball.x)) ||
					(Math.abs(NorthWall - ball.y) > 10) ||
					(Math.abs(southWall - ball.y) > 10) ||
					(Math.abs(eastWall - ball.x)>10) ||
					(Math.abs(westWall - ball.x)>10) ) {
				target = ball;
			}
			
			anglePointing = (float) Alfi.getAngle();
			moveTo(Alfi, target, anglePointing);
			
			
		}
	}
	
	
	
	public void getPitchInfo(){
				  
		pitchInfo PitchInfo = new pitchInfo();
		
		//getObject info has yet to written
		// PitchInfo = viewer.getObjectInfo();
		
		ball.x = PitchInfo.getBallCoors().x;
		ball.y = PitchInfo.getBallCoors().y;
		
		if(areWeBlue){
			Alfi.setRoboInfo(PitchInfo.getBlueBot());
			opponent.setRoboInfo(PitchInfo.getYellowBot());
		}else{
			Alfi.setRoboInfo(PitchInfo.getYellowBot());
			opponent.setRoboInfo(PitchInfo.getBlueBot());
		}
		

		
	}
	
	
	
	public void moveTo(robot Alfi,  Point target, float angleFace ){
		
		
		
		float angleDiff = Math.abs( angleFace - getAngle(Alfi.getCoors(), target) );
		float angleToRotate =0;
	
		if(angleDiff>180){
			angleToRotate = 360 - angleDiff;
			anti_clockwise = false;
		}else{
			angleToRotate = angleDiff;
		}
		
		if(anti_clockwise){
			//send the signal that is equivalent to brain function below
			// Brain.spinToLeft(speed, angleToRotate);
		}else{
			//send the signal that is equivalent to brain function below
			// Brain.spinToRight(speed, angleToRotate);
		
		}
	
		//Alfi should be facing the correct way. send forward signal
		while(!closeToTarget()){
			// Brain.goForward();
		}
		 // Brain.stop();
	}
	
	
	
	
	public boolean closeToTarget(){
		int distance = (int) viewer.calcDistanceBetweenPoints(Alfi.getCoors(), target);
		if(distance> 10){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public float getAngle(Point origin, Point target){
		
		float a = Math.abs(origin.x - target.x);
		float o = Math.abs(origin.y - target.y);
		
		float angle = 0;
		
		float tanT = (o/a);
		
		angle = (float) Math.atan(tanT);
		return angle;
	}
	
	
}