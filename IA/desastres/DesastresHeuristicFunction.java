package IA.desastres;

import aima.search.framework.HeuristicFunction;

public class DesastresHeuristicFunction implements HeuristicFunction {

 /*!\brief Gets the heuristic value that corresponds to the state state
  *
  * The heuristic function tries to minimize the SUM of the time 
  * it takes to rescue all of the groups and the time it takes to rescue 
  * all the priority 1 groups. 
  *
  * A better state would be one that has a smaller heuristic value.
  *
  * @param [Object] state State of the problem
  * @return double hueristic value calculated by the heuristic function
  */
  public double getHeuristicValue(Object state) {
    DesastresState st = (DesastresState)state;
    return st.getTypeASolutionCost();
  }

}
