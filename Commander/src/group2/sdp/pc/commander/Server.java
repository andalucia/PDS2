package group2.sdp.pc.commander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * Connects to the robot and can send commands to it.
 * Op codes:
 * 0 - ?
 * 1 - go forward,
 * 2 - stop,
 * 3 - kick,
 * 126 - reset,
 * 127 - terminate.
 */
public class Server {
	
	private String nxtAddress = "btspp://group2";
	
	private NXTConnector conn;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	/**
	 * Default constructor. Initialises the blue-tooth connection and adds a 
	 * log listener.
	 */
	public Server () {
		conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener() {

			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: " + message);				
			}

			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();
			}
		} 
		);
		// Connect to our NXT
		boolean connected = conn.connectTo(nxtAddress);
	
		if (!connected) {
			System.err.println("Failed to connect to the NXT brick");
			System.exit(1);
		}
		
		dos = conn.getDataOut();
		dis = conn.getDataIn();
	}
	
	/**
	 * Called when the object is garbage-collected. Closes the connections.
	 */
	@Override
	protected void finalize() throws Throwable {		
		try {
			dis.close();
			dos.close();
			conn.close();
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
		super.finalize();
	}

	/**
	 * Converts an integer to four bytes.
	 * @param arg The integer to convert.
	 * @return A byte array consisting of four bytes.
	 */
	public byte[] intToByte4(int arg) {
		byte [] result = new byte [4];
		result[0] = (byte)(arg >> 24);
		result[1] = (byte)((arg >> 16) & 255);
		result[2] = (byte)((arg >> 8) & 255);
		result[3] = (byte)(arg & 255);
		return result;
	}
	
	/**
	 * Sends a 'go forward' command to the NXT. 
	 * @param speed The speed for the command.
	 */
	public void sendGoForward(int speed) {
		byte op = 1;
		byte [] speed_b = intToByte4(speed);
		byte [] b = {op, 0, 0, 0, speed_b[0], speed_b[1], speed_b[2], speed_b[3], 0, 0, 0, 0, 0, 0, 0, 0,
					 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		sendBytes(b, true);
	}
	
	/**
	 * Sends a 'stop' command to the NXT.  
	 */
	public void sendStop() {
		byte op = 2;
		byte [] b = {op, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		sendBytes(b, true);
	}
	
	/**
	 * Sends a 'kick' command to the NXT. 
	 * @param power The power for the kick.
	 */
	public void sendKick(int power) {
		byte op = 3;
		byte [] power_b = intToByte4(power);
		byte [] b = {op, 0, 0, 0, power_b[0], power_b[1], power_b[2], power_b[3], 0, 0, 0, 0, 0, 0, 0, 0,
					 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		sendBytes(b, true);
	}
	
	/**
	 * Tells the robot to reset communication.
	 */
	public void sendReset() {
		byte op = 126;
		byte [] b = {op, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		sendBytes(b, true);
	}
	
	/**
	 * Tells the robot to stop execution.
	 */
	public void sendExit() {
		byte op = 127;
		byte [] b = {op, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		sendBytes(b, true);
	}
	
	/**
	 * Sends the given bytes across the opened connection and checks the 
	 * response. 
	 * @param b The bytes to send.
	 * @param verbose If true, the bytes are printed before being sent.
	 */
	private void sendBytes(byte [] b, boolean verbose) {
		//long start = System.currentTimeMillis();
		
		boolean success = false;
		do {
			try {
				// Print output if requested
				if (verbose) {
					System.out.print("Sending bytes:");
					for (int i = 0; i < b.length; ++i) {
						System.out.print(" " + b[i]);
					}
					System.out.println();
				}
	
				// Send bytes
				dos.write(b, 0, b.length);
				dos.flush();
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
			
			try {
				// On success the NXT should return the command back.
				byte [] b2 = new byte [b.length];
				dis.read(b2, 0, b.length);
				success = true;
				for (int i = 0; i < b.length; ++i) {
					if (b[i] != b2[i]) {
						success = false;
						break;
					}
				}
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
		} while (!success) ;
		
		//System.out.println(System.currentTimeMillis() - start);
	}
}
