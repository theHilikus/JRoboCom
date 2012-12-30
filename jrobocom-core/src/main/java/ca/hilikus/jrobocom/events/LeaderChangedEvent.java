package ca.hilikus.jrobocom.events;

import java.util.EventObject;
import java.util.List;

import ca.hilikus.jrobocom.Player;

/**
 * Event generated when the player currently in the lead changed
 * 
 * @author hilikus
 */
public class LeaderChangedEvent extends EventObject {

    private static final long serialVersionUID = 4880896479958718623L;
    private List<Player> leaders;

    /**
     * Constructs a change of leader event
     * 
     * @param source who triggered the event
     * @param pLeaders new list of leaders
     */
    public LeaderChangedEvent(Object source, List<Player> pLeaders) {
	super(source);
	leaders = pLeaders;
    }

    /**
     * @return the leaders
     */
    public List<Player> getLeaders() {
	return leaders;
    }

}
