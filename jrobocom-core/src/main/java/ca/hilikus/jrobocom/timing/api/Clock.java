package ca.hilikus.jrobocom.timing.api;

/**
 * A central unit to control turns and time
 * 
 * @author hilikus
 */
public interface Clock {

    /**
     * A single clock tick
     */
    public void step();

    /**
     * Stops the clock
     */
    public void stop();

    /**
     * Stops and cleans the clock
     */
    public void clean();

    /**
     * Changes the ticking period.
     * 
     * @param pPeriod new period in ms.
     */
    public void setPeriod(int pPeriod);
    
    /**
     * @return the period configured
     * @see #isRunning()
     */
    public int getPeriod();

    /**
     * @return the number of total cycles since the clock started running
     */
    public long getCycles();

    /**
     * @return true if the timer is currently running
     */
    public boolean isRunning();

    /**
     * Starts the clock and resets the cycles to 0 if specified
     * 
     * @param reset true if cycles counter you be reset to 0 (e.g. in new sessions)
     */
    public void start(boolean reset);

    /**
     * Starts the clock and resets the cycles to 0
     */
    public void start();

    

}