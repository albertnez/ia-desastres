package IA.desastres;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.*;

public class DesastresSuccessorFunctionSA implements SuccessorFunction {
  @SuppressWarnings("unchecked")
  /*!\brief Generates a list with ONE random successesor of aState
  *
  * Returns a List with a pair (string, State) such that State is a 
  * successor of aState and string describes the operation applied
  * to aState in order to obtain that successor. One of the many 
  * successors of aState is chosen.
  *
  * @param [Object] aState State of the problem
  * @return List successesors of the state aState
  */
  public List getSuccessors(Object aState) {
    ArrayList retVal = new ArrayList();
    //if (true) return retVal;
    DesastresState state = (DesastresState) aState;
    DesastresHeuristicFunction dhf = new DesastresHeuristicFunction();

    // Current cost:
    double v = dhf.getHeuristicValue(state);
    // number of operators
    Random rand = new Random();

    ArrayList<Integer> options = new ArrayList<Integer>();
    int numOptions = 0;
    // check 
    // SwapGroupsBetweenExpeditions
    // MoveGroupBetweenExpeditions
    if (state.existsMoreThanOneExpedition()) {
      numOptions += 2;
      options.add(0);
      options.add(2);
    }
    // SwapGroupsFromSameExpedition
    if (state.existsExpeditionWithThreeGroups()) {
      ++numOptions;
      options.add(1);
    }
    // MoveGroupToNonExistentExpedition
    if (state.getNCenters()*state.getNHelicoptersPerCenter() > 1 &&
        state.existsExpeditionWithGroups()) {
      ++numOptions;
      options.add(3);
    }
    
    int srcH, srcE, srcG, dstH, dstE, dstG;
    srcH = srcE = srcG = dstH = dstE = dstG = -1;
    String S = "";
    DesastresState newState = new DesastresState((DesastresState)aState);
    switch (options.get(rand.nextInt(numOptions))) {
      case 0:
        //swapGroupsBetweenExpeditions
        // source helicopter
        do {
          do {
            srcH = rand.nextInt(state.getTotalHelicopters());
          } while (state.getNumExpeditionsHeli(srcH) == 0);

          srcE = rand.nextInt(state.getNumExpeditionsHeli(srcH));
          srcG = rand.nextInt(state.getExpeditions(srcH).get(srcE).size());
          do {
            dstH = rand.nextInt(state.getTotalHelicopters());
          } while (state.getNumExpeditionsHeli(dstH) == 0);

          dstE = rand.nextInt(state.getNumExpeditionsHeli(dstH));
          dstG = rand.nextInt(state.getExpeditions(dstH).get(dstE).size());
        } while (!state.isGroupsSwapValid(srcH, srcE, srcG, dstH, dstE, dstG));

        newState.swapGroupsBetweenExpeditions(srcH, srcE, srcG, dstH, dstE, dstG);
        v = dhf.getHeuristicValue(newState);
        S = new String(DesastresState.INTERCAMBIO_GRUPOS + srcG + " de la expedición " 
          + srcE + " del helicoptero " + srcH + " con el grupo " + dstG + " de la expedición " 
          + dstE + " del helicoptero " + dstH + " Coste(" + v + ") ---> "+ newState.toString());
        retVal.add(new Successor(S, newState));
        break;
      case 1:
        // swapGroupsFromSameExp
        do {
          srcH = rand.nextInt(state.getTotalHelicopters());
        } while (!state.heliHasExpeditionWithThreeGroups(srcH));

        do {
          srcE = rand.nextInt(state.getNumExpeditionsHeli(srcH));
        } while (state.getExpeditions(srcH).get(srcE).size() != 3);

        // only usefull swaps are 0 with 1, and 1 with 2. Choose one randomly.
        srcG = 0;
        dstG = 1;
        if (rand.nextBoolean()) {
          srcG = 2;
        }

        newState.swapGroupsFromSameExp(srcH, srcE, srcG, dstG);
        v = dhf.getHeuristicValue(newState);
        S = new String(DesastresState.INTERCAMBIO_GRUPOS + srcG + " de la expedición " 
                  + srcE + " del helicoptero " + srcH + " cont grupo " + dstG 
                  + " Coste(" + v + ") ---> " + newState.toString());
        retVal.add(new Successor(S, newState));
        break;
      case 2:
        // moveGroupBetweenExpeditions
        do {
          do {
            srcH = rand.nextInt(state.getTotalHelicopters());
          } while (state.getNumExpeditionsHeli(srcH) == 0);

          srcE = rand.nextInt(state.getNumExpeditionsHeli(srcH));
          srcG = rand.nextInt(state.getExpeditions(srcH).get(srcE).size());
          
          do {
            dstH = rand.nextInt(state.getTotalHelicopters());
          } while (state.getNumExpeditionsHeli(dstH) == 0);

          dstE = rand.nextInt(state.getNumExpeditionsHeli(dstH));
        } while ((srcH != dstH || srcE != dstE) && state.doesGroupFitInExp(dstH, dstE, srcH, srcE, srcG));


        newState.moveGroupBetweenExpeditions(srcH, srcE, srcG, dstH, dstE);
        v = dhf.getHeuristicValue(newState);
        S = new String(DesastresState.MOVER_GRUPO_EXPEDICION + srcG + " de la expedición " 
          + srcE + " del helicoptero " + srcH + " a la expedición " + dstE + " del helicoptero " 
          + dstH + " Coste(" + v + ") ---> "+ newState.toString());
        retVal.add(new Successor(S, newState));
        break;
      case 3:
        // moveGroupToNonExistentExpedition
        do {
          srcH = rand.nextInt(state.getTotalHelicopters());
        } while (state.getNumExpeditionsHeli(srcH) == 0);

        srcE = rand.nextInt(state.getNumExpeditionsHeli(srcH));
        srcG = rand.nextInt(state.getExpeditions(srcH).get(srcE).size());

        do {
          dstH = rand.nextInt(state.getTotalHelicopters());  // any helicopter here will fit
        } while (state.getExpeditions(srcH).get(srcE).size() == 1 && dstH == srcH);

        newState.moveGroupToNonExistentExpedition(srcH, srcE, srcG, dstH);
        v = dhf.getHeuristicValue(newState);
        S = new String(DesastresState.CREAR_EXPEDICION + srcG + " de la expedición " 
                  + srcE + " del helicoptero " + srcH + " a una nueva expedición del helicoptero " 
                  + dstH + " Coste(" + v + ") ---> " + newState.toString());
        retVal.add(new Successor(S, newState));
        break;
    }
    //System.out.println(S);
    return retVal;
  }
}
