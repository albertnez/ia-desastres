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
    for (int srcH = 0; srcH < state.getTotalHelicopters(); ++srcH) {
      for (int srcE = 0; srcE < state.getNumExpeditionsHeli(srcH); ++srcE) {
        // If there are 3 groups in this expedition
        if (state.getExpeditions(srcH).get(srcE).size() == 3) {
          int groupA = 0, groupB = 1;
          DesastresState newState = new DesastresState((DesastresState)aState);
          newState.swapGroupsFromSameExp(srcH, srcE, groupA, groupB);
          double v = dhf.getHeuristicValue(newState);
          String S = new String(DesastresState.INTERCAMBIO_GRUPOS + groupA + " de la expedición " 
                    + srcE + " del helicoptero " + srcH + " con grupo " + groupB 
                    + " Coste(" + v + ") ---> " + newState.toString());
          retVal.add(new Successor(S, newState));

          //Now the other swap
          // Undo swap...
          newState = new DesastresState((DesastresState)aState);
          //newState.swapGroupsFromSameExp(srcH, srcE, groupA, groupB);
          // New swap
          groupA = 2;
          newState.swapGroupsFromSameExp(srcH, srcE, groupA, groupB);
          v = dhf.getHeuristicValue(newState);
          S = new String(DesastresState.INTERCAMBIO_GRUPOS + groupA + " de la expedición " 
                    + srcE + " del helicoptero " + srcH + " con grupo " + groupB 
                    + " Coste(" + v + ") ---> " + newState.toString());
          retVal.add(new Successor(S, newState));
        }
        for (int srcG = 0; srcG < state.getExpeditions(srcH).get(srcE).size(); ++srcG) {
          // Now the second group
          for (int dstH = srcH; dstH < state.getTotalHelicopters(); ++dstH) {
            // Move each group 'srcG' to helicopter dstH.
            // If helicopters are diferent, or group 'srcG' is from an expedition of size 2 or more, split
            if (srcH != dstH) {
              DesastresState newState = new DesastresState((DesastresState)aState);
              newState.moveGroupToNonExistentExpedition(srcH, srcE, srcG, dstH);
              double v = dhf.getHeuristicValue(newState);
              String S = new String(DesastresState.CREAR_EXPEDICION + srcG + " de la expedición " 
                        + srcE + " del helicoptero " + srcH + " a una nueva expedición del helicoptero " 
                        + dstH + " Coste(" + v + ") ---> " + newState.toString());
              retVal.add(new Successor(S, newState));
            }
            for (int dstE = 0; dstE < state.getNumExpeditionsHeli(dstH); ++dstE) {
              if (dstH > srcH || dstE > srcE) {
                for (int dstG = 0; dstG < state.getExpeditions(dstH).get(dstE).size(); ++dstG) {
                  // SWAP, only possible if the expedition has room for the group
                  DesastresState newState = new DesastresState((DesastresState)aState);

                  if (state.doesGroupFitInExp(dstH,dstE,srcH,srcE,srcG) && state.doesGroupFitInExp(srcH,srcE,dstH,dstE,dstG)){
                    newState.swapGroupsBetweenExpeditions(srcH, srcE, srcG, dstH, dstE, dstG);
                    // aima stuff
                    double v = dhf.getHeuristicValue(newState);              
                    String S = new String(DesastresState.INTERCAMBIO_GRUPOS + srcG + " de la expedición " 
                           + srcE + " del helicoptero " + srcH + " con el grupo " + dstG + " de la expedición " 
                           + dstE + " del helicoptero " + dstH + " Coste(" + v + ") ---> "+ newState.toString());
                    retVal.add(new Successor(S, newState));
                  }
                  
                  // Group move, only possible if the expedition has room for the group
                  if (state.getExpeditions(dstH).get(dstE).size() < 3 && state.doesGroupFitInExp(dstH,dstE,srcH,srcE,srcG)) {
                    newState = new DesastresState((DesastresState)aState);
                    newState.moveGroupBetweenExpeditions(srcH, srcE, srcG, dstH, dstE);
                    double v = dhf.getHeuristicValue(newState);                   
                    String S = new String(DesastresState.MOVER_GRUPO_EXPEDICION + srcG + " de la expedición " 
                          + srcE + " del helicoptero " + srcH + " a la expedición " + dstE + " del helicoptero " 
                          + dstH + " Coste(" + v + ") ---> "+ newState.toString());
                    retVal.add(new Successor(S, newState));
                  }
                  // Move group dstG to expedition srcE if there is space
                  if (state.getExpeditions(srcH).get(srcE).size() < 3 && state.doesGroupFitInExp(srcH,srcE,dstH,dstE,dstG)) {
                    newState = new DesastresState((DesastresState)aState);
                    newState.moveGroupBetweenExpeditions(dstH, dstE, dstG, srcH, srcE);
                    double v = dhf.getHeuristicValue(newState);
                    String S = new String(DesastresState.MOVER_GRUPO_EXPEDICION + dstG + " de la expedición " 
                        + dstE + " del helicoptero " + dstH + " a la expedición " + srcE + " del helicoptero " 
                        + srcH + " Coste(" + v + ") ---> "+ newState.toString());
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
