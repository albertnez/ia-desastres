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

    for (int i = 0; i < state.getTotalHelicopters(); ++i){
      for (int j = 0; j < state.getNumExpeditionsHeli(i); ++j){
	for (int k = 0; k < state.getExpeditions(i).get)
      }
    }
    DesastresState newState = new DesastresState(state);
    double v = dhf.getHeuristicValue(newState);
    String S = DesastresState.INTERCAMBIO_GRUPOS + " Coste(" + v + ") ---> " + newState.toString();
    retVal.add(new Successor(S, newState));
    
    return retVal;
  }

}
