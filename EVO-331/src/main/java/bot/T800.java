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
package bot;

import behavior.primaryStates.Attack;
import behavior.primaryStates.Camp;
import behavior.primaryStates.Greedy;
import behavior.primaryStates.Hunt;
import behavior.primaryStates.PrimaryState;
import behavior.primaryStates.Retreat;
import behavior.secondaryStates.CriticalHealth;
import behavior.secondaryStates.CriticalWeaponry;
import behavior.secondaryStates.DefensiveProfile;
import behavior.secondaryStates.OfensiveProfile;
import behavior.secondaryStates.PickupAmmo;
import behavior.secondaryStates.PickupHealth;
import behavior.secondaryStates.PickupWeapon;
import behavior.secondaryStates.SecondaryState;
import brain.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.*;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import enumTypes.rayCardinals;
import evolutionaryComputation.*;
import java.io.IOException;
import java.util.logging.Level;
import javax.vecmath.Vector3d;
import knowledge.*;
import org.apache.log4j.Logger;
import synchro.Job;
import synchro.SyncMessage;
import synchro.WorkQueueClient;
import utilities.*;

/**
 * This class contains the main loop of execution. From it, we communicate with
 * the rest of the classes that are responsible for behavior, knowledge etc.
 *
 * @author Francisco Aisa García
 * @author Ricardo Caballero Moral
 * @version 1.0.4M
 */
public class T800 extends UT2004BotModuleController {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(T800.class);
    // *************************************************************************
    //                              STATIC FIELDS
    // *************************************************************************
    /**
     * Array containing all the path nodes from the current map
     */
    public static NavPoint pathNodes[];
    /**
     * Array containing all the important areas of the current map
     */
    public static NavPoint areas[];
    // *************************************************************************
    //                             INSTANCE FIELDS
    // *************************************************************************
    private boolean busy = false;
    /**
     * T800 primary state
     */
    private int primaryState = PrimaryState.States.HUNT.ordinal();
    /**
     * T800 secondary state
     */
    private int secondaryState = SecondaryState.States.DISABLED.ordinal();
    /**
     * Current Individual to be evaluated
     */
    Individual testIndividual;
    /**
     * Array containing all the primary states
     */
    private PrimaryState primaryStateArray[];
    /**
     * Array containing all the secondary states
     */
    private SecondaryState secondaryStateArray[];
    /**
     * Temporary information (that we know or have guessed) about the enemy
     */
    private EnemyInfo enemyInfo;
    /**
     * Module that controls the access to the data base
     */
    private Memoria memory;
    /**
     * It is the brains of the bot, it decides when to switch from one state to
     * another
     */
    private Skynet skynet;
    /**
     * Genetic algorithm being used
     */
    /**
     * Enemy's information (it is null when we are not seeing the enemy)
     */
    private Player enemy = null;
    /**
     * It represents the destination where we want to make the bot go (no matter
     * what)
     */
    private Location destination = null;
    /**
     * True if we have just killed an enemy
     */
    private boolean enemyKilled = false;
    /**
     * Memorable quotes to be said by the T800
     */
    private String memorableQuotes[] = {"Hasta la vista, baby", "No problemo", "Terminated",
        "Your clothes... give them to me, now!", "I'll be back!",
        "Chill out, dickwad", "It's in your nature to destroy yourselves",
        "I need a vacation", "I need your clothes, boots and your motorcycle",
        "My mission is to protect you", "Come with me if you want to live!",
        "I swear I will not kill anyone"};
    /**
     * Array containing all the rays (
     *
     * @see AutoTraceRay.class). To make it easier, we will use constants to
     * reference certain rays in the array. NORTH RAY = 0 NORTHEAST RAY = 1 EAST
     * RAY = 2 SOUTHEAST RAY = 3 SOUTH RAY = 4 SOUTHWEST RAY = 5 WEST RAY = 6
     * NORTHWEST RAY = 7
     */
    private AutoTraceRay cardinalRayArray[] = new AutoTraceRay[8];
    private WorkQueueClient workClient;
    private int number = 0;
    // *************************************************************************
    //                                METHODS
    // *************************************************************************

