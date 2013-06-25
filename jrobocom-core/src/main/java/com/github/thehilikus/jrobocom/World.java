package com.github.thehilikus.jrobocom;

import java.awt.Point;
import java.util.EventListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.events.event_manager.api.EventPublisher;
import com.github.thehilikus.jrobocom.events.RobotAddedEvent;
import com.github.thehilikus.jrobocom.events.RobotMovedEvent;
import com.github.thehilikus.jrobocom.events.RobotRemovedEvent;
import com.github.thehilikus.jrobocom.events.TickEvent;
import com.github.thehilikus.jrobocom.player.ScanResult;
import com.github.thehilikus.jrobocom.player.ScanResult.Found;
import com.github.thehilikus.jrobocom.robot.Robot;
import com.github.thehilikus.jrobocom.security.GamePermission;
import com.github.thehilikus.jrobocom.timing.Delayer;
import com.github.thehilikus.jrobocom.timing.api.Clock;
import com.github.thehilikus.jrobocom.timing.api.ClockListener;

/**
 * The game board
 * 
 * @author hilikus
 * 
 */
public class World implements ClockListener, EventPublisher {

    private Map<Robot, Point> robotsPosition = new ConcurrentHashMap<>();

    private Clock clock;
    
    private Delayer delayer;

    private static final Logger log = LoggerFactory.getLogger(World.class);

    private EventDispatcher eventDispatcher;

    /**
     * Notification interface to be implemented by listeners of World events
     * 
     */
    public static interface WorldListener extends EventListener {
	/**
	 * Called when a robot was added to the world
	 * 
	 * @param add event details
	 */
	public void update(RobotAddedEvent add);

	/**
	 * Called when a robot was removed from the world
	 * 
	 * @param rem event details
	 */
	public void update(RobotRemovedEvent rem);

	/**
	 * Called when a robot changed its location
	 * 
	 * @param mov event details
	 */
	public void update(RobotMovedEvent mov);

    }

    /**
     * Common random number generator
     */
    private static Random generator = new Random();

    /**
     * @param pClock the world clock
     * @param pDelayer in charge of synchronization
     */
    public World(Clock pClock, Delayer pDelayer) {
	clock = pClock;
	delayer = pDelayer;
    }

    /**
     * Adds a new robot to the world in the reference field of the parent. The child becomes owned
     * by <i>this</i> object
     * 
     * @param parent the creating robot
     * @param child the newly created robot
     * @return true if the child was added successfully; false if there was a normal problem with
     *         the addition.
     * @throws IllegalArgumentException if child is not a direct descendant of parent
     */
    public boolean add(Robot parent, Robot child) {
	Point newPosition = getReferenceField(parent, 1);
	if (isOccupied(newPosition)) {
	    return false;
	} else {
	    if (child.getData().getGeneration() != parent.getData().getGeneration() + 1) {
		throw new IllegalArgumentException("Child robot is not a direct descendant of this parent");
	    }
	    addCommon(child, newPosition);
	}
	return true;
    }

    /**
     * Adds the first robot of a team. The robot becomes owned by <i>this</i> object
     * 
     * @param eve the first robot of the player
     * @throws IllegalArgumentException if the robot is not the first from its team or it exists
     *             already
     */
    public void addFirst(Robot eve) {

	if (eve.getData().getGeneration() != 0) {
	    throw new IllegalArgumentException("Robot is not first generation");
	}
	if (getBotsCount(eve.getData().getTeamId(), false) > 0) {
	    throw new IllegalArgumentException(
		    "Provided robot is not the first from its team. Use add(Robot, Robot) instead");
	}
	Point newPosition;
	do {
	    // TODO: check this, looks biased
	    int x = generator.nextInt(GameSettings.getInstance().BOARD_SIZE);
	    int y = generator.nextInt(GameSettings.getInstance().BOARD_SIZE);
	    newPosition = new Point(x, y);
	} while (isOccupied(newPosition));

	addCommon(eve, newPosition);
    }

    private void addCommon(Robot newRobot, Point newPosition) {
	if (robotsPosition.containsKey(newRobot)) {
	    // robot already exists
	    throw new IllegalArgumentException("Trying to add an existing robot");
	}

	robotsPosition.put(newRobot, newPosition);
	delayer.addListener(newRobot.getSerialNumber());
	eventDispatcher.fireEvent(new RobotAddedEvent(newRobot, newPosition));
	log.trace("[addFirst] Added robot {}", newRobot);

    }

    /**
     * Removes a robot from the world and triggers a {@link RobotRemovedEvent}
     * 
     * @param robot the robot to remove from the board
     */
    public void remove(Robot robot) {
	if (!robotsPosition.containsKey(robot)) {
	    throw new IllegalArgumentException("Robot doesn't exist");
	}

	Point lastPosition = robotsPosition.get(robot);
	robotsPosition.remove(robot);
	delayer.removeListener(robot.getSerialNumber());
	eventDispatcher.fireEvent(new RobotRemovedEvent(robot, lastPosition));

    }

