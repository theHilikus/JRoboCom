package ca.hilikus.jrobocom.timing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;

/**
 * tests for {@link MasterClock}
 * 
 * @author hilikus
 * @see DelayerTest
 */
@Test
public class MasterClockTest extends AbstractTest {
    // TODO: make multithreaded

    /**
     * 
     */
    public MasterClockTest() {
	super(MasterClock.class);
    }

    private MasterClock TU;

    /**
     * Initializes each test
     * 
     */
    @BeforeMethod
    public void setUpTU() {
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
     * Tests changing the period
     * 
     */
    @Test
    public void testChangePeriod() {
	//TODO:
    }
}
