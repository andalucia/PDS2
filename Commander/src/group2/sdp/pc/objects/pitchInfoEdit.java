package group2.sdp.pc.objects;

import java.awt.Point;

public class pitchInfoEdit extends pitchInfo {

	//not sure about the y value using 480 -p.y
	
	public void updateYellowBot(Point p, int angle)
    {
    	if (p != null)
    	{
            yellowBot.coors.x = p.x;
            yellowBot.angle = angle;
            yellowBot.coors.y = 480 - p.y;
    	}
    }

    public void updateBlueBot(Point p, int angle)
    {
    	if (p != null)
    	{
            blueBot.coors.x = p.x;
            blueBot.coors.y = 480 - p.y;
            blueBot.angle = angle;
    	}
    }

    public void updateBall(Point p)
    {
    	if (p != null)
    	{
            ballCoors.x = p.x;
            ballCoors.y = 480 - p.y;
    	}
    }
	
	
	
}