    @Override
    public void prepareBot(UT2004Bot bot) {
        // TODO used for initialization, initialize agent modules here






        //geneticAlg = new ExtremeElitism(50, 30, new UniformCrossover(), memory);

        // Load the genetic algorithm

        try {
            //Get current individual ID from server
            workClient = new WorkQueueClient(4000);
            if (workClient != null) {
                synchro.SyncMessage id = workClient.readMessage(null);
                testIndividual = (IndividualV1) id.getData();

                if (logger.isDebugEnabled()) {
                    logger.info("prepareBot(UT2004Bot) - ID recibido= " + id.getId()); //$NON-NLS-1$
                }
                //   id.setStatus(Job.Estado.Running);
                workClient.sendMessage(id);
                number = id.getId();

                //  memory.loadPoblacion(26);
                if (logger.isDebugEnabled()) {
                    logger.info("prepareBot(UT2004Bot) - Confirmación ID enviado= " + id.getId()); //$NON-NLS-1$
                }
            }
        } catch (IOException ex) {
            logger.error("prepareBot(UT2004Bot): Connection problem", ex); //$NON-NLS-1$
            throw new PogamutException("No DB server found", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("prepareBot(UT2004Bot)", ex); //$NON-NLS-1$
        }
        logger.info("Using ID " + number);
        //      geneticAlg.setCurrentIndividual(number);
        // Get a reference to the individual that is goint to play the current match
        //     testIndividual = memory.getPopulation()[number];

        enemyInfo = new EnemyInfo(body);
        skynet = new Skynet(body, testIndividual);
        // Initialize the Data Base controller
        memory = new Memoria(false, false, 26, false);
        //    memory.storeBestIndividuo(IndividualV1.class.getSimpleName(), null, true);
        memory.debug(false);
        // Initialize all primary states
        primaryStateArray = new PrimaryState[5];
        primaryStateArray[PrimaryState.States.ATTACK.ordinal()] = new Attack(body, act, world, game, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray, shoot, testIndividual);
        primaryStateArray[PrimaryState.States.RETREAT.ordinal()] = new Retreat(body, act, world, game, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray, shoot, testIndividual);
        primaryStateArray[PrimaryState.States.HUNT.ordinal()] = new Hunt(body, act, world, game, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray, shoot, testIndividual);
        primaryStateArray[PrimaryState.States.GREEDY.ordinal()] = new Greedy(body, act, world, game, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray, shoot, testIndividual);
        primaryStateArray[PrimaryState.States.CAMP.ordinal()] = new Camp(body, act, world, game, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray, shoot, testIndividual);

        // Initialize all secondary states
        secondaryStateArray = new SecondaryState[8];
        secondaryStateArray[SecondaryState.States.DISABLED.ordinal()] = null;
        secondaryStateArray[SecondaryState.States.DEFENSIVE_PROFILE.ordinal()] = new DefensiveProfile(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.OFENSIVE_PROFILE.ordinal()] = new OfensiveProfile(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.PICKUP_WEAPON.ordinal()] = new PickupWeapon(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.PICKUP_AMMO.ordinal()] = new PickupAmmo(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.PICKUP_HEALTH.ordinal()] = new PickupHealth(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.CRITICAL_HEALTH.ordinal()] = new CriticalHealth(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);
        secondaryStateArray[SecondaryState.States.CRITICAL_WEAPONRY.ordinal()] = new CriticalWeaponry(body, act, world, items, info, weaponry, pathPlanner, pathExecutor, move, raycasting, cardinalRayArray);



    }

    //__________________________________________________________________________
    @Override
    public Initialize getInitializeCommand() {
        // TODO init bot's params there

        // Initialize the bot's name

        String name = "TX-V";
        int currentIndv = number;
        if (currentIndv < 10) {
            name = name + memory.getCurrentGeneration() + "0" + currentIndv;
        } else {
            name = name + memory.getCurrentGeneration() + currentIndv;
        }
       
        return new Initialize().setName(name);
       
    }

    //__________________________________________________________________________
    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @SuppressWarnings("unchecked")
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange config, InitedMessage init) {
        // Initialize listeners for pathExecutor
        initializePathListeners();
        // Initialize raycasting
        initializeRayCasting();
        // Get all the path nodes in the current map
        pathNodes = Initialization.initializePathNodes(world);
        // Get all the important areas in the current map
        areas = Initialization.initializeAreas(items);

        // Load all the known information about the current map in the data base
        memory.load(game.getMapName());
    }

