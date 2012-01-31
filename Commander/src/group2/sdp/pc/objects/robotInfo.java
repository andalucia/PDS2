package group2.sdp.pc.objects;

import java.awt.Point;

//this class hold the info about the robots on the pitch
public class robotInfo {

	
	public static final int ROBOT_LENGTH = 70;
	public static final int ROBOT_WIDTH = 50;

    protected Point coors;
    protected double angle;
    protected Rectangle rect = new Rectangle(); //rectangle is the rotated rectangle around the robot
    
    protected robotInfo() {};

    public robotInfo(Point coors, float angle) {
        super();
        this.coors = coors;
        this.angle = angle;
        updateRect();
    }
    
    //gets a copy of the class
    public robotInfo(robotInfo old)
    {
        coors = old.getCoors();
        angle = Math.toRadians(old.getAngle());
        updateRect();
    }

    public Point getCoors()
    {
        return coors;
    }

    public double getAngle()
    {
        return angle;
    }
    
    public Rectangle getRect()
    {
    	return rect;
    }

   
    
    public void updateRect()
    {
    	
    }
    
    
	
}
