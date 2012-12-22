package ca.hilikus.jrobocom.timing;

import java.util.concurrent.Semaphore;

/**
 * A waiting entity
 * 
 * @author hilikus
 */
final class BlockedEntry {

    private long timeout;

    Semaphore sync = new Semaphore(0);

    /**
     * Constructs a waiting entity
     * 
     * @param pTimeout the cycle where the entity unblocks
     */
    public BlockedEntry(long pTimeout) {
	timeout = pTimeout;
    }

    /**
     * @return the synchronization element
     */
    public Semaphore getSync() {
	return sync;
    }

    /**
     * @return the cycle where the entity unblocks
     */
    public long getTimeout() {
	return timeout;
    }
}