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
public abstract class IndividualStats implements Serializable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IndividualStats.class);

    // *************************************************************************
    //                             INSTANCE FIELDS
    // *************************************************************************


    /** Number of times the individual has killed */
    protected int kills;
    /** Number of times the individual has died */
    protected int deaths;
    /** Total amount of damage the individual has given to the enemy */
    protected int totalDamageGiven;
    /** Total amount of damage the individual has received from the enemy */
    protected int totalDamageTaken;
    /** Total Super Shield Packs picked up */
    protected int nSuperShields;
    /** Total Shields Packs picked up */
    protected int nShields;
    /** Total time we had the shock rifle */
    protected int totalTimeShock;
    /** Total time we had the sniper rifle */
    protected int totalTimeSniper;

    /** Clock to time out how much time we spent with the shock rifle */
    protected Timer shockClock;
    /** Clock to time out how much time we spent with the sniper rifle */
    protected Timer sniperClock;


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
            public void actionPerformed(ActionEvent evt) {
               totalTimeShock += 1;
            }
        };

        ActionListener taskPerformer2 = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               totalTimeSniper += 1;
            }
        };

        shockClock = new Timer(delay, taskPerformer1);
        sniperClock = new Timer(delay, taskPerformer2);
    }

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
    public int getTotalDamageTaken () {
        return totalDamageTaken;
    }

    //__________________________________________________________________________

    /** Get the total amount of damage given */
    public int getTotalDamageGiven () {
        return totalDamageGiven;
    }

    //__________________________________________________________________________

    /** Get the number of the deaths */
    public int getDeaths () {
        return deaths;
    }

    //__________________________________________________________________________

    /** Get the number of kills */
    public int getKills () {
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
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage taken, it ONLY should be used by the DB
     * @param damage Total amount of damage taken
     */
    void setTotalDamageTaken (int damage) {
        totalDamageTaken = damage;
    }

    //__________________________________________________________________________

    /**
     * Set the total amount of damage given, it ONLY should be used by the DB
     * @param damage Total amount of damage given
     */
    void setTotalDamageGiven (int damage) {
        totalDamageGiven = damage;
    }

    //__________________________________________________________________________

    /**
     * Set the number of kills, it ONLY should be used by the DB
     * @param kills Number of kills to set
     */
    void setKills (int kills) {
        this.kills = kills;
    }

    //__________________________________________________________________________

    /** Set the number of the deaths, it should ONLY be used by the DB */
    void setDeaths (int deaths) {
        this.deaths = deaths;
    }

    //__________________________________________________________________________

    /** Set the number of the super shields taken, it should ONLY be used by the DB */
    void setNSuperShields (int nSuperShields) {
        this.nSuperShields = nSuperShields;
    }

    //__________________________________________________________________________

    /** Set the number of the shields taken, it should ONLY be used by the DB */
    void setNShields (int nShields) {
        this.nShields = nShields;
    }

    //__________________________________________________________________________

    /** Set the total time the shock has been in ours, it should ONLY be used by the DB */
    void setTotalTimeShock (int totalTimeShock) {
        this.totalTimeShock = totalTimeShock;
    }

    //__________________________________________________________________________

    /** Set the number of the sniper has been ours, it should ONLY be used by the DB */
    void setTotalTimeSniper (int totalTimeSniper) {
        this.totalTimeSniper = totalTimeSniper;
    }

    //__________________________________________________________________________

    /**
     * Get the number of Super Shields taken
     *
     * @return Number of Super Shields taken
     */
    int getNSuperShields () {
        return nSuperShields;
    }

    //__________________________________________________________________________

    /**
     * Get the number of Shields taken
     *
     * @return Number of Shields taken
     */
    int getNShields () {
        return nShields;
    }

    //__________________________________________________________________________

    /**
     * Get the total time that we have had the shock rifle
     *
     * @return Total time that we have had the shock rifle
     */
    int getTotalTimeShock () {
        return totalTimeShock;
    }

    //__________________________________________________________________________

    /**
     * Get the total time that we have had the sniper rifle
     *
     * @return Total time that we have had the sniper rifle
     */
    int getTotalTimeSniper () {
        return totalTimeSniper;
    }
}
