package group2.sdp.pc.strategy; 

import group2.sdp.pc.objects.pitchInfo;
import group2.sdp.pc.vision.SimpleViewer;
import group2.sdp.pc.commander.Server;

import java.awt.Point;

/**
 * this code is not complete the robot class that is used for Alfi and opponent
 * is not made yet needs to be implemented.
 * 
 * i have yet to correctly implement the function to make the robot move forward and turn etc
 */
public class stratMain extends Thread {
	
	// Server for controlling Alfi
	private Server alfiServer;
	
	private SimpleViewer viewer;
	public Point ball = new Point();
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
	public int westWall = 0;
	
	// Constants for movement speed
	private static final int MOVEMENT_SPEED = 40;
	
	/**
	 * If no Server object is passed then we initialise a new one here, this can be used to test the
	 * strategy as a standalone program
	 * 
	 * If no connection is made then the program will exit, this could ammended to make multiple 
	 * connection attempts.
	 */
	public stratMain() {
		try {
			alfiServer = new Server();
		} catch (Exception e) {
			System.out.println("Unable to connect to Alfie");
			System.exit(1);
		}	
	}
	
	/**
	 * If a Server object is passed then we simply pass that object to alfiServer, this will
	 * happen if the Strategy is initialised from the GUI
	 * 
	 * @param alfieServer The initialised server object
	 */
	public stratMain(Server alfiServer) {
		this.alfiServer = alfiServer; 
	}	
	
	public void getReady(){
		//@todo
		//set up connection with the camera or simulator
		
		
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
			/*if( Math.sqrt( Math.abs(Alfi.getCoors().x - ball.x) + Math.abs(Alfi.getCoors().y - 
					ball.y)) < Math.sqrt(Math.abs(opponent.getCoors().x-ball.x) + 
					Math.abs(opponent.getCoors().x-ball.x)) ||
					
					(Math.abs(NorthWall - ball.y) > 10) ||
					(Math.abs(southWall - ball.y) > 10) ||
					(Math.abs(eastWall - ball.x)>10) ||
					(Math.abs(westWall - ball.x)>10) ) {
				target = ball;
			}
			
			anglePointing = (float) Alfi.getAngle();
			moveTo(Alfi, target, anglePointing);*/
			
			
		}
	}
	
	
	
	public void getPitchInfo(){
				  
		pitchInfo PitchInfo = new pitchInfo();
		
		//getObject info has yet to written
		// PitchInfo = viewer.getObjectInfo();
		
		ball.setLocation(PitchInfo.getBallCoors().getX(), PitchInfo.getBallCoors().getY());
		
		if(areWeBlue){
			//Alfi.setRoboInfo(PitchInfo.getBlueBot());
			//opponent.setRoboInfo(PitchInfo.getYellowBot());
		}else{
			//Alfi.setRoboInfo(PitchInfo.getYellowBot());
			//opponent.setRoboInfo(PitchInfo.getBlueBot());
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
			// Currently spins at max speed, this could cause accuracy problems
			alfiServer.sendSpinLeft(1024, (int) angleToRotate);
		}else{
			//send the signal that is equivalent to brain function below
			// Currently spins at max speed, this could cause accuracy problems
			alfiServer.sendSpinRight(1024, (int) angleToRotate);		
		}
	
		//Alfi should be facing the correct way. send forward signal
		while(!closeToTarget()){
			// Brain.goForward();
			// Just now Alfi is moving as fast as he can, this probably isn't ideal in the long run
			alfiServer.sendGoForward(MOVEMENT_SPEED, 0);
		}
		
		// Alfi is close to target so stop moving
		alfiServer.sendStop();
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
