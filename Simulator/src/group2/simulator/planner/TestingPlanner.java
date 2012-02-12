package group2.simulator.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.PlanExecutor;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

public class TestingPlanner extends PlannerSkeleton {

	public TestingPlanner(PlanExecutor executor) {
		super(executor);
	}

	@Override
	protected ComplexCommand planNextCommand(DynamicPitchInfo dpi) {
		return new ReachDestinationCommand(
				new Point2D.Double(100, 0), 
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection());
	}

	@Override
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		return false;
	}

	@Override
	protected boolean problemExists(DynamicPitchInfo dpi) {
		return false;
	}
	
	

}
