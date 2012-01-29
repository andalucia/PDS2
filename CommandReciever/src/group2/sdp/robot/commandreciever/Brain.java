package group2.sdp.robot.commandreciever;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
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
	
	// The minimum speed that could ever be received.
	private static final int MIN_SPEED = 0;
	// The maximum speed that could ever be received.
	private static final int MAX_SPEED = 1024;
	
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
	
	// sensor flag
	private static boolean Sensor_Switch = false;

	// Create a Thread to detect whether touch sensors have been touched
	private static Thread StopByTouch = new Thread() {

		public void run() {
			while (true) {				
					if ((LEFT_TOUCH_SENSOR.isPressed() || RIGHT_TOUCH_SENSOR
							.isPressed()) && Sensor_Switch) {
						stop();
				}
			}
		}

	};

	/**
	 * All methods in this class are static so there is no constructor.
	 * This method should be called before running any other methods in the class.	
	 */
	public static void init () {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL, RIGHT_WHEEL);
		initialized = true;
		//start the touch sensor thread
		StopByTouch.start();
	}
	
	/**
	 * Make Alfie go forward.
	 * 
	 * @param speed The speed in cm/s
	 */
	public static void goForward(int speed) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);
		
		pilot.setTravelSpeed(speed);
		pilot.forward();
		
		LCD.clear();
		LCD.drawString(FWD1, 0, 0);
		LCD.drawString(FWD2, 0, 1);
		LCD.drawInt(speed, 1, 2);
		LCD.drawString("MAX SPEED", 0, 3);
		LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
		LCD.refresh();
		
		Sensor_Switch = true;
	}
	

	/**
	 * Make Alfie go backwards.
	 * 
	 * @param speed The speed in cm/s
	 */
	public static void goBackwards(int speed) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);
		
		pilot.setTravelSpeed(speed);
		pilot.backward();
		
		LCD.clear();
		LCD.drawString(BWD1, 0, 0);
		LCD.drawString(BWD2, 0, 1);
		LCD.drawInt(speed, 1, 2);
		LCD.drawString("MAX SPEED", 0, 3);
		LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
		LCD.refresh();
		
		Sensor_Switch = false;
	}
	
	/**
	 * Makes Alfie spin around his centre at the given angle and with the given speed.
	 * FIXME: the speed argument does not seem to affect the actual speed of the robot.
	 */
	public static void spin(int speed, int angle) {
		assert(initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);
		angle = SanitizeInput(angle, MIN_ANGLE, MAX_ANGLE);
		
		pilot.setTravelSpeed(speed);
		pilot.rotate(angle);
		
		LCD.clear();
		LCD.drawString(SPN, 0, 0);
		LCD.refresh();
		
		Sensor_Switch = true;
	}

	/**
	 * spin for control 
	 */
	
	public static void spinToLeft(int speed) {
		assert (initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);

		pilot.setTravelSpeed(speed);
		LEFT_WHEEL.forward();
		RIGHT_WHEEL.backward();

		LCD.clear();
		LCD.drawString(SPN, 0, 0);
		LCD.refresh();	
		Sensor_Switch = true;
	}
	
	public static void spinToRight(int speed) {
		assert (initialized);
		speed = SanitizeInput(speed, MIN_SPEED, MAX_SPEED);

		pilot.setTravelSpeed(speed);
		RIGHT_WHEEL.forward();
		LEFT_WHEEL.backward();

		LCD.clear();
		LCD.drawString(SPN, 0, 0);
		LCD.refresh();
		Sensor_Switch = true;
	}

	/**
	 * Make Alfie stop moving.
	 */
	public static void stop() {
		assert(initialized);
		
		pilot.stop();
		
		LCD.clear();
		LCD.drawString(STP, 0, 0);
		LCD.refresh();
		
		Sensor_Switch = false;
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
			
			LCD.clear();
			LCD.drawString(KCK1, 0, 0);
			LCD.drawString(KCK2, 0, 1);
			LCD.drawInt(power, 1, 2);
			LCD.refresh();
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
}
