package ca.hilikus.jrobocom.timing.api;

import java.util.EventListener;

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