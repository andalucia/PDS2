package group2.simulator.core;


public class RobotState {
		
		public enum Movement {
			DO_NOTHING,
			GOING_FORWARDS,
		}
			
		private Movement currentMovement;	
		private double speedOfTravel;
		private double angleOfRotation;
		
		public RobotState() {
			currentMovement = Movement.DO_NOTHING;
			speedOfTravel = 0;
			angleOfRotation = 0;
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
	}