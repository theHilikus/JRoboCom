package ca.hilikus.jrobocom.timing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ch.qos.logback.classic.Level;

/**
 * tests for {@link MasterClock}
 * 
 * @author hilikus
 * @see DelayerTest
 */
@Test
public class MasterClockTest {
    // TODO: make multithreaded

    private MasterClock TU;

    private Logger log = LoggerFactory.getLogger(MasterClock.class);

    /**
     * Changes TU debug level to trace
     */
    @BeforeClass
    public void setUpOnce() {
	ch.qos.logback.classic.Logger TULog = (ch.qos.logback.classic.Logger) LoggerFactory
		.getLogger(MasterClock.class);
	TULog.setLevel(Level.TRACE);
    }

    /**
     * Initializes each test
     * 
     * @param met test about to be called
     */
    @BeforeMethod
    public void setUp(Method met) {
	log.info("\n====== Starting " + met.getName() + " ======");
	TU = new MasterClock();
	TU.setPeriod(200);

    }

    /**
     * Cleans tests
     */
    @AfterMethod
    public void tearDown() {
	TU.stop();
    }

    /**
     * Multiple users at the same time
     * 
     * @throws Throwable
     */
    @Test(dependsOnMethods={"ca.hilikus.jrobocom.timing.DelayerTest.blockMe"})
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
	TU.start(true);

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

    /**
     * Tests starting of the clock
     */
    public void testStartStop() {
	assertEquals(0, TU.getCycles(), "Cycles start at 0");
	assertFalse(TU.isRunning());

	TU.start(true);
	assertTrue(TU.isRunning());

	TU.stop();
	assertFalse(TU.isRunning());

    }

    /**
     * Tests whether changing the period after clients are waiting is done smoothly
     * 
     * @throws ExecutionException
     * 
     * @throws InterruptedException
     */
    @Test(dependsOnMethods = { "testStartStop" })
    public void testChangePeriod() throws InterruptedException, ExecutionException {
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

	TU.start(true);
	TU.waitFor(332, 2);
	TU.setPeriod(100);

	assertTrue(secondTask.get());
	TU.stop();
	assertEquals(TU.getCycles(), 4);
    }
}
