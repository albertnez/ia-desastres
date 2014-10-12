package IA.desastres;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.*;

public class DesastresSuccessorFunction implements SuccessorFunction {

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

    //TODO: aqui s'han de posar fors per tal de generar TOTS els successors del estat base
    // s'ha de canviar la paraula "INTERCAMBIO" segons la paraula utilitzada
    DesastresState newState = new DesastresState(state);
    double v = dhf.getHeuristicValue(newState);
    //String S = DesastresState.INTERCAMBIO + " " + i + " " + j + " Coste(" + v + ") ---> " + newState.toString();
    //retVal.add(new Successor(S, newState));
    //

    return retVal;
  }

}
