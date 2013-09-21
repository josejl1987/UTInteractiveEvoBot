/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionaryComputation;

/**
 *
 * @author Jose
 */
public class HumanityFitness extends ComplexFitness {
    
  private  double humanFitness;

    public HumanityFitness(double humanFitness) {
        super();
        this.humanFitness = humanFitness;
    }    
       public HumanityFitness() {
        super();
        this.humanFitness = 1;
    }  
    public double getHumanFitness() {
        return humanFitness;
    }

    public void setHumanFitness(double humanFitness) {
        this.humanFitness = humanFitness;
    }
    
    
}
