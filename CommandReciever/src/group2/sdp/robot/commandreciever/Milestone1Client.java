package group2.sdp.robot.commandreciever;

import group2.sdp.common.candypacket.CandyPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Milestone1Client {
	
	private static BTConnection btc;

	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static void main(String [] args)  throws Exception 
	{
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        
        // Initiate Milestone1Brain!
        Milestone1Brain.init();
        
        boolean exit = false;
		while (!exit) {
			LCD.drawString(waiting,0,0);
			LCD.refresh();
		
		    btc = Bluetooth.waitForConnection();
		    
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();	
		
			dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();
		
			boolean reset = false; 
			while (!exit && !reset) {
				CandyPacket candy = receiveCandy();
				if (candy != null) {
					consumeCandy(candy);
					if (candy.getBrand() == CandyPacket.RESET_CANDY)
						reset = true;
					if (candy.getBrand() == CandyPacket.SLEEP_CANDY)
						exit = true;
					giveCandyBack(candy);
				} else {
					reset = true;
				}
			}
		
			dis.close();
			dos.close();
			Thread.sleep(100); // wait for data to drain
			LCD.clear();
			LCD.drawString(closing,0,0);
			LCD.refresh();
			btc.close();
			LCD.clear();
		}
	}
	

	/**
	 * Receives a packet full of candy over the blue-tooth connection. 
	 * @return The CandyPacket that was received.
	 */
	private static CandyPacket receiveCandy() {
		CandyPacket candy = null;
		try {
			byte [] b = new byte [CandyPacket.PACKET_SIZE];
			if (dis.read(b, 0, CandyPacket.PACKET_SIZE) != CandyPacket.PACKET_SIZE) {
				// Connection was closed by the server, without telling Alfie!
				return null;
			}
			candy = new CandyPacket(b);
		} catch (IOException e) {
			// Connection was closed by the server, without telling Alfie!
			return null;
		}
		return candy;
	}

	/**
	 * Alfie kindly returns the CandyPacket he was given. Unfortunately, he drooled
	 * on the feedback sweets, so they are modified now.
	 * @param candy The CandyPacket, modified after being drooled on.
	 */
	private static void giveCandyBack(CandyPacket candy) {
		try {
			dos.write(candy.getSweets(), 0, CandyPacket.PACKET_SIZE);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Alfie first reacts on the CandyPacket and then drools on it in order
	 * to modify its feedback sweets.
	 * @param candy The candy that was given to Alfie.
	 */
	private static void consumeCandy(CandyPacket candy) {
		reactOnCandy(candy);
		droolOnCandy(candy);
	}
	
	/**
	 * Makes Alfie react depending on the type of CandyPacket he received.
	 * @param candy The CandyPacket that was given to Alfie over blue-tooth.
	 */
	private static void reactOnCandy(CandyPacket candy) {
		switch (candy.getBrand()) {
		case CandyPacket.STOP_CANDY:
			Milestone1Brain.stop();
			break;
		case CandyPacket.GO_FORWARD_CANDY:
			Milestone1Brain.goForward(candy.getPretzel(0), candy.getPretzel(1));
			break;
		case CandyPacket.GO_BACKWARDS_CANDY:
			Milestone1Brain.goBackwards(candy.getPretzel(0), candy.getPretzel(1));
			break;
		case CandyPacket.SPIN_LEFT_CANDY:
			Milestone1Brain.spin(candy.getPretzel(0), candy.getPretzel(1));
    		break;
		case CandyPacket.SPIN_RIGHT_CANDY:
			int angle = -candy.getPretzel(1);
			if (angle != 0)
				Milestone1Brain.spin(candy.getPretzel(0), angle);
			else
				Milestone1Brain.spin(-candy.getPretzel(0), 0);
			break;
		case CandyPacket.KICK_CANDY:     
			Milestone1Brain.kick(candy.getPretzel(0));
			break;
		case CandyPacket.RESET_CANDY:
			break;
		case CandyPacket.SLEEP_CANDY:
			break;
		}
	}

	/**
	 * Modifies the CandyPacket as a result of drooling over it. The feedback
	 * sweets and pretzels of the CandyPacket are updated. 
	 * @param candy The CandyPacket that was given to Alfie.
	 */
	private static void droolOnCandy(CandyPacket candy) {
		candy.setFeedbackSweet(0, Milestone1Brain.getLeftTouchFlagAndReset() ? (byte)1 : 0);
		candy.setFeedbackSweet(1, Milestone1Brain.getRightTouchFlagAndReset() ? (byte) 1 : 0);
	}
}