    //__________________________________________________________________________
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // bot is spawned for the first time in the environment
        // examine 'self' to examine current bot's location and other stuff
        // receive logs from the path executor so you can get a grasp on how it is working
        pathExecutor.getLog().setLevel(Level.INFO);
        testIndividual.setDeaths(0);
        testIndividual.setDeaths(0);
        testIndividual.setKills(0);
        testIndividual.setTotalDamageGiven(0);
        testIndividual.setTotalDamageTaken(0);
        testIndividual.setTotalTimeShock(0);
        testIndividual.setTotalTimeSniper(0);
        testIndividual.setNShields(0);

        testIndividual.setNSuperShields(0);
        System.out.println("Comienza bot");


    }

    // *************************************************************************
    //                   THREAD THAT EXECUTES BOT'S LOGIC
    // *************************************************************************
    @Override
    public void logic() throws PogamutException {
        // Which are the following states?
        int behaviorArray[] = skynet.behave(info, weaponry, enemy, enemyInfo, game);
        primaryState = behaviorArray[0];
        secondaryState = behaviorArray[1];

        // Must we go somwhere?
        destination = skynet.estimateDestination(info, enemy, weaponry, items);

        // Should we be facing anything?
        /*
         Used to tell the bot where he should be facing (useful when using
         pathExecutor)
         */
        Location facingSpot = enemy != null ? enemy.getLocation() : null;

        // Should we blow a combo or through a spam?
        /*
         Location of a feasible spam or combo
         */
        Location bullseye = skynet.estimateTarget();

        // Switch to best weapon, move and shoot (if necessary)
        primaryStateArray[primaryState].switchToBestWeapon(enemy, enemyInfo);
        primaryStateArray[primaryState].executeMovement(secondaryStateArray[secondaryState], destination, enemy, facingSpot, enemyInfo);
        primaryStateArray[primaryState].engage(enemy, bullseye);

        // Reset temporary information
        skynet.resetTempInfo();
    }

    // *************************************************************************
    //                                LISTENERS
    // *************************************************************************
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Item.class)
    protected void objectAppeared(WorldObjectAppearedEvent<Item> event) {
        Item item;
        item = event.getObject();
        Memoria.update(item);
    }

    //__________________________________________________________________________
    /**
     * Whenever an object of a certain type gets in or out of our line of vision
     * it gets triggered. In this particular case, it gets triggered when the
     * object is of type Player.
     *
     * @param event Player appeared..
     */
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
    protected void playerAppeared(WorldObjectAppearedEvent<Player> event) {
        // Update enemy information
        enemy = event.getObject();
        // Update the last known location of the enemy
        enemyInfo.updateEnemyLocation(enemy.getLocation(), game.getTime());
    }

    //__________________________________________________________________________
    /**
     * Whenever an object is updated, it gets triggered. In this particular
     * case, it gets triggered when the object is of type Player.
     *
     * @param event Player updated..
     */
    @ObjectClassEventListener(eventClass = WorldObjectUpdatedEvent.class, objectClass = Player.class)
    protected void playerUpdated(WorldObjectUpdatedEvent<Player> event) {
        // Update enemy information
        // Note that it is null during the handshake, hence, if we are going to use
        // getLocation we should check if it is null first.
        enemy = event.getObject();
        Location enemyLocation = enemy.getLocation();

        if (enemyLocation == null) {
            enemyInfo.updateEnemyLocation(null, -1);
        } else {
            enemyInfo.updateEnemyLocation(enemyLocation, game.getTime());
        }

        enemyInfo.updateWeapon(enemy.getWeapon());
        if (enemyKilled) {
            enemyInfo.reset();
            enemyKilled = false;
        }
    }

    //__________________________________________________________________________
    /**
     * Whenever an object gets out of sight, it gets triggered. In this
     * particular case, it gets triggered when the enemy gets out of sight.
     *
     * @param event Player disappeared.
     */
    @ObjectClassEventListener(eventClass = WorldObjectDisappearedEvent.class, objectClass = Player.class)
    protected void playerDisappeared(WorldObjectDisappearedEvent<Player> event) {
        enemy = null;
        // Update relevant information in the states
        primaryStateArray[primaryState].playerDisappeared();
    }

    //__________________________________________________________________________
    /**
     * Whenever a projectile is in the bots field of vision, it gets triggered.
     * Note that projectiles can also be the ones we shoot.
     *
     * @param event Projectile coming.
     */
    @ObjectClassEventListener(eventClass = WorldObjectUpdatedEvent.class, objectClass = IncomingProjectile.class)
    protected void incomingProjectile(WorldObjectUpdatedEvent<IncomingProjectile> event) {
        // Get the object of type IncomingProjectile
        IncomingProjectile projectile = event.getObject();
        // Classifie the projectile
        skynet.incomingProjectile(projectile, enemy);
    }

    //__________________________________________________________________________
    /**
     * Whenever we get hit, it gets triggered.
     *
     * @param event BotDamaged event.
     */
    @EventListener(eventClass = BotDamaged.class)
    protected void botDamaged(BotDamaged event) {
        // Trigger the non cognitive behavior of the bot in response to the hit
        primaryStateArray[primaryState].botDamaged(event, game.getTime(), enemy);
        // Increment the amount of damage that has been taken
        testIndividual.incrementDamageTaken(event.getDamage());
    }

    @Override
    public void botShutdown() {
        saveInfo();
        super.botShutdown();
    }

    @Override
    public void logicShutdown() {

        super.logicShutdown();
    }
    //__________________________________________________________________________

    /**
     * Whenever the bot hears a noise, it gets triggered (usually when he hears
     * another bot/user).
     *
     * @param event HearNoise event.
     */
    @EventListener(eventClass = HearNoise.class)
    protected void hearNoise(HearNoise event) {
        // Trigger the non cognitive behavior of the bot in response to the noise
        primaryStateArray[primaryState].hearNoise(event, game.getTime(), enemy);
    }

    //____________restar______________________________________________________________
    /**
     * Whenever an item is picked up, it gets triggered. Note that it also gets
     * triggered when the bot picks up things (he hears it).
     *
     * @param event HearPickup event.
     */
    @EventListener(eventClass = HearPickup.class)
    protected void hearPickup(HearPickup event) {
        // Update enemy's information
        enemyInfo.hearPickup(event, info, game, items);
        // Trigger the non cognitive behavior of the bot in response to the noise
        primaryStateArray[primaryState].hearPickup(event, game.getTime(), enemy);
    }

    /**
     * Whenever we pick up an item, it gets triggered.
     *
     * @param event ItemPickedUp event.
     */
    @EventListener(eventClass = ItemPickedUp.class)
    protected void itemPickedUp(ItemPickedUp event) {
        ItemType randomItem = event.getType();

        if (randomItem.equals(ItemType.SUPER_SHIELD_PACK)) {
            testIndividual.superShieldPickedUp();
        } else if (randomItem.equals(ItemType.SHIELD_PACK)) {
            testIndividual.shieldPickedUp();
        } else if (randomItem.equals(ItemType.SHOCK_RIFLE)) {
            testIndividual.shockPickedUp();
        } else if (randomItem.equals(ItemType.SNIPER_RIFLE)
                || randomItem.equals(ItemType.LIGHTNING_GUN)) {

            testIndividual.sniperPickedUp();
        }
    }

    //__________________________________________________________________________
    /**
     * Whenever a player dies, it gets triggered.
     *
     * @param event PlayerKilled event.
     */
    @EventListener(eventClass = PlayerKilled.class)
    protected void playerKilled(PlayerKilled event) {
        // If we have killed him, let's say a charming comment :P
        UnrealId killerId = event.getKiller();
        if (killerId != null && info.getId().equals(killerId)) {
            if (Math.random() < 0.5) {
                int randomQuote = Arithmetic.doRandomNumber(0, memorableQuotes.length - 1);
                body.getCommunication().sendGlobalTextMessage(memorableQuotes[randomQuote]);
            }
        }

        // Reset enemy's information
        enemyInfo.reset();
        enemyKilled = true;

        // Increment the count of kills
        testIndividual.incrementKills();
    }

    //__________________________________________________________________________
    /**
     * Whenever a user/bot joins the match it gets triggered.
     *
     * @param event PlayerJoinsGame event.
     */
    @EventListener(eventClass = PlayerJoinsGame.class)
    protected void playerJoinedGame(PlayerJoinsGame event) {
        enemyInfo.setName(event.getName());
    }

    //__________________________________________________________________________
    /**
     * Whenever a user/bot leaves the match it gets triggered.
     *
     * @param event PlayerLeft event.
     */
    @EventListener(eventClass = PlayerLeft.class)
    protected void playerLeft(PlayerLeft event) {
        enemyInfo.eraseName();
        enemyInfo.reset();
    }

    //__________________________________________________________________________
    /**
     * When the match ends it gets triggered.
     *
     * @param event MapFinished event.
     */
    @EventListener(eventClass = MapFinished.class)
    protected void mapFinished(MapFinished event) {
        // Stop execution if running
        if (pathExecutor.isExecuting()) {
            pathExecutor.stop();
        }
        
        // Stop the clocks
        testIndividual.setMatchTime(stats.getCurrentMatchTime());
        testIndividual.timeout();
        //  saveInfo();
        //testIndividual.setPogamutStats(stats);
        
        this.bot.stop();
    }
    //__________________________________________________________________________
    /**
     * Game restart trigger.
     *
     * @param event GameRestarted event.
     */
    @EventListener(eventClass = GameRestarted.class)
    protected void gameRestarted(GameRestarted event) {
   
        testIndividual.setDeaths(0);
        testIndividual.setDeaths(0);
        testIndividual.setKills(0);
        testIndividual.setTotalDamageGiven(0);
        testIndividual.setTotalDamageTaken(0);
        testIndividual.setTotalTimeShock(0);
        testIndividual.setTotalTimeSniper(0);
        testIndividual.setNShields(0);
        stats.resetMatchTime();
        testIndividual.setNSuperShields(0);
        System.out.println("Comienza bot");
   
    }

    //__________________________________________________________________________
    /**
     * Whenever a bot gets hit it gets triggered.
     *
     * @param event PlayerDamaged event.
     */
    @EventListener(eventClass = PlayerDamaged.class)
    protected void playerDamaged(PlayerDamaged event) {
        // Depending on what hit the player, we may have to consider certain strategies
        primaryStateArray[primaryState].playerDamaged(event);
        // Update the amount of damage we assume the enemy just lost
        enemyInfo.hit(event.getDamage());
        // Increment the amount of damage that has been given
        testIndividual.incrementDamageGiven(event.getDamage());
    }

    //__________________________________________________________________________
    /**
     * Whenever the bot spawns it gets triggered.
     *
     * @param event Spawn event.
     */
    @EventListener(eventClass = Spawn.class)
    public void newSpawn(Spawn event) {
        // Empty
    }

    //__________________________________________________________________________
    /**
     * Whenever the bot dies it gets triggered.
     *
     * @param event BotKilled event.
     */
    @Override
    public void botKilled(BotKilled event) {
        primaryStateArray[primaryState].stopExecution(secondaryStateArray[secondaryState]);
        // Reset temporary information
        PrimaryState.resetTempInfo();
        // Increment the count of deaths
        testIndividual.incrementDeaths();
        // Stop the clocks
        testIndividual.timeout();
    }

    // *************************************************************************
    //                             OTHER METHODS
    // *************************************************************************
    /**
     * Initialize pathListener. Depending on what happens during execution of a
     * plan the pathListener will raise certain flags. For example, if we reach
     * our destination a flag will be raised, if we get stuck another flag will
     * be raised on so on.
     */
    private void initializePathListeners() {
        // add stuck detector that watch over the path-following, if it (heuristicly) finds out that the bot has stuck somewhere,
        // it reports an appropriate path event and the path executor will stop following the path which in turn allows
        // us to issue another follow-path command in the right time

        //pathExecutor.addStuckDetector (new UT2004TimeStuckDetector(bot, 3.0));       // if the bot does not move for 3 seconds, considered that it is stuck

        pathExecutor.addStuckDetector(new UT2004PositionStuckDetector(bot)); // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck

        // IMPORTANT
        // adds a listener to the path executor for its state changes, it will allow you to
        // react on stuff like "PATH TARGET REACHED" or "BOT STUCK"
        pathExecutor.getState().addStrongListener(new FlagListener<IPathExecutorState>() {
            @Override
            public void flagChanged(IPathExecutorState changedValue) {
                switch (changedValue.getState()) {
                    // If the computation fails
                    case PATH_COMPUTATION_FAILED:
                        body.getCommunication().sendGlobalTextMessage("PATH_COMPUTATION_FAILED!");

                    //break;
                    // If we reach the destination
                    case TARGET_REACHED:
                        //body.getCommunication ().sendGlobalTextMessage ("TARGET_REACHED");

                        // If the spot where we are is the one that skynet wants us to go
                        // we set it to null
                        if (destination != null && info.getDistance(destination) < 50) {
                            destination = null;
                        }

                        // Notify the primary state that we have reached the destination
                        primaryStateArray[primaryState].destinationReached(secondaryStateArray[secondaryState]);

                        break;
                    // If we are stuck
                    case STUCK:
                        //body.getCommunication().sendGlobalTextMessage ("STUCK");

                        // Set destination to null
                        destination = null;

                        // Notify the primary state that we are stucked
                        primaryStateArray[primaryState].botStuck(secondaryStateArray[secondaryState]);

                        break;
                }
            }
        });
    }

    //__________________________________________________________________________
    @Override
    protected void finalize() throws Throwable {
        saveInfo();
        super.finalize();
    }

    /**
     * Initialize raycasting.
     */
    private void initializeRayCasting() {
        // initialize rays for raycasting
        final int rayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 10);
        final int backRayLength = 100000;

        // settings for the rays
        // fastTrace = true, nos proporciona informacion solo sobre colision
        // aunque es la version mas rapida
        boolean fastTrace = false;        // perform only fast trace == we just need true/false information
        boolean floorCorrection = true;   // provide floor-angle correction for the ray (when the bot is running on the skewed floor, the ray gets rotated to match the skew)
        boolean traceActor = false;       // whether the ray should collid with other actors == bots/players as well

        // 1. remove all previous rays, each bot starts by default with three
        // rays, for educational purposes we will set them manually
        getAct().act(new RemoveRay("All"));

        // 2. create new rays
        raycasting.createRay(rayCardinals.NORTH.toString(), new Vector3d(1, 0, 0), rayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.NORTH_EAST.toString(), new Vector3d(1, 1, 0), rayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.EAST.toString(), new Vector3d(0, 1, 0), backRayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.SOUTH_EAST.toString(), new Vector3d(-1, 1, 0), backRayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.SOUTH.toString(), new Vector3d(-1, 0, 0), backRayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.SOUTH_WEST.toString(), new Vector3d(-1, -1, 0), backRayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.WEST.toString(), new Vector3d(0, -1, 0), backRayLength, fastTrace, floorCorrection, traceActor);
        raycasting.createRay(rayCardinals.NORTH_WEST.toString(), new Vector3d(1, -1, 0), rayLength, fastTrace, floorCorrection, traceActor);

        // register listener called when all rays are set up in the UT engine
        raycasting.getAllRaysInitialized().addListener(new FlagListener<Boolean>() {
            @Override
            public void flagChanged(Boolean changedValue) {
                // once all rays were initialized store the AutoTraceRay objects
                // that will come in response in local variables, it is just
                // for convenience
                cardinalRayArray[0] = raycasting.getRay(rayCardinals.NORTH.toString());
                cardinalRayArray[1] = raycasting.getRay(rayCardinals.NORTH_EAST.toString());
                cardinalRayArray[2] = raycasting.getRay(rayCardinals.EAST.toString());
                cardinalRayArray[3] = raycasting.getRay(rayCardinals.SOUTH_EAST.toString());
                cardinalRayArray[4] = raycasting.getRay(rayCardinals.SOUTH.toString());
                cardinalRayArray[5] = raycasting.getRay(rayCardinals.SOUTH_WEST.toString());
                cardinalRayArray[6] = raycasting.getRay(rayCardinals.WEST.toString());
                cardinalRayArray[7] = raycasting.getRay(rayCardinals.NORTH_WEST.toString());
            }
        });
        // have you noticed the FlagListener interface? The Pogamut is often using {@link Flag} objects that
        // wraps some iteresting values that user might respond to, i.e., whenever the flag value is changed,
        // all its listeners are informed

        // 3. declare that we are not going to setup any other rays, so the 'raycasting' object may know what "all" is
        raycasting.endRayInitSequence();

        // change bot's default speed
        //config.setSpeedMultiplier(10.0f);

        // IMPORTANT:
        // The most important thing is this line that ENABLES AUTO TRACE functionality,
        // without ".setAutoTrace(true)" the AddRay command would be useless as the bot won't get
        // trace-lines feature activated
        getAct().act(new Configuration().setDrawTraceLines(false).setAutoTrace(true));

        // FINAL NOTE: the ray initialization must be done inside botInitialized method or later on inside
        //             botSpawned method or anytime during doLogic method
    }

    // *************************************************************************
    //                                 MAIN
    // *************************************************************************
    /**
     * This method is called when the bot is started either from IDE or from
     * command line. It connects the bot to the game server.
     *
     * @param args
     */
    public static void main(String args[]) throws PogamutException, Exception {
        // Launch a local bot
        // Launch a local bot


        UT2004BotRunner bot = new UT2004BotRunner(T800.class, "T800").setMain(true);
        bot.setName("Prueba");
        bot.startAgent();

        // Launch a remote bot
        //new UT2004BotRunner (T800.class, "T800", "<ip address>", 3000).setMain(true).startAgent();
        // Launch a remote bot
        //new UT2004BotRunner (T800.class, "T800", "<ip address>", 3000).setMain(true).startAgent();
    }

    private void saveInfo() {
        // The match has ended, store level information
        if (logger.isDebugEnabled()) {
            logger.info("saveInfo() - Grabando items"); //$NON-NLS-1$
        }
        memory.store(game.getMapName());
        // Store genetic algorithm information
        if (logger.isDebugEnabled()) {
            logger.info("saveInfo() - Grabando genes"); //$NON-NLS-1$
        }
        

        try {

            SyncMessage msg = new synchro.SyncMessage(this.number, Job.Estado.Finished);
            msg.data = ((IndividualV1) testIndividual);
       //     logger.info("Daño recibido-FINAL" + ((Individual) (msg.getData())).getTotalDamageTaken());
            workClient.sendMessage(msg);
            msg.data = ((IndividualStats) testIndividual.getStats());
            msg.setStatus(Job.Estado.Init);
            workClient.sendMessage(msg);
            logger.info("Daño recibido-FINALENVIO" + ((Individual) (msg.getData())).getTotalDamageTaken());
            if (logger.isDebugEnabled()) {
                logger.info("saveInfo() - Confirmación enviada"); //$NON-NLS-1$
            }
        } catch (IOException ex) {
            logger.error("saveInfo()", ex); //$NON-NLS-1$
        }
        workClient.close();
    }
}
