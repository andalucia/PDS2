package group2.simulator.starter;

import group2.sdp.common.util.Tools;
import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.PlanExecutor;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;
import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.simulator.core.RobotState;
import group2.simulator.physical.Ball;
import group2.simulator.physical.BoardObject;
import group2.simulator.physical.Robot;
import group2.simulator.planner.TestingPlanner;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.strategies.QuadSpaceStrategy;


public class SimulatorI implements ServerSkeleton {

	/** The frame displaying the simulation */
	
	static JFrame frame;
	public static int boardWidth = 630;
	public static int boardHeight = 330;
	public static int padding = 100;
	public static int wallThickness = 20;
	public static int goalWidth = 144;
	public static int goalThickness = 50;

	private static Body leftGoalLine;
	private static Body rightGoalLine;
	public static int robotStartX = padding + wallThickness; //120
	public static int oppRobotStartX = padding + boardWidth - wallThickness; //710
	public static int ballStartX = boardWidth/2 + padding;
	public static int ballStartY = boardHeight/2 + padding;
	public static int robotStartY = boardHeight/2 + padding; //265
	private static Robot robot;
	private static Robot oppRobot;
	private static Ball ball;
	public static ComplexCommand.Type currentCommand;
	
	private boolean check =false;
	private static Checkbox reset;
	private static Button runButton;
	private static volatile RobotState robotState;

	private PlannerSkeleton planner;
	private PlanExecutor executor;
	private final Lock commandLock = new ReentrantLock();
	
	
	private static Boolean isGoal;

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

	public static void main(String args []) {
		prepareSimulator(); // this function will create and start the simulator and create a planner executor for the simulator
		initializeArea(); // this function will initialize the GUI frame, set controls for the keyboard
						// and will initialise the world where the robots and the ball will "perform" 
	}

