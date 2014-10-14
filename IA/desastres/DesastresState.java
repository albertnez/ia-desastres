package IA.desastres;
import IA.Desastres.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

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
  // Helicopters containing the expeditions. For each helicopters, priority 1 expeditions
  // will appear before priority 2 expeditions.
  private ArrayList<ArrayList<ArrayList<Grupo>>> helicopters;
  // Time when last priority 1 groups is rescued in each helicopter
  private double[] typeBCostHelicopters;
  // Sum of all trip times.
  private double typeASolutionCost;
  // Time when last last priority 1 groups is rescued.
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
    typeBCostHelicopters = new double[nhelicopters];
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
      //update cost values
      double cost = getTripCost(helicoptersCenter[ind], expeditions.get(i));
      typeASolutionCost += cost;
      if (expIsHighPriority(expeditions.get(i))) typeBSolutionCost += cost;
      ind = (ind + 1)%nhelicopters;
    }
    // Rearrange expeditions in each helicopter, so that priority 1 ones are executed first

    for (int hInd = 0; hInd < nhelicopters; ++hInd) {
      ArrayList<ArrayList<Grupo>> heli = helicopters.get(hInd);
      int rInd = 0;
      double cost = 0.0;
      for (int i = 0; i < heli.size(); ++i) {
        if (expIsHighPriority(heli.get(0))) {
          if (i != rInd) {
            Collections.swap(heli, rInd, i);
          }
          cost += getTripCost(helicoptersCenter[hInd], heli.get(rInd));
          ++rInd;
        }
      }
      // Update global typeBSolution cost
      typeBCostHelicopters[hInd] = cost;
      typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, cost);
    }
  }

  /*!\brief Copy constructor
   *
   * @param [in] d DesastresState object we want to copy
   */
  public DesastresState(DesastresState d) {
    expeditions = new ArrayList< ArrayList<Grupo> > (d.getAllExpeditions());
    helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>> (d.getAllHelicopters());
    typeASolutionCost = d.getTypeASolutionCost();
    typeBSolutionCost = d.getTypeBSolutionCost();
  }
  
  
  /*!\brief Returns the number of centers
   *
   */
  public int getNCenters(){
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
  /*!\brief Returns all helicopters
   *
   */
  public ArrayList<ArrayList<ArrayList<Grupo>>> getAllHelicopters() {
    return helicopters;
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
  /*!\brief Returns all expeditions.
  */
  public ArrayList< ArrayList<Grupo> > getAllExpeditions() {
    return expeditions;
  }
  
  
  /*!\brief Returns the helicopter id in which the expedition 
   * exp is assigned to. If the expedition exp does not
   * exist, a -1 is returned. 
   * @param [in] exp ArrayList of the Grupos that form the expedition
   */
  public int getHelicopter(ArrayList<Grupo> exp){
    for (int i = 0; i < helicopters.size(); ++i){
      if (helicopters.get(i).contains(exp)) return i;
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
  
  /*\!brief Return the time when last priority 1 group is rescued.
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
   *        The helicopter that recieves the new group must be able to carry the amount
   *        of people of that group.
   * @param [in] a Grupo
   * @param [in] b Grupo
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
    double tripcostA = getTripCost(centerA, expeditionA);
    double tripcostB = getTripCost(centerB, expeditionB);
    typeASolutionCost -= tripcostA+tripcostB;
    boolean is_urgentA = false, is_urgentB = false;

    for (int i = 0; i < expeditionA.size(); ++i) {
      if (expeditionA.get(i).getPrioridad() == 1) is_urgentA = true;
    }
    if (is_urgentA) typeBSolutionCost -= tripcostA;

    for (int i = 0; i < expeditionB.size(); ++i) {
      if (expeditionB.get(i).getPrioridad() == 1) is_urgentB = true;
    }
    if (is_urgentB) typeBSolutionCost -= tripcostB;

    // Remove groups from their original expeditions, add them to their new.
    expeditionA.remove(a);
    expeditionA.add(b);
    // Same with B.
    expeditionB.remove(b);
    expeditionB.add(a);
    // Rearrange the (new) expeditions to their optimum costs.
    rearrangeExpeditionToOptimumTrip(centerA, expeditionA);
    rearrangeExpeditionToOptimumTrip(centerB, expeditionB);
    // Add the new (optimum) trip costs.
    tripcostA = getTripCost(centerA, expeditionA);
    tripcostB = getTripCost(centerB, expeditionB);
    typeASolutionCost += tripcostA + tripcostB;
    if (is_urgentA) typeBSolutionCost += tripcostA;
    if (is_urgentB) typeBSolutionCost += tripcostB;
  }

  /*\!brief Moves a group from its expedition, to the desired expedition and readjusts the
   *        solution cost. The expedition that recieves the group should not exceed helicopters
   *        capacity nor have more than 3 groups. 
   * @param [in] g Group being movedExpedition 1
   * @param [in] dst Expedition destiny where g will be moved
   */
  // TODO change typeBSolutionCost
  public void moveGroupBetweenExpeditions(Grupo g, ArrayList<Grupo> dst) {
    ArrayList<Grupo> src = expeditions.get(getExpedition(g));
    int srcH = getHelicopter(src);
    int dstH = getHelicopter(dst);

    Centro srcCenter = getCenter(srcH);
    Centro dstCenter = getCenter(dstH);

    double srcTripCost = getTripCost(srcCenter, src);
    double dstTripCost = getTripCost(dstCenter, dst);

    typeASolutionCost -= (srcTripCost + dstTripCost);

    dst.add(g);
    src.remove(g);

    // Recalculate dst expedition cost
    rearrangeExpeditionToOptimumTrip(dstCenter, dst);
    dstTripCost = getTripCost(dstCenter, dst);
    typeASolutionCost += dstTripCost;

    // If the source expedition is not empty, recalculate
    if (src.size() > 0) {
      rearrangeExpeditionToOptimumTrip(srcCenter, src);
      srcTripCost = getTripCost(srcCenter, src);
      typeASolutionCost += srcTripCost;
    }
  }

  
  /*\!brief Moves a expedition from its helicopter, to the desired helicopter and readjusts the
   *        solution cost.
   * @param [in] exp expedition being moved to helicopter heli
   * @param [in] heli helicopter where the expedition exp will be moved
   */
  // TODO change typeBSolutionCost
  public void moveExpeBetweenHeliopters(ArrayList<Grupo> exp, ArrayList<ArrayList<Grupo>> heli) {
    int srcH = getHelicopter(exp);
    int dstH = helicopters.indexOf(heli);
    Centro srcCenter = getCenter(srcH);
    Centro dstCenter = getCenter(dstH);

    typeASolutionCost -= getTripCost(srcCenter, exp);

    heli.add(exp);
    helicopters.get(srcH).remove(exp);

    rearrangeExpeditionToOptimumTrip(dstCenter, exp);

    typeASolutionCost += getTripCost(dstCenter, exp);
  }

  /*\!brief Swaps two expeditions between their helicopters and readjusts the solution cost.
   *        The helicopter that recieves the new expedition must be able to carry the amount
   *        of people of that group, having present that new places have been freed due to 
   *        the swap of the expeditions.
   * @param [in] exp1 Expedition 1
   * @param [in] exp2 Expedition 2
   */
  public void swapExpeBetweenHelicopters (ArrayList<Grupo> exp1, ArrayList<Grupo> exp2){
    int helicopterA = getHelicopter(exp1);
    Centro centerA = getCenter(helicopterA);
    int helicopterB = getHelicopter(exp2);
    Centro centerB = getCenter(helicopterB);

    double tripcostA = getTripCost(centerA, exp1);
    double tripcostB = getTripCost(centerB, exp2);
    typeASolutionCost -= tripcostA+tripcostB;
    boolean is_urgentA = false, is_urgentB = false;

    for (int i = 0; i < exp1.size(); ++i) {
      if (exp1.get(i).getPrioridad() == 1) is_urgentA = true;
    }
    if (is_urgentA) typeBSolutionCost -= tripcostA;

    for (int i = 0; i < exp2.size(); ++i) {
      if (exp2.get(i).getPrioridad() == 1) is_urgentB = true;
    }
    if (is_urgentB) typeBSolutionCost -= tripcostB;

    ArrayList<Grupo> newexp1 = new ArrayList<Grupo> (exp1.size());
    for(Grupo item: exp1) newexp1.add(item); 
    //tenir present que item NO es una copia de cada grup, sino una referencia al mateix objecte
    ArrayList<Grupo> newexp2 = new ArrayList<Grupo> (exp2.size());
    for(Grupo item: exp2) newexp2.add(item);

    rearrangeExpeditionToOptimumTrip(centerA, newexp2);
    rearrangeExpeditionToOptimumTrip(centerB, newexp1);

    helicopters.get(helicopterA).remove(exp1);
    helicopters.get(helicopterA).add(newexp2);

    helicopters.get(helicopterB).remove(exp2);
    helicopters.get(helicopterB).add(newexp1);

    tripcostA = getTripCost(centerA, newexp2);
    tripcostB = getTripCost(centerB, newexp1);
    typeASolutionCost += tripcostA + tripcostB;
    if (is_urgentA) typeBSolutionCost += tripcostA;
    if (is_urgentB) typeBSolutionCost += tripcostB; 
  }

  /*!\brief Returns true if the expedition contains a priority 1 group.
   *
   * @param [in] exp Expedition
   */
  public boolean expIsHighPriority (ArrayList<Grupo> exp) {
    for (Grupo g : exp) {
      if (g.getPrioridad() == 1) return true;
    }
    return false;
  }
}
