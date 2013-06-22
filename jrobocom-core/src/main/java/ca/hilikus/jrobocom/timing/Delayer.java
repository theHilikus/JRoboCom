package ca.hilikus.jrobocom.timing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that blocks threads for a specified number of cycles. A separate thread needs to make the
 * delayer tick
 * 
 * @author hilikus
 * @see MasterClock
 */
public class Delayer {

    private volatile long cycles;

    private List<BlockedEntry> blockedCollection = new CopyOnWriteArrayList<>();

    private Logger log = LoggerFactory.getLogger(Delayer.class);

    private List<Integer> registered = new ArrayList<>();

    private List<Integer> waitingList = new ArrayList<>();

    /**
     * Blocks the calling thread until enough turns have elapsed
     * 
     * @param cyclesToBlock number of ticks to block
     */
    private void blockMe(int cyclesToBlock) {
	long unblock = cycles + cyclesToBlock;

	BlockedEntry newEntry = new BlockedEntry(unblock);
	blockedCollection.add(newEntry);

	while (unblock > cycles) {
	    try {
		newEntry.getSync().acquire(); // blocks
	    } catch (InterruptedException exc) {
		log.error("[temporaryBlock] Spurious wakeup", exc);
	    }
	}

    }

    /**
     * Signals one cycles has elapsed
     */
    public void tick() {
	log.trace("Tick {}", cycles);

	cycles++;

	// check for robots up for unblocking
	for (BlockedEntry entry : blockedCollection) {
	    if (entry.getTimeout() >= cycles) {
		entry.getSync().release();
	    }
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
     * @param clientId unique ID of the client.
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

	log.trace("[waitFor] Blocking {} for {} turns", clientId, turns);
	blockMe(turns);
	log.trace("[waitFor] Unblocked {}", clientId);

	synchronized (waitingList) {
	    waitingList.remove(clientId);
	}

    }

    /**
     * cleans all the registered listeners and waiting clients
     */
    public void clean() {
	if (registered != null) {
	    registered.clear();
	}
	if (waitingList != null) {
	    waitingList.clear();
	}
    }

}
