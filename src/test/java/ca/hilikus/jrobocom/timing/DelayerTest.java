package ca.hilikus.jrobocom.timing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.Thread.State;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ch.qos.logback.classic.Level;

/**
 * Tests for {@link Delayer}
 * 
 * @author hilikus
 * 
 */
@Test
public class DelayerTest {
    private Delayer TU;

    private Logger log = LoggerFactory.getLogger(MasterClock.class);

    /**
     * Configures TU log level
     */
    @BeforeClass
    public void setUpOnce() {
	ch.qos.logback.classic.Logger TULog = (ch.qos.logback.classic.Logger) LoggerFactory
		.getLogger(MasterClock.class);
	TULog.setLevel(Level.TRACE);
    }

    /**
     * Configures each test
     * 
     * @param met the upcoming test
     */
    @BeforeMethod
    public void setUp(Method met) {
	log.info("\n====== Starting " + met.getName() + " ======");
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

	Callable<Boolean> blocked = new Callable<Boolean>() {

	    @Override
	    public Boolean call() throws Exception {
		TU.blockMe(2);

		return true;
	    }

	};

	FutureTask<Boolean> blockingTask = new FutureTask<>(blocked);
	Thread thread = new Thread(blockingTask, "Fake blocking task");
	thread.start();

	while (State.BLOCKED != thread.getState());
	// assertEquals(State.BLOCKED, thread.getState(), "Blocked");
	TU.tick();
	assertEquals(State.BLOCKED, thread.getState(), "Still Blocked");
	TU.tick();
	assertTrue(blockingTask.get(), "Blocking thread finished correctly");
    }
}
