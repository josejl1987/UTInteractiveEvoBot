/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import GUI.EvolutionMain;
import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;
import org.uncommons.watchmaker.framework.*;

import java.io.IOException;
import java.security.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Jose
 */
public class IndividualV1EvolutionEngine extends GenerationalEvolutionEngine<IndividualV1> {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IndividualV1EvolutionEngine.class);
    private EvolutionMain main;
    private long startTime;
    private int currentGeneration;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }
    
    public  List<EvaluatedCandidate<IndividualV1>> evaluatePopulationArray(IndividualV1[] population){
        
       
        return  evaluatePopulation(Arrays.asList(population));
        
    }
    public IndividualV1EvolutionEngine(
            CandidateFactory<IndividualV1> candidateFactory,
            EvolutionaryOperator<IndividualV1> evolutionScheme,
            FitnessEvaluator<? super IndividualV1> fitnessEvaluator,
            SelectionStrategy<? super IndividualV1> selectionStrategy,
            Random rng, EvolutionMain main) {

        super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy,
                rng);
        this.main = main;
        startTime = System.currentTimeMillis();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected List<EvaluatedCandidate<IndividualV1>> nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>> evaluatedPopulation, int eliteCount, Random rng) {
        if (logger.isDebugEnabled()) {
            logger.debug("nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>>, int, Random) - start"); //$NON-NLS-1$
        }

        int count = 0;
        for (EvaluatedCandidate<IndividualV1> c : evaluatedPopulation) {
            c.getCandidate().resetStats();
            ;
            main.getPopulation()[count] = c.getCandidate();

            count++;
        }
        main.getMem().storeGenes(main.getMem().getCurrentGeneration(), -1, main.getPopulation());

        String fitnessName = main.getPopulation()[0].getFitnessClass().getSimpleName();
        if (fitnessName.equals("RandomFitness")) {
        } else {
            for (int j = 0; j < Integer.parseInt(main.getBotsGUIMainWindow().getIterationsField().getText()) && !main.isCancel(); j++) {
                if (!main.isCancel()) {
                    if (main.getServer().remainingJobList.isEmpty()) {
                        //                GeneticAlg geneticAlg;
                        //                Class<?> geneticClass = (Class<?>) this.jComboBox1.getSelectedItem();
                        //                try {
                        //                    geneticAlg = (GeneticAlg) geneticClass.newInstance();
                        //                    main.setPopulation main.getMem().loadPoblacion(26);
                        //                    main.setPopulation geneticAlg.evolve(main.getPopulation());
                        //                    main.getMem().storeGenes(main.getMem().getCurrentGeneration() + 1, -1, main.getPopulation());
                        //                    initMemoria();
                        //                } catch (InstantiationException ex) {
                        //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        //                } catch (IllegalAccessException ex) {
                        //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        //                }
                        main.setPopulation(main.getMem().loadPoblacion(26));
                        main.getMem().storeGenes(main.getMem().getCurrentGeneration(), -1, main.getPopulation());
                        main.initMemoria();
                    }
                    main.iterateOnce();
                    try {
                        main.killUCCServers();
                    } catch (IOException ex) {
                        logger.error("nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>>, int, Random)", ex); //$NON-NLS-1$


                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        logger.warn("nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>>, int, Random) - exception ignored", ex); //$NON-NLS-1$

                    }
                    main.getServer().updateRemainingList(true);
                }
            }
        }

        main.setPopulation(main.getMem().loadPoblacion(26));
        main.saveXML("./backup.xml");
        Collection<Individual> oldpop = Arrays.asList(main.getPopulation().clone());
        List<IndividualV1> Listv1 = (List<IndividualV1>) (List<?>) oldpop;


        List<EvaluatedCandidate<IndividualV1>> newpop = evaluatePopulation((List<IndividualV1>) (List<?>) oldpop);
        count = 0;
        IndividualV1[] copyList = new IndividualV1[main.getPopulation().length];
        for (IndividualV1 i : Listv1) {
            IndividualV1 newi;
            newi = new IndividualV1(i);
            copyList[count] = newi;
            count++;
        }

        count = 0;
        for (EvaluatedCandidate<IndividualV1> c : newpop) {
            main.getPopulation()[count] = c.getCandidate();

            count++;
        }

        EvolutionUtils.sortEvaluatedPopulation(newpop, true);
        main.observer.populationUpdate(EvolutionUtils.getPopulationData(newpop,true, eliteCount, currentGeneration, startTime));
        
  //      Collections.reverse(newpop);
        main.getGenerationTableList().add(copyList);
        main.updateGenerationComboBox();

        newpop = super.nextEvolutionStep(newpop, eliteCount, rng);
        count = 0;
        main.getMem().storeGenes(main.getMem().getCurrentGeneration() + 1, -1, main.getPopulation());
        main.initMemoria();


        if (logger.isDebugEnabled()) {
            logger.debug("nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>>, int, Random) - end"); //$NON-NLS-1$
        }
        currentGeneration++;
     
        return newpop;


    }
}
