package group2.sdp.pc.controlstation;

import group2.sdp.pc.server.Server;

public class TestServer {
	public static void main (String [] args) throws InterruptedException {
		Server s;
		try {
			s = new Server();
//			s.sendExit();
			
			s.sendForwardArcRight(20, 90);
			Thread.sleep(4000);
			s.sendBackwardsArcRight(20, 90);
			Thread.sleep(4000);
			s.sendForwardArcLeft(20, 90);
			Thread.sleep(4000);
			s.sendBackwardsArcLeft(20, 90);
			Thread.sleep(4000);
			s.sendStop();
			s.sendReset();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
