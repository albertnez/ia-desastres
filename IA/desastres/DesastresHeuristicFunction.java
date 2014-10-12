package IA.desastres;

import aima.search.framework.HeuristicFunction;

public class DesastresHeuristicFunction implements HeuristicFunction {

  public double getHeuristicValue(Object state) {
    DesastresState st = (DesastresState)state;
    return st.getTypeASolutionCost();
  }

}
