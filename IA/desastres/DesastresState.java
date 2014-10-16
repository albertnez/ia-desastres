package IA.desastres;
import IA.Desastres.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class DesastresState {
  //Strings for printing out the result
  public static String INTERCAMBIO_GRUPOS = "Intercambio de los grupos";
  public static String MOVER_GRUPO_EXPEDICION = "Movemos un grupo a una expedición";
  public static String CREAR_EXPEDICION = "Creamos una expedicion y movemos el grupo";

  private static Centros centers;
  private static Grupos groups;
  // Array where the i-th Center is the center of the i-th helicopter
  private static Centro[] helicoptersCenter;

  // Number of centers 
  private static int ncenters;
  // Number of helicopters per center
  private static int nhelicopterspercenter;
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

  /*!\brief Returns true if the expedition contains a priority 1 group.
   *
   * @param [in] exp Expedition
   */
  private boolean expIsHighPriority (ArrayList<Grupo> exp) {
    for (Grupo g : exp) {
      if (g.getPrioridad() == 1) return true;
    }
    return false;
  }

  /*!\brief Update the typeBSolutionCost from individual typeBCost of each helicopter
   */
  private void updateTypeBSolutionCost() {
    typeBSolutionCost = 0.0;
    for (int i = 0; i < typeBCostHelicopters.length; ++i) {
      typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, typeBCostHelicopters[i]);
    }
  }

  /*!\brief Rearranges an expedition from an helicopter so that priority order is consistent
   *
   * As expeditions with priority 1 should be done before priority 2 expeditions, exp must be
   * rearranged so that this order is consistent
   * 
   * @param [in] exp Expedition that may need to be moved
   * @param [in] heli Helicopter that contains that expedition
   */
  private void rearrangeExpeditionByPriority(ArrayList<Grupo> exp, ArrayList<ArrayList<Grupo>> heli) {
    int ind = heli.indexOf(exp);
    if (expIsHighPriority(exp)) {
      // If it has high priority, it should be moved to the left until finds another priority 1 expedition
      while (ind-1 >= 0 && !expIsHighPriority(heli.get(ind-1))) {
        Collections.swap(heli, ind-1, ind);
        --ind;
      }
    }
    else {
      // Else, move to the right until end or finds another priority 2 expedition
      while (ind+1 < heli.size() && expIsHighPriority(heli.get(ind))) {
        Collections.swap(heli, ind, ind+1);
        ++ind;
      }
    }
  }

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
  public DesastresState(int nc, int nh, int ng, int seed) {
    centers = new Centros(nc, nh, seed);
    groups = new Grupos(ng, seed);
    helicopterSpeed = 100000.0/60.0;
    maximumHelicopterCapacity = 15;
    ncenters = nc;
    nhelicopterspercenter = nh;
    ngroups = ng;
    typeBCostHelicopters = new double[nhelicopterspercenter * ncenters];
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
    helicoptersCenter = new Centro[nh*nc]; 
    int ind = 0;
    for (Centro c : centers) {
      for (int i = 0; i < c.getNHelicopteros(); ++i) {
        helicoptersCenter[i] = c;
        ++ind;
      }
    }

    // Assign each expedition to one helicopter
    helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(nh*nc);
    while (helicopters.size() < nh*nc) {
      helicopters.add(new ArrayList<ArrayList<Grupo>>());
    }
    ind = 0;
    for (int i = 0; i < ngroups; ++i) {
      helicopters.get(ind).add(expeditions.get(i));
      //update cost values
      double cost = getTripCost(helicoptersCenter[ind], expeditions.get(i));
      typeASolutionCost += cost;
      if (expIsHighPriority(expeditions.get(i))) typeBSolutionCost += cost;
      ind = (ind + 1)%(nhelicopterspercenter*ncenters);
    }
    // Rearrange expeditions in each helicopter, so that priority 1 ones are executed first

    for (int hInd = 0; hInd < nhelicopterspercenter*ncenters; ++hInd) {
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
      // Update global typeBSolution cost. typeBSolution will always have a +10 in time
      // that should be substracted when retrieving its value
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
    typeBCostHelicopters = d.getTypeBCostHelicopters().clone();
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
  public int getNHelicoptersPerCenter(){
    return nhelicopterspercenter;
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

 /*!\brief Returns the total number of helicopters
  *
  */
  public int getTotalHelicopters(){
    return helicopters.size();
  }
  
 /*!\brief Returns the total number of expeditions
  * performed by helicopter idH.
  * @param [in] idH ID of the helicopter
  */
  public int getNumExpeditionsHeli(int idH){
    return helicopters.get(idH).size();
  }
  
  
  /*!\brief Returns the id of the expedition in which the group g
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

  /*\!brief Returns array containing the typeBCost of each helicopters
   */
  public double[] getTypeBCostHelicopters() {
    return typeBCostHelicopters;
  }

  /*\!brief Returns the sum of all trip times.
   */
  public double getTypeASolutionCost() {
    if (typeASolutionCost > 0) return typeASolutionCost-10;
    else return 0;
  }
  
  /*\!brief Return the time when last priority 1 group is rescued.
   */
  public double getTypeBSolutionCost() {
    if (typeBSolutionCost > 0) return typeBSolutionCost-10;
    else return 0;
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
    boolean updateTypeB = false;

    // Substract typeBCost as they have to be recalculated
    if (expIsHighPriority(expeditionA)) is_urgentA = true;
    if (is_urgentA) {
      if (typeBCostHelicopters[helicopterA] == typeBSolutionCost) updateTypeB = true;
      typeBCostHelicopters[helicopterA] -= tripcostA;
    }

    if (expIsHighPriority(expeditionB)) is_urgentB = true;
    if (is_urgentB) {
      if (typeBCostHelicopters[helicopterB] == typeBSolutionCost) updateTypeB = true;
      typeBCostHelicopters[helicopterB] -= tripcostB;
    }

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
    
    boolean newIsUrgentA = false, newIsUrgentB = false;
    if (expIsHighPriority(expeditionA)) newIsUrgentA = true;
    if (expIsHighPriority(expeditionB)) newIsUrgentB = true;
    
    // If there is a change, rearrange
    if (is_urgentA != newIsUrgentA) {
      rearrangeExpeditionByPriority(expeditionA, helicopters.get(helicopterA));
    }
    // Same for b
    if (is_urgentB != newIsUrgentB) {
      rearrangeExpeditionByPriority(expeditionB, helicopters.get(helicopterB));
    }

    // Now update type B costs if required
    if (newIsUrgentA) typeBCostHelicopters[helicopterA] += tripcostA;
    if (newIsUrgentB) typeBCostHelicopters[helicopterB] += tripcostB;
    // If one of these costs are greater or equal, update
    if (typeBCostHelicopters[helicopterA] >= typeBSolutionCost || 
        typeBCostHelicopters[helicopterB] >= typeBSolutionCost) {
      typeBSolutionCost = java.lang.Math.max(typeBCostHelicopters[helicopterA], 
                                             typeBCostHelicopters[helicopterB]);
      updateTypeB = false;
    }
    //If typeB needs to be updated, update with other helicopters value
    if (updateTypeB) {
      updateTypeBSolutionCost();
    }
  }

  /*\!brief Moves a group from its expedition, to the desired expedition and readjusts the
   *        solution cost. The expedition that recieves the group should not exceed helicopters
   *        capacity nor have 3 or more groups and should exist. The expedition dst must NOT be
   *        empty.
   * @param [in] g Group being moved
   * @param [in] dst Expedition destiny where g will be moved
   */
  public void moveGroupBetweenExpeditions(Grupo g, ArrayList<Grupo> dst) {
    ArrayList<Grupo> src = expeditions.get(getExpedition(g));
    int srcH = getHelicopter(src);
    int dstH = getHelicopter(dst);

    Centro srcCenter = getCenter(srcH);
    Centro dstCenter = getCenter(dstH);

    double srcTripCost = getTripCost(srcCenter, src);
    double dstTripCost = getTripCost(dstCenter, dst);

    typeASolutionCost -= (srcTripCost + dstTripCost);

    boolean oldSrcPriority = expIsHighPriority(src);
    boolean oldDstPriority = expIsHighPriority(dst);
    // As typeBcost of source is the only one that can decrease, we may update it only if it equals
    // the typeBSolutionCost now, and then src and dst cost is less.
    boolean updateTypeB = (typeBCostHelicopters[srcH] == typeBSolutionCost);

    if (oldSrcPriority) typeBCostHelicopters[srcH] -= srcTripCost;
    if (oldDstPriority) typeBCostHelicopters[dstH] -= dstTripCost;

    dst.add(g);
    src.remove(g);

    boolean nowSrcPriority = expIsHighPriority(src);
    boolean nowDstPriority = expIsHighPriority(dst);

    // Recalculate dst expedition cost
    rearrangeExpeditionToOptimumTrip(dstCenter, dst);
    dstTripCost = getTripCost(dstCenter, dst);
    typeASolutionCost += dstTripCost;

    if (nowDstPriority) typeBCostHelicopters[dstH] += dstTripCost;
    if (typeBCostHelicopters[dstH] >= typeBSolutionCost) {
      typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, typeBCostHelicopters[dstH]); 
      updateTypeB = false;
    }

    // If the source expedition is not empty, recalculate
    if (src.size() > 0) {
      rearrangeExpeditionToOptimumTrip(srcCenter, src);
      srcTripCost = getTripCost(srcCenter, src);
      typeASolutionCost += srcTripCost;

      if (nowSrcPriority) typeBCostHelicopters[srcH] += srcTripCost;
      if (typeBCostHelicopters[srcH] >= typeBSolutionCost) {
        typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, typeBCostHelicopters[srcH]);
        updateTypeB = false;
      }
    }
    else {
      helicopters.get(srcH).remove(src);
      expeditions.remove(src);
    }

    if (updateTypeB) {
      updateTypeBSolutionCost();
    }
  }

  /*\!brief Creates a expedition to be performed by the helicopter
   * heli with the group g and eliminates the group g from its former
   * expedition. The helicopter heli must have all it's others
   * expeditions full or have no expeditions at all.  
   * @param [in] g initial Grupo of the expedition
   * @param [in] heli Helicopter that performs the new expedition
   */
  public void moveGroupToNonExistentExpedition (Grupo g, int heli){
    ArrayList<Grupo> oldexp = expeditions.get(getExpedition(g));
    ArrayList<Grupo> newexp = new ArrayList<Grupo>();
    int srcH = getHelicopter(oldexp);

    Centro srcCenter = getCenter(srcH);
    Centro dstCenter = getCenter(heli);

    double srcTripCost = getTripCost(srcCenter, oldexp);

    typeASolutionCost -= srcTripCost;

    // As typeBcost of source is the only one that can decrease, we may update it only if it equals
    // the typeBSolutionCost now, and then src and dst cost is less.
    boolean updateTypeB = (typeBCostHelicopters[srcH] == typeBSolutionCost);

    if (expIsHighPriority(oldexp)) typeBCostHelicopters[srcH] -= srcTripCost;

    oldexp.remove(g);
    newexp.add(g);
    helicopters.get(heli).add(newexp);
    expeditions.add(newexp);

    rearrangeExpeditionToOptimumTrip(dstCenter, newexp);
    double dstTripCost = getTripCost(dstCenter, newexp);
    typeASolutionCost += dstTripCost;

    //If group g was priority 1 add it to the cost of heli
    if (g.getPrioridad() == 1) 
      typeBCostHelicopters[heli] += dstTripCost;

    if (typeBCostHelicopters[heli] >= typeBSolutionCost) {
      typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, typeBCostHelicopters[heli]); 
      updateTypeB = false;
    }

    // If the source expedition is not empty, recalculate
    if (oldexp.size() > 0) {
      rearrangeExpeditionToOptimumTrip(srcCenter, oldexp);
      srcTripCost = getTripCost(srcCenter, oldexp);
      typeASolutionCost += srcTripCost;
      //if oldexp still has a group with priority 1, then add its cost to the array
      if (expIsHighPriority(oldexp)) 
        typeBCostHelicopters[srcH] += srcTripCost;

      if (typeBCostHelicopters[srcH] >= typeBSolutionCost) {
        typeBSolutionCost = java.lang.Math.max(typeBSolutionCost, typeBCostHelicopters[srcH]);
        updateTypeB = false;
      }
    }
    else {
      helicopters.get(srcH).remove(oldexp);
      expeditions.remove(oldexp); 
    }
    if (updateTypeB) updateTypeBSolutionCost();
  }

  /*!\brief Returns the state in string form
   *
   */
  public String toString() {
    String retVal = "";
    for (int i = 0; i < helicopters.size(); ++i){
      retVal += "Helicoptero " + i + " pertenece al centro en " + helicoptersCenter[i].getCoordX() + " " + helicoptersCenter[i].getCoordX() + ":\n";
      ArrayList<ArrayList<Grupo>> heli = helicopters.get(i);
      for (int j = 0; j < heli.size(); ++j){
        ArrayList<Grupo> exp = heli.get(j);
        retVal += "\tExpedición " + expeditions.indexOf(exp) + " recoje a los grupos:\n";
        for (int k =0; k < exp.size(); ++k){
          retVal += "\t\tGrupo en: " + exp.get(k).getCoordX() + " " + exp.get(k).getCoordY() + "\n";
        }
      }
    }
    return retVal;
  }

}
