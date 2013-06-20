package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.timing.Clock;

public class TickEvent extends EventObject {

    private long cycles;
    
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
