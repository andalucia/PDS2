package group2.simulator.starter;

import group2.sdp.common.util.Tools;

import group2.simulator.physical.Ball;
import group2.simulator.physical.BoardObject;
import group2.simulator.physical.Robot;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

public class SimulatorI {

	/** The frame displaying the simulation */

	public static JFrame frame;
	public static int boardWidth = 470;
	public static int boardHeight = 350;
	public static int padding = 90;
	public static int wallThickness = 20;
	public static int goalWidth = 144;
	public static int goalThickness = 30;

	private static Body leftGoalLine;
	private static Body rightGoalLine;
	public static int robotStartX = padding + wallThickness; // 120
	public static int oppRobotStartX = padding + boardWidth - wallThickness; // 710
	public static int ballStartX = boardWidth / 2 + padding;
	public static int ballStartY = boardHeight / 2 + padding;
	public static int robotStartY = boardHeight / 2 + padding; // 265
	private static Robot robot;
	private static Robot oppRobot;
	private static Ball ball;
	// public static Operation.Type currentCommand;
	//
	private boolean check = false;
	private static Checkbox reset;
	private static Button runButton;

	private static Graphics2D ggg;

	private BufferedImage Frame = new BufferedImage(640, 480,
			BufferedImage.TYPE_3BYTE_BGR);

	public static final Color pitchColor = new Color(0, 150, 0);

	private static Boolean isGoal;

	private static Robot selectRobot;
	private static Ball selectBall;
	private static Boolean robotDragged = false;
	private static Boolean ballDragged = false;

	/** The title of the simulation */
	private String title;
	/** True if the simulation is running */
	private static boolean running = true;

	/** The world containing the physics model */
	private static World world = new World(new Vector2f(0.0f, 10.0f), 10,
			new QuadSpaceStrategy(20, 5));
	/** The rendering strategy */
	private static BufferStrategy strategy;

	public static int goalPost1y = (boardHeight - goalWidth) / 2 + padding
			- wallThickness / 2;
	public static int goalPost2y = (boardHeight + goalWidth) / 2 + padding
			+ wallThickness / 2;

	/**
	 * Fully initialises the simulator constructor, together with the timer that
	 * updates the RobotState (every one second-for now)
	 * 
	 * @param title
	 * @param robot
	 * @param oppRobot
	 * @param ball
	 */
	public SimulatorI() {

		prepareSimulator(); // this function will create and start the simulator
							// and create a planner executor for the simulator
		initializeArea(); // this function will initialize the GUI frame, set
							// controls for the keyboard
							// and will initialise the world where the robots
							// and the ball will "perform"

	}

	/**
	 * Create a simulator for our world Load the images that are going to be
	 * used for the robots' appearance
	 */
	public static void prepareSimulator() {

		BufferedImage blueImage = loadImage("../Simulator/data/blueRobot3.jpeg");
		BufferedImage yellowImage = loadImage("../Simulator/data/yellowRobot2.jpeg");

		int newRobotStartX = robotStartX;
		int newOppRobotStartX = oppRobotStartX;
		int newBallStartX = ballStartX;

		SimulatorI.oppRobot = new Robot(newRobotStartX, robotStartY, 50, 36,
				Color.BLUE, blueImage, 0);
		SimulatorI.robot = new Robot(newOppRobotStartX, robotStartY, 50, 36,
				Color.YELLOW, yellowImage, 180);
		SimulatorI.ball = new Ball(newBallStartX, ballStartY, 7, Color.RED, 15);
		System.out.println("simulator created");

	}

