package group2.sdp.robot.commandreciever;

import group2.sdp.common.candypacket.CandyPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Client {
	
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
				byte [] b = new byte [CandyPacket.PACKET_SIZE];
				if (recieveBytes(b)) {
					int rslt = executeCommand(b);
					if (rslt == CandyPacket.RESET_CANDY)
						reset = true;
					if (rslt == CandyPacket.SLEEP_CANDY)
						exit = true;
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
	 * Receives 32 bytes from the blue-tooth connection. 
	 * @param b The array in which to store the bytes.
	 */
	private static boolean recieveBytes(byte [] b) {
		try {
			if (dis.read(b, 0, CandyPacket.PACKET_SIZE) != CandyPacket.PACKET_SIZE)
				// Connection was closed by the server, without telling Alfie!
				return false;
		} catch (IOException e) {
			// Connection was closed by the server, without telling Alfie!
			return false;
		}
		try {
			dos.write(b, 0, CandyPacket.PACKET_SIZE);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
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
		int speed = 0, angle;
		switch (b[0]) {
		case CandyPacket.STOP_CANDY:
			Brain.stop();
			break;
		case CandyPacket.GO_FORWARD_CANDY:
			speed = byte4ToInt(b, 4);
			Brain.goForward(speed);
			break;
		case CandyPacket.GO_BACKWARDS_CANDY:
			speed = byte4ToInt(b, 4);     
			Brain.goBackwards(speed);
			break;
		case CandyPacket.SPIN_CANDY:
			speed = byte4ToInt(b, 4);
			angle = byte4ToInt(b, 8);
			Brain.spin(speed, angle);
			break;
		case CandyPacket.KICK_CANDY:
			int kick = byte4ToInt(b, 4);     
			Brain.kick(kick);
			break;
		case CandyPacket.RESET_CANDY:
			break;
		case CandyPacket.SLEEP_CANDY:
			break;
		}
		return b[0];
	}
}
