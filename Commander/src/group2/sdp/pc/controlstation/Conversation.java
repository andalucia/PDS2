package group2.sdp.pc.controlstation;

import group2.sdp.pc.mouth.Mouth;

public class Conversation {
	public static void main (String [] args) throws InterruptedException {
		Mouth s;
		try {
			s = new Mouth();
//			s.sendExit();
			
//			s.sendGoForward(35, 10);
//			Thread.sleep(4000);
//			s.sendSpinLeft(0, 90);
//			Thread.sleep(4000);
//			s.sendGoForward(35,10);
//			Thread.sleep(4000);
//			s.sendSpinRight(0,90);
//			Thread.sleep(4000);
//			s.sendGoForward(35, 30);
//			s.sendSpinRight(0, 90);
//			Thread.sleep(4000);
//			s.sendGoForward(35, 30);
//			Thread.sleep(4000);
//			s.sendSpinRight(0,90);
//			
//			Paul's Test ^^^
			
			long prev = 0;
			for (int i = 0; i < 2000; ++i) {
				s.sendGoForward(15, 0);
				Thread.sleep(40); // 5 FPS
				System.out.println(1000.0 / (System.currentTimeMillis() - prev));
				prev = System.currentTimeMillis();
			}
			
			s.sendStop();
			Thread.sleep(1000);
			s.sendReset();
			Thread.sleep(1000);
			s.cleanup();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
