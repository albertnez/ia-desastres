package IA.desastres;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.*;

public class DesastresSuccessorFunction implements SuccessorFunction {
  @SuppressWarnings("unchecked")
  /*!\brief Generates a list of all the possible successors of aState
  * 
  * Returns a List with pairs (string, State) such that State is a 
  * successor of aState and string describes the operation applied
  * to aState in order to obtain that successor.
  *
  * @param [Object] aState State of the problem
  */
  public List getSuccessors(Object aState) {
    ArrayList retVal = new ArrayList();
    DesastresState state = (DesastresState) aState;
    DesastresHeuristicFunction dhf = new DesastresHeuristicFunction();

    // SWAPS
    // Each gorup of three nested fors are for selecting each group
    // Averall complexity is O(n^2), where n is the number of groups
    for (int i = 0; i < state.getTotalHelicopters(); ++i) {
      for (int j = 0; j < state.getNumExpeditionsHeli(i); ++j) {
        for (int k = 0; k < state.getExpeditions(i).get(j).size(); ++k) {
          // Now the second group
          for (int l = i; l < state.getTotalHelicopters(); ++l) {
            for (int m = 0; j < state.getNumExpeditionsHeli(l); ++m) {
              if (l > i || m > l) {
                for (int n = 0; n < state.getExpeditions(l).get(m).size(); ++n) {
                  // Always possible to swap
                  DesastresState newState = new DesastresState((DesastresState)aState);
                  newState.swapGroupsBetweenExpeditions(i, j, k, l, m, n);
                }
              }
            }
          }
        }
      }
    }

    DesastresState newState = new DesastresState(state);
    double v = dhf.getHeuristicValue(newState);
    String S = DesastresState.INTERCAMBIO_GRUPOS + " Coste(" + v + ") ---> " + newState.toString();
    retVal.add(new Successor(S, newState));
    
    return retVal;
  }

}
