package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.DribbleCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

import java.awt.geom.Point2D;

/**
 * The planner to use for milestone 2.
 */
public class Milestone2Planner extends PlannerSkeleton {

	private boolean verbose = false;

	/**
	 * The distance from the ball at which Alfie should stop. 
	 */
	private final int CLOSE_DISTANCE = 20;
	
	/**
	 * Possible modes for the planner. Set manually.
	 */
	public enum Mode {
		DRIBBLE,
		GET_TO_BALL
	}
	
	/**
	 * The mode of the planner.
	 */
	private Mode currentMode;	


	/**
	 * See parent.
	 */
	public Milestone2Planner(PlanExecutor executor) {
		super(executor);
	}

 
	/**
	 * Get the mode of the planner.
	 * @return The mode of the planner.
	 */
	public Mode getCurrentMode() {
		return currentMode;
	}

	/**
	 * Set the mode of the planner. 
	 * @param currentMode The mode of the planner.
	 */
	public void setCurrentMode(Mode currentMode) {
		this.currentMode = currentMode;
	}
	

	/**
	 * Checks the mode of the planner, constructs the appropriate command.
	 */
	@Override
	protected ComplexCommand planNextCommand(DynamicPitchInfo dpi) {
		if (verbose) {
			System.out.println("Planning next command.");
		}

		DynamicRobotInfo AlfieInfo = dpi.getAlfieInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();

		Point2D ball = ballInfo.getPosition();
		Point2D alfie = AlfieInfo.getPosition();
		double facing = AlfieInfo.getFacingDirection();
		
		if (currentMode == null) {
			System.err.println("No current mode. Exiting.");
			System.exit(1);
			return null;
		}
		switch (currentMode) {
		case GET_TO_BALL:
			if (currentCommand instanceof ReachDestinationCommand && commandSuccessful(dpi)) {
				stop();
				return null;
			} else {
				ReachDestinationCommand cmd = new ReachDestinationCommand(ball, alfie, facing);
				return cmd;
			}
			
		case DRIBBLE:
			return new DribbleCommand(ball, alfie, facing);

		default:
			System.err.println("No current mode. Exiting.");
			System.exit(1);
			return null;
		}
	}

	@Override
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		boolean b = super.commandSuccessful(dpi);
		double distance;
		if (!b) {
			switch (currentMode) {
			case GET_TO_BALL:
				distance = dpi.getBallInfo().getPosition().distance(
						dpi.getAlfieInfo().getPosition());
				if (verbose) {
					System.out.println("Distance to the ball: " + distance);
				}
				return distance < CLOSE_DISTANCE;
			case DRIBBLE:
				distance = dpi.getBallInfo().getPosition().distance(
						dpi.getAlfieInfo().getPosition());
				return distance < CLOSE_DISTANCE;
			default:
				System.err.println("No current mode. Exiting.");
				System.exit(1);
				return true;
			}
		} else {
			return b;
		}
	}

	@Override
	protected boolean problemExists(DynamicPitchInfo dpi) {
		boolean b = super.problemExists(dpi);
		if (!b) {
			
			return false;
		} else {
			return b;
		}
	}
}
