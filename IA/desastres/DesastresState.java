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
  private static int ncenters;
  // Number of helicopters
  private static int nhelicopters;
  // Number of groups
  private static int ngroups;
  // Speed (on meters per minute) of all helicopters
  private static double helicopterSpeed;
  // Maximum people an helicopter can hold at the same time.
  private static int maximumHelicopterCapacity;
  // Expeditions containing groups
  private ArrayList<ArrayList<Grupo>> expeditions;
  // Helicopters containing the expeditions
  private ArrayList<ArrayList<ArrayList<Grupo>>> helicopters;
  // Sum of all trip times.
  private double typeASolutionCost;
  // Sum of trips which have at least one priority 1 group.
  private double typeBSolutionCost;
  
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
    helicopterSpeed = 100000.0/60.0;
    maximumHelicopterCapacity = 15;
    ncenters = nc;
    nhelicopters = nh;
    ngroups = ng;
    typeASolutionCost = 0.0;
    typeBSolutionCost = 0.0;
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
  public ArrayList<ArrayList<Grupo>> getExpeditions(int idH) {
    return helicopters.get(idH);
  }

  /*!\brief Returns the expedition in which the group g
   * is assigned to. If the group g does not have any 
   * expedition assigned to him, it returns -1;
   * @param [in] g Grupo
   */
  public int getExpedition(Grupo g){
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

  /*\!brief Returns the sum of all trip times.
   */
  public double getTypeASolutionCost() {
    return typeASolutionCost;
  }
  
  /*\!brief Return the sum of trip times of expeditions which have at least
            one priority 1 group.
   */
  public double getTypeBSolutionCost() {
    return typeBSolutionCost;
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

  /*!\brief Returns the distance between two groups
   *
   * @param [in] g1 Grupo 1   
   * @param [in] g2 Grupo 2   
   */
  public double getDistBetweenGroups (Grupo g1, Grupo g2) {
    return Math.sqrt( Math.pow(g2.getCoordX()-g1.getCoordX(),2) +  Math.pow(g1.getCoordY()-g2.getCoordY(),2) );
  }

  /*!\brief Returns the distance between a group
   * and a center
   *
   * @param [in] c Centro   
   * @param [in] g Grupo    
   */
  public double getDistBetweenCenterGroup (Centro c, Grupo g) {
    return Math.sqrt( Math.pow(g.getCoordX()-c.getCoordX(),2) +  Math.pow(c.getCoordY()-g.getCoordY(),2) );
  }
  
  /*!\brief Returns the time (in minutes) that would take to rescue all groups in an expedition
   *  if they are rescued in the given order and from a given center.
   *
   * @param [in] c Centro
   * @param [in] g Grupo
   */
  public double getTripCost(Centro c, ArrayList<Grupo> expedition) {
    //from center to first group
    double ret = getDistBetweenCenterGroup(c, expedition.get(0))/helicopterSpeed;
    //extra time per people
    ret += (expedition.get(0).getPrioridad() == 1 ? 2.0 : 1.0) * expedition.get(0).getNPersonas();
    for(int i=1; i<expedition.size(); ++i) {
        //from previous to next group
        ret += getDistBetweenGroups(expedition.get(i-1), expedition.get(i))/helicopterSpeed;
        //extra time per people
        ret += (expedition.get(i).getPrioridad() == 1 ? 2.0 : 1.0) * expedition.get(i).getNPersonas(); 
    }
    //from last group to center
    ret += getDistBetweenCenterGroup(c, expedition.get(expedition.size()-1))/helicopterSpeed;
    //we must add additional waiting minutes
    return ret + 10.0;
  }
  
  /*!\brief Rearranges an expedition, minimizing the trip cost it would have if rescued from a given center.
   *
   * @param [in] c Centro
   * @param [in] g Grupo
   */
  public void rearrangeExpeditionToOptimumTrip(Centro c, ArrayList<Grupo> expedition) {
    if(expedition.size()==1) return; // n=1 -> We'll always have the optimum arrangement.
    double currentCost = getTripCost(c, expedition);
    if(expedition.size()==2) { //n=2 -> We need to check the original and inverse order
        ArrayList<Grupo> test = new ArrayList<>();
        test.add(expedition.get(1));
        test.add(expedition.get(0));
        double theOtherCost = getTripCost(c, test);
        if(theOtherCost<currentCost) expedition =  new ArrayList<Grupo>(test);
    }
    else if(expedition.size()==3) { //n=3 -> We need to check 3! = 6 possible arrangements
        for(int i=0; i<expedition.size(); ++i) {
            for(int j=0; j<expedition.size(); ++j) {
                if(i!=j)
                    for(int k=0; k<expedition.size(); ++k) {
                        if(j!=k && i!=k) {
                            ArrayList<Grupo> test = new ArrayList<>();
                            test.add(expedition.get(i));
                            test.add(expedition.get(j));
                            test.add(expedition.get(k));
                            double theOtherCost = getTripCost(c, test);
                            if(theOtherCost<currentCost) {
                                currentCost = theOtherCost;
                                expedition = new ArrayList<Grupo>(test);
                            }
                        }
                    }
            }
        }
    }
  }
  /*\!brief Swaps two groups between their expeditions and readjusts the solution cost.
   * @param [in] a Grupo
     @param [in] b Grupo
   */
  public void swapGroupsBetweenExpeditions(Grupo a, Grupo b) {
    // Get expeditions and centers (needed to compute new trip times).
    ArrayList<Grupo> expeditionA = expeditions.get(getExpedition(a));
    int helicopterA = getHelicopter(expeditionA);
    Centro centerA = getCenter(helicopterA);
    ArrayList<Grupo> expeditionB = expeditions.get(getExpedition(b));
    int helicopterB = getHelicopter(expeditionB);
    Centro centerB = getCenter(helicopterB);
    // Remove from total cost the old trip costs.
    typeASolutionCost -= (getTripCost(centerA, expeditionA) + getTripCost(centerB, expeditionB));
    
    // Remove groups from their original expeditions, add them to their new.
    for(int i=0; i<expeditionA.size(); ++i) {
        if(expeditionA.get(i)==a) { 
            expeditionA.remove(i);
            break;
        }
    }
    expeditionA.add(a);
    // Same with B.
    for(int i=0; i<expeditionB.size(); ++i) {
        if(expeditionB.get(i)==b) {
            expeditionB.remove(i);
            break;
        }
    }
    expeditionB.add(b);
    // Rearrange the (new) expeditions to their optimum costs.
    rearrangeExpeditionToOptimumTrip(centerA, expeditionA);
    rearrangeExpeditionToOptimumTrip(centerB, expeditionB);
    // Add the new (optimum) trip costs.
    typeASolutionCost += getTripCost(centerA, expeditionA) + getTripCost(centerB, expeditionB);
  }
  
}
