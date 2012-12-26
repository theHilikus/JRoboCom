package ca.hilikus.jrobocom;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.awt.Point;
import java.util.Random;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.World.WorldListener;
import ca.hilikus.jrobocom.events.ResultEvent;
import ca.hilikus.jrobocom.events.RobotAddedEvent;
import ca.hilikus.jrobocom.events.RobotMovedEvent;
import ca.hilikus.jrobocom.events.RobotRemovedEvent;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.robot.api.RobotStatusLocal;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * Automatic tests for {@link World}
 * 
 * @author hilikus
 */
public class WorldTest extends AbstractTest {

    /**
     * an event receiver to test methods
     * 
     * @author hilikus
     */
    public final class EventReceiver implements WorldListener {
	private RobotAddedEvent added;
	private ResultEvent result;
	private RobotMovedEvent moved;
	private RobotRemovedEvent removed;

	@Override
	public void update(ResultEvent pResult) {
	    this.result = pResult;
	}

	@Override
	public void update(RobotMovedEvent mov) {
	    moved = mov;

	}

	@Override
	public void update(RobotRemovedEvent rem) {
	    removed = rem;
	}

	@Override
	public void update(RobotAddedEvent add) {
	    added = add;
	}

	/**
	 * @return the added
	 */
	public RobotAddedEvent getAdded() {
	    return added;
	}

	/**
	 * @return the result
	 */
	public ResultEvent getResult() {
	    return result;
	}

	/**
	 * @return the moved
	 */
	public RobotMovedEvent getMoved() {
	    return moved;
	}

	/**
	 * @return the removed
	 */
	public RobotRemovedEvent getRemoved() {
	    return removed;
	}
    }

    private World TU;

    /**
     * 
     */
    public WorldTest() {
	super(World.class);
    }

    /**
     * Initializes testing unit
     */
    @BeforeMethod
    public void setUp() {
	TU = new World(new MasterClock());
	World.setRandGenerator(new Random()); // reset it in case one of the tests changed it
    }

    private static Robot createRobotMockup(int teamId, int serialNumber) {
	Robot mockRobot = mock(Robot.class);
	RobotStatusLocal mockStatus = mock(RobotStatusLocal.class);
	when(mockRobot.isAlive()).thenReturn(true);
	when(mockRobot.getData()).thenReturn(mockStatus);
	when(mockRobot.getSerialNumber()).thenReturn(serialNumber);
	when(mockStatus.getTeamId()).thenReturn(teamId);
	when(mockStatus.getFacing()).thenReturn(Direction.EAST);

	return mockRobot;
    }

    /**
     * A normal add
     */
    @Test(dependsOnMethods = { "addFirst" })
    public void add() {
	Robot mockRobot = createRobotMockup(311, 0);
	when(mockRobot.getData().getGeneration()).thenReturn(0);
	TU.addFirst(mockRobot);

	Robot mock2 = createRobotMockup(311, 1);
	when(mock2.getData().getGeneration()).thenReturn(1);
	TU.add(mockRobot, mock2);
    }

    /**
     * Checks adding a child robot with a fake parent (due to generations)
     */
    @Test(dependsOnMethods = { "addFirst" }, expectedExceptions = IllegalArgumentException.class)
    public void addFakeParent() {
	Robot mockRobot = createRobotMockup(311, 0);
	TU.addFirst(mockRobot);

	Robot mock2 = createRobotMockup(311, 1);
	TU.add(mockRobot, mock2);
	fail("Should have failed since robots had same generation");
    }

    /**
     * Checks addings two robots from different teams but same serial number
     */
    @Test(expectedExceptions = IllegalArgumentException.class, enabled = false)
    // disable since can't mock equals
    public void addSameSerial() {
	Robot mockRobot = createRobotMockup(1, 311);
	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(-1, true), 1, "Successful add");