	/**
	 * Fully initialises the simulator constructor, together with the timer
	 * that updates the RobotState (every one second-for now)
	 * @param title
	 * @param robot
	 * @param oppRobot
	 * @param ball
	 */
	public SimulatorI(World world, Robot robot, Robot oppRobot, Ball ball) {
		SimulatorI.world = world;
		SimulatorI.robot = robot;
		SimulatorI.oppRobot = oppRobot;
		SimulatorI.ball = ball;

		SimulatorI.robotState = new RobotState();
		
		executor = new PlanExecutor(this);
		
		//System.out.println("initial speed of travel for robot state is" + SimulatorI.robotState.getSpeedOfTravel());
		
		Timer imageGrabberTimer = new Timer();
		imageGrabberTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				DynamicPitchInfo dpi = generateDynamicInfo();
				executor.execute(
					new ReachDestinationCommand(
						dpi.getBallInfo().getPosition(), 
						dpi.getAlfieInfo().getPosition(), 
						dpi.getAlfieInfo().getFacingDirection()
					)
				);
			}
		}, 0, 100);
		
		Timer timeSimulatorTimer = new Timer();
		timeSimulatorTimer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				//System.out.println(SimulatorI.robotState.getCurrentMovement());
			    
			    switch (SimulatorI.robotState.getCurrentMovement()) {
				case DO_NOTHING:
					break;
				case GOING_FORWARD:
					SimulatorI.robot.moveForwards(SimulatorI.world, SimulatorI.ball.getBody() /* / CM_PER_PIXEL / TIMESTEP */);
				
					break;
				case GOING_BACKWARDS:
					SimulatorI.robot.moveBackward(SimulatorI.world, SimulatorI.ball.getBody() /* / CM_PER_PIXEL / TIMESTEP */);
					break;
				case KICK:
					SimulatorI.robot.kick(SimulatorI.ball);
					break;
				case SPIN_RIGHT:
					SimulatorI.robot.turn((int)SimulatorI.robotState.getAngleOfRotation());
					System.out.println("!!!! angle to rotate is  " +(int)SimulatorI.robotState.getAngleOfRotation());
					break;
				case SPIN_LEFT:
					SimulatorI.robot.turn((int)SimulatorI.robotState.getAngleOfRotation());
					System.out.println("!!!to rotate is  " +(int)SimulatorI.robotState.getAngleOfRotation());
					break;
				}
			  }
			}, 0, 100);
		
		
	}
	
	private DynamicPitchInfo generateDynamicInfo() {
		long start = System.currentTimeMillis();
		DynamicBallInfo dball = new DynamicBallInfo(ball.getPosition(), 0, 0,start);
		DynamicRobotInfo dalfie = new DynamicRobotInfo(robot.getPosition(), robot.getFacingDirection(), true, 0, 0,start);
		DynamicRobotInfo dopp = new DynamicRobotInfo(oppRobot.getPosition(), oppRobot.getFacingDirection(), false, 0, 0,start);
		DynamicPitchInfo dpi = new DynamicPitchInfo(dball, dalfie, dopp);
		return dpi;
	}

	/**
	 * Create a simulator for our world
	 * Load the images that are going to be used for the robots' appearance 
	 */
	public static void prepareSimulator() {

		BufferedImage blueImage = loadImage("data/blueRobot.jpeg");
		BufferedImage yellowImage = loadImage("data/yellowRobot.jpeg");

		int newRobotStartX = robotStartX;
		int newOppRobotStartX = oppRobotStartX;
		int newBallStartX = ballStartX;

	
		SimulatorI simulatoor = new SimulatorI (world,new Robot(newRobotStartX, robotStartY , 70, 50, Color.BLUE, blueImage, 0),
				new Robot(newOppRobotStartX, robotStartY, 70, 50, Color.YELLOW, yellowImage, 180),
				new Ball(newBallStartX, ballStartY+30, 10, Color.RED, 15));
		System.out.println("simulator created");
	}

	/**
	 * Put the foundations for the simulator
	 */
	public static void initializeArea(){
		initializeFrame(); // initialize the GUI
		setControls();
		initSimulation();
		
		while(running)
		{
			  // initialise the simulator

			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.pink);

			g.fillRect(0,0,(boardWidth + 2*padding),(boardHeight + 2*padding));
			BoardObject.draw(g, world);  // draw the object in the world
			displayControlsAndScore(g);
			strategy.show();
			for (int i=0;i<5;i++) {
				world.step();
			}
			checkForGoal();
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
		
		frame = new JFrame();
		frame.setLayout(null);  
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setSize((boardWidth + 2*padding), (boardHeight + 2*padding)+100);
		frame.setTitle("Alfie Simulator");
		frame.setVisible(true);
		frame.setFocusable(true);
		
		
		//Me messing with buttons, no need for them now
		
		/*reset = new Checkbox();
		
		reset.setLabel("Reset");
		reset.setBounds(10, 30, 160, 25);
		reset.setState(false);
		frame.getContentPane().add(reset);
		
		runButton = new Button();
		runButton.setLabel("click");
		runButton.setBounds(42, 208, 100, 25);
		frame.add(runButton);
		runButton.setFocusable(false);
		runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetSimulation();
				
			}
		});*/
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				running = false;
				System.exit(0);
			}
			public void windowClosed(WindowEvent arg0) {
	            	
	        }
			public void windowOpened(WindowEvent arg0) {
            	
            }
		});
		

		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();

	}

	/**
	 * Initialise the simulator and place the robots and the ball
	 */
	private static void initSimulation() {
		world.clear();
		world.setGravity(0, 0);
		ball.stop();

		float newOppRobotStartX = oppRobot.getX();
		float newOppRobotStartY = oppRobot.getY();
		
		float newRobotStartX = robot.getX();
		float newRobotStartY = robot.getY();
		
		isGoal = false;
		
		oppRobot.setPosition(newOppRobotStartX, newOppRobotStartY);
		robot.setPosition(newRobotStartX, newRobotStartY);
		ball.setPosition(ball.getX(), ballStartY);
		
		
		
		//ball.performKickedBallMovements();
		//goalCheck();
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
	

	/**
	 * Set the command for the key controls -- oppRobot controlled by the keyboard
	 */
	public static void setControls()
	{
		frame.setFocusable(true);
		frame.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent event) {
				switch(event.getKeyCode()){
					case KeyEvent.VK_ESCAPE :
							System.exit(0);
							break;
					case KeyEvent.VK_UP :	
							oppRobot.moveForwards(world, ball.getBody());
							break;
					case KeyEvent.VK_DOWN :	
							oppRobot.moveBackward(world, ball.getBody());
							break;
					case KeyEvent.VK_RIGHT:
							oppRobot.turn(10);
							break;
					case KeyEvent.VK_LEFT:
							oppRobot.turn(-10);					
							break;
					case KeyEvent.VK_R:
							// Resetting Simulation
							resetSimulation();
							break;
					case KeyEvent.VK_ENTER:
							oppRobot.kick(ball);
							break;
					
				}

			}
			@Override
			public void keyReleased(KeyEvent event) {

			}
			public void keyTyped(KeyEvent event) {
				
			}

		});
	}
	
	
	
	public static void resetSimulation(){
		oppRobot.setAngle(180);
		oppRobot.setPosition(padding + boardWidth - wallThickness, boardHeight/2 + padding);
		ball.setPosition(boardWidth/2 + padding, boardHeight/2 + padding);
		robot.setPosition(padding + wallThickness, robotStartY);
		ball.ignoreGoalLines();
		ball.stop();
		
		
	}

	public static void displayControlsAndScore(Graphics2D g){
		g.setColor(Color.BLACK);
		g.drawString("CONTROLS:", 10, boardHeight + 2*padding + 25);
		g.drawString("Up - moves forward", 10, boardHeight + 2*padding + 50);
		g.drawString("Up - moves backwards", 10, boardHeight + 2*padding + 70);
		g.drawString("left or right - rotates robot", 10, boardHeight + 2*padding + 90);

		g.drawString("Enter - kick the ball", 200, boardHeight + 2*padding + 25);
		g.drawString("S - stops robot", 200, boardHeight + 2*padding + 50);
		g.drawString("ESC - exits the simulator", 200, boardHeight + 2*padding + 70);
		g.drawString("R - resets the simulation", 200, boardHeight + 2*padding + 90);

		g.drawString("INCOMING NEXT EVENING UPDATES:", 450, boardHeight + 2*padding + 25);
		g.drawString("Ability to score a goal", 450, boardHeight + 2*padding + 50);
		g.drawString("Ball bounces to wall and obstacles:", 450, boardHeight + 2*padding + 70);
		g.drawString("Score Goal Feature", 450, boardHeight + 2*padding + 90);
		
		Font score = new Font("Book Antiqua", Font.BOLD, 40);
		g.setFont(score);
		g.drawString(robot.getScore()+ " : " + oppRobot.getScore(), 350, 65);
		
	
	}
	
	public static void checkForGoal(){
		if(isGoal == false)
		{
			if (ball.getX() < (padding)){
				oppRobot.incrScore();
				ball.stayInGoal();
				isGoal = true;
			}
		else if ((ball.getX() > (padding + boardWidth))){
			robot.incrScore();
			ball.stayInGoal();
			isGoal = true;
			}
		}
	}
		
	
	@Override
	public void sendStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendGoForward(int speed, int distance) {
		while (!commandLock.tryLock());
		
		speed = convertSpeed(speed);
		robotState.setCurrentMovement(RobotState.Movement.GOING_FORWARD);
		robotState.setSpeedOfTravel(speed);
		commandLock.unlock();
	}

	private int convertSpeed(int speed) {
		return Tools.sanitizeInput(speed, 0, 54);
	}

	@Override
	public void sendGoBackwards(int speed, int distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendKick(int power) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendSpinLeft(int speed, int angle) {
		while (!commandLock.tryLock());
		robotState.setAngleOfRotation(angle);
		robotState.setCurrentMovement(RobotState.Movement.SPIN_LEFT);
		
//		System.out.println("robot state current movement is " + RobotState.getCurrentMovement());
		robot.turn(angle);
		commandLock.unlock();
	}

	@Override
	public void sendSpinRight(int speed, int angle) {
		while (!commandLock.tryLock());
		
		robotState.setAngleOfRotation(-angle);
		robotState.setCurrentMovement(RobotState.Movement.SPIN_RIGHT);
//		System.out.println("robot state current movement is " + RobotState.getCurrentMovement());
		robot.turn(-angle);
		commandLock.unlock();
	}
	
	


}
