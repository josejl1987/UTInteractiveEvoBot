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

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.Timer;


/**
 * This class should contain statistics about an individual's match. That information
 * is to be used to estimate the fitness. Each derived class must implement a new
 * fitness function.
 *
 * @author Francisco Aisa Garcia
 */

/*
 * Although this class stores some other additional info, it's main purpose is to
 * facilitate trials with different fitness and invididuals (different chromosomes).
 * That is the main reason why the derived classes should have names of fitness techniques
 * (to allow clients to know rapidly which kind of fitness they are using).
 */
public abstract class IndividualStats implements Serializable  {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IndividualStats.class);

    // *************************************************************************
    //                             INSTANCE FIELDS
    // *************************************************************************
        



    /** Number of times the individual has killed */
    protected double kills;
    /** Number of times the individual has died */
    protected double deaths;
    /** Total amount of damage the individual has given to the enemy */
    protected double totalDamageGiven;
    /** Total amount of damage the individual has received from the enemy */
    protected double totalDamageTaken;
    /** Total Super Shield Packs picked up */
    protected double nSuperShields;
    /** Total Shields Packs picked up */
    protected double nShields;
    /** Total time we had the shock rifle */
    protected double totalTimeShock;
    /** Total time we had the sniper rifle */
    protected double totalTimeSniper;

    /** Clock to time out how much time we spent with the shock rifle */
    private Timer shockClock;
    /** Clock to time out how much time we spent with the sniper rifle */
    private Timer sniperClock;
    /** Time that this individual has been playing*/

    private double matchTime;
    // *************************************************************************
    //                                METHODS
    // *************************************************************************

    /**
     * Initialize all stats to 0 (although this is done by java, we've done it
     * explicitly for the sake of simplicity.
     */
    IndividualStats () {
        kills = deaths = totalDamageGiven = totalDamageTaken = 0;
        totalTimeShock = totalTimeSniper = 0;

        int delay = 1000;
        ActionListener taskPerformer1 = new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
               totalTimeShock += 1;
            }
        };

        ActionListener taskPerformer2 = new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
               totalTimeSniper += 1;
            }
        };

        shockClock = new Timer(delay, taskPerformer1);
        sniperClock = new Timer(delay, taskPerformer2);
    }

    public double averageFitness;

    
    public double updateAverageFitness(double newFitness){
        
        if(evaluations==0){
            evaluations=1;
            averageFitness=newFitness;
        }
        else{
            evaluations++;
            averageFitness=(evaluations-1)*averageFitness/evaluations+newFitness/evaluations;
            
        }
        return averageFitness;
    }
    
    public double getAverageFitness() {
        return averageFitness;
    }

    public void setAverageFitness(double averageFitness) {
        this.averageFitness = averageFitness;
    }
    public int evaluations=0;
    //__________________________________________________________________________

    /** Estimate fitness */
    public abstract double fitness ();

    //__________________________________________________________________________

    /**
     * Increment the total amount of damage given.
     * @param amount Amount to be incremented.
     */
    public void incrementDamageGiven (int amount) {
        totalDamageGiven = totalDamageGiven + amount;
    }

    //__________________________________________________________________________

    /**
     * Increment the total amount of damage taken.
     * @param amount Amount to be incremented.
     */
    public void incrementDamageTaken (int amount) {
        totalDamageTaken = totalDamageTaken + amount;
    }

    //__________________________________________________________________________

    /** Increment the number of kills */
    public void incrementKills () {
        kills = kills + 1;
    }

    //__________________________________________________________________________

    /** Increment the number of deaths */
    public void incrementDeaths () {
        deaths = deaths + 1;
    }

    //__________________________________________________________________________

    /** Increment the total number of super shields picked up */
    public void superShieldPickedUp () {
        nSuperShields += 1;
    }

    //__________________________________________________________________________

    /** Increment the total number of shields picked up */
    public void shieldPickedUp () {
        nShields += 1;
    }

    //__________________________________________________________________________

    /** Time out time with the shock rifle */
    public void shockPickedUp () {
        if (!shockClock.isRunning ()) {
            shockClock.start ();
        }
    }

    //__________________________________________________________________________

    /** Time out time with the sniper rifle */
    public void sniperPickedUp () {
        if (!sniperClock.isRunning ()) {
            sniperClock.start ();
        }
    }

    //__________________________________________________________________________

    /** Stop the clocks */
    public void timeout () {
        shockClock.stop ();
        sniperClock.stop ();
 
    }

    //__________________________________________________________________________

    /** Get the total amount of damage taken */
    public double getTotalDamageTaken () {
        return totalDamageTaken;
    }

    //__________________________________________________________________________

    /** Get the total amount of damage given */
    public double getTotalDamageGiven () {
        return totalDamageGiven;
    }

    //__________________________________________________________________________

    /** Get the number of the deaths */
    public double getDeaths () {
        return deaths;
    }

    //__________________________________________________________________________

    /** Get the number of kills */
    public double getKills () {
        return kills;
    }

    //__________________________________________________________________________

    /**
     * Resets the individual's temporary information about the match
     */
    public void reset () {
        kills = deaths = totalDamageGiven = totalDamageTaken = 0;
        nSuperShields = nShields = 0;
        totalTimeShock = totalTimeSniper = 0;
        shockClock.stop ();
        sniperClock.stop();
       matchTime=0;
    }

    public double getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(double matchTime) {
        this.matchTime = matchTime;
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage taken, it ONLY should be used by the DB
     * @param damage Total amount of damage taken
     */
    void setTotalDamageTaken (double damage) {
        totalDamageTaken = damage;
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage given, it ONLY should be used by the DB
     * @param damage Total amount of damage given
     */
    void setTotalDamageGiven (double damage) {
        totalDamageGiven = damage;
    }

    //__________________________________________________________________________

    /**
     * Set the number of kills, it ONLY should be used by the DB
     * @param kills Number of kills to set
     */
    void setKills (double kills) {
        this.kills = kills;
    }

    //__________________________________________________________________________

    /** Set the number of the deaths, it should ONLY be used by the DB */
    void setDeaths (double deaths) {
        this.deaths = deaths;
    }

    //__________________________________________________________________________

    /** Set the number of the super shields taken, it should ONLY be used by the DB */
    void setNSuperShields (double nSuperShields) {
        this.nSuperShields = nSuperShields;
    }

    //__________________________________________________________________________

    /** Set the number of the shields taken, it should ONLY be used by the DB */
    void setNShields (double nShields) {
        this.nShields = nShields;
    }

    //__________________________________________________________________________

    /** Set the total time the shock has been in ours, it should ONLY be used by the DB */
    void setTotalTimeShock (double totalTimeShock) {
        this.totalTimeShock = totalTimeShock;
    }

    //__________________________________________________________________________

    /** Set the number of the sniper has been ours, it should ONLY be used by the DB */
    void setTotalTimeSniper (double totalTimeSniper) {
        this.totalTimeSniper = totalTimeSniper;
    }

    //__________________________________________________________________________

    /**
     * Get the number of Super Shields taken
     *
     * @return Number of Super Shields taken
     */
    double getNSuperShields () {
        return nSuperShields;
    }

    //__________________________________________________________________________

    /**
     * Get the number of Shields taken
     *
     * @return Number of Shields taken
     */
    double getNShields () {
        return nShields;
    }

    //__________________________________________________________________________

    /**
     * Get the total time that we have had the shock rifle
     *
     * @return Total time that we have had the shock rifle
     */
    double getTotalTimeShock () {
        return totalTimeShock;
    }

    //__________________________________________________________________________

    /**
     * Get the total time that we have had the sniper rifle
     *
     * @return Total time that we have had the sniper rifle
     */
    double getTotalTimeSniper () {
        return totalTimeSniper;
    }
    
    public void add(IndividualStats other,double weight){
            /** Number of times the individual has killed */
      kills+=other.kills*weight;
    /** Number of times the individual has died */
      deaths+=other.deaths*weight;
    /** Total amount of damage the individual has given to the enemy */
      totalDamageGiven+=other.totalDamageGiven*weight;;
    /** Total amount of damage the individual has received from the enemy */
      totalDamageTaken+=other.totalDamageTaken*weight;
    /** Total Super Shield Packs picked up */
      nSuperShields+=other.nSuperShields*weight;
    /** Total Shields Packs picked up */
      nShields+=other.nShields*weight;
    /** Total time we had the shock rifle */
      totalTimeShock+=other.getTotalTimeShock()*weight;;
    /** Total time we had the sniper rifle */
      totalTimeSniper+=other.getTotalTimeSniper()*weight;;
      this.matchTime+=other.matchTime*weight;
    }
}
