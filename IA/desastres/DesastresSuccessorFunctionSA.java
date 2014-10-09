package IA.desastres;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.*;

public class DesastresSuccessorFunction implements SuccessorFunction {

  /*!\brief Generates a list with ONE random successesor of aState
  *
  * Returns a List with a pair (string, State) such that State is a 
  * successor of aState and string describes the operation applied
  * to aState in order to obtain that successor. One of the many 
  * successors of aState is chosen.
  *
  * @param [Object] aState State of the problem
  */
  public List getSuccessors(Object aState) {
    ArrayList retVal = new ArrayList();
    DesastresState state = (DesastresState) aState;
    DesastresHeuristicFunction dhf = new DesastresHeuristicFunction();
    Random myRandom=new Random();

    //TODO: aqui només tindrem que fer que s'esculli una operacio de transformació random i aplicar-la i ja està
    DesastresState newState = new DesastresState(state.getNCenters(), state.getNHelicopters(), state.getNGroups());
    double v = dhf.getHeuristicValue(newState);
    //String S = DesastresState.INTERCAMBIO + " " + i + " " + j + " Coste(" + v + ") ---> " + newState.toString();
    //retVal.add(new Successor(S, newState));

    return retVal;
    }

}
