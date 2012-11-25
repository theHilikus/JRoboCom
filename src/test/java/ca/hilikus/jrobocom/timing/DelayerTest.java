package ca.hilikus.jrobocom.timing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.Thread.State;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger log = LoggerFactory.getLogger(MasterClock.class);

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

	while (!thread.isAlive() || thread.getState() != State.WAITING);
	// assertEquals(State.BLOCKED, thread.getState(), "Blocked");
	TU.tick();
	assertEquals(State.WAITING, thread.getState(), "Still Blocked");
	TU.tick();
	assertTrue(blockingTask.get(), "Blocking thread finished correctly");
    }
}
