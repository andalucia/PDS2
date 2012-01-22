package sdp2.robot;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * The brain of the robot
 * 
 * This class controls the actions of the robot
 * 
 * @author Chris Howard
 */
public class BrainUnstable {

	// List of commands
	private static final int DO_NOTHING = 0x00;
	private static final int MOVE_FORWARDS = 0x01;
	private static final int STOP = 0x02;
	private static final int KICK = 0x03;
	private static final int ROTATE = 0x04;

	// The motors to be controlled
	private static final NXTRegulatedMotor LEFT_WHEEL = Motor.C;
	private static final NXTRegulatedMotor RIGHT_WHEEL = Motor.A;
	private static final NXTRegulatedMotor KICKER = Motor.B;

	// The sensors
	private static final UltrasonicSensor sonic = new UltrasonicSensor(
			SensorPort.S1);

	// All motors will be set to this speed initially
	private static final int DEFAULT_SPEED = 100;
	private static final int KICKER_SPEED = 10000;

	// Robot state indicators
	private static volatile boolean kicking = false;

	public static void main(String[] args) {

		// The robot won't do anything until it's told to
		int command = KICK;

		LEFT_WHEEL.setSpeed(DEFAULT_SPEED);
		RIGHT_WHEEL.setSpeed(DEFAULT_SPEED);
		KICKER.setSpeed(KICKER_SPEED);

		LCD.drawString("Robot Ready!", 0, 0);

		while (true) {

			// Detect distance, if the robot too close to something (wall), the
			// stop.
			if (ultrasonic_sensor()) {
				command = STOP;
			}

			switch (command) {

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
			case ROTATE:
				rotate(0, 0);
				break;

			}

			// The robot will continue to do it's current command until it's
			// told to
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
	 * Rotate the robot!
	 * 
	 */

	public static void rotate(int Left_Wheel_Speed, int Right_Wheel_Speed) {
		// To rotate the robot by set two wheels' speed
		// Turn to the left, Left_Wheel_Speed > Right_Wheel_Speed
		// Turn to the right, Left_Wheel_Speed < Right_Wheel_Speed
		// this function is not tested, not sure whether speed can be set native
		// value

		LEFT_WHEEL.setSpeed(Left_Wheel_Speed);
		RIGHT_WHEEL.setSpeed(Right_Wheel_Speed);

	}

	/**
	 * Ultrasonic Sensor get distance
	 * 
	 */
	public static boolean ultrasonic_sensor() {

		return sonic.getDistance() < 20;

	}

	/**
	 * Kick the ball!
	 * 
	 * Starts a new thread and makes the robot kick if it isn't already kicking
	 */
	public static void kickIt() {
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

		if (!kicking) {
			kicking = true;
			Kick_thread.start();
		}
	}

}