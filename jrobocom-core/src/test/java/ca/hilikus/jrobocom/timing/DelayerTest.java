package ca.hilikus.jrobocom.timing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.Thread.State;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;

/**
 * Tests for {@link Delayer}
 * 
 * @author hilikus
 * 
 */
@Test
public class DelayerTest extends AbstractTest {
    /**
     * 
     */
    public DelayerTest() {
	super(Delayer.class);
    }

    private Delayer TU;

    /**
     * Configures each test
     * 
     */
    @BeforeMethod
    public void setUpTU() {
	TU = new Delayer();

    }

    /**
     * Tests the normal behaviour of the delayer
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test(timeOut = 100)
    public void blockMe() throws InterruptedException, ExecutionException {

	final int BLOCKED_ID = 311;
	
	Callable<Boolean> blocked = new Callable<Boolean>() {

	    @Override
	    public Boolean call() throws Exception {
		try {
		    TU.waitFor(BLOCKED_ID, 2);
		} catch (Exception exc){
		    return false;
		}

		return true;
	    }

	};

	TU.addListener(BLOCKED_ID);
	FutureTask<Boolean> blockingTask = new FutureTask<>(blocked);
	Thread thread = new Thread(blockingTask, "Fake blocking task");
	thread.start();

	while (!thread.isAlive() || thread.getState() != State.WAITING);
	// assertEquals(State.BLOCKED, thread.getState(), "Blocked");
	TU.tick();
	assertEquals(State.WAITING, thread.getState(), "Still Blocked");
	TU.tick();
	assertTrue(blockingTask.get(), "Blocking thread finished correctly");
    }
    
    /**
     * Multiple users at the same time
     * 
     * @throws Throwable
     */
    @Test(dependsOnMethods = { "ca.hilikus.jrobocom.timing.DelayerTest.blockMe" })
    public void testMultipleWaits() throws Throwable {
	TU.addListener(332);

	Callable<Boolean> second = new Callable<Boolean>() {

	    @Override
	    public Boolean call() throws Exception {
		TU.addListener(333);
		TU.waitFor(333, 4);

		return true;
	    }

	};

	FutureTask<Boolean> secondTask = new FutureTask<>(second);
	new Thread(secondTask, "Fake second robot").start();

	TU.waitFor(332, 2);

	assertFalse(secondTask.isDone(), "Second robot is still waiting");
	boolean finished = false;
	try {
	    finished = secondTask.get();
	} catch (ExecutionException exc) {
	    throw exc.getCause();
	}
	assertTrue(finished, "Second thread finished");
    }
    
    /**
     * Test the same robot trying to wait twice (not allowed in single thread mode)
     * 
     * @throws Throwable
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMutipleWaitsSameRobot() throws Throwable {

	TU.addListener(332);

	Callable<Boolean> second = new Callable<Boolean>() {

	    @Override
	    public Boolean call() throws Exception {
		TU.waitFor(332, 4);
		return true;
	    }
	};

	FutureTask<Boolean> secondTask = new FutureTask<>(second);
	new Thread(secondTask, "Fake second robot").start();

	TU.waitFor(332, 2);
	try {
	    secondTask.get();
	} catch (ExecutionException exc) {
	    throw exc.getCause();
	}
	fail("Should throw exception");
    }
    
    /**
     * Tests signalling a robot that's not registered
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNotRegisteredSignal() {
	TU.waitFor(123, 3);
    }
}
