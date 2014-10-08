package IA.desastres;
import IA.Desastres.*;

import java.util.Random;
import java.util.ArrayList;

public class DesastresState {
  private Centros centers;
  private Grupos groups;

  // Number of centers 
  private int ncenters;
  // Number of helicopters
  private int nhelicopters;
  // Number of groups
  private int ngroups;

  // Expeditions containing groups
  private ArrayList<ArrayList<Integer>> expeditions;
  // Helicopters cointaining the expeditions
  private ArrayList<ArrayList<ArrayList<Integer>>> helicopters;

  /*!\brief Generates an instance of Desastres problem with an initial solution
   *
   * Creates a new instance of the problem with nc centers, nh helicopters and ng groups
   * with an initial solution which consists of expeditions of 1 group each, and each expedition
   * being executed by one different helicopter chosen in the order they appear
   * 
   * @param [in] nc Number of centers
   * @param [in] nh Number of helicopters
   * @param [in] ng Number of groups
   */
  public DesastresState(int nc, int nh, int ng) {
    Random myRandom = new Random();
    int seed = myRandom.nextInt();
    centers = new Centros(nc, nh, seed);
    groups = new Grupos(ng, seed);

    ncenters = nc;
    nhelicopters = nh;
    ngroups = ng;

    // Assign each group to one expedition.
    expeditions = new ArrayList<ArrayList<Integer>>(ngroups);
    for (int i = 0; i < ngroups; ++i) {
      expeditions.get(i).add(i);
    }


    // Assign each expedition to one helicopter
    helicopters = new ArrayList<ArrayList<ArrayList<Integer>>>(nhelicopters);
    int ind = 0;
    for (int i = 0; i < ngroups; ++i) {
      helicopters.get(i).add(expeditions.get(i));
    }
  }

}
