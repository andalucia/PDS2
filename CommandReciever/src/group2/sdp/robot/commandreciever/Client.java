package group2.sdp.robot.commandreciever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Client {
	
	private static final int PACKET_SIZE = 32;
	
	private static final byte GO_FORWARD = 1;
	private static final byte STOP = 2;
	private static final byte KICK = 3;
	private static final byte SPIN = 4;
	private static final byte RESET = 126;
	private static final byte EXIT = 127;

	private static BTConnection btc;

	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static void main(String [] args)  throws Exception 
	{
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        
        // Initiate BRAIN!
        Brain.init();
        
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
				byte [] b = new byte [PACKET_SIZE];
				recieveBytes(b);
				int rslt = executeCommand(b);
				if (rslt == RESET)
					reset = true;
				if (rslt == EXIT)
					exit = true;
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
	 * Receives 32 bytes from the blue-tooth connection. 
	 * @param b The array in which to store the bytes.
	 */
	private static void recieveBytes(byte [] b) {
		try {
			dis.read(b, 0, PACKET_SIZE);
			dos.write(b, 0, PACKET_SIZE);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts four bytes of an array of bytes to an integer.
	 * @param b The array of bytes to convert.
	 * @param offset The offset at which to get the four bytes
	 * @return The integer.
	 */
	private static int byte4ToInt(byte [] b, int offset) {
		int result = 0;
		for (int i = 0; i < 4; ++i) {
			result <<= 8;
			result += b[offset + i] < 0 ? (int)b[offset + i] + 256 : (int) b[offset + i];			
		}
		return result;
	}
	
	/**
	 * Deciphers the command that is received and executes it. 
	 * @param b The commands.
	 * @return The first byte of the command.
	 */
	private static int executeCommand(byte [] b) {
		switch (b[0]) {
		case GO_FORWARD:
			int speed = byte4ToInt(b, 4);     
			Brain.goForward(speed);
			break;
		case SPIN:
			Brain.spin();
			break;
		case STOP:
			Brain.stop();
			break;
		case KICK:
			int kick = byte4ToInt(b, 4);     
			Brain.kick(kick);
			break;
		case RESET:
			break;
		case EXIT:
			break;
		}
		return b[0];
	}
}
