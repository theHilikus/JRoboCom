package com.github.thehilikus.jrobocom.timing;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.thehilikus.jrobocom.AbstractTest;
import com.github.thehilikus.jrobocom.timing.MasterClock;

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
	TU = new MasterClock(mock(Delayer.class));
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
	// TODO:
    }

    /**
     * Tests cleaning a running clock
     */
    @Test
    public void testClean() {
	TU.start();
	assertTrue(TU.isRunning(), "Start failed");
	TU.clean();
	assertFalse(TU.isRunning(), "Should have stopped");
    }
}
