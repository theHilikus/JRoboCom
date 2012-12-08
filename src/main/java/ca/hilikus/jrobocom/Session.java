package ca.hilikus.jrobocom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.gui.events.GameListener;
import ca.hilikus.jrobocom.robot.Robot;
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

    /**
     * Creates a new session with a list of player whose code is to be loaded
     * 
     * @param pPlayers list of player whose code is to be loaded
     * @param controller receiver of game events
     */
    public Session(List<Player> pPlayers, GameListener controller) {
	theWorld = new World(clock);
	if (controller != null) {
	    theWorld.getEventHandler().addListener(controller);
	}
	players = pPlayers;
	for (Player onePlayer : pPlayers) {
	    Robot eve = new Robot(theWorld, clock, onePlayer.getCode(), onePlayer.getTeamName() + " Alpha",
		    onePlayer);
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
}
