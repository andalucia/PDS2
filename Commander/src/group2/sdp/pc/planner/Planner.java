package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
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

	@Override
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		if (!super.commandSuccessful(dpi)) {
			//TODO: implement here and remove return false;
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected boolean problemExists(DynamicPitchInfo dpi) {
		if (!super.problemExists(dpi)) {
			//TODO: implement here and remove return false;
			return false;
		} else {
			return true;
		}
	}
}
