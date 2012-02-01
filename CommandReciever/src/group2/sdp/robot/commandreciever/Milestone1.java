package group2.sdp.robot.commandreciever;

import lejos.nxt.LCD;
import lejos.util.TextMenu;

/**
 * This class presents a menu for selecting which of the Milestone1 tasks should 
 * be executed (Cross Pitch or Kick).
 */
public class Milestone1 {
	
	// The time to wait before executing a command.
	private static final int TIMEOUT_BEFORE_EXECUTION = 1800;
	// Defines the speed by which the robot will cross the pitch. 
	private static final int CRUISE_SPEED = 35;
    private static final String [] menuItems = {"Cross Pitch", "Travel 2m", "Kick", "Exit"};
	
	public static void main (String [] args) {
		// Initiate BRAIN!
        Brain.init();
        Brain.setVerbose(false);
        
        TextMenu menu = new TextMenu(menuItems);
        
        for (;;) {
        	LCD.clear();
        	int selection = menu.select();
        	switch (selection) {
        	case 0:
        		crossPitch();
        		break;
        	case 1:
        		travel2m();
        		break;
        	case 2:
        		kick();
        		break;
    		default:
    			LCD.clear();
				LCD.drawString("Finished",3,4);
				LCD.refresh();
    			return;
        	}
        }
	}
	
	/**
	 * Waits for {@link #TIMEOUT_BEFORE_EXECUTION} ms and performs a kick 
	 * as required for milestone 1.
	 */
	private static void kick() {
		try {
			LCD.clear();
			LCD.drawString("Waiting for " + TIMEOUT_BEFORE_EXECUTION + "ms", 0, 0);
			LCD.refresh();
			Thread.sleep(TIMEOUT_BEFORE_EXECUTION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LCD.clear();
		LCD.drawString("Kick!", 0, 0);
		LCD.refresh();
		
		Brain.kick(0);
	}

	/**
	 * Waits for {@link #TIMEOUT_BEFORE_EXECUTION} ms and travels 2 meters.
	 */
	private static void travel2m() {
		try {
			LCD.clear();
			LCD.drawString("Waiting for " + TIMEOUT_BEFORE_EXECUTION + "ms", 0, 0);
			LCD.refresh();
			Thread.sleep(TIMEOUT_BEFORE_EXECUTION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LCD.clear();  
		LCD.drawString("Travelling 2m", 0, 0);
		LCD.refresh();
		
		Brain.goForward(CRUISE_SPEED, 200);
	}
	
	/**
	 * Waits for {@link #TIMEOUT_BEFORE_EXECUTION} ms and crosses the pitch 
	 * as required for milestone 1.
	 */
	private static void crossPitch() {
		try {
			LCD.clear();
			LCD.drawString("Waiting for " + TIMEOUT_BEFORE_EXECUTION + "ms", 0, 0);
			LCD.refresh();
			Thread.sleep(TIMEOUT_BEFORE_EXECUTION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LCD.clear();
		LCD.drawString("Crossing the pitch...", 0, 0);
		LCD.refresh();
		
		Brain.goForward(CRUISE_SPEED, 0);
	}
}
