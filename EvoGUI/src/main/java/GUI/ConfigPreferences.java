/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import evolutionaryComputation.Individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Jose
 */
public class ConfigPreferences {

	Random rnd=null;
    List<Individual[]> generationTableList;
    HashMap<String, String> parameters;
    HashMap<Integer,Integer> evaluationsMap;

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
    public Random getRandomGenerator(){
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
