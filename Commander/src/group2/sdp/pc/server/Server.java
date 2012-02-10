package group2.sdp.pc.server;

import group2.sdp.common.candypacket.CandyPacket;
import group2.sdp.pc.server.skeleton.ServerSkeleton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * Connects to the robot and can send commands to it.
 */
public class Server implements ServerSkeleton {
		
	private String nxtAddress = "btspp://group2";
	
	private NXTConnector conn;
	private DataOutputStream dos;
	private DataInputStream dis;	
	
	private boolean isConnected;

	/**
	 * Default constructor. Initialises the blue-tooth connection.
	 * @throws Exception 
	 */
	public Server() throws Exception {
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

		// Connect to Alfie
		boolean connected = conn.connectTo(nxtAddress);
	
		if (!connected) {
			isConnected = false;
			System.err.println("Failed to connect to Alfie");
			throw new Exception();
		} else {
			isConnected = true;
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
	 * Tells Alfie to stop moving.
	 */
	public void sendStop() {
		sendCandyPacket(new CandyPacket(CandyPacket.STOP_CANDY), true);
	}
	
	/**
	 * Tells Alfie to start moving forward. 
	 * @param speed The speed for the command.
	 * @param distance The distance to travel in an inspecified unit, 0 to travel indefinitely
	 */
	public void sendGoForward(int speed, int distance) {
		sendCandyPacket(new CandyPacket(CandyPacket.GO_FORWARD_CANDY, speed, distance), true);
	}
	
	/**
	 * Tells Alfie to start moving backwards. 
	 * @param speed The speed for the command.
	 */
	public void sendGoBackwards(int speed, int distance) {
		sendCandyPacket(new CandyPacket(CandyPacket.GO_BACKWARDS_CANDY, speed, distance), true);
	}
	
	/**
	 * Tells Alfie to become aggressive.
	 * @param power The power for the kick.
	 */
	public void sendKick(int power) {
		sendCandyPacket(new CandyPacket(CandyPacket.KICK_CANDY, power), true);
	}
	
	/**
	 * Tells Alfie to spin on the spot counter-clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinLeft(int speed, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.SPIN_LEFT_CANDY, speed, angle), true);
	}
	
	/**
	 * Tells Alfie to spin on the spot clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinRight(int speed, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.SPIN_RIGHT_CANDY, speed, angle), true);
	}
	
	/**
	 * Tells Alfie to reset communication.
	 */
	public void sendReset() {
		sendCandyPacket(new CandyPacket(CandyPacket.RESET_CANDY), true);
	}
	
	/**
	 * Tells the Alfie to go to sleep.
	 */
	public void sendExit() {
		sendCandyPacket(new CandyPacket(CandyPacket.SLEEP_CANDY), true);
	}
	
	/**
	 * Sends the given bytes across the opened connection and checks the 
	 * response. 
	 * @param b The bytes to send.
	 * @param verbose If true, the bytes are printed before being sent.
	 */
	private void sendCandyPacket(CandyPacket packet, boolean verbose) {
		//long start = System.currentTimeMillis();
		
		System.out.print("Trying to send packet");
		
		boolean success = false;
		do {
			try {
				// Print output if requested
				if (verbose) {
					packet.printSweets();
				}
	
				byte b [] = packet.getSweets();
				// Send bytes
				dos.write(b, 0, CandyPacket.PACKET_SIZE);
				dos.flush();
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
			
			try {
				// On success Alfie should repeat the command back.
				byte [] b = new byte [CandyPacket.PACKET_SIZE];
				dis.read(b, 0, CandyPacket.PACKET_SIZE);
				success = true;
				if (!packet.contentsEqual(b)) {
					success = false;
					System.out.println("WARNING: command is not the same; RESENDING...");
					continue;
				}
				new CandyPacket(b).printSweets();
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
		} while (!success) ;
		
		//System.out.println(System.currentTimeMillis() - start);
	}
	
	/**
	 * Checks whether a connection to Alfi has been established
	 * @return
	 */
	public boolean alfiConnected() {
		return isConnected;
	}
}
