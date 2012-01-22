package sdp2.robot;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * The brain of the robot
 * 
 * This class controls the actions of the robot
 * 
 * @author Chris Howard
 */
public class Brain {
	
	// List of commands
	private static final int DO_NOTHING = 0x00;
	private static final int MOVE_FORWARDS = 0x01;
	private static final int STOP = 0x02;
	private static final int KICK = 0x03;
	
	// The motors to be controlled
	private static final NXTRegulatedMotor LEFT_WHEEL = Motor.C;
	private static final NXTRegulatedMotor RIGHT_WHEEL = Motor.A;
	private static final NXTRegulatedMotor KICKER = Motor.B;
	
	// All motors will be set to this speed initially
	private static final int DEFAULT_SPEED = 100;
	private static final int KICKER_SPEED = 10000;
	
	// Robot state indicators
	private static volatile boolean kicking = false;

	
	public static void main(String[] args) {
		
		// The robot won't do anything until it's told to
		int command = KICk;
		
		LEFT_WHEEL.setSpeed(DEFAULT_SPEED);
		RIGHT_WHEEL.setSpeed(DEFAULT_SPEED);
		KICKER.setSpeed(KICKER_SPEED);
		
		LCD.drawString("Robot Ready!", 0, 0);
		
		while(true) {
			switch(command) {
			
			case DO_NOTHING:
				break;
			
			case MOVE_FORWARDS:
				moveForwards();
				break;
				
			case STOP:
				stop();
				break;
			
			case KICK:
				kickIt();
				break;
			}
			
			// The robot will continue to do it's current command until it's told to
			// stop, so setting DO_NOTHING here really means "continue"
			command = DO_NOTHING;
		}
	}
	
	public static void moveForwards() {
		LEFT_WHEEL.forward();
		RIGHT_WHEEL.forward();		
	}
	
	public static void stop() {
		LEFT_WHEEL.stop();
		RIGHT_WHEEL.stop();		
	}
	
	/**
	 * Kick the ball!
	 * 
	 * Starts a new thread and makes the robot kick if it isn't already kicking
	 */
	public static void kickIt() {
		Thread Kick_thread = new Thread() {
			public void run() {
				
				KICKER.rotate(1, true);
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				
				KICKER.rotate(-1, true);
				KICKER.stop();
				
				kicking = false;
			}
		};
		
		if (!kicking) {
			kicking = true;
			Kick_thread.start();
		}	
	}
	
}