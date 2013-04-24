/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionaryComputation;

/**
 *
 * @author jose
 */
public class NewFitness  extends IndividualStats {

    @Override
    public double fitness() {
        
       double A=0;
       if(kills==deaths) {
            A=0.5;
        }
       else if (kills>deaths) {
            A=1;
        }
       else {
            A=0;
        }
        
        
       double fitness= 10*(kills-deaths)+(totalDamageGiven-totalDamageTaken);
       if(fitness<=0) fitness=0.0001;
       return fitness;
    }
    
}
