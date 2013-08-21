/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import evolutionaryComputation.Individual;
import genetic.IndividualV1HumanEvaluation;
import genetic.IndividualV1ChromosomeCopy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Jose
 */
public class ConfigPreferences {

    Random rnd = null;
    List<Individual[]> generationTableList;
    HashMap<String, String> parameters;
    HashMap<Integer, Integer> evaluationsMap;
    IndividualV1HumanEvaluation humanEvaluation;
    IndividualV1ChromosomeCopy chromosomeCopy;

    public IndividualV1ChromosomeCopy getChromosomeCopy() {
        return chromosomeCopy;
    }

    public void setChromosomeCopy(IndividualV1ChromosomeCopy chromosomeCopy) {
        this.chromosomeCopy = chromosomeCopy;
    }

    public IndividualV1HumanEvaluation getHumanEvaluation() {
        return humanEvaluation;
    }

    public void setHumanEvaluation(IndividualV1HumanEvaluation humanEvaluation) {
        this.humanEvaluation = humanEvaluation;
    }

    public HashMap<Integer, Integer> getEvaluationsMap() {
        return evaluationsMap;
    }

    public void setEvaluationsMap(HashMap<Integer, Integer> evaluationsMap) {
        this.evaluationsMap = evaluationsMap;
    }
    int currentGeneration;
    int currentEval;

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public Random getRandomGenerator() {
        return rnd;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }

    public List<Individual[]> getGenerationTableList() {
        return generationTableList;
    }

    public void setGenerationTableList(List<Individual[]> generationTableList) {
        this.generationTableList = generationTableList;
    }

    public ConfigPreferences() {
        this.generationTableList = new ArrayList<Individual[]>();

    }
}
