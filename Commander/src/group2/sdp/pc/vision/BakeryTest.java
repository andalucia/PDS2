package group2.sdp.pc.vision;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.junit.Test;

public class BakeryTest {

	
	@Test
	public void testComputeBallRollingDirection() {
		//set up lists of info to be passed
		Point2D.Double position1= new Point2D.Double(0.0,0.0);
		Point2D.Double position2= new Point2D.Double(1.0,1.0);
		Point2D.Double position3= new Point2D.Double(2.0,2.0);
		Point2D.Double position4= new Point2D.Double(2.0,0.0);
		Point2D.Double position5= new Point2D.Double(-1.0,-1.0);
		Point2D.Double position6= new Point2D.Double(-1.0,1.0);
		Point2D.Double position7= new Point2D.Double(1.0,-1.0);
		Point2D.Double position8= new Point2D.Double(2.0,0.0);
		//create balls
		StaticBallInfo ball1=new StaticBallInfo(position1,0);
		StaticBallInfo ball2=new StaticBallInfo(position2,0);
		StaticBallInfo ball3=new StaticBallInfo(position3,0);
		StaticBallInfo ball4=new StaticBallInfo(position4,0);
		StaticBallInfo ball5=new StaticBallInfo(position5,0);
		StaticBallInfo ball6=new StaticBallInfo(position6,0);
		StaticBallInfo ball7=new StaticBallInfo(position7,0);
		StaticBallInfo ball8=new StaticBallInfo(position8,0);
		
		//creates ball history and robot history infos
		LinkedList<StaticBallInfo> ballHistoryInfos= new LinkedList<StaticBallInfo>();
		Bakery bake= new Bakery(null);
		

		// top right quadrant
		//test with 0 points should return 0
		
		assertTrue("0 balls does not equal zero",bake.computeBallRollingDirection(ballHistoryInfos)==0.0 );
		//test with one point should return 0
		ballHistoryInfos.add(ball1);		
		assertTrue("1 balls does not equal zero", bake.computeBallRollingDirection(ballHistoryInfos)==0.0);
		
		
		// should return 45 as angle from ball1 to ball2 =45
		ballHistoryInfos.add(ball2);		
		assertTrue("2 balls result: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 45",bake.computeBallRollingDirection(ballHistoryInfos)==45.0);
		
		//bottom left quadrant (travelling -x-y)
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball3);
		ballHistoryInfos.add(ball2);
		ballHistoryInfos.add(ball1);		
		assertTrue("bot left result: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 225",bake.computeBallRollingDirection(ballHistoryInfos)==225.0);
		
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball5);		
		assertTrue("bot left result: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 225",bake.computeBallRollingDirection(ballHistoryInfos)==225.0);
		
		//top left quadrant (travelling -x+y)
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball6);		
		assertTrue("top left result: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 135",bake.computeBallRollingDirection(ballHistoryInfos)==135.0 );
		
		//bottom right quadrant (travelling +x -y)
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball7);		
		assertTrue("bottom right result: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 315",bake.computeBallRollingDirection(ballHistoryInfos)==315);
		//test averaging
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball5);
		ballHistoryInfos.add(ball2);		
		assertTrue("averaging 45: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 45",bake.computeBallRollingDirection(ballHistoryInfos)==45);
		
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball8);
		ballHistoryInfos.add(ball2);		
		assertTrue("averaging 90: "+bake.computeBallRollingDirection(ballHistoryInfos)+ " expected: 90",bake.computeBallRollingDirection(ballHistoryInfos)==90); 
		//large amount of points test
		ballHistoryInfos.clear();
		ballHistoryInfos.add(ball1);
		ballHistoryInfos.add(ball2);
		ballHistoryInfos.add(ball3);
		ballHistoryInfos.add(ball4);
		ballHistoryInfos.add(ball5);
		ballHistoryInfos.add(ball6);
		ballHistoryInfos.add(ball7);
		ballHistoryInfos.add(ball8);
		//System.out.println(bake.computeBallRollingDirection(ballHistoryInfos));
		assertTrue("large number of points" ,bake.computeBallRollingDirection(ballHistoryInfos)!=0);
	}

	@Test
	public void testComputeBallRollingSpeed() {
		Point2D.Double position1= new Point2D.Double(0.0,0.0);
		Point2D.Double position2= new Point2D.Double(2.0,0.0);
		StaticBallInfo ball1=new StaticBallInfo(position1,0);
		StaticBallInfo ball2=new StaticBallInfo(position2,1000);
		
		LinkedList<StaticBallInfo> ballHistoryInfos= new LinkedList<StaticBallInfo>();
		Bakery bake= new Bakery(null);
		
		//0 balls should equal 0
		assertTrue("0 balls does not equal zero",bake.computeBallRollingSpeed(ballHistoryInfos)==0.0 );
		//1 balls should equal 0
		ballHistoryInfos.add(ball1);
		assertTrue("1 balls does not equal zero",bake.computeBallRollingSpeed(ballHistoryInfos)==0.0 );
		//should equal 2cm/s
		ballHistoryInfos.add(ball2);
		assertTrue("expected 2cm/s actual: "+bake.computeBallRollingSpeed(ballHistoryInfos),bake.computeBallRollingSpeed(ballHistoryInfos)==2.0 );
	}

	@Test
	public void testComputeRobotTravelSpeed() {
		fail("Not yet implemented");
	}

	@Test
	public void testComputeRobotTravelDirection() {
		//set up lists of info to be passed
		Point2D.Double position1= new Point2D.Double(0.0,0.0);
		Point2D.Double position2= new Point2D.Double(1.0,1.0);
		Point2D.Double position3= new Point2D.Double(2.0,2.0);
		Point2D.Double position4= new Point2D.Double(2.0,0.0);
		Point2D.Double position5= new Point2D.Double(-1.0,-1.0);
		Point2D.Double position6= new Point2D.Double(-1.0,1.0);
		Point2D.Double position7= new Point2D.Double(1.0,-1.0);
		Point2D.Double position8= new Point2D.Double(2.0,0.0);
		
	
		//create robots
		StaticRobotInfo robot1=new StaticRobotInfo(position1,0,true,0);
		StaticRobotInfo robot2=new StaticRobotInfo(position2,0,true,0);
		StaticRobotInfo robot3=new StaticRobotInfo(position3,0,true,0);
		StaticRobotInfo robot4=new StaticRobotInfo(position4,0,true,0);
		StaticRobotInfo robot5=new StaticRobotInfo(position5,0,true,0);
		StaticRobotInfo robot6=new StaticRobotInfo(position6,0,true,0);
		StaticRobotInfo robot7=new StaticRobotInfo(position7,0,true,0);
		StaticRobotInfo robot8=new StaticRobotInfo(position8,0,true,0);
		
		LinkedList<StaticRobotInfo> robotHistoryInfos= new LinkedList<StaticRobotInfo>();
		Bakery bake= new Bakery(null);
		
		assertTrue("0 robots does not equal zero",bake.computeRobotTravelDirection(robotHistoryInfos)==0.0 );
		//
		robotHistoryInfos.add(robot1);
		assertTrue("1 robot does not equal zero", bake.computeRobotTravelDirection(robotHistoryInfos)==0.0 );
		//
		robotHistoryInfos.add(robot2);
		assertTrue("2 robots result: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 45",bake.computeRobotTravelDirection(robotHistoryInfos)==45.0);
		//
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot3);
		robotHistoryInfos.add(robot2);
		robotHistoryInfos.add(robot1);		
		assertTrue("bot left result: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 225",bake.computeRobotTravelDirection(robotHistoryInfos)==225.0);
		//
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot5);		
		assertTrue("bot left result: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 225",bake.computeRobotTravelDirection(robotHistoryInfos)==225.0);
		//
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot6);		
		assertTrue("top left result: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 135",bake.computeRobotTravelDirection(robotHistoryInfos)==135.0 );
		//bottom right quadrant (travelling +x -y)
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot7);		
		assertTrue("bottom right result: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 315",bake.computeRobotTravelDirection(robotHistoryInfos)==315);
		//test averaging
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot5);
		robotHistoryInfos.add(robot2);		
		assertTrue("averaging 45: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 45",bake.computeRobotTravelDirection(robotHistoryInfos)==45);
		
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot8);
		robotHistoryInfos.add(robot2);		
		assertTrue("averaging 90: "+bake.computeRobotTravelDirection(robotHistoryInfos)+ " expected: 90",bake.computeRobotTravelDirection(robotHistoryInfos)==90); 
		//large amount of points test
		robotHistoryInfos.clear();
		robotHistoryInfos.add(robot1);
		robotHistoryInfos.add(robot2);
		robotHistoryInfos.add(robot3);
		robotHistoryInfos.add(robot4);
		robotHistoryInfos.add(robot5);
		robotHistoryInfos.add(robot6);
		robotHistoryInfos.add(robot7);
		robotHistoryInfos.add(robot8);
		//System.out.println(bake.computeBallRollingDirection(ballHistoryInfos));
		assertTrue("large number of points" ,bake.computeRobotTravelDirection(robotHistoryInfos)!=0);
	}

}
