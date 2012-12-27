/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.ComplexFitness;
import evolutionaryComputation.IndividualStats;
import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;

import java.util.Random;

/**
 * @author Jose
 */
public class IndividualV1Factory extends IndividualFactory {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1Factory.class);

    public void setFitness(IndividualStats stats) {
        IndividualStats stats1 = stats;
    }


    @Override
    public IndividualV1 generateRandomCandidate(Random random) {
        if (logger.isDebugEnabled()) {
            logger.debug("generateRandomCandidate(Random) - start"); //$NON-NLS-1$
        }

        try {
            IndividualV1 i = new IndividualV1(true, ComplexFitness.class);
            i.createRandomIndividual();

            if (logger.isDebugEnabled()) {
                logger.debug("generateRandomCandidate(Random) - end"); //$NON-NLS-1$
            }
            return i;
        } catch (NullPointerException e) {
            logger.error("generateRandomCandidate(Random)", e); //$NON-NLS-1$

            e.printStackTrace();

            if (logger.isDebugEnabled()) {
                logger.debug("generateRandomCandidate(Random) - end"); //$NON-NLS-1$
            }
            return null;
        }
    }

}