	private Thread runSim = new Thread() {
		public void run() {
			// int x = 1;
			while (running) {
				// initialise the simulator
				BufferedImage nextFrame = new BufferedImage(640, 480,
						BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g2 = (Graphics2D) nextFrame.createGraphics();

				g2.setColor(pitchColor);

				g2.fillRect(0, 0, (boardWidth + 2 * padding),
						(boardHeight + 2 * padding));

				BoardObject.draw(g2, world); // draw the object in the world

				displayControlsAndScore(g2);

				checkForGoal();

				Frame = nextFrame;

				ggg.drawImage(nextFrame, null, 0, 0);
				for (int i = 0; i < 5; i++) {
					world.step();
				}
				// System.out.println("a");
				/*
				 * try { // // retrieve image if(x == 1){ x++; File outputfile =
				 * new File("background.png"); ImageIO.write(nextFrame, "png",
				 * outputfile); } } catch (IOException e) {
				 * 
				 * }
				 * 
				 * try { Thread.sleep(1); } catch (InterruptedException e) {
				 * System.out.println("interrupted"); }
				 */

			}
		}
	};

	/**
	 * Put the foundations for the simulator
	 */
	public void initializeArea() {
		initializeFrame(); // initialize the GUI
		setControls();
		initSimulation();
		runSim.start();
	}

	/**
	 * Initialize the GUI
	 */
	private static void initializeFrame() {

		frame = new JFrame();
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setSize(640, 480);
		frame.setTitle("Alfie Simulator");
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Me messing with buttons, no need for them now

		/*
		 * reset = new Checkbox();
		 * 
		 * reset.setLabel("Reset"); reset.setBounds(10, 30, 160, 25);
		 * reset.setState(false); frame.getContentPane().add(reset);
		 * 
		 * runButton = new Button(); runButton.setLabel("click");
		 * runButton.setBounds(42, 208, 100, 25); frame.add(runButton);
		 * runButton.setFocusable(false); runButton.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * resetSimulation();
		 * 
		 * } });
		 */

		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				running = false;
				System.exit(0);
			}

			public void windowClosed(WindowEvent arg0) {
				System.exit(0);
			}

			public void windowOpened(WindowEvent arg0) {

			}
		});

		ggg = (Graphics2D) frame.getGraphics();
		strategy = frame.getBufferStrategy();

	}

	/**
	 * Initialise the simulator and place the robots and the ball
	 */
	private static void initSimulation() {
		world.clear();
		world.setGravity(0, 0);
		// ball.stop();

		float newOppRobotStartX = oppRobot.getX();
		float newOppRobotStartY = oppRobot.getY();

		float newRobotStartX = robot.getX();
		float newRobotStartY = robot.getY();

		isGoal = false;

		oppRobot.setPosition(newOppRobotStartX, newOppRobotStartY);
		robot.setPosition(newRobotStartX, newRobotStartY);
		ball.setPosition(ball.getX(), ballStartY);

		// ball.performKickedBallMovements();
		// goalCheck();
		init(world);
	}

	/**
	 * Initialize the world in the simulator
	 */
	private static void init(World world) {
		world.setGravity(0, 0);

		// Set up top and bottom walls
		Body topWall = new StaticBody("TopWall", new Box(
				(boardWidth + 2 * wallThickness), wallThickness));
		topWall.setPosition((boardWidth / 2 + padding),
				(padding - wallThickness / 2));
		topWall.setRestitution(1.0f);
		world.add(topWall);

		Body bottomWall = new StaticBody("BottomWall", new Box(
				(boardWidth + 2 * wallThickness), wallThickness));
		bottomWall.setPosition((boardWidth / 2 + padding), (boardHeight
				+ padding + wallThickness / 2));
		bottomWall.setRestitution(1.0f);
		world.add(bottomWall);

		// Set up left wall and goal
		Body topLeftWall = new StaticBody("TopLeftWall", new Box(wallThickness,
				((boardHeight - goalWidth) / 2 + wallThickness)));
		topLeftWall.setPosition((padding - wallThickness / 2), ((goalPost1y
				+ padding - (wallThickness / 2)) / 2));
		topLeftWall.setRestitution(1.0f);
		world.add(topLeftWall);
		Body bottomLeftWall = new StaticBody("BottomLeftWall", new Box(
				wallThickness, ((boardHeight - goalWidth) / 2 + wallThickness)));
		bottomLeftWall.setPosition((padding - wallThickness / 2), ((padding
				+ boardHeight + (wallThickness / 2) + goalPost2y) / 2));
		bottomLeftWall.setRestitution(1.0f);
		world.add(bottomLeftWall);
		Body topLeftGoal = new StaticBody("TopLeftGoal", new Box(
				(goalThickness + 2 * wallThickness), wallThickness));
		topLeftGoal.setPosition((padding - goalThickness / 2 - wallThickness),
				goalPost1y);
		topLeftGoal.setRestitution(1.0f);
		world.add(topLeftGoal);
		Body bottomLeftGoal = new StaticBody("BottomLeftGoal", new Box(
				(goalThickness + 2 * wallThickness), wallThickness));
		bottomLeftGoal.setPosition(
				(padding - goalThickness / 2 - wallThickness), goalPost2y);
		bottomLeftGoal.setRestitution(1.0f);
		world.add(bottomLeftGoal);
		Body backLeftGoal = new StaticBody("BackLeftGoal", new Box(
				wallThickness, (goalWidth + 2 * wallThickness)));
		backLeftGoal.setPosition(
				(padding - goalThickness - 3 * wallThickness / 2),
				(padding + boardHeight / 2));
		backLeftGoal.setRestitution(1.0f);
		world.add(backLeftGoal);

		// Set up right wall and goal
		Body topRightWall = new StaticBody("TopRightWall", new Box(
				wallThickness, ((boardHeight - goalWidth) / 2 + wallThickness)));
		topRightWall.setPosition((boardWidth + padding + wallThickness / 2),
				((goalPost1y + padding - (wallThickness / 2)) / 2));
		topRightWall.setRestitution(1.0f);
		world.add(topRightWall);
		Body bottomRightWall = new StaticBody("BottomRightWall", new Box(
				wallThickness, ((boardHeight - goalWidth) / 2 + wallThickness)));
		bottomRightWall
				.setPosition(
						(boardWidth + padding + wallThickness / 2),
						((padding + boardHeight + (wallThickness / 2) + goalPost2y) / 2));
		bottomRightWall.setRestitution(1.0f);
		world.add(bottomRightWall);
		Body topRightGoal = new StaticBody("TopRightGoal", new Box(
				(goalThickness + 2 * wallThickness), wallThickness));
		topRightGoal.setPosition(
				(boardWidth + padding + goalThickness / 2 + wallThickness),
				goalPost1y);
		topRightGoal.setRestitution(1.0f);
		world.add(topRightGoal);
		Body bottomRightGoal = new StaticBody("BottomRightGoal", new Box(
				(goalThickness + 2 * wallThickness), wallThickness));
		bottomRightGoal.setPosition(
				(boardWidth + padding + goalThickness / 2 + wallThickness),
				goalPost2y);
		bottomRightGoal.setRestitution(1.0f);
		world.add(bottomRightGoal);
		Body backRightGoal = new StaticBody("BackRightGoal", new Box(
				wallThickness, (goalWidth + 2 * wallThickness)));
		backRightGoal.setPosition(
				(boardWidth + padding + goalThickness + 3 * wallThickness / 2),
				(padding + boardHeight / 2));
		backRightGoal.setRestitution(1.0f);
		world.add(backRightGoal);

		leftGoalLine = new StaticBody("BackLeftGoal", new Box(1,
				(goalWidth + 2 * wallThickness)));
		leftGoalLine.setPosition((padding), (padding + boardHeight / 2));
		leftGoalLine.setRestitution(1.0f);
		world.add(leftGoalLine);
		rightGoalLine = new StaticBody("BackRightGoalrobot.setAngle(180);",
				new Box(1, (goalWidth + 2 * wallThickness)));
		rightGoalLine.setPosition((boardWidth + padding + 1),
				(padding + boardHeight / 2));
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
	 * Set the command for the key controls -- oppRobot controlled by the
	 * keyboard
	 */
	public static void setControls() {
		frame.setFocusable(true);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
					break;
				case KeyEvent.VK_UP:
					robot.moveForward(world, ball.getBody());
					break;
				case KeyEvent.VK_DOWN:
					robot.moveBackward(world, ball.getBody());
					break;
				case KeyEvent.VK_RIGHT:
					robot.turn(-3);
					break;
				case KeyEvent.VK_LEFT:
					robot.turn(3);
					break;
				case KeyEvent.VK_R:
					resetSimulation();
					break;
				case KeyEvent.VK_ENTER:
					robot.kick(ball);
					break;
				case KeyEvent.VK_1:
					arc(50, 90);
					break;
				}

			}

			@Override
			public void keyReleased(KeyEvent event) {

			}

			public void keyTyped(KeyEvent event) {

			}

		});
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (calcDistanceBetweenPoints(robot.getPosition(), e.getPoint()) < 10) {
					selectRobot = robot;
					robotDragged = true;
				}
				if (calcDistanceBetweenPoints(oppRobot.getPosition(),
						e.getPoint()) < 10) {
					selectRobot = oppRobot;
					robotDragged = true;
				}
				if (calcDistanceBetweenPoints(ball.getPosition(), e.getPoint()) < 10) {
					selectBall = ball;
					ballDragged = true;
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (robotDragged)
					robotDragged = false;
				if (ballDragged)
					ballDragged = false;
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if (robotDragged)
					selectRobot.setPosition(arg0.getPoint().x,
							arg0.getPoint().y);

				if (ballDragged)
					selectBall.setPosition(arg0.getPoint().x, arg0.getPoint().y);
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static void resetSimulation() {
		oppRobot.setAngle(180);
		oppRobot.setPosition(padding + boardWidth - wallThickness, boardHeight
				/ 2 + padding);
		ball.setPosition(boardWidth / 2 + padding, boardHeight / 2 + padding);
		robot.setPosition(padding + wallThickness, robotStartY);
		ball.ignoreGoalLines();
		ball.stop();

	}

	public static void displayControlsAndScore(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawString("CONTROLS:", 10, boardHeight + 2 * padding + 25);
		g.drawString("Up - moves forward", 10, boardHeight + 2 * padding + 50);
		g.drawString("Up - moves backwards", 10, boardHeight + 2 * padding + 70);
		g.drawString("left or right - rotates robot", 10, boardHeight + 2
				* padding + 90);

		g.drawString("Enter - kick the ball", 200, boardHeight + 2 * padding
				+ 25);
		g.drawString("S - stops robot", 200, boardHeight + 2 * padding + 50);
		g.drawString("ESC - exits the simulator", 200, boardHeight + 2
				* padding + 70);
		g.drawString("R - resets the simulation", 200, boardHeight + 2
				* padding + 90);

		g.drawString("INCOMING NEXT EVENING UPDATES:", 450, boardHeight + 2
				* padding + 25);
		g.drawString("Ability to score a goal", 450, boardHeight + 2 * padding
				+ 50);
		g.drawString("Ball bounces to wall and obstacles:", 450, boardHeight
				+ 2 * padding + 70);
		g.drawString("Score Goal Feature", 450, boardHeight + 2 * padding + 90);

		Font score = new Font("Book Antiqua", Font.BOLD, 40);
		g.setFont(score);
		// g.drawString(robot.getScore() + " : " + oppRobot.getScore(), 280,
		// 65);

	}

	public static void checkForGoal() {
		if (isGoal == false) {
			if (ball.getX() < (padding)) {
				oppRobot.incrScore();
				ball.stayInGoal();
				isGoal = true;
			} else if ((ball.getX() > (padding + boardWidth))) {
				ball.stayInGoal();
				isGoal = true;
			}
		}
	}

	public static void goForward() {
		robot.moveForward(world, ball.getBody());
	}

	public static void goBackwards() {
		robot.moveBackward(world, ball.getBody());
	}

	public static void spinLeft(int angle) {
		robot.turn(-angle);
	}

	public static void spinRight(int angle) {
		robot.turn(angle);
	}

	/**
	 * arc <br\>
	 * |radius | angle | arc | <br\>
	 * | + | + | FL | <br\>
	 * | + | - | BL | <br\>
	 * | - | + | BR | <br\>
	 * | - | - | FR | <br\>
	 * 
	 */

	public static void arc(double radius, double angle) {
		boolean isLeft = false;
		double rotateAngle = 0;
		double turn = 1;
		if (radius > 0 && angle > 0) {
			// FL
			isLeft = true;
			rotateAngle = -1;
			turn = 1;
		} else if (radius > 0 && angle < 0) {
			isLeft = true;
			rotateAngle = 1;
			turn = -1;
		} else if (radius < 0 && angle > 0) {
			// BR
			isLeft = false;
			rotateAngle = -1;
			turn = 1;
		} else if (radius < 0 && angle < 0) {
			// FR
			isLeft = false;
			rotateAngle = 1;
			turn = -1;
		}

		radius = Math.abs(radius);
		angle = Math.abs(angle);
		Point2D circleCentre = calCircleCentre(robot.getPosition(), radius,
				robot.getAngle(), 90, isLeft);
		int angleCounter = 0;
		while (angleCounter < angle) {
			angleCounter++;
			Point2D nextPostition = calPoint(robot.getPosition(), circleCentre,
					rotateAngle);
			robot.setPosition((float) nextPostition.getX(),
					(float) nextPostition.getY());
			robot.turn((int) (turn * needRotateAngle(robot.getPosition(),
					circleCentre, robot.getAngle())));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private int convertSpeed(int speed) {
		return Tools.sanitizeInput(speed, 0, 54);
	}

	public BufferedImage getNextFrame() {
		return Frame;
	}

	public static double calcDistanceBetweenPoints(Point2D p1, Point p2) {
		return Math.sqrt((double) (Math.pow(p1.getX() - p2.x, 2) + (Math.pow(
				p1.getY() - p2.y, 2))));
	}

	public static void placeRobot() {
		world.add(robot.getBody());
		world.add(oppRobot.getBody());
		world.add(ball.getBody());
	}

	public static void clearPitch() {
		world.remove(robot.getBody());
		world.remove(oppRobot.getBody());
		world.remove(ball.getBody());
	}

	public static Point2D nextPosition(Point2D robot, double radius,
			double angle, boolean isLeft) {

		double x = Math.cos(Math.toRadians(1)) * radius + robot.getX();
		double y = Math.sin(Math.toRadians(1)) * radius + robot.getY();

		return new Point2D.Double(x, y);
	}

	public static Point2D calCircleCentre(Point2D robot, double radius,
			double robotDirection, double rotateAngle, boolean isLeft) {

		double angle = isLeft ? robotDirection - rotateAngle : robotDirection
				+ rotateAngle;

		double x = Math.cos(Math.toRadians(angle)) * radius + robot.getX();
		double y = Math.sin(Math.toRadians(angle)) * radius + robot.getY();

		return new Point2D.Double(x, y);
	}

	public static Point2D calPoint(Point2D origin, Point2D round, double angle) {
		double x = origin.getX() - round.getX();
		double y = origin.getY() - round.getY();
		double newX = x * Math.cos(Math.toRadians(angle)) - y
				* Math.sin(Math.toRadians(angle));
		double newY = x * Math.sin(Math.toRadians(angle)) + y
				* Math.cos(Math.toRadians(angle));
		newX += round.getX();
		newY += round.getY();
		return new Point2D.Double(newX, newY);

	}

	public static double needRotateAngle(Point2D robot, Point2D circle,
			double robotDirection) {

		double m = (robot.getX() - circle.getX())
				/ (robot.getY() - circle.getY());
		double m1 = 1 / m;
		double m2 = Math.tan(Math.toRadians(robotDirection));

		return Math.abs(Math.toDegrees(Math.atan(((m2 - m1) / (1 + m2 * m1)))));

	}

}