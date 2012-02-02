package group2.simulator;

import static org.junit.Assert.*;

import java.awt.Color;
import net.phys2d.raw.World;
import org.junit.Before;
import org.junit.Test;

public class RobotTest {

	public static int boardWidth = 750;
	public static int boardHeight = 400;
	public static int padding = 100;
	public static int wallThickness = 20;
	public static int goalWidth = boardHeight/2;
	public static int goalThickness = 50;
	public static int robotStartX = padding + wallThickness; //120
	public static int robotStartY = boardHeight/2 + padding; //300
	
	public static int ballStartX = boardWidth/2 + padding; //475
	public static int ballStartY = boardHeight/2 + padding; //300
	
	Robot robot;
	Ball ball;
	World world;
	
	@Before
	public void setUp(){
		
		robot = new Robot(robotStartX,robotStartY, 70, 50, Color.BLUE, null, 0);
		ball = new Ball(ballStartX, ballStartY, 9, Color.RED, 0);
	}
	

	@Test
	//test the robot angle after turning right
	public final void testTurnRight() {

		double originalAngle = robot.getAngle();
		robot.turn(+5);
		assertTrue(robot.convertAngle((originalAngle + 5)) == robot.getAngle());
		}
	
	@Test
	//test the robot angle after turning left
	public final void testTurnLeft() {

		double originalAngle = robot.getAngle();
		robot.turn(-5);
		assertTrue(robot.convertAngle((originalAngle - 5)) == robot.getAngle());
		}
	
	@Test
	//test the robot moving forward
	public final void testMoveForward(){
		//System.out.println("Initial robotX position is "+robotStartX);
		int speed =3; // values for computing the move forward (same as in the Robot class)
		int mult =3;
		//computing the robot x-position as in moving forward
		float expected_x = (robot.getX() + (speed*mult * (float) Math.cos(Math.toRadians(robot.getAngle()))));
		// move the robot forward
		robot.moveForward(world, ball);
		float actual_x = robot.getX();
		assertTrue(actual_x==expected_x);
		
	}
	@Test
	//test the robot moving backwards
	public final void testMoveBackwords(){
		//System.out.println("Initial robotX position is "+robotStartX);
		int speed =3; // values for computing the move backwards (same as in the Robot class)
		int mult =-3;
		//computing the robot x-position as in moving forward
		float expected_x = (robot.getX() + (speed*mult * (float) Math.cos(Math.toRadians(robot.getAngle()))));
		// move the robot backward
		robot.moveBackwards(world, ball);
		float actual_x = robot.getX();
		assertTrue(actual_x==expected_x);
		
	}
	
	@Test
	public final void testIsCloseToFront(){

		System.out.println("robot x-position "+robot.getX()); //140 (after move forward and backwards from the previous tests
		System.out.println("robot y-position "+robot.getY()); // 300
		System.out.println("ball x-position is "+ball.getX()); //475.5
		
		float new_ball_positionX = ball.getX() - (float) 306.8; //make ball position close enough to the robot
		ball.setPosition(new_ball_positionX, ballStartY);
		
		assertTrue(robot.isCloseToFront(ball));
	}


}
