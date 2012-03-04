package group2.sdp.pc.controlstation;

import group2.sdp.pc.server.Server;

public class TestServer {
	public static void main (String [] args) throws InterruptedException {
		Server s;
		try {
			s = new Server();
//			s.sendExit();
			
			s.sendGoForward(35, 10);
			Thread.sleep(4000);
			s.sendSpinLeft(0, 90);
			Thread.sleep(4000);
			s.sendGoForward(35,10);
			Thread.sleep(4000);
			s.sendSpinRight(0,90);
			Thread.sleep(4000);
			s.sendGoForward(35, 30);
			s.sendSpinRight(0, 90);
			Thread.sleep(4000);
			s.sendGoForward(35, 30);
			Thread.sleep(4000);
			s.sendSpinRight(0,90);
			
			
			s.sendStop();
			s.sendReset();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
