package group2.simulator.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.Overlord;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;

public class TestingPlanner extends Overlord {

	public TestingPlanner(PathFinder executor) {
		super(executor);
	}


	protected Operation planNextCommand(DynamicPitchInfo dpi) {
		return new OperationReallocation(
				new Point2D.Double(100, 0), 
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection());
	}

	protected boolean operationSuccessful(DynamicPitchInfo dpi) {
		return false;
	}

	protected boolean problemExists(DynamicPitchInfo dpi) {
		return false;
	}
	
	

}
