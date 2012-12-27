/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

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
public class IndividualV1Crossover extends AbstractCrossover<IndividualV1> {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1Crossover.class);

    public IndividualV1Crossover(int crossoverPoints) {
        super(crossoverPoints);
    }

    public IndividualV1Crossover(int crossoverPoints, Probability crossoverProbability) {
        super(crossoverPoints, crossoverProbability);
    }

    public IndividualV1Crossover(NumberGenerator<Integer> crossoverPointsVariable) {
        super(crossoverPointsVariable);
    }

    public IndividualV1Crossover(NumberGenerator<Integer> crossoverPointsVariable, NumberGenerator<Probability> crossoverProbabilityVariable) {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }

    @Override
    protected List<IndividualV1> mate(IndividualV1 t, IndividualV1 t1, int i, Random random) {
        if (logger.isDebugEnabled()) {
            logger.debug("mate(IndividualV1, IndividualV1, int, Random) - start"); //$NON-NLS-1$
        }

        IntArrayCrossover arrayXover;
        arrayXover = new IntArrayCrossover(i);
        List<int[]> chromList = new ArrayList<int[]>();
        chromList.add(t.getChromosome());
        chromList.add(t1.getChromosome());
        List<int[]> evolvedChromList = arrayXover.apply(chromList, random);

        List<IndividualV1> evolvedIndividuals = new ArrayList<IndividualV1>();
        IndividualV1 i1 = new IndividualV1(t);
        IndividualV1 i2 = new IndividualV1(t1);
        i1.setChromosome(evolvedChromList.get(0));
        i2.setChromosome(evolvedChromList.get(1));
        evolvedIndividuals.add(i1);
        evolvedIndividuals.add(i2);

        if (logger.isDebugEnabled()) {
            logger.debug("mate(IndividualV1, IndividualV1, int, Random) - end"); //$NON-NLS-1$
        }
        return evolvedIndividuals;
    }
}
