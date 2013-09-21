/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.IndividualV1;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 *
 * @author Jose
 */
public class HumanSelectionStrategy implements SelectionStrategy<IndividualV1>{

    double pruneValue;
    
    public <S extends IndividualV1> List<S> select(List<EvaluatedCandidate<S>> population, boolean naturalFitnessScores, int selectionSize, Random rng) {
        
        List<S> selectedList=new ArrayList<S>();
        
        for (EvaluatedCandidate<S> individual:population){
            if(individual.getFitness()>pruneValue){
            selectedList.add(individual.getCandidate());
        }
    }
        
       
        
        return selectedList;
      
    }



    
}
