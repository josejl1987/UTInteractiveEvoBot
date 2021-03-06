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

package exceptions;

import org.apache.log4j.Logger;


/**
 * This class implements a logical exception that should be thrown when a secondary
 * state can't be executed.
 *
 * @author Francisco Aisa García
 */


public class SubStatusException extends Exception {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SubStatusException.class);

    /**
     * Argument based constructor.
     * @param msg It indicates what the problem is.
     */
    public SubStatusException (String msg) { super (msg); }
}
