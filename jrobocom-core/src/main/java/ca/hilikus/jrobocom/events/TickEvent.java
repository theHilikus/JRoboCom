package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.timing.api.Clock;

/**
 * Event to mark a clock pulse
 *
 * @author hilikus
 */
public class TickEvent extends EventObject {

    private static final long serialVersionUID = -3594255939076973376L;
    private long cycles;
    
    /**
     * @param source event origin
     * @param pCycles tick count
     */
    public TickEvent(Clock source, long pCycles) {
	super(source);
	cycles = pCycles;
    }

    /**
     * @return the cycles
     */
    public long getCycles() {
        return cycles;
    }
    
    

}
