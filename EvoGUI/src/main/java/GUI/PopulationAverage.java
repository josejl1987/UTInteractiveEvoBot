/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;

/**
 *
 * @author jose
 */
public class PopulationAverage {

    
    
        static public IndividualV1[] geometricAverage(IndividualV1[][] populationArray, int populationSize, int iterations) {

        IndividualV1[] population = new IndividualV1[populationSize];

        for (int i = 0; i < populationSize; i++) {
            population[i] = new IndividualV1(populationArray[i][0]);
            if(population[i].shouldEvaluate)
            population[i].resetStats();
  
        }


        for (int j = 0; j < iterations; j++) {

            for (int i = 0; i < populationSize; i++) {
                if (population[i].shouldEvaluate) {
                    double weight = 1.0 / (double) iterations;
                    population[i].getStats().updateAverageFitness(populationArray[i][j].getStats().fitness());
                    population[i].getStats().add(populationArray[i][j].getStats(), weight);
                }
              
            }
        }


        return population;


    }
    
    static public IndividualV1[] meanAverage(IndividualV1[][] populationArray, int populationSize, int iterations) {

        IndividualV1[] population = new IndividualV1[populationSize];

        for (int i = 0; i < populationSize; i++) {
             population[i] = new IndividualV1(populationArray[i][0]);
                        population[i].resetStats();

            if(population[i].shouldEvaluate)
            population[i].resetStats();
            population[i].getStats().evaluations=populationArray[i][0].getStats().evaluations;
            population[i].getStats().averageFitness=populationArray[i][0].getStats().averageFitness;

  
        }


        for (int j = 0; j < iterations; j++) {

            for (int i = 0; i < populationSize; i++) {
                if (population[i].shouldEvaluate) {
                    double weight = 1.0 / (double) iterations;
                   population[i].getStats().updateAverageFitness(populationArray[i][j].getStats().fitness());
                    population[i].getStats().add(populationArray[i][j].getStats(), weight);
                             population[i].shouldEvaluate=false;
                
                }
                          
            }
        }


        return population;


    }
}
