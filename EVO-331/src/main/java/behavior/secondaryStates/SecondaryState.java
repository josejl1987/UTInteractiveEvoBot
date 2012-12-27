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

package behavior.secondaryStates;

import org.apache.log4j.Logger;

import exceptions.SubStatusException;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;


/**
 * This class is meant to be used in combination with PrimaryState. It should NOT
 * be used by itself to move the bot around. Secondary states can just take control
 * over movement. That's the main reason why most of the function members of this
 * class are related to movement.
 * The main idea is to use a primary state, and mix it with a secondary state whenever
 * the bot has to satisfy a need.
 *
 * @author Francisco Aisa García
 */


public abstract class SecondaryState {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SecondaryState.class);

    // *************************************************************************
    //                              ENUM TYPES
    // *************************************************************************


    public enum States {
        DISABLED, DEFENSIVE_PROFILE, OFENSIVE_PROFILE, PICKUP_WEAPON, PICKUP_AMMO,
        PICKUP_HEALTH, CRITICAL_HEALTH, CRITICAL_WEAPONRY
    }


    // *************************************************************************
    //                             INSTANCE FIELDS
    // *************************************************************************


    /** Pointer to the act field from T800 */
    protected IAct act;
    /** Pointer to the items field from T800 */
    protected Items items;
    /** Pointer to the info field from T800 */
    protected AgentInfo info;
    /** Pointer to the pathPlanner field from T800 */
    protected IPathPlanner <ILocated> pathPlanner;
    /** Pointer to the pathExecutor field from T800 */
    protected IUnrealPathExecutor <ILocated> pathExecutor;
    /** Pointer to the move field from T800 */
    protected AdvancedLocomotion move;
    /** Pointer to the cardinalRayArray field from T800 */
    protected AutoTraceRay cardinalRayArray [];


    // *************************************************************************
    //                           STATIC FIELDS
    // *************************************************************************


    /** Location were the bot is headed */
    protected static Location destination;


    // *************************************************************************
    //                               METHODS
    // *************************************************************************


    /** static constructor */
    static {
        destination = null;
    }

    //__________________________________________________________________________

    /**
     * Argument based constructor.
     * @param body body field from T800.
     * @param act act field from T800.
     * @param world world field from T800.
     * @param items items field from T800.
     * @param info info field from T800.
     * @param weaponry weaponry field from T800.
     * @param pathPlanner pathPlanner field from T800.
     * @param pathExecutor pathExecutor field from T800.
     * @param move move field from T800.
     * @param raycasting raycasting field from T800.
     * @param cardinalRayArray cardinalRayArray field from T800.
     */
    protected SecondaryState (final CompleteBotCommandsWrapper body, final IAct act, final IVisionWorldView world,
                        final Items items, final AgentInfo info, final Weaponry weaponry,
                        final IPathPlanner <ILocated> pathPlanner, final IUnrealPathExecutor <ILocated> pathExecutor,
                        final AdvancedLocomotion move, final Raycasting raycasting, final AutoTraceRay cardinalRayArray []) {

        /* Pointer to the body field from T800 */
        CompleteBotCommandsWrapper body1 = body;
        this.act = act;
        /* Pointer to the world field from T800 */
        IVisionWorldView world1 = world;
        this.items = items;
        this.info = info;
        /* Pointer to the weaponry field from T800 */
        Weaponry weaponry1 = weaponry;
        this.pathPlanner = pathPlanner;
        this.pathExecutor = pathExecutor;
        this.move = move;
        /* Pointer to the raycasting field from T800 */
        Raycasting raycasting1 = raycasting;
        this.cardinalRayArray = cardinalRayArray;
    }

    //__________________________________________________________________________

    /**
     * Each sub state tells how the bot should move.
     * @param enemy Enemy.
     * @param facingSpot Location where we want to make the bot face.
     * @throws SubStatusException Thrown when the sub state can't be executed.
     */
    public abstract void executeMovement (final Player enemy, final Location facingSpot) throws SubStatusException;

    //__________________________________________________________________________

    /**
     * We have reached the desired destination.
     */
    public void destinationReached () {
        destination = null;
    }

    //__________________________________________________________________________

    /**
     * Stops execution.
     */
    public void stopExecution () {
        destination = null;
    }
}
