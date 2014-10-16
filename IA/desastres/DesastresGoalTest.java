package IA.desastres;

import aima.search.framework.GoalTest;

public class DesastresGoalTest implements GoalTest {

  /*!\brief Returns true if the state aState is a valid
  * final state, false otherwise
  *
  * Because this program is only being executed with SA
  * and HC, the goal state function will always return false
  *
  * @param [Object] aState State of the problem
  * @return boolean indicates if aState is a goal state
  */
  public boolean isGoalState(Object aState) {
    return(false);
  }

}
