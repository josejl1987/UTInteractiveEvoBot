    /*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Copyright © 2011-2012 Francisco Aisa Garcia and Ricardo Caballero Moral
 */

package evolutionaryComputation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import utilities.RandomGenerator;


/**
 * This class implements a small chromosome with lesser control over the weapon
 * selection. It is meant to accelerate the convergence of any given genetic algorithm
 * (less matches to be played).
 *
 * @author Francisco Aisa Garcia
 */


public class IndividualV1 extends Individual  implements Serializable{


   public  enum chromosomeGroup{Distancia,PrioridadArmas,Salud,Riesgo,Tiempo,Items};
    public chromosomeGroup getChromosomeGroup(int index){
        if(index>=0 && index<3){
           return chromosomeGroup.Distancia;
        }
           else if(index>=3 && index<12){
               return chromosomeGroup.PrioridadArmas;
           }
           else if (index>=12 && index<14){
               return chromosomeGroup.Salud;
           }
           else if (index>=14&&index<19){
               return chromosomeGroup.Riesgo;
           }
           else if (index>=19&&index<20){
               return chromosomeGroup.Tiempo;
               
           }
           else if (index>=20 && index<26){
             return  chromosomeGroup.Items;
           }
        return null;
    }
	/**1
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IndividualV1.class);

    // *************************************************************************
    //                                METHODS
    // *************************************************************************

    /** Create a chromosome with random gene values */
    @Override
	public void createRandomIndividual () {

        // CLOSE, AVERAGE AND FAR DISTANCE

        chromosome [0] = RandomGenerator.doRandomNumber (0, 1200);
        chromosome [1] = RandomGenerator.doRandomNumber (chromosome [0], 2000);
        chromosome [2] = RandomGenerator.doRandomNumber (chromosome [1], 2800);

        // ALLELES FOR WEAPON CHOICE. THERE ARE 12 GENES FOR EACH WEAPON (3 GENES PER
        // HEIGHT AND 4 HEIGHTS PER DISTANCE = 12 GENES).
        // THERE ARE 9 WEAPONS AND WE NEED ANOTHER 18 ADDITIONAL GENES FOR SPAM
        // AND NON ENEMY ON SIGHT BEHAVIORS WHICH MAKES UP TO:
        // 9*12 + 2*9 GENES MAKING A TOTAL OF = 126 GENES
        // EXAMPLE: SHIELD GUN
        // CLOSE - LOW, MIDDLE, HIGH ; AVERAGE - LOW, MIDDLE, HIGH ; FAR - LOW, MIDDLE, HIGH
        // WEAPONS ORDER: SHIELD GUN, ASSAULT RIFLE, BIO RIFLE, LINK GUN, MINIGUN
        //                FLAK CANNON, ROCKET LAUNCHER, SHOCK RIFLE, SNIPER RIFLE

        for (int locus = 3; locus < 12; ++locus) {
            chromosome [locus] = RandomGenerator.doRandomNumber (0, 100);
        }

        // ALLELES FOR SKYNET DECISIONS

        // HEALTH ALLELES

        chromosome [12] = RandomGenerator.doRandomNumber (0, 100);
        chromosome [13] = RandomGenerator.doRandomNumber (chromosome [12], 160);

        // HEALTH RISK ALLELES

        chromosome [14] = RandomGenerator.doRandomNumber (5, 30);
        chromosome [15] = RandomGenerator.doRandomNumber (15, 80);
        chromosome [16] = RandomGenerator.doRandomNumber (15, 60);
        chromosome [17] = RandomGenerator.doRandomNumber (10, 120);
        chromosome [18] = RandomGenerator.doRandomNumber (20, 100);

        // ELAPSED TIME

        chromosome [19] = RandomGenerator.doRandomNumber (3, 9);

        // DESTINATION BASED ON ITEMS PRIORITY  
     
        for (int locus = 20; locus < 26; ++locus) {
            chromosome [locus] = RandomGenerator.doRandomNumber (0, 100);
        }
    }

    //__________________________________________________________________________

    /**
     * Argument based constructor.
     * @param initialize True if we want to generate random gene values.
     */
    public IndividualV1 (boolean initialize,Class<? extends IndividualStats> clazz) {
        super (26,clazz);

        if (initialize) {
            createRandomIndividual ();
        }
    }

    public IndividualV1(Individual copy) {
        super(copy);
    }

    public List<IndividualV1> generateHillClimbing(){
        
        int amount=1;
        List<IndividualV1> hillList=new ArrayList<>();
        
        int[] newChromosome=this.getChromosome().clone();
        for (int i = 0; i < this.chromosomeSize(); i++) {
            int[] currentChromosome = newChromosome.clone();
            currentChromosome[i] += amount;
            IndividualV1 newIndividual = new IndividualV1(false, this.getFitnessClass());
            newIndividual.setChromosome(currentChromosome);
            hillList.add(newIndividual);
        }
                for (int i = 0; i < this.chromosomeSize(); i++) {
            int[] currentChromosome = newChromosome.clone();
            currentChromosome[i] -= amount;
            IndividualV1 newIndividual = new IndividualV1(false, this.getFitnessClass());
            newIndividual.setChromosome(currentChromosome);
            hillList.add(newIndividual);
        }
        return hillList;
    }

    
}
