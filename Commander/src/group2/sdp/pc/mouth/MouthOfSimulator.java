package group2.sdp.pc.mouth;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import group2.sdp.common.candypacket.CandyPacket;
import group2.simulator.starter.SimulatorI;

/**
 * Connects to the robot and can send commands to it.
 */
public class MouthOfSimulator implements MouthInterface {

	private final boolean verbose = false;

	private boolean isConnected;

	/**
	 * Default constructor. Initialises the blue-tooth connection.
	 * 
	 * @throws Exception
	 */
	public MouthOfSimulator() throws Exception {

		System.out.println("in simulator");

	}

	public void cleanup() {

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

	/**
	 * Tells Alfie to move in an arc going forward and turning right.
	 * 
	 * @param radius
	 *            in cm
	 * @param angle
	 *            in degrees
	 */
	public void sendForwardArcRight(float radius, int angle) {

	}

	/**
	 * Tells Alfie to move in an arc going forward and turning left.
	 * 
	 * @param radius
	 *            in cm
	 * @param angle
	 *            in degrees
	 */
	public void sendForwardArcLeft(float radius, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.FORWARD_LEFT_ARC_CANDY,
				(int) (10000 * radius), angle));
	}

	/**
	 * Tells Alfie to move in an arc going backwards and turning right.
	 * 
	 * @param radius
	 *            in cm
	 * @param angle
	 *            in degrees
	 */
	public void sendBackwardsArcRight(float radius, int angle) {

		sendCandyPacket(new CandyPacket(CandyPacket.BACKWARDS_RIGHT_ARC_CANDY,
				(int) (10000 * radius), angle));
	}

	/**
	 * Tells Alfie to move in an arc going backwards and turning left.
	 * 
	 * @param radius
	 *            in cm
	 * @param angle
	 *            in degrees
	 */
	public void sendBackwardsArcLeft(float radius, int angle) {
		sendCandyPacket(new CandyPacket(CandyPacket.BACKWARDS_LEFT_ARC_CANDY,
				(int) (10000 * radius), angle));
	}

	/**
	 * Tells Alfie to start moving forward.
	 * 
	 * @param speed
	 *            The speed for the command.
	 * @param distance
	 *            The distance to travel in an inspecified unit, 0 to travel
	 *            indefinitely
	 */
	public void sendGoForward(int speed, int distance) {
		System.out.println("gogogo");
		SimulatorI.goForward();

	}

	/**
	 * Tells Alfie to start moving backwards.
	 * 
	 * @param speed
	 *            The speed for the command.
	 */
	public void sendGoBackwards(int speed, int distance) {
		System.out.println(speed + " " + distance);
		SimulatorI.goBackwards();

	}

	/**
	 * Tells Alfie to become aggressive.
	 * 
	 * @param power
	 *            The power for the kick.
	 */
	public void sendKick(int power) {

	}

	/**
	 * Tells Alfie to spin on the spot counter-clock wise.
	 * 
	 * @param speed
	 *            The speed for the spin.
	 * @param angle
	 *            The angle for the spin.
	 */
	public void sendSpinLeft(int speed, int angle) {
		System.out.println(speed + " " + angle);
		SimulatorI.spinLeft(angle);
		// for(int i = 0; i<angle;i++){
		// SimulatorI.spinLeft();
		// }

	}

	/**
	 * Tells Alfie to spin on the spot clock wise.
	 * 
	 * @param speed
	 *            The speed for the spin.
	 * @param angle
	 *            The angle for the spin.
	 */
	public void sendSpinRight(int speed, int angle) {
		System.out.println(speed + " " + angle);
		SimulatorI.spinRight(angle);

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
	 * 
	 * @param b
	 *            The bytes to send.
	 * @param verbose
	 *            If true, the bytes are printed before being sent.
	 */
	private void sendCandyPacket(CandyPacket packet) {
		// long start = System.currentTimeMillis();

		boolean success = false;
		do {
			try {
				// Print output if requested
				if (verbose) {
					System.out.println("Sending bytes:");
					packet.printSweets();
				}

				byte b[] = packet.getSweets();
				// Send bytes

				success = true; // TODO this should be removed if feedback is
								// put back in
			} catch (Exception ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}

			// TODO getting feedback seems to give us problems (lag)
			// try {
			// // On success Alfie should repeat the command back.
			// byte [] b = new byte [CandyPacket.PACKET_SIZE];
			// dis.read(b, 0, CandyPacket.PACKET_SIZE);
			// if (verbose) {
			// System.out.println("Recieved bytes:");
			// new CandyPacket(b).printSweets();
			// }
			// success = true;
			// if (!packet.contentsEqual(b)) {
			// success = false;
			// System.out.println("WARNING: command is not the same; RESENDING...");
			// continue;
			// }
			// } catch (IOException ioe) {
			// System.out.println("IO Exception reading bytes:");
			// System.out.println(ioe.getMessage());
			// break;
			// }
		} while (!success);

		// System.out.println(System.currentTimeMillis() - start);
	}

	/**
	 * Checks whether a connection to Alfi has been established
	 * 
	 * @return
	 */
	public boolean alfiConnected() {
		return isConnected;
	}

	@Override
	public void sendForwardArcLeft(double radius, double angle) {
		// TODO Auto-generated method stub
		SimulatorI.arc(radius, angle, 1);
		System.out.println("FL " + radius + " " + angle);

	}

	@Override
	public void sendForwardArcRight(double radius, double angle) {
		// TODO Auto-generated method stub
		SimulatorI.arc(radius, angle, 4);
		System.out.println("FR " + radius + " " + angle);
	}

	@Override
	public void sendBackwardsArcLeft(double radius, double angle) {
		// TODO Auto-generated method stub
		SimulatorI.arc(radius, angle, 2);
		System.out.println("BL " + radius + " " + angle);
	}

	@Override
	public void sendBackwardsArcRight(double radius, double angle) {
		// TODO Auto-generated method stub
		SimulatorI.arc(radius, angle, 3);
		System.out.println("BR " + radius + " " + angle);
	}

}