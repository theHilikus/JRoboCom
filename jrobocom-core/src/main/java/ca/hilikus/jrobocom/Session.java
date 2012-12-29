package ca.hilikus.jrobocom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.events.GameListener;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.security.GameSecurityManager;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * A game session. It groups all the players, settings and world of a single session
 * 
 * @author hilikus
 * 
 */
public class Session {
    private MasterClock clock = new MasterClock();
    private World theWorld;
    private List<Player> players;

    private static final Logger log = LoggerFactory.getLogger(Session.class);

    static {
	System.setSecurityManager(new GameSecurityManager());
    }

    /**
     * Creates a new session with a list of player whose code is to be loaded
     * 
     * @param pPlayers list of player whose code is to be loaded
     * @param controller receiver of game events. Can be null
     */
    public Session(List<Player> pPlayers, GameListener controller) {
	theWorld = new World(clock);
	if (controller != null) {
	    theWorld.getEventHandler().addListener(controller);
	}
	clock.addListener(theWorld);
	players = pPlayers;
	for (Player onePlayer : pPlayers) {
	    Robot eve = new Robot(theWorld, clock, onePlayer.getCode(), onePlayer.getTeamName() + " Alpha",
		    onePlayer);
	    eve.getEventHandler().addListener(controller);
	    Robot.setInheritableListener(controller);
	    theWorld.addFirst(eve);
	    onePlayer.startRobot(eve);

	}
    }

    /**
     * Constructor without event listeners
     * 
     * @param pPlayers list of player whose code is to be loaded
     */
    public Session(List<Player> pPlayers) {
	this(pPlayers, null);
    }

    /**
     * Starts the clock for this session
     */
    public void start() {
	log.info("[start] Starting session");
	clock.start(true);
    }

    /**
     * Stops the clock for this session
     */
    public void stop() {
	log.info("[stop] Stopping session");
	clock.stop();
    }

    /**
     * Execute a single turn
     */
    public void step() {
	log.debug("[step] Single step");
	clock.step();
    }

    /**
     * @param teamId the team number to search for
     * @return the team with the matching id or null if no match found
     */
    public Player getPlayer(int teamId) {
	for (Player player : players) {
	    if (player.getTeamId() == teamId) {
		return player;
	    }
	}

	return null;
    }

    /**
     * @return true if the session is currently running; false otherwise
     */
    public boolean isRunning() {
	return clock.isRunning();
    }

    /**
     * Sets the period of the session's clock.
     * 
     * @param millis period length in milliseconds
     */
    public void setClockPeriod(int millis) {
	clock.setPeriod(millis);
    }

    /**
     * @return the period in milliseconds of the session's clock
     */
    public int getClockPeriod() {
	return clock.getPeriod();
    }

}