    /**
     * Called to dispose of the world
     */
    public void clean() {
	log.debug("[clean] Cleaning world");
	
	for (Robot bot : robotsPosition.keySet()) {
	    bot.die("World cleanup");
	    if (robotsPosition.containsKey(bot)) {
		log.warn("[clean] Robot did not unregister itself. Removing it");
		remove(bot);
	    }
	}

    }

    /**
     * Changes the position of a robot to its current reference field
     * 
     * @param robot the robot
     */
    public void move(Robot robot) {
	if (!robotsPosition.containsKey(robot)) {
	    throw new IllegalArgumentException("Robot doesn't exist");
	}
	if (!robot.getData().isMobile()) {
	    throw new IllegalArgumentException("Robot can't move");
	}

	Point newPosition = getReferenceField(robot, 1);
	if (!isOccupied(newPosition)) {
	    Point oldPosition = robotsPosition.get(robot);
	    robotsPosition.put(robot, newPosition);
	    eventDispatcher.fireEvent(new RobotMovedEvent(robot, oldPosition, newPosition));
	}

    }

    private boolean isOccupied(Point newPosition) {
	return robotsPosition.containsValue(newPosition);
    }

    private Point getReferenceField(Robot robot, int dist) {
	assert dist > 0;
	assert robotsPosition.containsKey(robot);
	int size = GameSettings.getInstance().BOARD_SIZE;

	if (dist >= size) {
	    throw new IllegalArgumentException("Cannot scan bigger than the board");
	}

	int x = robotsPosition.get(robot).x;
	int y = robotsPosition.get(robot).y;

	switch (robot.getData().getFacing()) {
	    case NORTH:
		y = (y - dist) % size;
		break;
	    case EAST:
		x = (x + dist) % size;
		break;
	    case SOUTH:
		y = (y + dist) % size;
		break;
	    case WEST:
		x = (x - dist) % size;
		break;

	}
	if (x < 0) {
	    x += size;
	}
	if (y < 0) {
	    y += size;
	}

	return new Point(x, y);
    }

    /**
     * Gets the robot in the reference field
     * 
     * @param robot the robot to use as centre
     * @return the robot in front, or null if field is empty
     */
    public Robot getNeighbour(Robot robot) {
	Point neighbourPos = getReferenceField(robot, 1);
	return getRobotAt(neighbourPos);

    }
    
    private Robot getRobotAt(Point position) {
	for (Entry<Robot, Point> robotInfo : robotsPosition.entrySet()) {
	    if (robotInfo.getValue().equals(position)) {
		return robotInfo.getKey();
	    }
	}
	//nothing found
	return null;
    }

    /**
     * Scans up to a number of fields in front of the robot, stopping on the first field that
     * contains a robot
     * 
     * @param robot scanning robot
     * @param dist maximum distance
     * @return a container of information about the scan
     * @see ScanResult
     */
    public ScanResult scan(Robot robot, int dist) {
	Point space = getReferenceField(robot, dist);
	Robot inPosition = getRobotAt(space);
	ScanResult ret = null;
	if (inPosition == null) {
	    ret = new ScanResult(Found.EMPTY, dist);
	} else {
	    if (robot.getData().getTeamId() == inPosition.getData().getTeamId()) {
		ret = new ScanResult(Found.FRIEND, dist);
	    } else {
		ret = new ScanResult(Found.ENEMY, dist);
	    }
	}

	return ret;
    }

    /**
     * Get the total number of living robots from or not from a team
     * 
     * @param teamId the team to search for
     * @param invert if true, find robots NOT in teamId
     * @return number of robots in the specified group
     */
    public int getBotsCount(int teamId, boolean invert) {
	int total = 0;
	for (Robot bot : robotsPosition.keySet()) {
	    if (bot.getData().getTeamId() == teamId) {
		if (!invert) {
		    total++;
		}
	    } else {
		if (invert) {
		    total++;
		}
	    }
	}
	return total;

    }

    /**
     * @return the age of the world in cycles / 1000
     */
    public int getAge() {
	return (int) clock.getCycles();
    }


    /**
     * @return the generator
     */
    public static final Random getRandGenerator() {
	return generator;
    }

    static final void setRandGenerator(Random newGen) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("setRandomGenerator"));
	}

	generator = newGen;
    }

    /**
     * Called in every clock tick
     * 
     * @param cyclesEvent info about the current pulse
     */
    @Override
    public void update(TickEvent cyclesEvent) {
	checkRobotsAge();

    }

    private void checkRobotsAge() {
	for (Robot robot : robotsPosition.keySet()) {
	    if (robot.getData().getAge() > GameSettings.getInstance().MAX_AGE) {
		robot.die("Too old to fight");
	    }
	}

    }

    @Override
    public void setEventDispatcher(EventDispatcher dispatcher) {
	eventDispatcher = dispatcher;
	
    }

}
