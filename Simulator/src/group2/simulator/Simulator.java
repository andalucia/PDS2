// Code is quite messy for now - im going to refactor and restructruze later on.


package group2.simulator;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

public class Simulator {
	
	/** The frame displaying the simulation */
	private static Frame frame;
	
	public static int boardWidth = 630;
	public static int boardHeight = 330;
	public static int padding = 100;
	public static int wallThickness = 20;
	public static int goalWidth = 144;
	public static int goalThickness = 50;
	
	private static Body leftGoalLine;
	private static Body rightGoalLine;
	public static int robotStartX = padding + wallThickness;
	public static int oppRobotStartX = padding + boardWidth - wallThickness;
	public static int ballStartX = boardWidth/2 + padding;
	public static int ballStartY = boardHeight/2 + padding;
	public static int robotStartY = boardHeight/2 + padding;
	private static Robot robot;
	private static Robot oppRobot;
	private static Ball ball;
	private static int robotMovingSpeed, robotRotationSpeed;
	private static int robotAngle;

	/** The title of the simulation */
	private String title;
	/** True if the simulation is running */
	private static boolean running = true;
	
	/** The world containing the physics model */
	private static World world = new World(new Vector2f(0.0f, 10.0f), 10, new QuadSpaceStrategy(20,5));
	
	/** The rendering strategy */
	private static BufferStrategy strategy;
	
	public static int goalPost1y = (boardHeight-goalWidth)/2 + padding - wallThickness/2;
	public static int goalPost2y = (boardHeight+goalWidth)/2 + padding + wallThickness/2;
	
	public static void main(String args []){
			prepareSimulator();
			initializeArea();				
	}

	public Simulator(String title, Robot robot, Robot oppRobot, Ball ball) {
		this.title = title;
		Simulator.robot = robot;
		Simulator.oppRobot = oppRobot;
		Simulator.ball = ball;
		//robot is not moving yet
		//We might need to create another initial function to inittialise values later on.
		robotMovingSpeed = 0;
		robotAngle = 0;
		robotRotationSpeed = 0;
		
	}
	
	/**
	 * Create a simulator for our world
	 */
	public static void prepareSimulator() {
		
		BufferedImage blueImage = loadImage("data/blueRobot.jpeg");
		BufferedImage yellowImage = loadImage("data/yellowRobot.jpeg");
		
		int newRobotStartX = robotStartX;
		int newOppRobotStartX = oppRobotStartX;
		int newBallStartX = ballStartX;
		
		final Simulator sim = new Simulator("SDP World",new Robot(newRobotStartX, robotStartY+60, 70, 50, Color.BLUE, blueImage, 0),
				new Robot(newOppRobotStartX, robotStartY, 70, 50, Color.YELLOW, yellowImage, 180),
				new Ball(newBallStartX, ballStartY, 9, Color.RED, 0));
	}

