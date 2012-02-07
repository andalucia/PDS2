package group2.simulator.core;

import java.util.Timer;
import java.util.TimerTask;

import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.simulator.physical.Ball;
import group2.simulator.physical.Robot;
import net.phys2d.raw.World;

public class Simulator  implements ServerSkeleton {

	private static World world;
	private Robot robot;
	private static Robot oppRobot;
	private static Ball ball;
	
	private Thread driver_thread;
	
	private volatile RobotState robotState;
	
	public Simulator(World world, Robot robot, Robot oppRobot, Ball ball) {
		this.world = world;
		this.robot = robot;
		this.oppRobot = oppRobot;
		this.ball = ball;
		
		robotState = new RobotState();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
			    System.out.println("test-stuff updated every two seconds");
			    initDriverThread();
			  }
			}, 0, 2000);
		
	
	}

	private void initDriverThread() {
		
		// TODO: Change to timer to control the number of updates per second
		driver_thread = new Thread() {
			public void run() {
				while (true) {
					switch (robotState.getCurrentMovement()) {
					case DO_NOTHING:
						break;
					case GOING_FORWARD:
						robot.move(world, ball, (int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
						break;
					case GOING_BACKWARDS:
						robot.move(world, ball, -(int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
						break;
					case KICK:
						if(robot.canRobotKick(ball)){
							robot.kick(ball);
						}
						break;
					case SPIN_RIGHT:
						robot.turn(-(int)robotState.getAngleOfRotation());
						break;
					case SPIN_LEFT:
						robot.turn((int)robotState.getAngleOfRotation());
						break;
					}
					
				}
			}
		};
		driver_thread.start();
	}

	@Override
	public void sendStop() {
		robotState.setCurrentMovement(RobotState.Movement.DO_NOTHING);
	}

	@Override
	public void sendGoForward(int speed, int distance) {
		// TODO: synchronize access to the RobotState
		robotState.setCurrentMovement(RobotState.Movement.GOING_FORWARD);
		robotState.setSpeedOfTravel(speed);
	}

	@Override
	public void sendGoBackwards(int speed, int distance) {
		robotState.setCurrentMovement(RobotState.Movement.GOING_BACKWARDS);
		robotState.setSpeedOfTravel(-speed);

	}

	@Override
	public void sendKick(int power) {
		robotState.setCurrentMovement(RobotState.Movement.KICK);
		robotState.setPowerOfKick(power);

	}

	@Override
	public void sendSpinLeft(int speed, int angle) {
		robotState.setCurrentMovement(RobotState.Movement.SPIN_LEFT);
		robotState.setAngleOfRotation(angle);

	}

	@Override
	public void sendSpinRight(int speed, int angle) {
		robotState.setCurrentMovement(RobotState.Movement.SPIN_RIGHT);
		robotState.setAngleOfRotation(-angle);

	}


}
