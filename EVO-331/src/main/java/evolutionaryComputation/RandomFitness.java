/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionaryComputation;

import java.util.Random;

/**
 *
 * @author Jose
 */
public class RandomFitness extends IndividualStats {
      static  Random rnd=new Random();
         double fit=0;
    @Override

    public double fitness() {
        if(fit==0) fit=rnd.nextDouble()*rnd.nextDouble()*10;
        return fit;
                
    }
    
}
