package group2.sdp.pc.planner.pathstep;

import java.awt.Point;
import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to start moving backwards in an anti-clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the left hand side of Alfie.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Forward Left object.)
 * 
 */
public class PathStepArcBackwardsLeft implements PathStep {
	
	/**
	 * these variables are used in construction of the step
	 * and stored for use when the step is pulled of the queue
	 * 
	 */
	private int radius;
	private int angle;
	private int threshold;
	private Point2D target;
	private Point2D alfie;
	private double distance;
	
	
	
	/**
	 * constructor for the class
	 * 
	 * @param radius : radius of the arc
	 * @param angle : angle we have to turn
	 * @param threshold : the degree of error we allow
	 * @param target : target this step should take alfie to
	 * @param alfie : alfie's position
	 */
	public PathStepArcBackwardsLeft(int radius, int angle, int threshold, Point2D target, Point2D alfie){
		this.angle = angle;
		this.radius = radius;
		this.threshold = threshold;
		this.target = target;
		this.alfie = alfie;
		
		this.distance = alfie.distance(target);
	}
	
	
	@Override
	public Type getType() {
		return Type.ARC_BACKWARDS_LEFT;
	}
	

	
	/**
	 * getter for the variable angle 
	 * @return int
	 */
	public int getAngle(){
		return this.angle;
	}
	
	/**
	 * getter for the variable threshold 
	 * @return int
	 */
	public int getThreshold(){
		return this.threshold;
	}
	
	
	/**
	 * getter for the variable radius 
	 * @return int
	 */
	public int getRadius(){
		return this.radius;
	}
	
	/**
	 * getter for the variable radius
	 * @return Point
	 */
	public Point2D getTarget(){
		return this.target;
	}

	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target position.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		Point2D target = this.target;
		
		//should we check the angle?
		double distance = alfie.distance(target);
		if(distance <=threshold){
			return true;
		}else{
			return false;
		}
	}

	
	/**
	 * Fail: 
	 * 		 Easy:
	 * 			  If Alfie is moving away from the target position.
	 * 		 
	 * 		 Hard:
	 * 			  If the target position is farther from Alfie's circular trajectory than a specified
	 * 			  threshold. This is hard, as the vision system might not be accurate enough to allow a
	 * 		 	  precise computation of the circular trajectory of Alfie. However, if implemented, this
	 * 		 	  would provide Alfie with a lot quicker reactions to problems with this Path Step.
	 */
	@Override
	public boolean problemExists(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		
		//easy
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		Point2D target = this.target;
		
		double newDistance = alfie.distance(target);
		if(newDistance <= distance){
			this.distance = newDistance;
			return false;
		}else{
			return true;
		}
	}
}
