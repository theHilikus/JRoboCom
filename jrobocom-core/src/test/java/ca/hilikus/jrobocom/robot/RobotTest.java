package ca.hilikus.jrobocom.robot;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
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

import java.util.concurrent.Semaphore;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import ca.hilikus.events.event_manager.api.EventDispatcher;
import ca.hilikus.jrobocom.AbstractTest;
import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.exceptions.BankInterruptedException;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.timing.Delayer;

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
	
	/**
	 * @param pTeamId
	 */
	public Killer(int pTeamId) {
	    super(pTeamId);
	}

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
	assertEquals(TU.getSerialNumber(), 0, "Serial number of first robot should be 0");
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

	Bank second = mock(Bank.class); //empty bank after which the robot jumps to bank 0
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
	Robot child = CreateRobotTests.getChild(mockWorld, TU);

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
	
	final Semaphore robotToTest = new Semaphore(0); //the robot controls flow
	Bank initial = new Bank(123) {

	    @Override
	    public void run() throws BankInterruptedException {
		robotToTest.release();
		control.scan(3);
		control.scan(3);
		control.scan(3);
		fail("Should have changed bank");
	    }
	};

	World mockWorld = mock(World.class);
	Player mockPlayer = mock(Player.class);
	
	final Semaphore testToRobot = new Semaphore(0); //the test controls flow
	Delayer delayer = mock(Delayer.class);
	doAnswer(new Answer<Void>() {
	    @Override
	    public Void answer(InvocationOnMock invocation) throws Throwable {
		testToRobot.acquire(); //block until test decides
		return null;
	    }}).when(delayer).waitFor(anyInt(), anyInt());
	
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { initial, initial }, "Unit test robot", mockPlayer);
	TU.setEventDispatcher(mock(EventDispatcher.class));
	Direction original = TU.getData().getFacing();
	TU.getData().setActiveState(1);
	
	Thread firstThread = new Thread(TU);
	firstThread.start();
	
	
	testToRobot.release();
	
	ChangerBank otherBank = new ChangerBank(311);
	
	robotToTest.acquire();
	TU.setBank(otherBank, 0, true);
	
	
	testToRobot.release();
	testToRobot.release();
	testToRobot.release();
	
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
	ChangerBank bank = new ChangerBank(311);
	Robot TU = new Robot(mockWorld, delayer, new Bank[] { bank, null }, "Test Robot", mockPlayer);
	TU.setEventDispatcher(mock(EventDispatcher.class));

	//clock.addListener(TU.getSerialNumber());
	TU.getData().setActiveState(1);

	Thread thread = new Thread(TU);
	thread.start();
	
	while (bank.ticking) {
	    delayer.tick();
	}

	thread.join();
    }
}