	/**
	 * Put the foundations for the simulator
	 */
	public static void initializeArea(){
		initializeFrame(); // initialize the GUI
		setControls();
		while(running)
		{
			initSimulation();  // initialize the simulator
		
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.GREEN);
		
			g.fillRect(0,0,(boardWidth + 2*padding),(boardHeight + 2*padding));
			BoardObject.draw(g, world);  // draw the object in the world
			displayControls(g);
			strategy.show();
			try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

		}
	}
	
	/**
	 * Initialize the GUI
	 */
	private static void initializeFrame() {
		
		frame = new Frame();
		frame.setLayout(null);  
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setSize((boardWidth + 2*padding), (boardHeight + 2*padding)+100);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				running = false;
				System.exit(0);
			}
		});
		
		
		
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();
		
	}
	
	/**
	 * Initialize the simulator and place the robots and the ball
	 */
	private static void initSimulation() {
		world.clear();
		world.setGravity(0, 0);
		robotAngle += robotRotationSpeed;
		robot.setAngle(robotAngle);
		
		
		//ball.stop();
		// Formulas for robot movement to various directions
		Float newRobotStartX = (robot.getX() + (robotMovingSpeed * (float) Math.cos(Math.toRadians(robotAngle))));
		Float newRobotStartY = (robot.getY() + (robotMovingSpeed * (float) Math.sin(Math.toRadians(robotAngle))));
		Float newBallStartX = (ball.getX() + (robotMovingSpeed * (float) Math.cos(Math.toRadians(robotAngle))));
		Float newBallStartY = (ball.getY() + (robotMovingSpeed * (float) Math.sin(Math.toRadians(robotAngle))));
		int newOppRobotStartX = oppRobotStartX;
		//int newBallStartX = ballStartX;
		
		//robotStartX += robotMovingSpeed;
		
		
		oppRobot.setAngle(180);
		robot.setPosition(newRobotStartX, newRobotStartY);
		oppRobot.setPosition(newOppRobotStartX, robotStartY);
		
		if(robot.isCloseToFront(ball) && robotMovingSpeed>0)
			ball.setPosition(newBallStartX, newBallStartY);
		
		
		init(world);
		
		
		
	}

	/**
	 * Initialize the world in the simulator
	 */
	private static void init(World world) {
		world.setGravity(0, 0);
		
		// Set up top and bottom walls
		Body topWall = new StaticBody("TopWall", new Box((boardWidth + 2*wallThickness), wallThickness));
		topWall.setPosition((boardWidth/2 + padding), (padding - wallThickness/2));
		topWall.setRestitution(1.0f);
		world.add(topWall);
		
		Body bottomWall = new StaticBody("BottomWall", new Box((boardWidth + 2*wallThickness), wallThickness));
		bottomWall.setPosition((boardWidth/2 + padding), (boardHeight + padding + wallThickness/2));
		bottomWall.setRestitution(1.0f);
		world.add(bottomWall);
		
		// Set up left wall and goal
		Body topLeftWall = new StaticBody("TopLeftWall", new Box(wallThickness, ((boardHeight - goalWidth)/2 + wallThickness)));
		topLeftWall.setPosition((padding - wallThickness/2), ((goalPost1y + padding - (wallThickness/2))/2));
		topLeftWall.setRestitution(1.0f);
		world.add(topLeftWall);
		Body bottomLeftWall = new StaticBody("BottomLeftWall", new Box(wallThickness, ((boardHeight - goalWidth)/2 + wallThickness)));
		bottomLeftWall.setPosition((padding - wallThickness/2), ((padding + boardHeight + (wallThickness/2) + goalPost2y)/2));
		bottomLeftWall.setRestitution(1.0f);
		world.add(bottomLeftWall);
		Body topLeftGoal = new StaticBody("TopLeftGoal", new Box((goalThickness + 2*wallThickness), wallThickness));
		topLeftGoal.setPosition((padding - goalThickness/2 - wallThickness), goalPost1y);
		topLeftGoal.setRestitution(1.0f);
		world.add(topLeftGoal);
		Body bottomLeftGoal = new StaticBody("BottomLeftGoal", new Box((goalThickness + 2*wallThickness), wallThickness));
		bottomLeftGoal.setPosition((padding - goalThickness/2 - wallThickness), goalPost2y);
		bottomLeftGoal.setRestitution(1.0f);
		world.add(bottomLeftGoal);
		Body backLeftGoal = new StaticBody("BackLeftGoal", new Box(wallThickness, (goalWidth + 2*wallThickness)));
		backLeftGoal.setPosition((padding - goalThickness - 3*wallThickness/2), (padding + boardHeight/2));
		backLeftGoal.setRestitution(1.0f);
		world.add(backLeftGoal);
		
		// Set up right wall and goal
		Body topRightWall = new StaticBody("TopRightWall", new Box(wallThickness, ((boardHeight - goalWidth)/2 + wallThickness)));
		topRightWall.setPosition((boardWidth + padding + wallThickness/2), ((goalPost1y + padding - (wallThickness/2))/2));
		topRightWall.setRestitution(1.0f);
		world.add(topRightWall);
		Body bottomRightWall = new StaticBody("BottomRightWall", new Box(wallThickness, ((boardHeight - goalWidth)/2 + wallThickness)));
		bottomRightWall.setPosition((boardWidth + padding + wallThickness/2), ((padding + boardHeight + (wallThickness/2) + goalPost2y)/2));
		bottomRightWall.setRestitution(1.0f);
		world.add(bottomRightWall);
		Body topRightGoal = new StaticBody("TopRightGoal", new Box((goalThickness + 2*wallThickness), wallThickness));
		topRightGoal.setPosition((boardWidth + padding + goalThickness/2 + wallThickness), goalPost1y);
		topRightGoal.setRestitution(1.0f);
		world.add(topRightGoal);
		Body bottomRightGoal = new StaticBody("BottomRightGoal", new Box((goalThickness + 2*wallThickness), wallThickness));
		bottomRightGoal.setPosition((boardWidth + padding + goalThickness/2 + wallThickness), goalPost2y);
		bottomRightGoal.setRestitution(1.0f);
		world.add(bottomRightGoal);
		Body backRightGoal = new StaticBody("BackRightGoal", new Box(wallThickness, (goalWidth + 2*wallThickness)));
		backRightGoal.setPosition((boardWidth + padding + goalThickness + 3*wallThickness/2), (padding + boardHeight/2));
		backRightGoal.setRestitution(1.0f);
		world.add(backRightGoal);	

	
		leftGoalLine = new StaticBody("BackLeftGoal", new Box(1, (goalWidth + 2*wallThickness)));
		leftGoalLine.setPosition((padding), (padding + boardHeight/2));
		leftGoalLine.setRestitution(1.0f);
		world.add(leftGoalLine);
		rightGoalLine = new StaticBody("BackRightGoalrobot.setAngle(180);", new Box(1, (goalWidth + 2*wallThickness)));
		rightGoalLine.setPosition((boardWidth + padding + 1), (padding + boardHeight/2));
		rightGoalLine.setRestitution(1.0f);
		world.add(rightGoalLine);
		
		
		ball.setGoalLines(leftGoalLine, rightGoalLine);
		ball.ignoreGoalLines();
		
		world.add(robot.getBody());
		world.add(oppRobot.getBody());
		world.add(ball.getBody());
	}
	
	/**
	 * Load an image from a file
	 * 
	 */
	public static BufferedImage loadImage(String fileName) {
		BufferedImage img = null;
		if (fileName != null) {
			try {
				img = ImageIO.read(new File(fileName));
			} catch (IOException e) {
				System.out.println("Could not load image " + fileName);
			}
		}
		return img;
	}
	
	
	
	public static void setControls()
	{
		frame.addKeyListener(new KeyListener()
				{
			public void keyPressed(KeyEvent event) {
				switch(event.getKeyCode()){
					case KeyEvent.VK_ESCAPE :
							System.exit(0);
							break;
					case KeyEvent.VK_UP :	
							robotMovingSpeed = 1;
							robotRotationSpeed = 0;
							break;
					case KeyEvent.VK_DOWN :	
							robotMovingSpeed = -1;
							robotRotationSpeed = 0;
							break;
					case KeyEvent.VK_S: 
							robotMovingSpeed = 0; 
							robotRotationSpeed = 0;robot.setAngle(180);
							break;
					case KeyEvent.VK_RIGHT:
							// for now just stopping the robot and checking if rotation works
							robotMovingSpeed = 0;
							robotRotationSpeed = 1;
							break;
					case KeyEvent.VK_LEFT:
							robotMovingSpeed = 0;
							robotRotationSpeed = -1;
							break;
					case KeyEvent.VK_R:
							// Resetting Simulation
							// restoring coordinates and angles, disabling movements
							
							robotAngle = 0;
							robotMovingSpeed = 0;
							robotRotationSpeed = 0;
							robot.setPosition(padding + wallThickness, boardHeight/2 + padding);
							ball.setPosition(boardWidth/2 + padding, boardHeight/2 + padding);
							break;
							
							
					
							
							
					
					
						
					
				}
				
			}
			public void keyReleased(KeyEvent event) {
				
			}
			public void keyTyped(KeyEvent event) {}

				});
	}	
	
	public static void displayControls(Graphics2D g){
		g.setColor(Color.BLACK);
		g.drawString("CONTROLS:", 10, boardHeight + 2*padding + 25);
		g.drawString("Up - moves forward", 10, boardHeight + 2*padding + 50);
		g.drawString("Up - moves backwards", 10, boardHeight + 2*padding + 70);
		g.drawString("left or right - rotates robot", 10, boardHeight + 2*padding + 90);
		
		g.drawString("S - stops robot", 200, boardHeight + 2*padding + 50);
		g.drawString("ESC - exits the simulator", 200, boardHeight + 2*padding + 70);
		g.drawString("R - resets the simulation", 200, boardHeight + 2*padding + 90);
		
		
		g.drawString("INCOMING NEXT EVENING UPDATES:", 450, boardHeight + 2*padding + 25);
		g.drawString("Kicker implementation", 450, boardHeight + 2*padding + 50);
		g.drawString("Code refactoring:", 450, boardHeight + 2*padding + 70);
		
	}

	
}
