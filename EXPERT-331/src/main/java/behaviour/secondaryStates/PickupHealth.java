package behaviour.secondaryStates;

import behaviour.primaryStates.PrimaryState;
import exceptions.SubStatusException;
import utilities.Arithmetic;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.agent.navigation.IUnrealPathExecutor;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Group;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetCrouch;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

import java.util.Map;


/**
 *
 * @author Francisco Aisa García
 */


public class PickupHealth extends SecondaryState{

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
    public PickupHealth (final CompleteBotCommandsWrapper body, final IAct act, final IVisionWorldView world,
                         final Items items, final AgentInfo info, final Weaponry weaponry,
                         final IPathPlanner <ILocated> pathPlanner, final IUnrealPathExecutor <ILocated> pathExecutor,
                         final AdvancedLocomotion move, final Raycasting raycasting, final AutoTraceRay cardinalRayArray []) {

        super (body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
    }

    //__________________________________________________________________________

    /**
     * Pickup visible health, if there's not any visible ammo, throw an exception.
     * @param enemy Enemy.
     * @param facingSpot Location where want to make the bot face.
     * @throws SubStatusException Thrown when there is no visible health.
     */
    public void executeMovement (final Player enemy, final Location facingSpot) throws SubStatusException {

        if (PrimaryState.crouched) {
            act.act(new SetCrouch ().setCrouch (false));
            PrimaryState.crouched = false;
        }

        // If we haven't looked for health
        if (destination == null) {
            Map <UnrealId, Item> healthVialMap = items.getVisibleItems(Group.MINI_HEALTH);
            Map <UnrealId, Item> healthPackMap = items.getVisibleItems(Group.HEALTH);

            if (info.getHealth() < 100) {
                if (healthPackMap.isEmpty()) {
                    if (healthVialMap.isEmpty()) {
                        throw new SubStatusException ("No visible health items");
                    }
                    else {
                        // Pickup the closests vials
                        destination = Arithmetic.getClosestItemLocation (info, healthVialMap);
                    }
                }
                else {
                    // Pickup the closests packs
                    destination = Arithmetic.getClosestItemLocation (info, healthPackMap);
                }
            }
            else {
                if (healthVialMap.isEmpty()) {
                    throw new SubStatusException ("No visible health items");
                }
                else {
                    // Pickup the closests vials
                    destination = Arithmetic.getClosestItemLocation (info, healthVialMap);
                }
            }

            IPathFuture <ILocated> pathHandle = pathPlanner.computePath(info.getLocation(), destination);

            if (pathExecutor.isExecuting()) {
                pathExecutor.stop ();
            }

            pathExecutor.followPath(pathHandle);
        }
    }

    //__________________________________________________________________________

    /**
     * Converts to string the state's name.
     * @return The name of the state.
     */
    public String toString () {
        return "PickupHealth";
    }
}
