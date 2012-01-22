package group2.sdp.pc.commander;

public class TestServer {
	public static void main (String [] args) {
		Server s = new Server();
		s.sendGoForward(1000);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		s.sendStop();
		//s.sendKick(200);
		s.sendExit();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s.sendStop();		
	}
}
