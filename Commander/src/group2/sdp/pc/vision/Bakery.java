package group2.sdp.pc.vision;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.vision.skeleton.BakerySkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * Description: The Bakery bakes series of Static Info objects into Dynamic Info
 * objects. Main client: Overlord (see Planning Pipeline section below).
 * Actions: Stores a history of Static Info objects, uses that history to
 * estimate derivative properties of objects: speed and direction are estimated
 * from sequence of positions, rotation speed and direction are estimated from
 * sequence of directions. Passes that information to a Dynamic Info Consumer,
 * supplied on construction of the Bakery.
 */
public class Bakery extends BakerySkeleton {

	/**
	 * Variables used to prevent too much correction in correct facing direction
	 */
	private int stopAflie;
	private int correctionsAlfie;
	private int counterAlfie;
	private int stopOp;
	private int correctionsOp;
	private int counterOp;

	public Bakery(DynamicInfoConsumer consumer) {
		super(consumer);
	}

	@Override
	protected double computeBallRollingSpeed(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate speed
		if (ballHistoryInfos.size() < 6) {
			// TODO: add a verbose flag and display debug data when it is set
			return 0;
		}
		double sumx = 0.0;
		double sumy = 0.0;
		double sumTime = 0.0;
		// find average position of last 3 points
		// TODO: BEGIN: Extract to a method in a Static/Dynamic Info HistoryInfo
		for (int i = ballHistoryInfos.size() - 1; i >= ballHistoryInfos.size() - 3; i--) {
			if (ballHistoryInfos.get(i).getPosition() != null) {
				sumx += ballHistoryInfos.get(i).getPosition().getX();
				sumy += ballHistoryInfos.get(i).getPosition().getY();
				sumTime += ballHistoryInfos.get(i).getTimeStamp();
			}
		}
		double recentTime = sumTime / 3;

		Point2D.Double recentAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// TODO: END.
		sumx = 0.0;
		sumy = 0.0;
		sumTime = 0.0;
		// find average position of 3 points before that
		// TODO: BEGIN: use the method from the previous TODO
		for (int i = ballHistoryInfos.size() - 4; i >= ballHistoryInfos.size() - 6; i--) {
			if (ballHistoryInfos.get(i).getPosition() != null) {
				sumx += ballHistoryInfos.get(i).getPosition().getX();
				sumy += ballHistoryInfos.get(i).getPosition().getY();
				sumTime += ballHistoryInfos.get(i).getTimeStamp();
			}
		}

		double oldTime = sumTime / 3;
		Point2D.Double oldAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// TODO: END.

		// test if robot is moving
		// TODO: Extract 0.1 to a constant; fix previous comment, as this is a
		// ball function.
		if (oldAvg.distance(recentAvg) < 0.1) {
			return 0;
		}

		double timeDif = (recentTime - oldTime);
		timeDif = timeDif / 1000;
		double speed = (recentAvg.distance(oldAvg)) / (timeDif);
		return speed;

	}

	@Override
	protected double computeBallRollingDirection(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		// TODO: make the comment on the next line easier to understand (it is 
		// better if there is no reference to other comments, which might 
		// disappear)
		
		// use similar algorithm to travel speed except work out angle with
		// average points
		double angle;
		if (ballHistoryInfos.size() < 6) {
			return 0;
		}
		double sumx = 0.0;
		double sumy = 0.0;
		//TODO: reuse the method for averaging points. 
		// find average of last 3 points
		for (int i = ballHistoryInfos.size() - 1; i >= ballHistoryInfos.size() - 3; i--) {
			if (ballHistoryInfos.get(i).getPosition() != null) {
				sumx += ballHistoryInfos.get(i).getPosition().getX();
				sumy += ballHistoryInfos.get(i).getPosition().getY();
			}
		}
		Point2D.Double recentAvg = new Point2D.Double(sumx / 3, sumy / 3);
		sumx = 0.0;
		sumy = 0.0;
		//TODO: reuse the method for averaging points.
		// find average of 3 points before
		for (int i = ballHistoryInfos.size() - 4; i >= ballHistoryInfos.size() - 6; i--) {
			if (ballHistoryInfos.get(i).getPosition() != null) {
				sumx += ballHistoryInfos.get(i).getPosition().getX();
				sumy += ballHistoryInfos.get(i).getPosition().getY();
			}
		}
		Point2D.Double oldAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// test if robot is moving
		//TODO: Extract 0.5 to a constant; fix previous comment, as this is a
		// ball function.
		if (oldAvg.distance(recentAvg) < 0.5) {
			return 0;
		}
		// now calculate angle
		double xDiff = recentAvg.getX() - oldAvg.getX();
		double yDiff = recentAvg.getY() - oldAvg.getY();

		if (yDiff >= 0) {
			angle = Math.toDegrees(Math.atan2(yDiff, xDiff));
		} else {
			angle = 360 + Math.toDegrees(Math.atan2(yDiff, xDiff));
		}

		return angle;
	}

