/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.Random;

/**
 *
 * @author jose
 */
public class RandomGenerator {
              private static       Random rnd=new Random(3);

    static int currentSeed;
     
    public static void setRandom(Random newRnd,int seed){
             rnd=newRnd;
        currentSeed=seed;
    }
    public static void setRandomSeed(int seed){
        rnd=new Random(seed);
        currentSeed=seed;
    }         
    public static Random getRnd() {
        return rnd;
    }
        public static int doRandomNumber (int lowerBound, int upperBound) {

        return rnd.nextInt(upperBound - lowerBound + 1)  + lowerBound;
        
    }
    
}