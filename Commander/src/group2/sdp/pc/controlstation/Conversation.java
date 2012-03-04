package group2.sdp.pc.controlstation;

import group2.sdp.pc.mouth.Mouth;

public class Conversation {
	public static void main (String [] args) throws InterruptedException {
		Mouth s;
		try {
			s = new Mouth();
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
			e.printStackTrace();
		}
	}
}
