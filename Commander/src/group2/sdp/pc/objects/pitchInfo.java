package group2.sdp.pc.objects;

import java.awt.Point;

public class pitchInfo {

	//robot info holds a point for the location of the robot then has a int/float for the angle
	protected robotInfo yellowBot = new robotInfoEdit(new Point(0, 0),0); 
	protected robotInfo blueBot = new robotInfoEdit(new Point(0, 0),0); 
	protected Point ballCoors = new Point(0,0);
	
	
	public robotInfo getYellowBot()
	{
            return new robotInfo(yellowBot);
	}

	public robotInfo getBlueBot()
	{
            return new robotInfo(blueBot);
	}

	public Point getBallCoors()
	{
            return new Point(ballCoors);
	}
	
}
