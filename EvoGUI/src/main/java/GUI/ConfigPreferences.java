/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;
import java.util.List;

import evolutionaryComputation.Individual;
import java.util.HashMap;

/**
 *
 * @author Jose
 */
public class ConfigPreferences {

	List<Individual[]> generationTableList;
    HashMap<String, String> parameters;
 
	
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
