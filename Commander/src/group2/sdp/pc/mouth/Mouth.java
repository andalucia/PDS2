package group2.sdp.pc.mouth;

import group2.sdp.common.candypacket.CandyPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * Connects to the robot and can send commands to it.
 */
public class Mouth implements MouthInterface {

	private static final boolean VERBOSE = true;
	
	private String nxtAddress = "btspp://group2";
	
	private NXTConnector conn;
	private DataOutputStream dos;
	private DataInputStream dis;	
	
	private boolean isConnected;

	/**
	 * Default constructor. Initialises the blue-tooth connection.
	 * @throws Exception 
	 */
	public Mouth() throws Exception {
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
	
	public void cleanup() {
		try {
			dis.close();
			dos.close();
			conn.close();
		} catch (IOException e) {
			System.out.println("IOException closing connection:");
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Called when the object is garbage-collected. Closes the connections.
	 */
	@Override
	protected void finalize() throws Throwable {
		sendStop();
		cleanup();
		super.finalize();
	}
	
	/**
	 * Tells Alfie to stop moving.
	 */
	public void sendStop() {
		sendCandyPacket(new CandyPacket(CandyPacket.STOP_CANDY));
	}
	
	/** Tells Alfie to move in an arc going forward and turning right.
	 * @param radius in cm
	 * @param angle in degrees
	 */
	public void sendForwardArcRight(double radius, double angle) {
		sendCandyPacket(
				new CandyPacket(
						CandyPacket.FORWARD_RIGHT_ARC_CANDY, 
						(int)(10000 * radius), 
						(int)(10000 * angle)
				)
		);
	}
	
	/**
	 * Tells Alfie to move in an arc going forward and turning left.
	 * @param radius in centimetres.
	 * @param angle in degrees
	 */
	public void sendForwardArcLeft(double radius, double angle) {
		sendCandyPacket(
				new CandyPacket(
						CandyPacket.FORWARD_LEFT_ARC_CANDY, 
						(int)(10000 * radius), 
						(int)(10000 * angle)
				)
		);
	}
	
	/**
	 * Tells Alfie to move in an arc going backwards and turning right.
	 * @param radius in cm, keep it positive.
	 * @param angle in degrees, keep it positive.
	 */
	public void sendBackwardsArcRight(double radius, double angle) {
		sendCandyPacket(
				new CandyPacket(
						CandyPacket.BACKWARDS_RIGHT_ARC_CANDY, 
						(int)(10000 * radius), 
						(int)(10000 * angle)
				)
		);
	}

	
	
	/**
	 * Tells Alfie to move in an arc going backwards and turning left.
	 * @param radius in cm
	 * @param angle in degrees
	 */
	public void sendBackwardsArcLeft(double radius, double angle) {
		sendCandyPacket(
				new CandyPacket(
						CandyPacket.BACKWARDS_LEFT_ARC_CANDY, 
						(int)(10000 * radius), 
						(int)(10000 * angle)
				)
		);
	}
	
	/**
	 * Tells Alfie to start moving forward. 
	 * @param speed The speed for the command.
	 * @param distance The distance to travel in an inspecified unit, 0 to travel indefinitely
	 */
	public void sendGoForward(int speed, int distance) {
		sendCandyPacket(new CandyPacket(CandyPacket.GO_FORWARD_CANDY, speed, distance));
	}
	
	/**
	 * Tells Alfie to start moving backwards. 
	 * @param speed The speed for the command.
	 */
	public void sendGoBackwards(int speed, int distance) {
		sendCandyPacket(new CandyPacket(CandyPacket.GO_BACKWARDS_CANDY, speed, distance));
	}
	
	/**
	 * Tells Alfie to become aggressive.
	 * @param power The power for the kick.
	 */
	public void sendKick(int power) {
		sendCandyPacket(new CandyPacket(CandyPacket.KICK_CANDY, power));
	}
	
	/**
	 * Tells Alfie to spin on the spot counter-clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinLeft(int speed, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.SPIN_LEFT_CANDY, speed, angle));
	}
	
	/**
	 * Tells Alfie to spin on the spot clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinRight(int speed, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.SPIN_RIGHT_CANDY, speed, angle));
	}
	
	/**
	 * Tells Alfie to reset communication.
	 */
	public void sendReset() {
		sendCandyPacket(new CandyPacket(CandyPacket.RESET_CANDY));
	}
	
	/**
	 * Tells the Alfie to go to sleep.
	 */
	public void sendExit() {
		sendCandyPacket(new CandyPacket(CandyPacket.SLEEP_CANDY));
	}
	
	/**
	 * Sends the given bytes across the opened connection and checks the 
	 * response. 
	 * @param b The bytes to send.
	 * @param VERBOSE If true, the bytes are printed before being sent.
	 */
	private void sendCandyPacket(CandyPacket packet) {
		//long start = System.currentTimeMillis();
		
		boolean success = false;
		do {
			try {
				// Print output if requested
				if (VERBOSE) {
					System.out.println("Sending bytes:");
					packet.printSweets();
				}
	
				byte b [] = packet.getSweets();
				// Send bytes
				dos.write(b, 0, CandyPacket.PACKET_SIZE);
				dos.flush();
				success = true; //TODO this should be removed if feedback is put back in
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
			
			//TODO getting feedback seems to give us problems (lag)
//			try {
//				// On success Alfie should repeat the command back.
//				byte [] b = new byte [CandyPacket.PACKET_SIZE];
//				dis.read(b, 0, CandyPacket.PACKET_SIZE);
//				if (verbose) {
//					System.out.println("Recieved bytes:");
//					new CandyPacket(b).printSweets();
//				}
//				success = true;
//				if (!packet.contentsEqual(b)) {
//					success = false;
//					System.out.println("WARNING: command is not the same; RESENDING...");
//					continue;
//				}
//			} catch (IOException ioe) {
//				System.out.println("IO Exception reading bytes:");
//				System.out.println(ioe.getMessage());
//				break;
//			}
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
