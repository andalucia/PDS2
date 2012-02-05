package group2.simulator.core;

import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.simulator.physical.Ball;
import group2.simulator.physical.Robot;
import net.phys2d.raw.World;

public class Simulator implements ServerSkeleton {

	private World world;
	private Robot robot;
	private Robot oppRobot;
	private Ball ball;
	
	private Thread driver_thread;
	
	private volatile RobotState robotState;
	
	public Simulator(World world, Robot robot, Robot oppRobot, Ball ball) {
		this.world = world;
		this.robot = robot;
		this.oppRobot = oppRobot;
		this.ball = ball;
		
		robotState = new RobotState();
		
		initDriverThread();
	}

	private void initDriverThread() {
		
		// TODO: Change to timer to control the number of updates per second
		driver_thread = new Thread() {
			public void run() {
				while (true) {
					switch (robotState.getCurrentMovement()) {
					case DO_NOTHING:
						break;
					case GOING_FORWARDS:
						robot.move(world, ball, (int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
						break;
					}
				}
			}
		};
		driver_thread.start();
	}

	@Override
	public void sendStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendGoForward(int speed, int distance) {
		// TODO: synchronize access to the RobotState
		robotState.setCurrentMovement(RobotState.Movement.GOING_FORWARDS);
		robotState.setSpeedOfTravel(speed);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void sendSpinRight(int speed, int angle) {
		// TODO Auto-generated method stub

	}

}
