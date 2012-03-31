package group2.sdp.pc.planner.operation;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;

import java.awt.geom.Point2D;

public class OperationPenaltyDefend implements Operation {

	private static final double FRONT_SECTOR_DISTANCE = 7;
	private static final double BACK_SECTOR_DISTANCE = -7;
	private static final double PENALTY_RADIUS = 60;
	private static final double ZONE_SIZE = 10;
	private double alfieFacingDirection;

	public OperationPenaltyDefend(double alfieFacingDirection) {
		this.alfieFacingDirection = alfieFacingDirection;
	}
	
	/**
	 * Gets the sector which we want Alfie to move to: This takes in to account 
	 * the sector the opponent is facing and how they are rotating.
	 * @param op
	 * @param opponentInfo
	 * @param opponentFacingSector
	 * @return
	 */
	public int getDesiredSector(DynamicRobotInfo opponentInfo, int opponentFacingSector) {
		int desiredSector;
		switch (opponentFacingSector) {
		case 1: 
			if (isAngleIncreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 2
				desiredSector = 2;
			} else {
				// move to 1
				desiredSector = 1;
			}
			break;
		case 2:
			if (isAngleIncreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 3
				desiredSector = 3;
			} else if (isAngleDecreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 1
				desiredSector = 1;
			} else {
				// move to 2
				desiredSector = 2;
			}
			break;
		case 3:
			if (isAngleDecreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 2
				desiredSector = 2;
			} else {
				// move to 3
				desiredSector = 3;
			}
			break;
		default:
			// should not reach here
			desiredSector = -1;
			break;
		}
		return desiredSector;
	}

	/**
	 * Returns the current zone which Alfie is in.
	 * @param alfiePosition The position of Alfie
	 */
	public int getCurrentSector(Point2D alfiePosition) {
		// facing up
		if (alfieFacingDirection < 180) {
			if (alfiePosition.getY() >= FRONT_SECTOR_DISTANCE) {
				return 1;
			}
			if (alfiePosition.getY() <= BACK_SECTOR_DISTANCE) {
				return 3;
			}
			return 2;
		} else {
			// facing down
			if (alfiePosition.getY() >= FRONT_SECTOR_DISTANCE) {
				return 3;
			}
			if (alfiePosition.getY() <= BACK_SECTOR_DISTANCE) {
				return 1;
			}
			return 2;
		}
	}

	/**
	 * Get the sector which the opponent is facing
	 * TODO remove alfiePosition: something else could be used instead
	 */
	public int getOpponentFacingSector(double opponentFacingDirection) {
		Point2D intersection = getIntersection(GlobalInfo.getDefendingPenalty(), opponentFacingDirection);
		Point2D defensiveGoalMiddle = GlobalInfo.getDefensiveGoalMiddle();
		
		if (defensiveGoalMiddle.distance(intersection) > ZONE_SIZE) {
			if (Geometry.isPointBehind(defensiveGoalMiddle, alfieFacingDirection, intersection)) {
				return 3;
			} else {
				return 1;
			}
		} else {
			return 2;
		}
	}

	/**
	 * The angle is increasing if the opponent is rotating from the front of Alfie to 
	 * the back.
	 * @param isOpponentRotatingCCW
	 * @param rotatingSpeed
	 * @return
	 */
	public boolean isAngleIncreasing(boolean isOpponentRotatingCCW, double rotatingSpeed) {
		//TODO check speed threshold
		if (rotatingSpeed < 90) {
			return false;
		}
		if (alfieFacingDirection < 180) {
			//facing up
			if (isOpponentRotatingCCW) {
				return true;
			} else {
				return false;
			}
		} else {
			//facing down
			if (isOpponentRotatingCCW) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * The angle is decreasing if the opponent is rotating from the back of Alfie to 
	 * the front
	 * @param isOpponentRotatingCCW
	 * @param rotatingSpeed
	 * @return
	 */
	public boolean isAngleDecreasing(boolean isOpponentRotatingCCW, double rotatingSpeed) {
		//TODO check speed threshold
		if(rotatingSpeed < 90) {
			return false;
		}
		if (alfieFacingDirection < 180) {
			//facing up
			if (isOpponentRotatingCCW) {
				return false;
			} else {
				return true;
			}
		} else {
			//facing down
			if (isOpponentRotatingCCW) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Finds the point on the defending arc at which the direction of the opponent 
	 * robot would intersect.
	 * @param opponentPosition
	 * @param opponentFacingDirection
	 * @return
	 */
	public Point2D getIntersection(Point2D opponentPosition, double opponentFacingDirection) {
		
		Point2D randomPoint = Geometry.generateRandomPoint(
				opponentPosition, 
				opponentFacingDirection
				);
		
		Pair<Point2D, Point2D> intersections = Geometry.getLineCircleIntersections(
				opponentPosition, 
				randomPoint, 
				GlobalInfo.getDefendingPenalty(), 
				PENALTY_RADIUS
				);
		if (Geometry.isPointBehind(opponentPosition, opponentFacingDirection, intersections.getFirst())) {
			return intersections.getSecond();
		} else {
			return intersections.getFirst();
		}
	}

	@Override
	public Type getType() {
		return Type.PENALTY_DEFEND;
	}

}
