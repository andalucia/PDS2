package group2.sdp.pc.controlstation;

import group2.sdp.pc.server.Server;

public class TestServer {
	public static void main (String [] args) throws InterruptedException {
		Server s;
		try {
			s = new Server();
//			s.sendExit();
			
			s.sendGoForward(100, 0);
			Thread.sleep(2000);
			for (int i = 0; i < 2; ++i)
			{
				s.sendMoveArc(20f, 180);
				System.out.println("After move");
				Thread.sleep(5000);
			}

			s.sendStop();
			s.sendReset();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
