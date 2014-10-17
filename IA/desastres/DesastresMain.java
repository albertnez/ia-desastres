package IA.desastres;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class DesastresMain {
  public static void main(String[] args){
    DesastresState d = new DesastresState(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    TSPHillClimbingSearch(d);
    //TSPSimulatedAnnealingSearch(d);
  }
      
  private static void TSPHillClimbingSearch(DesastresState d) {
    System.out.println("\nTSP HillClimbing  -->");
    try {
      Problem problem =  new Problem(d,new DesastresSuccessorFunction(), new DesastresGoalTest(),new DesastresHeuristicFunction());
      Search search =  new HillClimbingSearch();
      SearchAgent agent = new SearchAgent(problem,search);
      
      System.out.println();
      printActions(agent.getActions());
      printInstrumentation(agent.getInstrumentation());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void TSPSimulatedAnnealingSearch(DesastresState d) {
    System.out.println("\nTSP Simulated Annealing  -->");
    try {
      Problem problem =  new Problem(d,new DesastresSuccessorFunctionSA(), new DesastresGoalTest(),new DesastresHeuristicFunction());
      SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(2000,100,5,0.001);
      //search.traceOn();
      SearchAgent agent = new SearchAgent(problem,search);
      
      System.out.println();
      printActions(agent.getActions());
      printInstrumentation(agent.getInstrumentation());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printInstrumentation(Properties properties) {
      Iterator keys = properties.keySet().iterator();
      while (keys.hasNext()) {
        String key = (String) keys.next();
        String property = properties.getProperty(key);
        System.out.println(key + " : " + property);
      }
    
  }

  private static void printActions(List actions) {
    for (int i = 0; i < actions.size(); i++) {
      String action = (String) actions.get(i);
      System.out.println(action);
    }
  }

}
