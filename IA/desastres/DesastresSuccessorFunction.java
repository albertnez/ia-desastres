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

    // Each gorup of three nested fors are for selecting each group
    // Averall complexity is O(n^2), where n is the number of groups
    for (int i = 0; i < state.getTotalHelicopters(); ++i) {
      for (int j = 0; j < state.getNumExpeditionsHeli(i); ++j) {
        for (int k = 0; k < state.getExpeditions(i).get(j).size(); ++k) {
          // Now the second group
          for (int l = i; l < state.getTotalHelicopters(); ++l) {
            // Move each group 'k' to helicopter l.
            // If helicopters are diferent, or group 'k' is from an expedition of size 2 or more, split
            if (i != l || state.getExpeditions(i).get(j).size() > 1) {
              DesastresState newState = new DesastresState((DesastresState)aState);
              newState.moveGroupToNonExistentExpedition(i, j, k, l);
              double v = dhf.getHeuristicValue(newState);
              String S = new String(DesastresState.CREAR_EXPEDICION + " Coste(" + v + ") ---> ");
              retVal.add(new Successor(S, newState));
            }
            for (int m = 0; m < state.getNumExpeditionsHeli(l); ++m) {
              if (l > i || m > l) {
                for (int n = 0; n < state.getExpeditions(l).get(m).size(); ++n) {
                  // SWAP, always possible
                  DesastresState newState = new DesastresState((DesastresState)aState);
                  System.out.println(i+ " "+ j + " "+ k + " "+ l + " "+ m + " "+ n);
                  newState.swapGroupsBetweenExpeditions(i, j, k, l, m, n);
                  // aima stuff
                  double v = dhf.getHeuristicValue(newState);
                  String S = new String(DesastresState.INTERCAMBIO_GRUPOS + " Coste(" + v + ") ---> ");
                  retVal.add(new Successor(S, newState));

                  // Group move
                  if (state.getExpeditions(l).get(m).size() < 3) {
                    newState = new DesastresState((DesastresState)aState);
                    newState.moveGroupBetweenExpeditions(i, j, k, l, m);
                    v = dhf.getHeuristicValue(newState);
                    S = new String(DesastresState.MOVER_GRUPO_EXPEDICION + " Coste(" + v + ") ---> ");
                    retVal.add(new Successor(S, newState));
                  }
                  // Move group n to expedition j if there is space
                  if (state.getExpeditions(i).get(j).size() < 3) {
                    newState = new DesastresState((DesastresState)aState);
                    newState.moveGroupBetweenExpeditions(l, m, n, i, j);
                    v = dhf.getHeuristicValue(newState);
                    S = new String(DesastresState.MOVER_GRUPO_EXPEDICION + " Coste(" + v + ") ---> ");
                    retVal.add(new Successor(S, newState));
                  }
                }
              }
            }
          }
        }
      }
    }
    return retVal;
  }

}
