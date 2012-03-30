package group2.sdp.pc.planner.operation;

import group2.sdp.pc.globalinfo.GlobalInfo;

import java.awt.geom.Point2D;

public class OperationPenaltyDefend implements Operation {

	private static final double FRONT_SECTOR_DISTANCE = 7;
	private static final double BACK_SECTOR_DISTANCE = -7;
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
			if (alfiePosition.getY() > FRONT_SECTOR_DISTANCE) {
				return 1;
			}
			if (alfiePosition.getY() < BACK_SECTOR_DISTANCE) {
				return 3;
			}
			return 2;
		} else {
			// facing down
			if (alfiePosition.getY() > FRONT_SECTOR_DISTANCE) {
				return 3;
			}
			if (alfiePosition.getY() < BACK_SECTOR_DISTANCE) {
				return 1;
			}
			return 2;
		}
	}

	/**
	 * Get the sector which the opponent is facing
	 * @return
	 */
	public int getOpponentFacingSector(double opponentFacingDirection) {
		return 0;
	}

	public boolean isAngleIncreasing() {
		return true;
	}

	public boolean isAngleDecreasing() {
		return true;
	}

	@Override
	public Type getType() {
		return Type.PENALTY_DEFEND;
	}

}
