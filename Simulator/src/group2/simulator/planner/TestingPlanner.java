package group2.simulator.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.planner.FieldMarshal;
import group2.sdp.pc.planner.Overlord;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;

public class TestingPlanner extends Overlord {

	public TestingPlanner(FieldMarshal executor) {
		super(executor);
	}


	protected Operation planNextCommand(DynamicInfo dpi) {
		return new OperationReallocation(
				new Point2D.Double(100, 0), 
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), dpi.getOpponentInfo().getPosition() );
	}

	protected boolean operationSuccessful(DynamicInfo dpi) {
		return false;
	}

	protected boolean problemExists(DynamicInfo dpi) {
		return false;
	}
	
	

}
