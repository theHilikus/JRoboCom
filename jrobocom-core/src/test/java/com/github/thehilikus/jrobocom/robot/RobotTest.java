package com.github.thehilikus.jrobocom.robot;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.Semaphore;

import org.testng.annotations.Test;

import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.jrobocom.AbstractTest;
import com.github.thehilikus.jrobocom.Direction;
import com.github.thehilikus.jrobocom.Player;
import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.timing.Delayer;

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

	@Override
	public void run() throws BankInterruptedException {
	    control.transfer(1, 0);
	    control.turn(true);
	    control.die();
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
	 * controls the clock's ticking
	 */
	public boolean ticking = true;

	@Override
	public void run() throws BankInterruptedException {
	    control.turn(true);
	    ticking = false;
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
	Robot TU = new Robot(mockWorld, new Delayer(), dummyBanks, "Test Robot", pla);

	// check generation and serial #?

	assertFalse(TU.getData().isMobile(), "First robot should not be mobile");
	assertEquals(TU.getBanksCount(), BANK_COUNT, "First robot has all banks provided");
	assertEquals(TU.getData().getAge(), 0, "Age at construction is 0");
	assertEquals(TU.getData().getInstructionSet(), InstructionSet.SUPER,
		"First robot should have all instruction sets");
	assertTrue(TU.isAlive(), "New robot is alive");
    }

    /**
     * Tests the jump to bank 0 after a bank finished execution
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testReboot() {
	World mockWorld = mock(World.class);
	Delayer mockDelayer = mock(Delayer.class);

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

	Bank second = mock(Bank.class); // empty bank after which the robot jumps to bank 0
	dummyBanks[2] = second;

	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, mockDelayer, dummyBanks, "Test Robot", pla);
	TU.setEventDispatcher(mock(EventDispatcher.class));
	TU.getData().setActiveState(1);

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
	Delayer delayer = mock(Delayer.class);
	Player pla = mock(Player.class);
	Robot TU = new Robot(mockWorld, delayer, dummyBanks, "Test Robot", pla);
	TU.setEventDispatcher(mock(EventDispatcher.class));
	TU.getData().setActiveState(1);

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
	Robot TU = new Robot(mockWorld, new Delayer(), dummyBanks, "Test Robot", pla);

	TU.die("test die");

	assertFalse(TU.isAlive(), "Robot didn't die");
	verify(mockWorld).remove(TU);
    }

    /**
     * Tests scanning when the robot doesn't have a valid instruction set for it
     */
    @Test
    public void testScan() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, new Delayer(), dummyBanks, "Test Robot", mockPlayer);

	when(mockWorld.add(eq(TU), isA(Robot.class))).thenReturn(true);
	TU.createRobot("Unit-test", InstructionSet.BASIC, 1, false);
	Robot child = CreateRobotTest.getChild(mockWorld, TU);

	assertNull(child.scan(3), "Should have been null");
	assertFalse(child.isAlive(), "Should have died due to scan");
    }

    /**
     * Tests whether overriding a running bank creates an interruption and starts on the same bank
     * 
     * @throws InterruptedException
     */
    @Test(timeOut = 1000)
    public void testModifyRunningBank() throws InterruptedException {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);

	Delayer mockDelayer = mock(Delayer.class);

	final Semaphore mutex = new Semaphore(0);

	Bank initial = new Bank() {

	    @Override
	    public void run() throws BankInterruptedException {

		synchronized (mutex) {
		    mutex.release();
		    try {
			mutex.wait(); // block to allow main thread to swap bank
			control.scan(); // this is necessary so that the robot realizes it was
					// interrupted
		    } catch (InterruptedException exc) {
			System.out.println("error");
		    }
		}
		fail("Should have changed bank");
	    }
	};
	initial.setTeamId(123);

	Robot TU = new Robot(mockWorld, mockDelayer, new Bank[] { initial, initial }, "Unit test robot", mockPlayer);
	TU.setEventDispatcher(mock(EventDispatcher.class));
	TU.getData().setActiveState(1);

	Direction original = TU.getData().getFacing();

	Thread firstThread = new Thread(TU);
	firstThread.start();
	mutex.acquire(); // block until bank executes

	synchronized (mutex) {
	    // robot is now wait()ing
	    ChangerBank otherBank = new ChangerBank(); // this bank changes the direction of the
						       // robot
	    otherBank.setTeamId(311);
	    TU.setBank(otherBank, 0, true); // change the running bank
	    mutex.notifyAll();
	}
	firstThread.join();

	assertNotEquals(original, TU.getData().getFacing(), "Overwriten bank didn't execute");

    }

    /**
     * Tests whether a robot's thread ends when a robot dies
     * 
     * @throws InterruptedException
     */
    @Test(timeOut = 1000)
    public void testRobotClean() throws InterruptedException {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	ChangerBank bank = new ChangerBank();
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Test Robot", mockPlayer);
	TU.setEventDispatcher(mock(EventDispatcher.class));

	// clock.addListener(TU.getSerialNumber());
	TU.getData().setActiveState(1);

	Thread thread = new Thread(TU);
	thread.start();

	while (bank.ticking) {
	    delayer.tick();
	}

	thread.join();
    }

    /**
     * Tests transferring a normal bank
     */
    @Test
    public void testTransfer() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	ChangerBank bank = new ChangerBank();
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Test Robot", mockPlayer);

	Robot target = new Robot(mockWorld, delayer, new Bank[] { null, null }, "Robot target", mockPlayer);

	when(mockWorld.getNeighbour(TU)).thenReturn(target);

	TU.transfer(0, 1);

	assertEquals(target.getBank(1).getClass(), bank.getClass(), "Target bank not transferred");
    }

    /**
     * Tests whether transferring a null bank succeeds
     */
    @Test(dependsOnMethods = { "testTransfer" })
    public void testNullTransfer() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	ChangerBank bank = new ChangerBank();
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Test Robot", mockPlayer);

	Robot target = new Robot(mockWorld, delayer, new Bank[] { null, bank }, "Robot target", mockPlayer);

	when(mockWorld.getNeighbour(TU)).thenReturn(target);

	TU.transfer(1, 1);

	assertNull(target.getBank(1), "Target bank not transferred");
    }

    /**
     * Tests reverse transferring a normal bank
     */
    @Test
    public void testReverseTransfer() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	ChangerBank bank = new ChangerBank();
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { null, null }, "Test Robot", mockPlayer);

	Robot target = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Robot target", mockPlayer);

	when(mockWorld.getNeighbour(TU)).thenReturn(target);

	TU.reverseTransfer(0, 1);

	assertEquals(TU.getBank(1).getClass(), bank.getClass(), "Target bank not transferred");
    }

    /**
     * Tests whether reverse transferring a null bank succeeds
     */
    @Test
    public void testReverseNullTransfer() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	ChangerBank bank = new ChangerBank();
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { null, bank }, "Test Robot", mockPlayer);

	Robot target = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Robot target", mockPlayer);

	when(mockWorld.getNeighbour(TU)).thenReturn(target);

	TU.reverseTransfer(1, 1);

	assertNull(TU.getBank(1), "Target bank not transferred");
    }

    /**
     * Tests setting a normal bank
     */
    @Test
    public void testSetBank() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { null, null }, "Test Robot", mockPlayer);

	Bank testBank = mock(Bank.class);
	TU.setBank(testBank, 0, false);

	assertSame(TU.getBank(0), testBank, "Bank set was the same");
    }

    /**
     * Tests setting a null bank
     */
    @Test
    public void testSetNullBank() {
	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	Delayer delayer = mock(Delayer.class);
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { new ChangerBank(), new ChangerBank() }, "Test Robot",
		mockPlayer);

	TU.setBank(null, 0, false);

	assertNull(TU.getBank(0), "Bank set was the same");
    }
}
