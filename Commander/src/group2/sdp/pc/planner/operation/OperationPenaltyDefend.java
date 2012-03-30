package group2.sdp.pc.planner.operation;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
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
	 * @return
	 */
	public int getOpponentFacingSector(Point2D alfiePosition, double opponentFacingDirection) {
		Point2D intersection = getIntersection(GlobalInfo.getDefendingPenalty(), opponentFacingDirection);
		Point2D alfieStartingPosition = new Point2D.Double(alfiePosition.getX(),0);
		if (alfieStartingPosition.distance(intersection) > ZONE_SIZE) {
			if (Geometry.isPointBehind(alfieStartingPosition, alfieFacingDirection, intersection)) {
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
		if (rotatingSpeed == 0) {
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
		if(rotatingSpeed == 0) {
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
