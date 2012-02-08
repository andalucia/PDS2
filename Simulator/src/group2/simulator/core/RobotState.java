package group2.simulator.core;

/**
 * This class represents the robot state.
 *  It has setters and getters for each of the robot's variables:
 *  speed of travel, angle of rotation etc.
 *
 */
public class RobotState {
		
		public enum Movement {
			DO_NOTHING,
			GOING_FORWARD,
			GOING_BACKWARDS,
			KICK,
			SPIN_RIGHT,
			SPIN_LEFT
		}
			
		private Movement currentMovement;	
		private double speedOfTravel;
		private double angleOfRotation;
		private int power;
		
		public RobotState() {
			currentMovement = Movement.DO_NOTHING;
			speedOfTravel = 40;
			angleOfRotation = 0;
			power = 0;
		}
		
		public Movement getCurrentMovement() {
			return currentMovement;
		}

		public void setCurrentMovement(Movement currentMovement) {
			this.currentMovement = currentMovement;
		}

		public double getSpeedOfTravel() {
			return speedOfTravel;
		}

		public void setSpeedOfTravel(double speedOfTravel) {
			this.speedOfTravel = speedOfTravel;
		}

		public double getAngleOfRotation() {
			return angleOfRotation;
		}

		public void setAngleOfRotation(double angleOfRotation) {
			this.angleOfRotation = angleOfRotation;
		}
		
		public void setPowerOfKick(int power) {
			this.power = power;
		}
		
		public double getPowerOfKick() {
			return power;
		}
	}