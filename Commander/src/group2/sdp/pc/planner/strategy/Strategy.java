package group2.sdp.pc.planner.strategy;

/**
 * <p><b>Success:</b></br>
 * Strategy is <b>OFFENSIVE</b> - successful if we score a goal</br>
   Strategy is <b>DEFENSIVE</b> - successful if there is no threat of the other team scoring a goal</br> 
   Strategy is <b>PENALTY_DEFEND</b> - successful if Alfie prevents the opponent from scoring a penalty</br>
   Strategy is <b>PENALTY_TAKE</b> - successful if we score a goal</br>
   Strategy is <b>STOP</b> - successful if Alfie stops</br></p>
  <p><b>Problem Exists:</b></br> 
  Strategy is <b>OFFENSIVE</b> - problem exists if the other robot gets the ball</br>
   Strategy is <b>DEFENSIVE</b> - problem exists if the other team scores a goal</br> 
   Strategy is <b>PENALTY_DEFEND</b> - problem exists if the opponent scores a goal</br>
   Strategy is <b>PENALTY_TAKE</b> - problem exists if Alfie misses after the shot</br>
   Strategy is <b>STOP</b> - problem exists if Alfie is being moved by the other robot</br></p>
	<p><b>Implemented by:</b> {@link Overlord}</p>
 */
public enum Strategy {	
	OFFENSIVE,
	DEFENSIVE,
	PENALTY_DEFEND,
	PENALTY_TAKE,
	STOP, 
	TEST
}