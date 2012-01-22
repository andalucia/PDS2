package group2.sdp.pc.commander;

public class TestServer {
	public static void main (String [] args) throws InterruptedException {
		Server s = new Server();
		s.sendGoForward(10);

		Thread.sleep(1000);

		s.sendStop();
		s.sendReset();
//		s.sendExit();
	}
}
