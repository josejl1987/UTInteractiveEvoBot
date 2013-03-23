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
public class IndividualV1AlternateEvaluator implements FitnessEvaluator<IndividualV1> {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1Evaluator.class);

    @Override
    public double getFitness(IndividualV1 t, List<? extends IndividualV1> list) {
        if (logger.isDebugEnabled()) {
            logger.debug("getFitness(IndividualV1, List<? extends IndividualV1>) - start"); //$NON-NLS-1$
        }

        double returndouble = t.fitness();
        if (logger.isDebugEnabled()) {
            logger.debug("getFitness(IndividualV1, List<? extends IndividualV1>) - end"); //$NON-NLS-1$
        }
        return returndouble;
    }

    @Override
    public boolean isNatural() {
        return true;
    }

}
