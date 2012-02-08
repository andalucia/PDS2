package group2.simulator.physical;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;


import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;


public class Robot extends BoardObject {

	final int xSize;
	final int ySize;
	Double rectangle;
	int speed = 3;
	BufferedImage img = null;
	private int score;
	

	/**
	 * Constructor that fully initiliases the Robot player
	 * @param x is the x coordinate position for the robot
	 * @param y is the y coordinate position for the robot
	 * @param xSize is the width of a robot
	 * @param ySize is the height of the robot
	 * @param color is the colour of the robot
	 * @param img the image of the robot
	 * @param angle the initial angle at which the robot is placed
	 */
	public Robot(int x, int y, int xSize, int ySize, Color color, BufferedImage img,
			int angle) {
		super(x, y, "Robot", new Box(xSize, ySize), 10000000, color, angle);
		this.img = img;
		this.body.setUserData(this);
		this.xSize = xSize;
		this.ySize = ySize;
		this.body.setFriction(100);
		this.body.setRestitution(1.0f);
		this.body.setDamping(2f);
		this.body.setRotatable(false);
	}
	
	public int getWidth() {
		return xSize;
	}

	public int getHeight() {
		return ySize;
	}

	/**
	 * Check is the robot is close to the ball
	 * @param ball the object which is going to be checked if it's close to the robot
	 * @return true or false, depending whether the robot is close to the ball
	 */
	public Boolean isCloseToFront(Ball ball){
		
		// performs a linear mapping from 2D coordinates to other 2D coordinates 
		//that preserves the 'parallelism' and 'perpendicularity' of lines
		AffineTransform at = AffineTransform.getRotateInstance(
				(Math.toRadians(this.getAngle()*(-1))), getX(), getY());
		Point2D.Double p = new Point2D.Double((double) ball.getX(), (double) ball.getY());
		at.transform(p,p);
		int kickDist = 10;
		if ( (Math.abs(p.getX() - getX() - (xSize/2)) <= kickDist) && (Math.abs(p.getY() - getY()) <= (ySize/2)) ) {
			
			return true;
		}
		return false;
	}
	// a copy isCloseTofront just with changed kickDist value - must be refactored later
	// really sorry for messy code :(
	public boolean canRobotKick(Ball ball){
		AffineTransform at = AffineTransform.getRotateInstance(
				(Math.toRadians(this.getAngle()*(-1))), getX(), getY());
		Point2D.Double p = new Point2D.Double((double) ball.getX(), (double) ball.getY());
		at.transform(p,p);
		int kickDist = 20;
		if ( (Math.abs(p.getX() - getX() - (xSize/2)) <= kickDist) && (Math.abs(p.getY() - getY()) <= (ySize/2)) ) {
			
			return true;
			
		}
		return false;
	}


	/**
	 * Robot moves forward and takes into account if it finds the ball in front of him
	 * if so, it will push the ball as it goes along
	 * @param world is the world where the action happens
	 * @param ball is the ball that can be encountered 
	 */
	public void moveForward(World world, Ball ball) {
		move(world,ball, 3);
	}
	/**TODO
	 * Robot moves backwards BUT NOW does NOT take into account if it finds the ball behind him
	 * if so, it will push the ball as it goes along
	 * @param world is the world where the action happens
	 * @param ball is the ball that can be encountered 
	 */
	public void moveBackwards(World world, Ball ball) {
		move(world,ball,-4);
	}
	
	
	/**Function moves robot and ball provided that it is moving forward and ball is in front
	 * Counting next position of the ball.
	 * 
	 * TODO: PUSHING BACKWARDS AND SIDES!
	 * @param world the world where the move occurs 
	 * @param ball the ball that will be moved along with the robot
	 * @param mult
	 */
	public void move(World world, Ball ball, int mult){
		
		int dist = speed * mult;
		double tempAngle = this.getAngle();
		float x = countNextPositionX(this.getX(),dist,tempAngle);
		float y = countNextPositionY(this.getY(),dist,tempAngle);
		
		if (isCloseToFront(ball) && dist > 0) {
			float ballCoordX = countNextPositionX(ball.getX(),dist,tempAngle);
			float ballCoordY = countNextPositionY(ball.getY(),dist,tempAngle);
			this.body.move(x, y);
			ball.body.move(ballCoordX, ballCoordY);
		}
			else
			{
				this.body.move(x, y);
			}
	}
	
	/**
	 * Computing the next position for the x-coordinate
	 * @param x is the current x-coordinate
	 * @param dist is value added to the initial position
	 * @param angle 
	 * @return the next x-coordinate position
	 */
	public float countNextPositionX(float x, int dist, double angle)
	{
		return x + (dist * (float) Math.cos(Math.toRadians(angle)));
	}
	
	/**
	 * Computing the next position for the y-coordinate
	 * @param y is the current y-coordinate
	 * @param dist is value added to the initial position
	 * @param angle
	 * @return the next y-coordinate position
	 */
	public float countNextPositionY(float y, int dist, double angle)
	{
		return y + (dist * (float) Math.sin(Math.toRadians(angle)));
	}
		
	/**
	 * Turn the robot at the given angle 
	 * Negative number turns to right - rotates clock-wise direction
	 * Positive number turns to left - rotates counter-clock wise direction
	 * @param value
	 */
	public void turn(int value) {
		if (value < 0) {
			this.setAngle(this.getAngle() - 5);
		} else if (value > 0) {
			this.setAngle(this.getAngle() + 5);
		}
		// rotates the body
		this.getBody().setRotation((float) Math.toRadians(getAngle()));
	
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public void incrScore()
	{
		score++;
	}
	
	/** It checks if the ball is in front sends kick command and fixed angle to Ball class
	 * @param ball is the ball you are kicking
	 */
	public void kick(Ball ball){
		if (canRobotKick(ball)){
			ball.setBallKicked(true);
			System.out.println("Ball is kicked");
			ball.setFixedAngle(this.getAngle());
			System.out.println("angle is set");
			
		}
		else
		{
		System.out.println("Too far away from the ball");
		}
	}
	
	
}