/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionaryComputation;

/**
 *
 * @author Jose
 */
public class KadlecFitness extends IndividualStats {

    @Override
    public double fitness() {
       double fitness= (this.totalDamageGiven-this.totalDamageTaken/10)+(this.kills*50-this.deaths*5);
       if(fitness<=0) fitness=0;
       return fitness;
    }
    
}
