package group2.sdp.pc.test;

import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsRight;

import java.awt.geom.Point2D;

import junit.framework.Assert;

import org.junit.Test;

public class PathStepArcTest {
	
	@Test
	public void testGetCentre() {
		PathStepArc arc = 
			new PathStepArcForwardsRight(
					new Point2D.Double(-5.0, 0.0),
					90.0,
					5.0,
					180.0,
					0.0
			);
		Assert.assertEquals(new Point2D.Double(0.0, 0.0), arc.getCentrePoint());
	}
	
	@Test
	public void testGetTargetDestination() {
		PathStepArc fra = 
			new PathStepArcForwardsRight(
					new Point2D.Double(-5.0, 0.0),
					90.0,
					5.0,
					180.0,
					0.0
			);
		Assert.assertEquals(
				new Point2D.Double(5.0, 0.0), 
				fra.getTargetDestination()
		);
		fra = 
			new PathStepArcForwardsRight(
					new Point2D.Double(-5.0, 0.0),
					90.0,
					5.0,
					135.0,
					0.0
			);
		Assert.assertEquals(
				new Point2D.Double(Math.sqrt(0.5) * 5, Math.sqrt(0.5) * 5), 
				fra.getTargetDestination()
		);
		PathStepArcForwardsLeft fla = 
			new PathStepArcForwardsLeft(
					new Point2D.Double(5.0, 0.0),
					90.0,
					5.0,
					135.0,
					0.0
			);
		Assert.assertTrue(
				new Point2D.Double(
						-Math.sqrt(0.5) * 5, 
						Math.sqrt(0.5) * 5
				).distance(
						fla.getTargetDestination()
				) < 1e-10
		);
		
		PathStepArcBackwardsRight bra = 
			new PathStepArcBackwardsRight(
					new Point2D.Double(-5.0, 0.0),
					90.0,
					5.0,
					135.0,
					0.0
			);
		Assert.assertTrue(
				new Point2D.Double(
						Math.sqrt(0.5) * 5, 
						-Math.sqrt(0.5) * 5
				).distance(
						bra.getTargetDestination()
				) < 1e-10
		);
		
		PathStepArcBackwardsLeft bla = 
			new PathStepArcBackwardsLeft(
					new Point2D.Double(5.0, 0.0),
					90.0,
					5.0,
					135.0,
					0.0
			);
		Assert.assertTrue(
				new Point2D.Double(
						-Math.sqrt(0.5) * 5, 
						-Math.sqrt(0.5) * 5
				).distance(
						bla.getTargetDestination()
				) < 1e-10
		);
	}
}
