package group2.sdp.robot.commandreciever;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class Brain {
	
	private static String fwd1 = "Going forward ";
	private static String fwd2 = "with speed:"; 
	private static String stp = "Stopped";
	private static String kck1 = "Kicking with ";
	private static String kck2 = "power:";
	
	// constants for the pilot class
	private static final float TRACK_WIDTH = (float) 14.5;
	private static final float WHEEL_DIAMETER = (float) 8.4;
	
	// The motors to be controlled
	private static final NXTRegulatedMotor LEFT_WHEEL = Motor.C;
	private static final NXTRegulatedMotor RIGHT_WHEEL = Motor.A;
	private static final NXTRegulatedMotor KICKER = Motor.B;
	
	// All motors will be set to this speed initially
	private static final int KICKER_SPEED = 10000;
	
	// Robot state indicators
	private static volatile boolean kicking = false;
	private static DifferentialPilot pilot;
	
	public static void init () {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL, RIGHT_WHEEL);
		KICKER.setSpeed(KICKER_SPEED);
	}
	
	public static void goForward(int speed) {
		LCD.clear();
		LCD.drawString(fwd1, 0, 0);
		LCD.drawString(fwd2, 0, 1);
		LCD.drawInt(speed, 1, 2);
		LCD.drawString("MAX SPEED", 0, 3);
		LCD.drawInt((int)pilot.getMaxTravelSpeed(), 1, 4);
		LCD.refresh();
		
		
		
		pilot.setTravelSpeed(speed);
		pilot.forward();
	}
	
	
	public static void stop() {
		LCD.clear();
		LCD.drawString(stp, 0, 0);
		LCD.refresh();
		
		pilot.stop();
	}
	
	/**
	 * Kick the ball!
	 * 
	 * Starts a new thread and makes the robot kick if it isn't already kicking
	 */
	public static void kick(int power) {
		LCD.clear();
		LCD.drawString(kck1, 0, 0);
		LCD.drawString(kck2, 0, 1);
		LCD.drawInt(power, 1, 2);
		LCD.refresh();
		
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
