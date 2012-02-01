package group2.sdp.robot.commandreciever;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Alfie's Milestone1Brain
 * 
 * This class is Alfie's Milestone1Brain and controls all Alfie's thoughts and actions, methods
 * from this class should be called by the Client (Alfie's ears)
 * 
 * Make sure you run Milestone1Brain.init() first!
 */
public class Milestone1Brain {
	
	// The minimum speed that could ever be received.
	private static final int MIN_SPEED = 0;
	// The maximum speed that could ever be received.
	private static final int MAX_SPEED = 1024;
	
	// The minimum turn speed that could ever be received.
	private static final int MIN_TURN_SPEED = -1024;
	// The maximum turn speed that could ever be received.
	private static final int MAX_TURN_SPEED = 1024;
	
	// The minimum speed that could ever be received.
	private static final int MIN_DISTANCE = 0;
	// The maximum speed that could ever be received.
	private static final int MAX_DISTANCE = 300;
	
	// The minimum speed that could ever be received.
	private static final int MIN_KICK_POWER = 0;
	// The maximum speed that could ever be received.
	private static final int MAX_KICK_POWER = 1024;
	
	// The minimum speed that could ever be received.
	private static final int MIN_ANGLE = -360;
	// The maximum speed that could ever be received.
	private static final int MAX_ANGLE = 360;
	
	
	// Alfie's mouth. String constants to be displayed on the LCD, each line is
	// defined as a different field.
	private static String FWD1 = "Going forward ";
	private static String FWD2 = "with speed:";
	private static String BWD1 = "Going backwards ";
	private static String BWD2 = "with speed:";
	private static String STP = "Stopped.";
	private static String KCK1 = "Kicking with ";
	private static String KCK2 = "power:";
	private static String SPN = "Spinning around...";
	
	// Alfie's physical attributes. Constants for the pilot class,
	// measurements are in centimetres.
	private static final float TRACK_WIDTH = (float) 12.65;
	private static final float WHEEL_DIAMETER = (float) 8.16;
	
	// Alfie's legs and arms. The motors to be controlled.
	private static final NXTRegulatedMotor LEFT_WHEEL = Motor.C;
	private static final NXTRegulatedMotor RIGHT_WHEEL = Motor.A;
	private static final NXTRegulatedMotor KICKER = Motor.B;
	
	// Alfie's finger tips
	private static final TouchSensor LEFT_TOUCH_SENSOR = new TouchSensor(
			SensorPort.S1);
	private static final TouchSensor RIGHT_TOUCH_SENSOR = new TouchSensor(
			SensorPort.S2);

	// The speed to set the kicker motor, determines the power of the kick.
	// private static final int KICKER_SPEED = 10000;
	// The angle of the kicker at the end of the kick.
	private static final int KICKER_ANGLE = 90;
	// The delay before resetting the kicker.
	private static final int KICKER_DELAY = 1000;
	
	// Alfie's actions. Robot state indicators.
	private static volatile boolean kicking = false;
	
	private static boolean initialized = false;
	
	private static DifferentialPilot pilot;
	
	// Indicates whether messages should be output to the LCD or not.
	private static boolean verbose = true;
	
	// If set to true, Alfie will stop when one of his touch sensors fires.
	private static boolean stopOnTouch = false;
	// These flags are true during the period after the corresponding touch 
	// sensor was fired and before a command was issued to Alfie. When Alfie 
	// drools on a candy packet, he returns these flags and sets them to false.
	private static boolean leftTouchFired = false;
	private static boolean rightTouchFired = false;
	// Create a Thread to detect whether touch sensors have been touched
	private static Thread touchSensorsThread;
	
	// Milestone 1 specific constants
	// The time to wait before executing a command.
	private static final int TIMEOUT_BEFORE_EXECUTION = 1800;
	// Defines the speed by which the robot will cross the pitch. 
	private static final int CRUISE_SPEED = 35;

	/**
	 * All methods in this class are static so there is no constructor.
	 * This method should be called before running any other methods in the class.	
	 */
	public static void init () {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL, RIGHT_WHEEL);
		initTouchThread();		
		initialized = true;
	}
	
	/**
	 * Initializes the thread that is watching the touch sensors.
	 */
	private static void initTouchThread() {
		touchSensorsThread = new Thread() {
			
			public void run() {
				while (true) {			
					if (LEFT_TOUCH_SENSOR.isPressed() || 
						RIGHT_TOUCH_SENSOR.isPressed()) {
						if (stopOnTouch) {
							stop();
							backAndRotate();
						}
						if (LEFT_TOUCH_SENSOR.isPressed()) {
							leftTouchFired = true;
						}
						if (RIGHT_TOUCH_SENSOR.isPressed()) {
							rightTouchFired = true;
						}
					}
				}
			}

		};
		//start the touch sensor thread
		touchSensorsThread.start();
	}
	
	/**
	 * Make Alfie go forward.
	 * 
	 * @param speed The speed in cm/s
	 * @param distance The distance that Alfie should cover. If 0, Alfie 
	 * goes until he hits a wall.
	 */
	public static void goForward(int speed, int distance) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);
		distance = SanitizeInput(distance, MIN_DISTANCE, MAX_DISTANCE);
		
		pilot.setTravelSpeed(speed);
		if (distance == 0) {
			pilot.forward();
		} else {
			pilot.travel(distance);
		}
		
		if (verbose) {
			LCD.clear();
			LCD.drawString(FWD1, 0, 0);
			LCD.drawString(FWD2, 0, 1);
			LCD.drawInt(speed, 1, 2);
			LCD.drawString("MAX SPEED", 0, 3);
			LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
			LCD.refresh();
		}
		
		stopOnTouch = true;
	}
	
	/**
	 * Make Alfie go backwards.
	 * 
	 * @param speed The speed in cm/s
	 * @param distance The distance that Alfie should cover. If 0, Alfie 
	 * goes until he hits a wall.
	 */
	public static void goBackwards(int speed, int distance) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);
		
		pilot.setTravelSpeed(speed);
		if (distance == 0) {
			pilot.backward();			
		} else {
			pilot.travel(-distance);
		}
		
		if (verbose) {
			LCD.clear();
			LCD.drawString(BWD1, 0, 0);
			LCD.drawString(BWD2, 0, 1);
			LCD.drawInt(speed, 1, 2);
			LCD.drawString("MAX SPEED", 0, 3);
			LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
			LCD.refresh();
		}
		
		stopOnTouch = false;
	}
	
	/**
	 * Makes Alfie spin around his centre at the given angle and with the given speed.
	 * If the angle is negative, he spins clock wise and otherwise he spins 
	 * counter-clock wise. If the angle is 0 and the speed is negative he spins forever
	 * in the clock wise direction and is the angle is 0 and the speed is positive
	 * he spins forever in the counter-clock wise direction. 
	 * FIXME: the speed argument does not seem to affect the actual speed of the robot.
	 */
	public static void spin(int speed, int angle) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_TURN_SPEED, MAX_TURN_SPEED);
		angle = SanitizeInput(angle, MIN_ANGLE, MAX_ANGLE);
		
		pilot.setTravelSpeed(speed);
		if (angle == 0) {
			if (speed < 0) {
				LEFT_WHEEL.backward();
				RIGHT_WHEEL.forward();
			} else {
				LEFT_WHEEL.forward();
				RIGHT_WHEEL.backward();
			}
		} else {
			pilot.rotate(-angle);
		}
		
		if (verbose) {
			LCD.clear();
			LCD.drawString(SPN, 0, 0);
			LCD.refresh();
		}
		
		stopOnTouch = true;
	}

	/**
	 * Make Alfie stop moving.
	 */
	public static void stop() {
		assert(initialized);
		
		pilot.stop();
		
		if (verbose) {
			LCD.clear();
			LCD.drawString(STP, 0, 0);
			LCD.refresh();
		}
		
		stopOnTouch = false;
	}
	
	/**
	 * Kick the ball!
	 * 
	 * Starts a new thread and makes the robot kick if it isn't already kicking
	 * @param power How hard should the motor rotate in degrees/s.
	 */
	public static void kick(int power) {
		assert(initialized);
		// Start a new thread to control the kicker
		Thread Kick_thread = new Thread() {
	
			public void run() {
				try {
					KICKER.rotate(KICKER_ANGLE, true);
					Thread.sleep(KICKER_DELAY);
								
					KICKER.rotate(-KICKER_ANGLE, true);
					Thread.sleep(KICKER_DELAY);
				} catch (InterruptedException exc) {
					System.out.println(exc.toString());
				}
				
				kicking = false;
			}
			
		};
		
		// Alfie only has 1 leg, so he can only make 1 kick at a time
		if (!kicking) {
			kicking = true;
			power = SanitizeInput(power, MIN_KICK_POWER, MAX_KICK_POWER);
			if (power == 0)
				power = MAX_KICK_POWER;
			KICKER.setSpeed(power);
			Kick_thread.start();

			if (verbose) {
				LCD.clear();
				LCD.drawString(KCK1, 0, 0);
				LCD.drawString(KCK2, 0, 1);
				LCD.drawInt(power, 1, 2);
				LCD.refresh();
			}
		}	
	}

	/**
	 * Puts the given value in reasonable limits.
	 * @param value The value to restrict.
	 * @param min Lower bound.
	 * @param max Upper bound.
	 * @return min if value < min, max if value > max, just value otherwise.
	 */
	private static int SanitizeInput(int value, int min, int max) {
		if (value > max)
			value = max;
		if (value < min)
			value = min;
		return value;
	}
	
	/**
	 * Sets whether the Milestone1Brain should output messages to the screen or not.
	 */
	public static void setVerbose(boolean value) {
		verbose = value;
	}
	
	/**
	 * Returns the left touch flag and resets it to false. 
	 * @return The left touch flag.
	 */
	public static boolean getLeftTouchFlagAndReset() {
		boolean result = leftTouchFired;
		leftTouchFired = false;
		return result;
	}
	
	/**
	 * Returns the right touch flag and resets it to false. 
	 * @return The right touch flag.
	 */
	public static boolean getRightTouchFlagAndReset() {
		boolean result = rightTouchFired;
		rightTouchFired = false;
		return result;
	}
	
	public static void backAndRotate() {
		goBackwards(CRUISE_SPEED, 5);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spin(MAX_TURN_SPEED, 180);
		stop();
	}
}