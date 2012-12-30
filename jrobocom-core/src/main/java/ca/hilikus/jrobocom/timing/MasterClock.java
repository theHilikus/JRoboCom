package ca.hilikus.jrobocom.timing;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.security.GamePermission;

/**
 * A central unit to control turns and time
 * 
 * @author hilikus
 * 
 */
public class MasterClock {

    private long cycles;

    private Logger log = LoggerFactory.getLogger(MasterClock.class);

    private int period = 400;

    private Timer timer;

    private Ticker currentTicker;

    private List<Integer> registered = new ArrayList<>();

    private List<Integer> waitingList = new ArrayList<>();

    private Delayer delayer = new Delayer();

    private List<ClockListener> listeners = new ArrayList<>();

    /**
     * Callback interface
     * 
     */
    public interface ClockListener extends EventListener {
	/**
	 * Method that gets called every time the clock ticks
	 * 
	 * @param cycles number of elapsed cycles
	 */
	public void tick(long cycles);
    }

    /**
     * Minimum clock period allowed
     */
    public static final int MIN_PERIOD = 10;

    /**
     * Starts the clock and resets the cycles to 0 if specified
     * 
     * @param reset true if cycles counter you be reset to 0 (e.g. in new sessions)
     */
    public void start(boolean reset) {
	log.debug("Starting Master Clock. Period = {}, Reset = {}", period, reset);
	if (reset) {
	    cycles = 0;
	}
	currentTicker = new Ticker();
	timer = new Timer("Master Clock Timer");
	timer.scheduleAtFixedRate(currentTicker, period, period);
    }

    /**
     * A single clock tick
     */
    public void step() {
	delayer.tick();
    }

    /**
     * Stops the clock
     */
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

    /**
     * Registers an object interested in notifications
     * 
     * @param listenerId a unique ID of the listener
     */
    public void addListener(int listenerId) {
	registered.add(listenerId);

    }

    /**
     * Removes an interested object
     * 
     * @param listenerId a unique ID of the listener
     */
    public void removeListener(int listenerId) {
	registered.remove(Integer.valueOf(listenerId));
    }

    /**
     * Blocks the calling thread for the specified number of cycles
     * 
     * @param clientId unique ID of the client. Must have registered with the clock using
     *            {@link #addListener(int)}
     * @param turns the number of clock ticks to block
     */
    public void waitFor(Integer clientId, int turns) {
	// for safety, check if we know the robot, otherwise fail
	if (!registered.contains(clientId)) {
	    throw new IllegalArgumentException("Unknown robot. All robots must first register with clock");
	}

	synchronized (waitingList) {

	    if (waitingList.contains(clientId)) {
		throw new IllegalArgumentException("Client " + clientId
			+ " is already waiting, no multithreading is allowed");
	    }

	    waitingList.add(clientId);
	}

	// we are in the robot's thread

	log.trace("[signalAfter] Blocking {} for {} turns", clientId, turns);
	delayer.blockMe(turns);
	log.trace("[signalAfter] Unblocked {}", clientId);

	synchronized (waitingList) {
	    waitingList.remove(clientId);
	}

    }

    private class Ticker extends TimerTask {

	@Override
	public void run() {
	    cycles++;
	    log.trace("Tick {}", cycles);

	    delayer.tick();

	    notifyListeners(cycles);
	}

    }

    /**
     * @return the number of total cycles since the clock started running
     */
    public long getCycles() {
	return cycles;
    }

    /**
     * Changes the ticking period. If the value is too small, it will be overwritten by
     * {@link #MIN_PERIOD}
     * 
     * @param pPeriod new period in ms.
     */
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

    /**
     * @return the period configured
     * @see #isRunning()
     */
    public int getPeriod() {
	return period;
    }

    /**
     * @return true if the timer is currently running
     */
    public boolean isRunning() {
	return currentTicker != null;
    }

    /**
     * Starts the clock and resets the cycles to 0
     */
    public void start() {
	start(true);

    }

    private void notifyListeners(long pCycles) {
	for (ClockListener listener : listeners) {
	    listener.tick(pCycles);
	}

    }

    /**
     * Adds a listener interesting in being notified
     * 
     * @param listener the interested object
     */
    public void addListener(ClockListener listener) {
	if (listener == null) {
	    throw new IllegalArgumentException("Listener can't be null");
	}
	checkPermission();
	if (listeners.contains(listener)) {
	    log.warn("[addListener] Listener is already registered");
	} else {
	    listeners.add(listener);
	}
    }

    /**
     * Removes a listener from the list
     * 
     * @param listener the object to stop notifying
     */
    public void removeListener(ClockListener listener) {
	if (listener == null) {
	    throw new IllegalArgumentException("Listener can't be null");
	}
	checkPermission();
	if (!listeners.contains(listener)) {
	    log.warn("[addListener] Unknown listener");
	} else {
	    listeners.remove(listener);
	}
    }

    private static void checkPermission() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("eventsListener"));
	}

    }

    /**
     * Stops and cleans the clock
     */
    public void clean() {
	if (isRunning()) {
	    stop();
	}
	if (listeners != null) {
	    listeners.clear();
	}
	if (registered != null) {
	    registered.clear();
	}
	if (waitingList != null) {
	    waitingList.clear();
	}

    }
}