	@Override
	protected double computeRobotTravelSpeed(
			LinkedList<StaticRobotInfo> historyInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate speed

		// at beginning when no history speed cannot be calculated
		if (historyInfos.size() < 6) {
			return 0;
		}
		double sumx = 0.0;
		double sumy = 0.0;
		double sumTime = 0.0;
		// TODO: three times is the charm. Extract a method doing this sequence of
		// commands.
		// find average position of last 3 points
		for (int i = historyInfos.size() - 1; i >= historyInfos.size() - 3; i--) {
			if (historyInfos.get(i).getPosition() != null) {
				sumx += historyInfos.get(i).getPosition().getX();
				sumy += historyInfos.get(i).getPosition().getY();
				sumTime += historyInfos.get(i).getTimeStamp();
			}
		}
		double recentTime = sumTime / 3;

		Point2D.Double recentAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// find average position of 3 points before that
		sumx = 0.0;
		sumy = 0.0;
		sumTime = 0.0;
		for (int i = historyInfos.size() - 4; i >= historyInfos.size() - 6; i--) {
			if (historyInfos.get(i).getPosition() != null) {
				sumx += historyInfos.get(i).getPosition().getX();
				sumy += historyInfos.get(i).getPosition().getY();
				sumTime += historyInfos.get(i).getTimeStamp();
			}
		}

		double oldTime = sumTime / 3;
		Point2D.Double oldAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// test if robot is moving
		if (oldAvg.distance(recentAvg) < 0.1) {
			return 0;
		}
		double timeDif = (recentTime - oldTime);
		timeDif = timeDif / 1000;
		double speed = (recentAvg.distance(oldAvg)) / (timeDif);
		return speed;
	}

	protected double correctRobotFacingDirection(
			LinkedList<StaticRobotInfo> historyInfos) {
		// TODO: add to design spec. and audit code
		// TODO check for negative angles (i.e. when robot is missing)
		// if we are correcting more than half the frames in the last 10 frames
		// then
		// set method to stop correcting for next 10 frames to avoid loop. use
		// vision values instead
		// use seperate counters for different robots.
		if (historyInfos.getLast().isAlfie()) {
			if (stopAflie > 0) {
				stopAflie--;
				return historyInfos.getLast().getFacingDirection();
			}
			if (correctionsAlfie >= 5) {
				correctionsAlfie = 0;
				counterAlfie = 0;
				stopAflie = 10;
				return historyInfos.getLast().getFacingDirection();
			}
			if (counterAlfie == 10) {
				counterAlfie = 0;
				correctionsAlfie = 0;
			}

			if (historyInfos.isEmpty()) {
				return 0;
			}
			counterAlfie++;
		} else {
			if (stopOp > 0) {
				stopOp--;
				return historyInfos.getLast().getFacingDirection();
			}
			if (correctionsOp >= 5) {
				correctionsOp = 0;
				counterOp = 0;
				stopOp = 10;
				return historyInfos.getLast().getFacingDirection();
			}
			if (counterOp == 10) {
				counterOp = 0;
				correctionsOp = 0;
			}

			if (historyInfos.isEmpty()) {
				return 0;
			}
			counterOp++;
		}

		if (historyInfos.size() < 6) {
			return historyInfos.getLast().getFacingDirection();
		}
		int numSim = 0;
		// alternative used to pass previous angle if current is extreme
		double alternative = 0.0;
		double angle = historyInfos.getLast().getFacingDirection();
		// loop through previous points and count how many are similar
		for (int i = historyInfos.size() - 2; i >= historyInfos.size() - 6
				&& i >= 1; i--) {
			double angleOld = historyInfos.get(i).getFacingDirection();
			// current threshold=90
			if (isSimilarAngle(angleOld, angle, 90)) {
				numSim++;
			} else {
				alternative = angleOld;
			}
		}
		// if more than half are similar then angle ok
		if (numSim >= 3) {
			return angle;
		} else {
			// correct angle because of extreme value
			if (historyInfos.getLast().isAlfie()) {
				correctionsAlfie++;
			} else {
				correctionsOp++;
			}
			return alternative;
		}
	}

