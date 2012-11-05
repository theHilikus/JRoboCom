package ca.hilikus.jrobocom.timing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

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

    /**
     * Blocks the calling thread until enough turns have elapsed
     * 
     * @param cyclesToBlock number of ticks to block
     */
    public void blockMe(int cyclesToBlock) {
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

    private class BlockedEntry {
	private long timeout;

	Semaphore sync = new Semaphore(0);

	public BlockedEntry(long pTimeout) {
	    timeout = pTimeout;
	}

	public Semaphore getSync() {
	    return sync;
	}

	public long getTimeout() {
	    return timeout;
	}
    }

}
