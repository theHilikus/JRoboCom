package ca.hilikus.jrobocom.robot;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.robot.api.RobotStatusLocal;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * Tests the robots, but not the control class
 * 
 * @author hilikus
 * 
 */
@Test
public class RobotTest extends AbstractTest {

    /**
     * 
     */
    public RobotTest() {
	super(Robot.class);
    }


    /**
     * Tests creation of subsequent robots
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" }, groups = "init")
    public void testOtherRobotsCreation() {

	World mockWorld = mock(World.class);
	when(mockWorld.validateTeamId(anyInt())).thenReturn(true);
	Bank[] dummyBanks = new Bank[3];
	Robot eve = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot");

	int banks = 3;

	InstructionSet set = InstructionSet.ADVANCED;
	Robot TU = new Robot(set, banks, false, eve, "Test Robot");
	assertFalse(TU.getData().isMobile());
	assertEquals(TU.getBanksCount(), banks);
	assertEquals(TU.getData().getAge(), 0);
	assertEquals(TU.getData().getInstructionSet(), set);
	assertEquals(TU.getSerialNumber(), eve.getSerialNumber() + 1, "Serial number increases every time");
	assertEquals(TU.getData().getGeneration(), eve.getData().getGeneration() + 1);
	assertTrue(TU.isAlive());

	Robot TU2 = new Robot(set, 8, true, eve, "Test Robot");
	assertEquals(TU2.getData().getGeneration(), eve.getData().getGeneration() + 1);
	assertEquals(TU2.getSerialNumber(), TU.getSerialNumber() + 1);

    }

    /**
     * Tests creation of first robot
     */
    @Test(groups = "init")
    public void testFirstRobotCreation() {
	World mockWorld = mock(World.class);
	when(mockWorld.validateTeamId(anyInt())).thenReturn(false, true);

	final int BANK_COUNT = 3;
	Bank[] dummyBanks = new Bank[BANK_COUNT];
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot");

	// check generation and serial #?
	verify(mockWorld, atLeast(2)).validateTeamId(anyInt());

	assertFalse(TU.getData().isMobile(), "First robot should not be mobile");
	assertEquals(TU.getBanksCount(), BANK_COUNT, "First robot has all banks provided");
	assertEquals(TU.getData().getAge(), 0, "Age at construction is 0");
	assertEquals(TU.getData().getInstructionSet(), InstructionSet.SUPER,
		"First robot should have all instruction sets");
	assertEquals(TU.getSerialNumber(), 0, "Serial number of first robot should be 0");
	assertTrue(TU.isAlive(), "New robot is alive");
    }

    /**
     * Tests an infertile robot trying to create
     */
    @Test(expectedExceptions = IllegalArgumentException.class, dependsOnGroups = { "init.*" })
    public void testInvalidCreation() {
	Robot mockParent = mock(Robot.class);
	RobotStatusLocal mockStatus = mock(RobotStatusLocal.class);
	when(mockStatus.getInstructionSet()).thenReturn(InstructionSet.ADVANCED);
	when(mockParent.getData()).thenReturn(mockStatus);

	@SuppressWarnings("unused")
	Robot TU = new Robot(InstructionSet.BASIC, 3, true, mockParent, "Test Robot");
	fail("Should not have created");
    }

    /**
     * Tests the jump to bank 0 after a bank finished execution
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testReboot() {
	World mockWorld = mock(World.class);
	when(mockWorld.validateTeamId(anyInt())).thenReturn(true);
	MasterClock mockClock = mock(MasterClock.class);

	mockClock.start(true);

	Bank[] dummyBanks = new Bank[3];
	Bank first = new Bank() {
	    int repeat = 0;

	    @Override
	    public void run() {
		if (repeat < 1) {
		    control.changeBank(2);
		    
		} else {
		    control.die();
		}
		repeat++;
	    }
	};

	first = spy(first);
	dummyBanks[0] = first;

	Bank second = mock(Bank.class);
	dummyBanks[2] = second;

	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot");
	mockClock.addListener(TU.getSerialNumber());
	TU.run();

	/*InOrder interleaved seems to not work
	InOrder banksOrder = inOrder(first, second);
	banksOrder.verify(first, atLeast(1)).run();
	banksOrder.verify(second).run();
	banksOrder.verify(first).run();
	*/
	
	verify(first, times(2)).run();
	verify(second).run();
    }

    /**
     * Tests running a robot that has a null 0 bank
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testDataHunger() {
	World mockWorld = mock(World.class);
	when(mockWorld.validateTeamId(anyInt())).thenReturn(true);

	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot");

	TU.run();

	verify(mockWorld).remove(TU);
    }

    /**
     * 
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testDie() {
	World mockWorld = mock(World.class);
	when(mockWorld.validateTeamId(anyInt())).thenReturn(true);

	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot");

	TU.die("test die");

	verify(mockWorld).remove(TU);
    }

}