	@Override
	protected double computeRobotTravelDirection(
			LinkedList<StaticRobotInfo> historyInfos) {
		// TODO: use common method from before.
		// use similar algorithm to travel speed except work out angle with
		// average points
		double angle;
		if (historyInfos.size() < 6) {
			return historyInfos.getLast().getFacingDirection();
		}
		double sumx = 0.0;
		double sumy = 0.0;
		// find average of last 3 points
		for (int i = historyInfos.size() - 1; i >= historyInfos.size() - 3; i--) {
			if (historyInfos.get(i).getPosition() != null) {
				sumx += historyInfos.get(i).getPosition().getX();
				sumy += historyInfos.get(i).getPosition().getY();
			}
		}
		Point2D.Double recentAvg = new Point2D.Double(sumx / 3, sumy / 3);
		sumx = 0.0;
		sumy = 0.0;
		for (int i = historyInfos.size() - 4; i >= historyInfos.size() - 6; i--) {
			if (historyInfos.get(i).getPosition() != null) {
				sumx += historyInfos.get(i).getPosition().getX();
				sumy += historyInfos.get(i).getPosition().getY();
			}
		}
		Point2D.Double oldAvg = new Point2D.Double(sumx / 3, sumy / 3);
		// test if robot is moving
		if (oldAvg.distance(recentAvg) < 0.5) {
			return historyInfos.getLast().getFacingDirection();
		}
		// now calculate angle
		double xDiff = recentAvg.getX() - oldAvg.getX();
		double yDiff = recentAvg.getY() - oldAvg.getY();

		if (yDiff >= 0) {
			angle = Math.toDegrees(Math.atan2(yDiff, xDiff));
		} else {
			angle = 360 + Math.toDegrees(Math.atan2(yDiff, xDiff));
		}
		return angle;

	}

	@Override
	protected double computeRobotRotatingSpeed(
			LinkedList<StaticRobotInfo> historyInfos) {
		// TODO: Add code that does that
		return 0;
	}

	@Override
	protected boolean isRobotRotatingCCW(
			LinkedList<StaticRobotInfo> historyInfos) {
		// TODO: Add code that does that
		return false;
	}

	/**
	 * Method for comparison of angles. If angle within certain threshold of
	 * each other then return true else false
	 * 
	 * @param angle1
	 *            first angle for comparison
	 * @param angle2
	 *            second angle for comparison
	 * @param threshold
	 *            max difference for angles to be similar
	 * @return are angles within threshold of each other
	 */
	protected boolean isSimilarAngle(double angle1, double angle2,
			double threshold) {
		double bigAngle;
		double smallAngle;
		if (angle1 == angle2) {
			return true;
		}
		if (angle1 >= angle2) {
			bigAngle = angle1;
			smallAngle = angle2;
		} else {
			bigAngle = angle2;
			smallAngle = angle1;
		}
		if (bigAngle - smallAngle <= threshold) {
			return true;
		}
		// check to solve 360-0 problem
		if (bigAngle >= (360 - threshold)
				&& smallAngle <= 0 + (threshold - (360 - bigAngle))) {
			return true;
		}
		return false;
	}
}
