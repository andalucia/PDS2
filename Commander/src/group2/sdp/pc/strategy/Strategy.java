package group2.sdp.pc.strategy;

import group2.sdp.pc.commander.Server;
import group2.simulator.Simulator;

/**
 * This is the main strategy class, it makes Alfi's decisions for him and sends commands to his Brain
 */
public class Strategy {
	
	// Server for sending commands to Alfi
	private Server alfiServer;
	
	/**
	 * Constructor for controlling the real life Alfi
	 * 
	 * A instantiated and connected Server object should be passed here to allow the strategy to send
	 * control commands to Alfi
	 */
	public Strategy(Server alfiServer) throws Exception {
		if(!alfiServer.alfiConnected()) {
			throw new Exception("The Server that was passed was not connected to Alfi");
		}
	}
	
	/**
	 * Constructor for controlling a simulated Alfi
	 * 
	 * This constructor creates opens the simulator and gets it ready for sending commands
	 */
	public Strategy() {
		Simulator.prepareSimulator();
		Simulator.initializeArea();
	}
}
