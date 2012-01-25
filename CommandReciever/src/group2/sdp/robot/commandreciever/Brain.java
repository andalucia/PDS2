package group2.sdp.robot.commandreciever;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Alfie's brain
 * 
 * This class is Alfie's brain and controls all Alfie's thoughts and actions, methods
 * from this class should be called by the Client (Alfie's ears)
 * 
 * Make sure you run Brain.init() first!
 */
public class Brain {
	
	// Alfie's mouth. String constants to be displayed on the LCD, each line is
	// defined as a different field.
	private static String FWD1 = "Going forward ";
	private static String FWD2 = "with speed:"; 
	private static String STP = "Stopped.";
	private static String KCK1 = "Kicking with ";
	private static String KCK2 = "power:";
	
	// Alfie's physical attributes. Constants for the pilot class,
	// measurements are in centimetres.
	private static final float TRACK_WIDTH = (float) 12.65;
	private static final float WHEEL_DIAMETER = (float) 8.16;
	
	// Alfie's legs and arms. The motors to be controlled.
	private static final NXTRegulatedMotor LEFT_WHEEL = Motor.C;
	private static final NXTRegulatedMotor RIGHT_WHEEL = Motor.A;
	private static final NXTRegulatedMotor KICKER = Motor.B;
	
	// The speed to set the kicker motor, determines the power of the kick.
	private static final int KICKER_SPEED = 10000;
	
	// Alfie's actions. Robot state indicators.
	private static volatile boolean kicking = false;
	private static boolean initialized = false;
	private static DifferentialPilot pilot;
	
	/**
	 * All methods in this class are static so there is no constructor.
	 * This method should be called before running any other methods in the class.	
	 */
	public static void init () {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL, RIGHT_WHEEL);
		KICKER.setSpeed(KICKER_SPEED);
		initialized = true;
	}
	
	/**
	 * Make Alfie go forwards.
	 * 
	 * @param speed The speed in cm/s
	 */
	public static void goForward(int speed) {
		assert(initialized);
		LCD.clear();
		LCD.drawString(FWD1, 0, 0);
		LCD.drawString(FWD2, 0, 1);
		LCD.drawInt(speed, 1, 2);
		LCD.drawString("MAX SPEED", 0, 3);
		LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
		LCD.refresh();			
		
		pilot.setTravelSpeed(speed);
		pilot.forward();
	}
	
	/**
	 * This method is for reference
	 */
	public static void spin() {
		assert(initialized);
		LCD.clear();
		LCD.drawString(FWD1, 0, 0);
		LCD.drawString(FWD2, 0, 1);
		//LCD.drawInt(speed, 1, 2);
		LCD.drawString("MAX SPEED", 0, 3);
		LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
		LCD.refresh();			
		
		pilot.setTravelSpeed(20);
		pilot.arc(100, 90);
		
		/*LEFT_WHEEL.setSpeed(90);
		RIGHT_WHEEL.setSpeed(45);
		LEFT_WHEEL.forward();
		RIGHT_WHEEL.forward();*/
	}
	
	
	
	/**
	 * Make Alfie stop moving
	 */
	public static void stop() {
		assert(initialized);
		LCD.clear();
		LCD.drawString(STP, 0, 0);
		LCD.refresh();
		
		pilot.stop();
	}
	
	/**
	 * Kick the ball!
	 * 
	 * Starts a new thread and makes the robot kick if it isn't already kicking
	 * @param power How hard should the motor rotate in degrees/s
	 */
	public static void kick(int power) {
		assert(initialized);
		// Start a new thread to control the kicker
		Thread Kick_thread = new Thread() {
	
			public void run() {			
				KICKER.rotate(90, true);
				
				try {
					Thread.sleep(1000);			
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
							
				KICKER.rotate(-90, true);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
				
				kicking = false;
			}
			
		};
		
		// Alfie only has 1 leg, so he can only make 1 kick at a time
		if (!kicking) {
			LCD.clear();
			LCD.drawString(KCK1, 0, 0);
			LCD.drawString(KCK2, 0, 1);
			LCD.drawInt(power, 1, 2);
			LCD.refresh();
			
			kicking = true;
			Kick_thread.start();
		}	
	}
}
