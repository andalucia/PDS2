package group2.simulator.starter;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.strategies.QuadSpaceStrategy;
import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.PlanExecutorSimulatorTest;
import group2.sdp.pc.planner.PlannerSimulatorTest;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.simulator.core.RobotState;
import group2.simulator.core.Simulator;
import group2.simulator.physical.Ball;
import group2.simulator.physical.BoardObject;
import group2.simulator.physical.Robot;


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
	static Thread actionThread = new Thread() {
		public void run() {

			//System.out.println("Thread for simulator");

		}
	};

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
	public  SimulatorI( World world, Robot robot, Robot oppRobot, Ball ball) {
		SimulatorI.world = world;
		SimulatorI.robot = robot;
		SimulatorI.oppRobot = oppRobot;
		SimulatorI.ball = ball;

		SimulatorI.robotState = new RobotState();
		//System.out.println("initial speed of travel for robot state is" + SimulatorI.robotState.getSpeedOfTravel());
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public synchronized void run() {
				//System.out.println("test-stuff updated every two seconds");

				switch (SimulatorI.robotState.getCurrentMovement()) {
				case DO_NOTHING:
					break;
				case GOING_FORWARD:
					SimulatorI.robot.move(SimulatorI.world, SimulatorI.ball, (int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);

					break;
				case GOING_BACKWARDS:
					SimulatorI.robot.move(SimulatorI.world, SimulatorI.ball, -(int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
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
					System.out.println("!!!!angle caca to rotate is  " +(int)SimulatorI.robotState.getAngleOfRotation());
					break;
				}
			}
		}, 0, 2000);


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


		SimulatorI simulatoor = new SimulatorI (world,new Robot(newRobotStartX, robotStartY, 70, 50, Color.BLUE, blueImage, -190),
				new Robot(newOppRobotStartX, robotStartY, 70, 50, Color.YELLOW, yellowImage, 180),
				new Ball(newBallStartX, ballStartY+30, 10, Color.RED, 15));
		System.out.println("simulator created");


		PlanExecutorSimulatorTest executor = new PlanExecutorSimulatorTest(simulatoor);
		PlannerSimulatorTest planner = new PlannerSimulatorTest(executor);

		DynamicBallInfo dball = new DynamicBallInfo(ball.getPosition(), 0, 0);
		DynamicRobotInfo dalfie = new DynamicRobotInfo(robot.getPosition(), robot.getFacingDirection(), true, 0, 0);
		DynamicRobotInfo dopp = new DynamicRobotInfo(oppRobot.getPosition(), oppRobot.getFacingDirection(), false, 0, 0);
		DynamicPitchInfo dpi = new DynamicPitchInfo(dball, dalfie, dopp);
		ComplexCommand command = planner.planNextCommand(dpi);
		executor.execute(command);

		//executor.execute2(currentCommand.REACH_DESTINATION);


		// TO BE IMPLEMENTED!!!
		//Planner planner = new Planner(executor);
		//Bakery bakery = new Bakery(planner);
		//new SimulatorDoughProvider(bakery); 

	}

	/**
	 * Put the foundations for the simulator
	 */
	public static void initializeArea(){
		initializeFrame(); // initialize the GUI
		setControls();

		while(running)
		{
			initSimulation();  // initialise the simulator

			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.pink);

			g.fillRect(0,0,(boardWidth + 2*padding),(boardHeight + 2*padding));
			BoardObject.draw(g, world);  // draw the object in the world
			displayControlsAndScore(g);
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
				actionThread.start();

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


		float newOppRobotStartX = oppRobot.getX();
		float newOppRobotStartY = oppRobot.getY();

		float newRobotStartX = robot.getX();
		float newRobotStartY = robot.getY();


		oppRobot.setPosition(newOppRobotStartX, newOppRobotStartY);
		robot.setPosition(newRobotStartX, newRobotStartY);

		float newBallStartX = ball.getX();
		float newBallStartY = ball.getY();

		ball.setPosition(newBallStartX, newBallStartY);

		ball.performKickedBallMovements();
		goalCheck();
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



		world.add(SimulatorI.robot.getBody());
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
					oppRobot.moveForward(world, ball);
					break;
				case KeyEvent.VK_DOWN :	
					oppRobot.moveBackwards(world, ball);
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

	public static void goalCheck()
	{
		if (ball.didItScore()){
			ball.stop();
			ball.setBallKicked(false);
			ball.setFixedAngle(0);
			oppRobot.incrScore();
		}
	}

	public static void resetSimulation(){
		oppRobot.setAngle(180);
		oppRobot.setPosition(padding + boardWidth - wallThickness, boardHeight/2 + padding);
		ball.setPosition(boardWidth/2 + padding, boardHeight/2 + padding);
		ball.setBallKicked(false);
		ball.setFixedAngle(0);
		oppRobot.setScore(0);
		ball.setDistance(5);
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


		g.setFont(new Font("Book Antiqua", Font.BOLD, 50));
		g.drawString(robot.getScore() + " : " + oppRobot.getScore(), 360 ,75);

	}


	@Override
	public synchronized void sendStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void  sendGoForward(int speed, int distance) {
		robotState.setCurrentMovement(RobotState.Movement.GOING_FORWARD);
		robotState.setSpeedOfTravel(speed);

	}

	@Override
	public synchronized void sendGoBackwards(int speed, int distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void sendKick(int power) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void sendSpinLeft(int speed, int angle) {
		robotState.setAngleOfRotation(angle);
		robotState.setCurrentMovement(RobotState.Movement.SPIN_LEFT);

		System.out.println("robot state current movement is " + RobotState.getCurrentMovement());
		robot.turn(angle);

	}

	@Override
	public synchronized void sendSpinRight(int speed, int angle) {
		robotState.setAngleOfRotation(-angle);
		robotState.setCurrentMovement(RobotState.Movement.SPIN_RIGHT);
		System.out.println("robot state current movement is " + RobotState.getCurrentMovement());
		robot.turn(-angle);

	}


}
