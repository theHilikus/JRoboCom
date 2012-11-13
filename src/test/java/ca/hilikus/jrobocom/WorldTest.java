package ca.hilikus.jrobocom;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.robot.api.RobotStatusLocal;

/**
 * Automatic tests for {@link World}
 * 
 * @author hilikus
 */
public class WorldTest extends AbstractTest {

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
	TU = new World();
    }

    private static Robot createRobotMockup(int teamId, int serialNumber) {
	Robot mockRobot = mock(Robot.class);
	RobotStatusLocal mockStatus = mock(RobotStatusLocal.class);
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
    @Test(expectedExceptions = IllegalArgumentException.class, enabled=false) //disable since can't mock equals
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

	TU.addFirst(mockRobot);
	assertEquals(TU.getBotsCount(-1, true), 1, "Successful add");
	TU.addFirst(mock2);
	assertEquals(TU.getBotsCount(-1, true), 2, "Successful second add");

    }
    
    /**
     * Add a robot that's not the first
     */
    @Test(expectedExceptions=IllegalArgumentException.class)
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
	when(mock2.isAlive()).thenReturn(true);
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
     * Checks repeated validation of team ids
     */
    @Test
    public void validateTeamId() {
	assertTrue(TU.validateTeamId(311));
	assertFalse(TU.validateTeamId(311));
	assertTrue(TU.validateTeamId(123));
    }
}
