/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import evolutionaryComputation.IndividualV1;
import org.apache.log4j.Logger;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jose
 */
public class IndividualV1ChromosomeCopy implements EvolutionaryOperator<IndividualV1> {

    public IndividualV1 selectedCandidate = null;

    public IndividualV1 getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(IndividualV1 selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }
    boolean[] locked;

    public IndividualV1ChromosomeCopy() {
        locked = new boolean[IndividualV1.chromosomeGroup.values().length];
    }

    public void setLockGroupValue(IndividualV1.chromosomeGroup group, boolean value) {
        locked[group.ordinal()] = value;
    }

    public List<IndividualV1> apply(List<IndividualV1> selectedCandidates, Random rng) {
        ArrayList<IndividualV1> newList=new ArrayList<IndividualV1>();
        if (selectedCandidate != null) {
            for (IndividualV1 individual : selectedCandidates) {

                for (int i = 0; i < selectedCandidate.chromosomeSize(); i++) {
                    IndividualV1.chromosomeGroup currentGroup = selectedCandidate.getChromosomeGroup(i);
                    if (locked[currentGroup.ordinal()]) {
                        individual.getChromosome()[i] = selectedCandidate.getGene(i);
                    }
                }
                newList.add(individual);
            }
               return newList;
        }
        return selectedCandidates;

    }
}
