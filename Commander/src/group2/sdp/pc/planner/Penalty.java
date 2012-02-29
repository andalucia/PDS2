package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.server.Server;
import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

public class Penalty implements DynamicInfoConsumer {


	private Overlord lord;
	/**
	 * true when penalty mode is running
	 */
	private boolean running;

	/**
	 * The SeverSkeleton implementation to use for executing the commands. 
	 * Can be the Alfie bluetooth server or the simulator.
	 */
	private ServerSkeleton alfieServer;


	public Penalty(ServerSkeleton alfieServer, Overlord lord) {
		this.alfieServer = alfieServer;
		this.lord=lord;
		
	}


	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		return;

	}

	public void go() {
		alfieServer.sendSpinLeft(50, 20);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("interrupted penalty");
			
		}
		alfieServer.sendKick(1024);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("interrupted penalty");
			
		}
		lord.start();
	}
	
	public void defend() {
		alfieServer.sendGoForward(1024, 5);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("interrupted penalty");
			
		}
		//basic loop that continues for a set time
		for(int i=0 ; i<5; i++){
			alfieServer.sendGoBackwards(1024, 10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("interrupted penalty");
				
			}
			alfieServer.sendGoForward(1024, 10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("interrupted penalty");
				
			}
		}
		
		
		lord.start();
	}
		
	
}
