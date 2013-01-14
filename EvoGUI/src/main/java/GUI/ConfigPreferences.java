/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import evolutionaryComputation.Individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jose
 */
public class ConfigPreferences {

    List<Individual[]> generationTableList;
    HashMap<String, String> parameters;
    int currentGeneration;

    public int getCurrentGeneration() {
        return currentGeneration;
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
