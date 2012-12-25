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


/**
 * A more complex fitness is implemented here. The purpose is to stabilize the
 * evaluation of individuals. That is the main reason why we added so many new
 * factors.
 *
 * @author Francisco Aisa García
 */


public class ComplexFitness extends IndividualStats {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ComplexFitness.class);

    // *************************************************************************
    //                                METHODS
    // *************************************************************************

    /** Estimate fitness */
    @Override
    public double fitness () {
        double killBalance;

        if(kills==0 && deaths==0) {
            return 0;
        }
        if (kills >= deaths) {
            killBalance = (kills - deaths) + 1;
        }
        else {
            killBalance = ((kills + 1) * 1.0) / ((deaths + 1) * 1.0);
        }

        double damageBalance = 0;

        if (totalDamageGiven >= totalDamageTaken) {
            damageBalance = Math.log10( (double) ((totalDamageGiven - totalDamageTaken) + 1) );
        }

        double fitnessValue = killBalance + (double) nSuperShields +
                ((nShields * 1.0) / 2.0) +
                ( ( (totalTimeShock * 1.0) / 10.0) / ((deaths + 1) * 1.0) ) +
                ( ( (totalTimeSniper * 1.0) / 10.0) / ((deaths + 1) * 1.0) ) +
                damageBalance;

        return fitnessValue;
    }
}
