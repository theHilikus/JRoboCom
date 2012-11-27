package ca.hilikus.jrobocom;

import java.awt.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.timing.MasterClock;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * The game board
 * 
 * @author hilikus
 * 
 */
public class World {

    private BiMap<Robot, Point> robotsPosition = HashBiMap.create();

    private Set<Integer> teamIds = new HashSet<>();

    private MasterClock clock;

    private static final Logger log = LoggerFactory.getLogger(World.class);

    /**
     * Common random number generator
     */
    public static Random generator = new Random();

    
    /**
     * @param pClock the world clock
     */
    public World(MasterClock pClock) {
	clock = pClock;
    }
    
    
    /**
     * Adds a new robot to the world in the reference field of the parent
     * 
     * @param parent the creating robot
     * @param child the newly created robot
     */
    public void add(Robot parent, Robot child) {
	Point newPosition = getReferenceField(parent, 1);
	if (isOccupied(newPosition)) {
	    child.die("Occupied position");
	}
	if (child.getData().getGeneration() != parent.getData().getGeneration() + 1) {
	    throw new IllegalArgumentException("Child robot is not a direct descentant of this parent");
	}
	addCommon(child, newPosition);
    }

    /**
     * Adds the first robot of a team
     * 
     * @param eve the first robot of the player
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
	    int x = generator.nextInt(GameSettings.BOARD_SIZE);
	    int y = generator.nextInt(GameSettings.BOARD_SIZE);
	    newPosition = new Point(x, y);
	} while (isOccupied(newPosition));

	addCommon(eve, newPosition);
    }

    private void addCommon(Robot newRobot, Point newPosition) {
	if (robotsPosition.containsKey(newRobot)) {
	    // robot already exists
	    throw new IllegalArgumentException("Trying to add an existing robot");
	}

	if (newRobot.isAlive()) {
	    robotsPosition.put(newRobot, newPosition);
	    clock.addListener(newRobot.getSerialNumber());
	    log.trace("[addFirst] Added robot {}", newRobot);
	} else {
	    log.debug("[add] Trying to add dead robot");

	}
    }

    /**
     * @param robot the robot to remove from the board
     */
    public void remove(Robot robot) {
	if (!robotsPosition.containsKey(robot)) {
	    throw new IllegalArgumentException("Robot doesn't exist");
	}

	robotsPosition.remove(robot);
	clock.removeListener(robot.getSerialNumber());

	if (robotsPosition.size() > 0) {
	    int someTeamId = robotsPosition.keySet().iterator().next().getData().getTeamId();
	    if (checkWinner(someTeamId)) {
		declareWinner(someTeamId);
	    }
	} else {
	    //no winner
	    declareDraw();
	}
    }

    private void declareDraw() {
	log.info("[declareDraw] No robots left. The game is a draw :S");
	clock.stop();
    }


    private void declareWinner(int someTeamId) {
	log.info("[declareWinner] Found winner! Team ID = {}", someTeamId);
	clock.stop();
    }

    /**
     * 
     * @return true if there is a winner
     */
    private boolean checkWinner(int someTeamId) {
	return getBotsCount(someTeamId, true) == 0;

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
	    robotsPosition.forcePut(robot, newPosition);
	}

    }

    private boolean isOccupied(Point newPosition) {
	return robotsPosition.containsValue(newPosition);
    }

    private Point getReferenceField(Robot robot, int dist) {
	assert dist > 0;
	assert robotsPosition.containsKey(robot);
	int size = GameSettings.BOARD_SIZE;

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
	return robotsPosition.inverse().get(neighbourPos);

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
	Robot inPosition = robotsPosition.inverse().get(space);
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
     * Checks if the teamId specified is available. if it is, it reserves it
     * 
     * @param teamId team id to query
     * @return true if the value is unique
     */
    public boolean validateTeamId(int teamId) {
	if (teamIds.contains(teamId)) {
	    return false;
	} else {
	    teamIds.add(teamId);
	    return true;
	}
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



}
