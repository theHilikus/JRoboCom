package ca.hilikus.jrobocom.timing;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.events.event_manager.api.EventDispatcher;
import ca.hilikus.events.event_manager.api.EventPublisher;
import ca.hilikus.jrobocom.events.TickEvent;
import ca.hilikus.jrobocom.timing.api.Clock;

/**
 * Time keeper
 * 
 * @author hilikus
 * 
 */
public class MasterClock implements Clock, EventPublisher {

    private long cycles;

    private Logger log = LoggerFactory.getLogger(MasterClock.class);

    private int period = 400;

    private Timer timer;

    private Ticker currentTicker;

    private Delayer delayer = new Delayer();

    private EventDispatcher eventDispatcher;


    /**
     * Minimum clock period allowed
     */
    public static final int MIN_PERIOD = 10;

    
    @Override
    public void start(boolean reset) {
	log.debug("Starting Master Clock. Period = {}, Reset = {}", period, reset);
	if (reset) {
	    cycles = 0;
	}
	currentTicker = new Ticker();
	timer = new Timer("Master Clock Timer");
	timer.scheduleAtFixedRate(currentTicker, period, period);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.timing.Clock#step()
     */
    @Override
    public void step() {
	delayer.tick();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.timing.Clock#stop()
     */
    @Override
    public void stop() {
	log.debug("Stopping Master Clock");
	if (currentTicker != null) {
	    currentTicker.cancel();
	    currentTicker = null;
	}
	if (timer != null) {
	    timer.cancel();
	    timer = null;
	}

    }

    private class Ticker extends TimerTask {

	@Override
	public void run() {
	    cycles++;
	    log.trace("Tick {}", cycles);

	    delayer.tick();

	    
	    eventDispatcher.fireEvent(new TickEvent(MasterClock.this, cycles));
	}

    }

    @Override
    public long getCycles() {
	return cycles;
    }

    /*
     * If the value is too small, it will be overwritten by {@link #MIN_PERIOD}
     * (non-Javadoc)
     * @see ca.hilikus.jrobocom.timing.Clock#setPeriod(int)
     */
    @Override
    public void setPeriod(int pPeriod) {
	if (pPeriod < 0) {
	    throw new IllegalArgumentException("Period cannot be negative");
	}
	int actual = Math.max(pPeriod, MIN_PERIOD);
	log.debug("Changing clock period to {}", actual);
	period = actual;

	if (isRunning()) {
	    stop();
	    start(false);
	}
    }

    
    @Override
    public int getPeriod() {
	return period;
    }

    @Override
    public boolean isRunning() {
	return currentTicker != null;
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.timing.Clock#start()
     */
    @Override
    public void start() {
	start(true);

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.timing.Clock#clean()
     */
    @Override
    public void clean() {
	if (isRunning()) {
	    stop();
	}

    }

    @Override
    public void setEventDispatcher(EventDispatcher dispatcher) {
	eventDispatcher = dispatcher;
	
    }

}
