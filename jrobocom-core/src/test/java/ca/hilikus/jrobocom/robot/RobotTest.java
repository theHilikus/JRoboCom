package ca.hilikus.jrobocom.robot;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;
import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.events.RobotChangedEvent;
import ca.hilikus.jrobocom.exceptions.BankInterruptedException;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;
import ca.hilikus.jrobocom.robot.Robot.RobotListener;
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
     * Used to test changing a bank
     * 
     */
    public static final class ChangerBank extends Bank {
	/**
	 * controls the clock's ticking
	 */
	public boolean ticking = true;

	/**
	 * @param pTeamId
	 */
	public ChangerBank(int pTeamId) {
	    super(pTeamId);
	}

	@Override
	public void run() throws BankInterruptedException {
	    control.transfer(1, 0);
	    control.turn(true);
	    ticking = false;
	}
    }

    /**
     * Bank to kill a robot
     * 
     * @author hilikus
     */
    public static class Killer extends Bank {

	/**
	 * @param pTeamId
	 */
	public Killer(int pTeamId) {
	    super(pTeamId);
	}

	@Override
	public void run() throws BankInterruptedException {
	    control.turn(true);
	    control.die("normal death");

	}

    }

    /**
     * Main constructor
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

	assertFalse(TU.isAlive(), "Robot didn't die");
	verify(mockWorld).remove(TU);
	verify(pla).clean(); // make sure the player was cleaned since it was its last robot
    }

    /**
     * Tests scanning when the robot doesn't have a valid instruction set for it
     */
    @Test
    public void testScan() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, new MasterClock(), dummyBanks, "Test Robot", mockPlayer);

	when(mockWorld.add(eq(TU), any(Robot.class))).thenReturn(true);
	TU.createRobot("Unit-test", InstructionSet.BASIC, 1, false);
	Robot child = CreateRobotTests.getChild(mockWorld, TU);

	assertNull(child.scan(3), "Should have been null");
	assertFalse(child.isAlive(), "Should have died due to scan");
    }

    /**
     * Tests whether overriding a running bank creates an interruption and starts on the same bank
     * 
     * @throws InterruptedException
     */
    @Test
    public void testModifyRunningBank() throws InterruptedException {
	Bank initial = new Bank(123) {

	    @Override
	    public void run() throws BankInterruptedException {
		control.scan(3);
		control.scan(3);
		control.scan(3);
		fail("Should have changed bank");
	    }
	};

	World mockWorld = mock(World.class);
	when(mockWorld.scan(any(Robot.class), anyInt())).thenReturn(new ScanResult(Found.EMPTY, 1));

	Player mockPlayer = mock(Player.class);
	MasterClock clock = new MasterClock();
	Robot TU = new Robot(mockWorld, clock, new Bank[] { initial }, "Unit test robot", mockPlayer);
	clock.addListener(TU.getSerialNumber());
	Direction original = TU.getData().getFacing();
	TU.getData().setActiveState(1);

	RobotListener TUListener = mock(RobotListener.class);
	TU.getEventHandler().addListener(TUListener);
	ChangerBank otherBank = new ChangerBank(311);

	Player mockPlayer2 = mock(Player.class);
	Robot other = new Robot(mockWorld, clock, new Bank[] { otherBank, new Killer(311) }, "Helping robot",
		mockPlayer2);
	clock.addListener(other.getSerialNumber());
	other.getData().setActiveState(1);

	when(mockWorld.getNeighbour(other)).thenReturn(TU).thenReturn(null);

	Thread firstThread = new Thread(TU);
	firstThread.start();
	Thread secondThread = new Thread(other);
	secondThread.start();

	while (otherBank.ticking) {
	    clock.step();
	}
	firstThread.join();
	assertNotEquals(original, TU.getData().getFacing(), "Overwriten bank didn't execute");
	verify(TUListener).update(any(RobotChangedEvent.class));
    }
}
