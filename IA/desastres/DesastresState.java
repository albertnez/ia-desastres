package IA.desastres;
import IA.Desastres.*;

import java.util.Random;
import java.util.ArrayList;

public class DesastresState {
  private static Centros centers;
  private static Grupos groups;
  // Array where the i-th Center is the center of the i-th helicopter
  private static Centro[] helicoptersCenter;

  // Number of centers 
  private int ncenters;
  // Number of helicopters
  private int nhelicopters;
  // Number of groups
  private int ngroups;

  // Expeditions containing groups
  private ArrayList<ArrayList<Grupo>> expeditions;
  // Helicopters containing the expeditions
  private ArrayList<ArrayList<ArrayList<Grupo>>> helicopters;

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
    expeditions = new ArrayList<ArrayList<Grupo>>(ngroups);
    for (Grupo g : groups) {
      ArrayList<Grupo> a = new ArrayList<Grupo>();
      a.add(g);
      expeditions.add(a);
    }

    // Assign the centers of each helicopter helicoptersCenters
    helicoptersCenter = new Centro[nh]; 
    int ind = 0;
    for (Centro c : centers) {
      for (int i = 0; i < c.getNHelicopteros(); ++i) {
        helicoptersCenter[i] = c;
        ++ind;
      }
    }

    // Assign each expedition to one helicopter
    helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(nh);
    while (helicopters.size() < nh) {
      helicopters.add(new ArrayList<ArrayList<Grupo>>());
    }
    ind = 0;
    for (int i = 0; i < ngroups; ++i) {
      helicopters.get(ind).add(expeditions.get(i));
      ind = (ind + 1)%nhelicopters;
    }
  }

  /*!\brief Returns the number of centers
   *
   */
  public int getNCenters  (){
    return ncenters;
  }

  /*!\brief Returns the number of helicopters
   *
   */
  public int getNHelicopters(){
    return nhelicopters;
  }

  /*!\brief Returns the number of helicopters
   *
   */
  public int getNGroups(){
    return ngroups;
  }

  /*!\brief Returns the expeditions assigned to
   * helicopter id.
   * @param [in] idH ID of the helicopter
   */
  public ArrayList<ArrayList<Grupo>> getExpenditions(int idH){
    return helicopters.get(idH);
  }

  /*!\brief Returns the expedition in which the group g
   * is assigned to. If the group g does not have any 
   * expedition assigned to him, it returns -1;
   * @param [in] g Grupo
   */
  public int getExpendition(Grupo g){
    for (int i = 0; i < expeditions.size(); ++i){
      ArrayList<Grupo> ex = expeditions.get(i);
      int s = ex.size();
      for (int j = 0; j < s; ++j){
        if (ex.get(j) == g) return i;
      }
    }
    return -1;
  }

  /*!\brief Returns the helicopter in which the expedition 
   * exp is assigned to. If the expedition exp does not
   * exist, a -1 is returned. 
   * @param [in] exp ArrayList of the Grupos that form the expedition
   */
  public int getHelicopter(ArrayList<Grupo> exp){
    for (int i = 0; i < helicopters.size(); ++i){
      if (exp.equals(helicopters.get(i))) return i;
    }
    return -1;
  }

  /*!\brief Returns the groups assigned to
   * the expedition id.
   * @param [in] idE ID of the expedition
   */
  public ArrayList<Grupo> getGroups(int idE){
    return expeditions.get(idE);
  }

  /*!\brief Returns the center in which helicopter
   * id belongs to
   * @param [in] idH ID of the helicopter 
   */
  public Centro getCenter(int idH){
    return helicoptersCenter[idH];
  }

  /*!\brief Returns the helicopters of center c
   *
   * @param [in] c Centro   
   */
  public ArrayList<Integer> getHelicopters(Centro c){
    ArrayList<Integer> retVal = new ArrayList<Integer>();
    for (int i = 0; i < helicoptersCenter.length; ++i){
      if (helicoptersCenter[i] == c) retVal.add(i);
    }
    return retVal;
  }
}
