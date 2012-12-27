/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.ComplexFitness;
import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jose
 */
public class IndividualCrossover extends AbstractCrossover<Individual> {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualCrossover.class);

    public IndividualCrossover(int crossoverPoints) {
        super(crossoverPoints);
    }

    public IndividualCrossover(int crossoverPoints, Probability crossoverProbability) {
        super(crossoverPoints, crossoverProbability);
    }

    public IndividualCrossover(NumberGenerator<Integer> crossoverPointsVariable) {
        super(crossoverPointsVariable);
    }

    public IndividualCrossover(NumberGenerator<Integer> crossoverPointsVariable, NumberGenerator<Probability> crossoverProbabilityVariable) {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }

    @Override
    protected List<Individual> mate(Individual t, Individual t1, int i, Random random) {
        if (logger.isDebugEnabled()) {
            logger.debug("mate(Individual, Individual, int, Random) - start"); //$NON-NLS-1$
        }

        IntArrayCrossover arrayXover;
        arrayXover = new IntArrayCrossover(i);
        List<int[]> chromList = new ArrayList<int[]>();
        chromList.add(t.getChromosome());
        chromList.add(t1.getChromosome());
        List<int[]> evolvedChromList = arrayXover.apply(chromList, random);

        List<Individual> evolvedIndividuals = new ArrayList<Individual>();
        IndividualV1 i1 = new IndividualV1(true, ComplexFitness.class);
        IndividualV1 i2 = new IndividualV1(true, ComplexFitness.class);
        i1.setChromosome(evolvedChromList.get(0));
        i2.setChromosome(evolvedChromList.get(1));
        evolvedIndividuals.add(i1);
        evolvedIndividuals.add(i2);

        if (logger.isDebugEnabled()) {
            logger.debug("mate(Individual, Individual, int, Random) - end"); //$NON-NLS-1$
        }
        return evolvedIndividuals;
    }


}
