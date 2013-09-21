/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.HumanityFitness;
import evolutionaryComputation.IndividualV1;
import java.util.List;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 *
 * @author Jose
 */
public class IndividualV1HumanEvaluator implements FitnessEvaluator<IndividualV1>  {

    public double getFitness(IndividualV1 candidate, List<? extends IndividualV1> population) {
        if(candidate.getFitnessClass().equals(HumanityFitness.class)){
            HumanityFitness a=(HumanityFitness) candidate.getStats();
            return  a.getHumanFitness();
        }
        return -100;
    }

    public boolean isNatural() {
       return true;
    }
    
    

    
    
}
