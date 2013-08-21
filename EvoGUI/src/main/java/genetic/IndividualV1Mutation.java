/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jose
 */
public class IndividualV1Mutation implements EvolutionaryOperator<IndividualV1> {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1Mutation.class);
    private double relativeMutationRatio;
    private double probability;

    public IndividualV1Mutation(double relativeMutationRatio, double probability) {
        this.relativeMutationRatio = relativeMutationRatio;
        this.probability = probability;
    }

    public List<IndividualV1> apply(List<IndividualV1> selectedCandidates, Random rng) {
        if (logger.isDebugEnabled()) {
            logger.debug("apply(List<IndividualV1>, Random) - start"); //$NON-NLS-1$
        }

        List<IndividualV1> result = new ArrayList<IndividualV1>(selectedCandidates.size());
        for (IndividualV1 candidate : selectedCandidates) {
            IndividualV1 individual = new IndividualV1(candidate);
            individual.shouldEvaluate = true;
            int chromosome[] = individual.getChromosome();
            for (int i = 0; i < individual.chromosomeSize(); i++) {
                boolean okMutation = false;
      
                    double r = rng.nextDouble();


                    if (r >= probability) {
                                  do {
                        chromosome[i] += 2 * (rng.nextDouble() - 0.5) * relativeMutationRatio * chromosome[i];
                        switch (i) {
                            case 0:
                                    if(chromosome[i]>IndividualV1.CLOSEDISTANCE){
                                    chromosome[i]=IndividualV1.CLOSEDISTANCE;
                                }
                                okMutation = chromosome[0] <= chromosome[1];
                                break;
                            case 1:
                                    if(chromosome[i]<IndividualV1.MEDIUMDISTANCE){
                                    chromosome[i]=IndividualV1.MEDIUMDISTANCE;
                                }
                                okMutation = chromosome[1] > chromosome[0];
                                break;
                            case 2:
                                if(chromosome[i]<IndividualV1.FARDISTANCE){
                                    chromosome[i]=IndividualV1.FARDISTANCE;
                                }
                                okMutation = chromosome[1] <= chromosome[2];
                                break;
                            default:
                                okMutation = true;
                                break;
                        }
                    }while (!okMutation);
                } 
            }
            result.add(individual);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("apply(List<IndividualV1>, Random) - end"); //$NON-NLS-1$
        }

        return result;
    }
}
