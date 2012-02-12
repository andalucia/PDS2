package group2.sdp.pc.controlstation;

import group2.sdp.pc.server.Server;

public class TestServer {
	public static void main (String [] args) throws InterruptedException {
		Server s;
		try {
			s = new Server();
			s.sendGoForward(512, 0);

			s.sendStop();
			s.sendReset();
//			s.sendExit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
