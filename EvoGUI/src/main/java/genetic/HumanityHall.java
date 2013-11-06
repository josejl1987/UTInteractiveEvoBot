/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.IndividualV1;
import java.util.ArrayList;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * @author Jose
 */
public class HumanityHall {
    
    ArrayList<IndividualV1> humanityList;

    public HumanityHall() {
        humanityList=new ArrayList<IndividualV1>();
        
    }
 
    
    public void addIndividual(IndividualV1 i){
        humanityList.add(i);
    }
    
    public void calculateHumanity(IndividualV1 i){
        double humanity=0;
        
        for(IndividualV1 h:humanityList){
        humanity+=MathArrays.distance(h.getChromosome(), i.getChromosome())/IndividualV1.distance/humanityList.size();
        }
        
        
    }
    
    
}