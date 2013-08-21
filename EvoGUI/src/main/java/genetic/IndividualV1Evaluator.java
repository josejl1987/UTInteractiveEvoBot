/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * @author Jose
 */
public class IndividualV1Evaluator implements FitnessEvaluator<IndividualV1> {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1Evaluator.class);

    public boolean isNatural() {
        return true;
    }

    public double getFitness(IndividualV1 candidate, List<? extends IndividualV1> population) {
        if (logger.isDebugEnabled()) {
            logger.debug("getFitness(IndividualV1, List<? extends IndividualV1>) - start"); //$NON-NLS-1$
        }

        double returndouble = candidate.getStats().getAverageFitness();
        if (logger.isDebugEnabled()) {
            logger.debug("getFitness(IndividualV1, List<? extends IndividualV1>) - end"); //$NON-NLS-1$
        }
        return returndouble;
    }
}
