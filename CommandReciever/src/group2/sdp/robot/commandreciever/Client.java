package group2.sdp.robot.commandreciever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Client {
	
	private static final int PACKET_SIZE = 32;
	
	private static final byte GO_FORWARD = 1;
	private static final byte STOP = 2;
	private static final byte KICK = 3;
	private static final byte EXIT = 127;

	private static BTConnection btc;

	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static void main(String [] args)  throws Exception 
	{
		String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";
        
		LCD.drawString(waiting,0,0);
		LCD.refresh();

        btc = Bluetooth.waitForConnection();
        
		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.refresh();	

		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
		
		boolean exit = false;
		while (!exit) {
			byte [] b = new byte [PACKET_SIZE];
			recieveBytes(b);
			if (executeCommand(b) == -1)
				exit = true;
			//Button.waitForPress();
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
	
	private static void recieveBytes(byte [] b) {
		try {
			dis.read(b, 0, PACKET_SIZE);
			dos.write(b, 0, PACKET_SIZE);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int byte4ToInt(byte [] b, int offset) {
		int result = 0;
		for (int i = 0; i < 4; ++i) {
			result <<= 8;
			result += b[offset + i] < 0 ? (int)b[offset + i] + 256 : (int) b[offset + i];			
		}
		return result;
	}
	
	private static int executeCommand(byte [] b) {
		switch (b[0]) {
		case GO_FORWARD:
			int speed = byte4ToInt(b, 4);     
			Brain.goForward(speed);
			break;
		case STOP:
			Brain.stop();
			break;
		case KICK:
			int kick = byte4ToInt(b, 4);     
			Brain.kick(kick);
			break;
		case EXIT:
			return -1;
		}
		return 0;
	}
}
