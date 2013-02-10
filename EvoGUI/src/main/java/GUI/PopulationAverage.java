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

    static public IndividualV1[] meanAverage(IndividualV1[][] populationArray, int populationSize, int iterations) {

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
                    population[i].getStats().add(populationArray[i][j].getStats(), weight);
                }
                          population[i].shouldEvaluate=false;
            }
        }


        return population;


    }
}