	Robot mock2 = createRobotMockup(2, 311);
	TU.addFirst(mock2);

    }

    /**
     * Checks adding two robots from different teams
     */
    @Test
    public void addFirst() {
	assertEquals(TU.getBotsCount(-1, true), 0, "Starts with 0 robots");
	Robot mockRobot = createRobotMockup(311, 0);

	Robot mock2 = createRobotMockup(3110, 5);

	Random rand = mock(Random.class);
	World.setRandGenerator(rand);

	int x = 5;
	int y = 8;
	Point pos = new Point(x, y);

	when(rand.nextInt(anyInt())).thenReturn(x).thenReturn(y).thenCallRealMethod(); // for second
										       // robot use
										       // real
										       // method

	EventReceiver listener = new EventReceiver();
	TU.getEventHandler().addListener(listener);

	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(-1, true), 1, "Successful add");

	assertNotNull(listener.getAdded(), "Check we got message");
	assertEquals(listener.getAdded().getCoordinates(), pos, "Check position added is as expected");

	TU.addFirst(mock2);
	assertEquals(TU.getBotsCount(-1, true), 2, "Successful second add");

    }

    /**
     * Add a robot that's not the first
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addFakeFirst() {
	Robot mockRobot = createRobotMockup(311, 0);
	when(mockRobot.getData().getGeneration()).thenReturn(10);
	TU.addFirst(mockRobot);
    }

    /**
     * Checks adding the same robot twice
     */
    @Test(dependsOnMethods = { "addFirst" }, expectedExceptions = IllegalArgumentException.class)
    public void addFirstTwiceRobot() {
	Robot mockRobot = createRobotMockup(311, 0);

	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(-1, true), 1, "Successful add");
	TU.add(mockRobot, mockRobot);
	fail("Should not allow second add of same robot");
    }

    /**
     * Checks adding two different robots from the same team
     */
    @Test(dependsOnMethods = { "addFirst" }, expectedExceptions = IllegalArgumentException.class)
    public void addFirstTwiceTeam() {

	Robot mockRobot = createRobotMockup(311, 0);

	Robot mock2 = createRobotMockup(311, 5); // same team, different robot

	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(-1, true), 1, "Successful add");
	TU.addFirst(mock2);
	fail("Should not allow second add of same team");
    }

    /**
     * Checks
     */
    @Test
    public void getBotsCount() {
	assertEquals(TU.getBotsCount(-1, true), 0, "Starts with 0 robots");
	Robot mockRobot = createRobotMockup(311, 0);

	Robot mock2 = createRobotMockup(311, 5); // same team, different robot
	when(mock2.getData().getGeneration()).thenReturn(1);
	TU.addFirst(mockRobot);
	TU.add(mockRobot, mock2);

	Robot mock3 = createRobotMockup(10, 8);
	TU.addFirst(mock3);

	assertEquals(TU.getBotsCount(311, false), 2);
	assertEquals(TU.getBotsCount(311, true), 1);
	assertEquals(TU.getBotsCount(1, false), 0);
	assertEquals(TU.getBotsCount(1, true), 3);
    }

    /**
     * Checks removing an existing robot
     */
    @Test
    public void remove() {
	Robot mockRobot = createRobotMockup(311, 0);
	when(mockRobot.getOwner()).thenReturn(mock(Player.class));

	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(311, false), 1);
	TU.remove(mockRobot);
	assertEquals(TU.getBotsCount(311, false), 0);
    }

    /**
     * Checks removing a robot that was never added
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeInexistent() {
	Robot mockRobot = createRobotMockup(311, 0);
	TU.remove(mockRobot);
    }

    /**
     * Checks scanning all the fields in front of the robot
     */
    @Test
    public void scan() {
	Robot mockRobot = createRobotMockup(311, 0);
	TU.addFirst(mockRobot);

	int size = GameSettings.BOARD_SIZE;
	for (int dist = 1; dist < size - 1; dist++) {
	    ScanResult res = TU.scan(mockRobot, dist);
	    assertEquals(res.getDistance(), dist, "Distance in result");
	    assertEquals(res.getResult(), Found.EMPTY, "Result should be empty");
	}
    }

    /**
     * Scanning wraps around
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void scanTooFar() {
	Robot mockRobot = createRobotMockup(311, 0);
	TU.addFirst(mockRobot);

	@SuppressWarnings("unused")
	ScanResult res = TU.scan(mockRobot, GameSettings.BOARD_SIZE);
	fail("Should throw exception");

    }

    /**
     * Tests a normal move of one field
     */
    @Test(dependsOnMethods = { "addFirst" })
    public void moveNormal() {
	Robot mockRobot = createRobotMockup(311, 0);

	/*	RobotStatusLocal data = new RobotStatusLocalAdapter() {
		    @Override
		    public Direction getFacing() {
		        return Direction.NORTH;
		    }
		};*/

	when(mockRobot.getData().getFacing()).thenReturn(Direction.NORTH);
	when(mockRobot.getData().isMobile()).thenReturn(true);

	EventReceiver listener = new EventReceiver();

	Random rand = mock(Random.class);

	World.setRandGenerator(rand);

	int x = 5;
	int y = 8;

	when(rand.nextInt(anyInt())).thenReturn(x).thenReturn(y);

	TU.getEventHandler().addListener(listener);

	TU.addFirst(mockRobot);

	TU.move(mockRobot);

	assertEquals(listener.getMoved().getOldPosition(), new Point(x, y), "Check old position in event");
	assertEquals(listener.getMoved().getNewPosition(), new Point(x, y - 1), "Check new position in event");

    }

    /**
     * Tests moving an inmobile robot
     */
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void moveInmobile() {
	Robot mockRobot = createRobotMockup(311, 0);
	when(mockRobot.getData().isMobile()).thenReturn(false);

	TU.addFirst(mockRobot);

	TU.move(mockRobot);
	fail("Should have thrown exception");
    }

    /**
     * Checks requesting a neighbour when there isn't one
     */
    @Test
    public void getNoNeighbour() {
	Robot mockRobot = createRobotMockup(311, 0);

	TU.addFirst(mockRobot);

	assertNull(TU.getNeighbour(mockRobot), "Check no neighbour is detected");
    }

    /**
     * Tests getting an existing neighbour
     */
    @Test
    public void getNeighbour() {
	Robot mockRobot = createRobotMockup(311, 0);

	when(mockRobot.getData().getFacing()).thenReturn(Direction.EAST);

	Random rand = mock(Random.class);

	World.setRandGenerator(rand);

	int x = 5;
	int y = 8;

	when(rand.nextInt(anyInt())).thenReturn(x).thenReturn(y);

	TU.addFirst(mockRobot);

	Robot mockRobot2 = createRobotMockup(312, 1);

	when(rand.nextInt(anyInt())).thenReturn(x + 1).thenReturn(y); // next to other robot

	TU.addFirst(mockRobot2);

	assertEquals(mockRobot2, TU.getNeighbour(mockRobot), "Check neighbour is detected");
    }

}
