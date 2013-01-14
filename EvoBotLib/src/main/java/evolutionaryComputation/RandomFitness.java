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
      private static  Random rnd=new Random();
         private double fit=0;
                 static int count=1;
    @Override

    public double fitness() {
        if(fit==0){
            fit=count;
            count++;
        }
        return fit;
                
    }
    
}
