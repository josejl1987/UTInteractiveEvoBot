/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class IndividualV1HumanEvaluation {
    
    private ArrayList<Integer> generations=new ArrayList<Integer>();

    public  ArrayList<Integer> getGenerations() {
        return generations;
    }

    public void setGenerations(String generationList){
        String[] split = generationList.split(",");
        for(String s:split){
            generations.add(Integer.parseInt(s));
        }
    }
    public void setGenerations( ArrayList<Integer> generations) {
        this.generations = generations;
    }
    
    public boolean shouldEvaluate(Integer currentGeneration){
       return generations.contains(currentGeneration);
    }
    
    
}
