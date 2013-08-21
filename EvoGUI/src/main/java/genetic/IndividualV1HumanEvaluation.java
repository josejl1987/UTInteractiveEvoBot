/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;

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
    
    public Individual getRandomIndividual(Individual[] array){
        
        Individual[] newArray=array.clone();
       double maxFitness=-10000;
       Arrays.sort(newArray);
       ArrayUtils.reverse(newArray);
       for(Individual ind:newArray){
           double currentFitness=ind.getStats().getAverageFitness();
           if(currentFitness>maxFitness)maxFitness=currentFitness;
       }
       int max=0;
       for(int i=0;i<newArray.length&&newArray[i].getStats().getAverageFitness()>0.9*maxFitness;i++){
           max=i;
       }
        
        int selected=(int) Math.round(Math.random()*max);
        return newArray[selected];
    }
    public boolean shouldEvaluate(Integer currentGeneration){
       return generations.contains(currentGeneration);
    }
    }
