package ca.hilikus.jrobocom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.events.event_manager.SubscriptionManager;
import ca.hilikus.events.event_manager.api.EventPublisher;
import ca.hilikus.jrobocom.GameTracker.GameStatusListener;
import ca.hilikus.jrobocom.events.GameListener;
import ca.hilikus.jrobocom.events.LeaderChangedEvent;
import ca.hilikus.jrobocom.events.PlayerEliminatedEvent;
import ca.hilikus.jrobocom.events.ResultEvent;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.security.GameSecurityManager;
import ca.hilikus.jrobocom.timing.Clock;
import ca.hilikus.jrobocom.timing.Delayer;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * A game session. It groups all the players, settings and world of a single session
 * 
 * @author hilikus
 * 
 */
public class Session {
    private Clock clock = new MasterClock();
    private Delayer delayer = new Delayer();
    private World theWorld;
    private GameTracker tracker = new GameTracker();
    private SubscriptionManager subscriptions = new SubscriptionManager();
    private List<Player> players;

    private static final Logger log = LoggerFactory.getLogger(Session.class);

    static {
	System.setSecurityManager(new GameSecurityManager());
    }

    /**
     * Receives events about the game
     * 
     */
    public class EventHandler implements GameStatusListener {

	@Override
	public void update(ResultEvent result) {
	    clean();
	}

	@Override
	public void update(LeaderChangedEvent event) {
	    List<Player> leaders = event.getLeaders();
	    for (Player player : players) {
		boolean found = false;
		for (Player leader : leaders) {
		    if (player.equals(leader)) {
			found = true;
			break;
		    }
		}
		player.setLeader(found);
	    }

	}

	@Override
	public void update(PlayerEliminatedEvent event) {
	    event.getEliminatedPlayer().clean();
	}

    }

    /**
     * Creates a new session with a list of player whose code is to be loaded
     * 
     * @param pPlayers list of player whose code is to be loaded
     * @param controller receiver of <u>all</u> game events. Can be null
     */
    public Session(List<Player> pPlayers, GameListener controller) {
	if (pPlayers == null) {
	    throw new IllegalArgumentException("List of players can't be null");
	}
	theWorld = new World(clock, delayer);
	theWorld.setEventDispatcher(subscriptions.getEventDispatcher(theWorld));
	tracker.setEventDispatcher(subscriptions.getEventDispatcher(tracker));
	
	if (controller != null) {
	    subscriptions.subscribe(theWorld, controller);
	    subscriptions.subscribe(tracker, controller);
	}
	subscriptions.subscribe(theWorld, tracker.getEventsReceiver());
	subscriptions.subscribe(tracker, new EventHandler());
	subscriptions.subscribe((EventPublisher)clock, theWorld); //TODO: fix this, don't assume that clock impl is event publisher
	
	
	players = pPlayers;
	for (Player onePlayer : pPlayers) {
	    Robot eve = new Robot(theWorld, delayer, onePlayer.getCode(), onePlayer.getTeamName() + " Alpha", onePlayer);
	    subscriptions.subscribe(eve, controller);
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
     * Stops the clock temporarily for this session
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

    /**
     * @return the list of players in the session
     */
    public List<Player> getPlayers() {
	return players;
    }

    /**
     * Cleans each component of the session
     */
    public void clean() {
	log.debug("[clean] Cleaning session {}", this);

	clock.clean();
	theWorld.clean();

    }
}
