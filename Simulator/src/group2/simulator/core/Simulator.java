package group2.simulator.core;

import java.util.Timer;
import java.util.TimerTask;

import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.simulator.physical.Ball;
import group2.simulator.physical.Robot;
import group2.simulator.starter.SimulatorStarter;
import net.phys2d.raw.World;

public class Simulator  implements ServerSkeleton {

	private static World world;
	private static Robot robot;
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
			    
			    switch (robotState.getCurrentMovement()) {
				case DO_NOTHING:
					break;
				case GOING_FORWARD:
					Simulator.robot.move(Simulator.world, Simulator.ball, (int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
					break;
				case GOING_BACKWARDS:
					Simulator.robot.move(Simulator.world, Simulator.ball, -(int)robotState.getSpeedOfTravel() /* / CM_PER_PIXEL / TIMESTEP */);
					break;
				case KICK:
					Simulator.robot.kick(Simulator.ball);// TODO: to move function in Robot class!
					break;
				case SPIN_RIGHT:
					Simulator.robot.turn(-(int)robotState.getAngleOfRotation());
					break;
				case SPIN_LEFT:
					Simulator.robot.turn((int)robotState.getAngleOfRotation());
					break;
				}
			  }
			}, 0, 2000);
		
	
	}



	@Override
	public synchronized void sendStop() {
		robotState.setCurrentMovement(RobotState.Movement.DO_NOTHING);
	}

	@Override
	public synchronized void sendGoForward(int speed, int distance) {
		robotState.setCurrentMovement(RobotState.Movement.GOING_FORWARD);
		robotState.setSpeedOfTravel(speed);
	}

	@Override
	public synchronized void sendGoBackwards(int speed, int distance) {
		robotState.setCurrentMovement(RobotState.Movement.GOING_BACKWARDS);
		robotState.setSpeedOfTravel(-speed);

	}

	@Override
	public synchronized void sendKick(int power) {
		robotState.setCurrentMovement(RobotState.Movement.KICK);
		robotState.setPowerOfKick(power);

	}

	@Override
	public synchronized void sendSpinLeft(int speed, int angle) {
		robotState.setCurrentMovement(RobotState.Movement.SPIN_LEFT);
		robotState.setAngleOfRotation(angle);

	}

	@Override
	public synchronized void sendSpinRight(int speed, int angle) {
		robotState.setCurrentMovement(RobotState.Movement.SPIN_RIGHT);
		robotState.setAngleOfRotation(-angle);

	}


}
