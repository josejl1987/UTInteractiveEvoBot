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
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.logging.Level;
import javax.swing.Timer;


/**
 * This class is meant to hide all the details of a chromosome. Because it contains
 * an IndividualStats object, it can be combined with different fitness functions.
 *
 * @author Francisco Aisa Garcia
 */

public abstract class Individual<T extends IndividualStats> implements Serializable,Cloneable{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Individual.class);

    // *************************************************************************
    //                             INSTANCE FIELDS
    // *************************************************************************

    Class<T> fitnessClass;

    public Class<T> getFitnessClass() {
        return fitnessClass;
    }
    
    	@Override
	public String toString() {
		return "IndividualV1 [Number()="
				+ (getChromosome()[0]) + ", Kills="
				+ getKills() + ", Deaths=" + getDeaths() + "]";
	}
    
    /** Individual chromosome */
    protected int chromosome [];

    public int[] getChromosome() {
        return chromosome;
    }

    public void setChromosome(int[] chromosome) {
        this.chromosome = chromosome;
    }
    /** Individual stats */
    protected T stats; // Must be initialized in the derived classes


    // *************************************************************************
    //                                METHODS
    // *************************************************************************


    /**
     * Argument based constructor.
     * @param nGenes Size of the chromosome.
     * @param stats IndividualStats object, it is meant to facilitate the association
     * of an Individual with different stats and fitness techniques. Stats can't be
     * a NULL object.
     */
    public Individual (int nGenes,Class<T> clazz) {
        chromosome = new int [nGenes];
        fitnessClass=clazz;
        try {
            this.stats=clazz.newInstance();
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    //__________________________________________________________________________

    
    public Individual(Individual<T> copy){
        try {
            this.chromosome=Arrays.copyOf(copy.chromosome,copy.chromosome.length);
              this.stats=(T) copy.stats.getClass().newInstance();
            this.stats.deaths=copy.stats.deaths;
            this.stats.kills=copy.stats.kills;
            this.stats.nShields=copy.stats.nShields;
            this.stats.nSuperShields=copy.stats.nSuperShields;
            this.stats.totalDamageGiven=copy.stats.totalDamageGiven;
            this.stats.totalDamageTaken=copy.stats.totalDamageTaken;
            this.stats.totalTimeShock=copy.stats.totalTimeShock;
            this.stats.totalTimeSniper=copy.stats.totalTimeSniper;
            this.fitnessClass=copy.fitnessClass;
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Individual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Set the value of a gene.
     * @param locus Position in the chromosome.
     * @param value Value to which we want to set the gene.
     */
    public void setGene (int locus, int value) {
        chromosome [locus] = value;
    }

    //__________________________________________________________________________

    /**
     * Get the value of a chromosome's gene.
     * @param locus Position of the gene in the chromosome.
     * @return The gene's value.
     */
    public int getGene (int locus) {
        return chromosome [locus];
    }

    //__________________________________________________________________________

    /** Chromosome's size */
    public int chromosomeSize () {
        return chromosome.length;
    }

    //__________________________________________________________________________

    /** Create an individual with random genes */
    public abstract void createRandomIndividual ();

    //__________________________________________________________________________

    /** Estimate fitness */
    public double fitness () {
        return stats.fitness ();
    }

    //__________________________________________________________________________

    /** Increment the number of kills */
    public void incrementKills () {
        stats.incrementKills ();
    }

    //__________________________________________________________________________

    /** Increment the number of deaths */
    public void incrementDeaths () {
        stats.incrementDeaths ();
    }

    //__________________________________________________________________________

    /** Increment the total number of super shields picked up */
    public void superShieldPickedUp () {
        stats.superShieldPickedUp ();
    }

    //__________________________________________________________________________

    /** Increment the total number of shields picked up */
    public void shieldPickedUp () {
        stats.shieldPickedUp ();
    }

    //__________________________________________________________________________

    /** Time out time with the shock rifle */
    public void shockPickedUp () {
        stats.shockPickedUp ();
    }

    //__________________________________________________________________________

    /** Time out time with the sniper rifle */
    public void sniperPickedUp () {
        stats.sniperPickedUp ();
    }

    //__________________________________________________________________________

    /** Stop the clocks */
    public void timeout () {
        stats.timeout ();
    }

    //__________________________________________________________________________

    /** Get the number of kills */
    public int getKills () {
        return stats.getKills ();
    }

    //__________________________________________________________________________

    /**
     * Set the number of kills, it ONLY should be used by the DB
     * @param kills Number of kills to set
     */
    public void setKills (int kills) {
        stats.setKills (kills);
    }

    //__________________________________________________________________________

    /** Get the number of the deaths */
    public int getDeaths () {
        return stats.getDeaths ();
    }

    //__________________________________________________________________________

    /** Set the number of the deaths, it ONLY should be used by the DB */
    public void setDeaths (int deaths) {
        stats.setDeaths (deaths);
    }

    public void setNSuperShields (int nSuperShields) {
        stats.setNSuperShields(nSuperShields);
    }

    public void setNShields (int nShields) {
        stats.setNShields(nShields);
    }

    public void setTotalTimeShock (int totalTimeShock) {
        stats.setTotalTimeShock(totalTimeShock);
    }

    public void setTotalTimeSniper (int totalTimeSniper) {
        stats.setTotalTimeSniper(totalTimeSniper);
    }

    public int getNSuperShields () {
        return stats.getNSuperShields();
    }

    public int getNShields () {
        return stats.getNShields();
    }

    public int getTotalTimeShock () {
        return stats.getTotalTimeShock();
    }

    public int getTotalTimeSniper () {
        return stats.getTotalTimeSniper();
    }

    //__________________________________________________________________________

    /** Get the total amount of damage given */
    public int getTotalDamageGiven () {
        return stats.getTotalDamageGiven ();
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage given, it ONLY should be used by the DB
     * @param damage Total amount of damage given
     */
    public void setTotalDamageGiven (int damage) {
        stats.setTotalDamageGiven (damage);
    }

    //__________________________________________________________________________

    /** Get the total amount of damage taken */
    public int getTotalDamageTaken () {
        return stats.getTotalDamageTaken ();
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage taken, it ONLY should be used by the DB
     * @param damage Total amount of damage taken
     */
    public void setTotalDamageTaken (int damage) {
        stats.setTotalDamageTaken (damage);
    }

    //__________________________________________________________________________

    /**
     * Increment the total amount of damage given.
     * @param amount Amount to be incremented.
     */
    public void incrementDamageGiven (int amount) {
        stats.incrementDamageGiven (amount);
    }

    //__________________________________________________________________________

    /**
     * Increment the total amount of damage taken.
     * @param amount Amount to be incremented.
     */
    public void incrementDamageTaken (int amount) {
        stats.incrementDamageTaken (amount);
    }

    //__________________________________________________________________________

    /**
     * Resets the individual's temporary information about the match
     */
    public void resetStats () {
        stats.reset ();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Arrays.hashCode(this.chromosome);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Individual other = (Individual) obj;
        if (!Arrays.equals(this.chromosome, other.chromosome)) {
            return false;
        }
        return true;
    }
    
}

