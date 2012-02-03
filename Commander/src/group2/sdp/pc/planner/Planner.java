package group2.sdp.pc.planner;

import group2.sdp.common.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

public class Planner extends PlannerSkeleton {

	public Planner(PlanExecutor executor) {
		super(executor);
	}

	@Override
	protected ComplexCommand planNextCommand(DynamicPitchInfo dpi) {
		// TODO Auto-generated method stub
		return null;
	}

}
