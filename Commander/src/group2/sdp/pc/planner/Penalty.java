package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.mouth.MouthInterface;
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
	private MouthInterface alfieServer;


	public Penalty(MouthInterface alfieServer, Overlord lord) {
		this.alfieServer = alfieServer;
		this.lord=lord;
		
	}


	@Override
	public void consumeInfo(DynamicInfo dpi) {
		return;

	}

	public void go() {
		if(System.currentTimeMillis()%2==1){
			alfieServer.sendSpinLeft(50, 20);
		}else{
			alfieServer.sendSpinRight(50, 20);
		}		
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
		alfieServer.sendGoForward(1024, 7);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("interrupted penalty");
			
		}
		//basic loop that continues for a set time
		for(int i=0 ; i<5; i++){
			alfieServer.sendGoBackwards(1024, 16);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("interrupted penalty");
				
			}
			alfieServer.sendGoForward(1024, 14);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("interrupted penalty");
				
			}
		}
		
		
		lord.start();
	}
		
	
}
