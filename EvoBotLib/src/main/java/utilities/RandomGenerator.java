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
              private static     final  Random rnd=new Random(1);
        public static int doRandomNumber (int lowerBound, int upperBound) {

        return rnd.nextInt(upperBound - lowerBound + 1)  + lowerBound;
        
    }
    
}
