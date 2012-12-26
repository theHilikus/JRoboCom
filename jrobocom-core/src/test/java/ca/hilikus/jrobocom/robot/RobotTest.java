package ca.hilikus.jrobocom.robot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;
import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
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
     * Tests creation of first robot
     */
    @Test(groups = "init")
    public void testFirstRobotCreation() {
	World mockWorld = mock(World.class);

	final int BANK_COUNT = 3;
	Bank[] dummyBanks = new Bank[BANK_COUNT];
	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot", pla);

	// check generation and serial #?

	assertFalse(TU.getData().isMobile(), "First robot should not be mobile");
	assertEquals(TU.getBanksCount(), BANK_COUNT, "First robot has all banks provided");
	assertEquals(TU.getData().getAge(), 0, "Age at construction is 0");
	assertEquals(TU.getData().getInstructionSet(), InstructionSet.SUPER,
		"First robot should have all instruction sets");
	assertEquals(TU.getSerialNumber(), 0, "Serial number of first robot should be 0");
	assertTrue(TU.isAlive(), "New robot is alive");
    }

    /**
     * Tests the jump to bank 0 after a bank finished execution
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testReboot() {
	World mockWorld = mock(World.class);
	MasterClock mockClock = mock(MasterClock.class);

	mockClock.start(true);

	Bank[] dummyBanks = new Bank[3];
	Bank first = new Bank(311) {
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

	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, mockClock, dummyBanks, "Test Robot", pla);
	TU.getData().setActiveState(1);
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

	Bank[] dummyBanks = new Bank[3];
	MasterClock clock = new MasterClock();
	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, clock, dummyBanks, "Test Robot", pla);
	TU.getData().setActiveState(1);
		
	clock.addListener(TU.getSerialNumber());
	clock.start();

	TU.run();

	verify(mockWorld).remove(TU);
    }

    /**
     * 
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testDie() {
	World mockWorld = mock(World.class);

	Bank[] dummyBanks = new Bank[3];
	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot", pla);

	TU.die("test die");

	verify(mockWorld).remove(TU);
	verify(pla).clean(); // make sure the player was cleaned since it was its last robot
    }
}
