package com.github.thehilikus.jrobocom.robot;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.thehilikus.jrobocom.AbstractTest;
import com.github.thehilikus.jrobocom.GameSettings;
import com.github.thehilikus.jrobocom.Player;
import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.robot.Robot;
import com.github.thehilikus.jrobocom.timing.Delayer;

/**
 * Tests many use-cases for {@link Robot#createRobot(String, InstructionSet, int, boolean)}
 * 
 * @author hilikus
 */
public class CreateRobotTest extends AbstractTest {

    private Robot TU;
    private World mockWorld;
    private Delayer mockDelayer;
    private Bank[] banks;
    private Player mockPlayer;

    /**
     * 
     */
    public CreateRobotTest() {
	super(Robot.class);
    }

    /**
     * Readies the testing unit
     */
    @BeforeMethod
    public void setUp() {
	mockWorld = mock(World.class);
	mockDelayer = mock(Delayer.class);
	banks = new Bank[3];
	mockPlayer = mock(Player.class);

	TU = new Robot(mockWorld, mockDelayer, banks, "Unity", mockPlayer);
	when(mockWorld.add(isA(Robot.class), isA(Robot.class))).thenReturn(true);
    }

    /**
     * Normal robot creation
     */
    @Test
    public void createNormalRobot() {
	when(mockWorld.getBotsCount(anyInt(), anyBoolean())).thenReturn(1);
	TU.createRobot("son", InstructionSet.SUPER, 1, false);

	Robot child = getChild(mockWorld, TU);

	assertTrue(child.isAlive(), "Child is not alive");
	assertEquals(child.getName(), "son", "Names don't match");
	assertEquals(child.getData().getInstructionSet(), InstructionSet.SUPER, "Wrong instruction set");
	assertEquals(child.getBanksCount(), 1, "Invalid number of banks");
	assertFalse(child.getData().isMobile(), "Robot is mobile");
	assertEquals(child.getData().getGeneration(), 1, "Invalid generation");
	assertFalse(child.getData().isEnabled(), "Robot was enabled by default");
	assertEquals(child.getOwner(), mockPlayer, "Owner was not passed to child");
	assertEquals(child.getData().getFacing(), TU.getData().getFacing(),
		"Child not facing same direction is parent");

    }

    /**
     * Extracts the child of a creation
     * 
     * @param world the mock world
     * @param parent the parent of the robot
     * @return the created robot if there was one created
     */
    public static Robot getChild(World world, Robot parent) {
	ArgumentCaptor<Robot> childCatcher = ArgumentCaptor.forClass(Robot.class);
	verify(world).add(eq(parent), childCatcher.capture());

	Robot child = childCatcher.getValue();
	assertNotNull(child, "Couldn't capture child");
	assertTrue(child.isAlive(), "Child should be alive");
	return child;
    }

    /**
     * Tests when a robot without the right instruction set tries to create a robot
     */
    @Test
    public void infertileCreate() {
	when(mockWorld.getBotsCount(anyInt(), anyBoolean())).thenReturn(1);
	TU.createRobot("son", InstructionSet.BASIC, 1, false);

	Robot child = getChild(mockWorld, TU);

	assertTrue(child.isAlive(), "Infertile robot is fine");
	child.createRobot("son^2", InstructionSet.BASIC, 1, false);
	assertFalse(child.isAlive(), "Infertile robot didn't die");
    }

    /**
     * Tests that creating a robot with too many banks throws an exception
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createWithTooManyBanks() {
	TU.createRobot("son", InstructionSet.BASIC, 0xfff, false);
	fail("Should have failed");
    }

    /**
     * Tests that trying to creating more robots than allowed doesn't actually create anything
     */
    @Test
    public void createTooManyRobots() {
	when(mockWorld.getBotsCount(anyInt(), anyBoolean())).thenReturn(0).thenReturn(
		GameSettings.getInstance().MAX_BOTS + 3);
	TU.createRobot("son", InstructionSet.BASIC, 1, false);
	verify(mockWorld, times(1)).add(eq(TU), isA(Robot.class));

	TU.createRobot("son", InstructionSet.BASIC, 1, false);
	verify(mockWorld, times(1)).add(eq(TU), isA(Robot.class)); // interactions should remain the
								   // same

    }

    /**
     * Checks creating robots with invalid number of banks
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createWithInvalidBanksCount() {
	TU.createRobot("son", InstructionSet.BASIC, -10, false);
    }
}
