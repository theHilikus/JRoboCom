package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.Player;

/**
 * Marks the elimination of a player from the game
 * 
 * @author hilikus
 */
public class PlayerEliminatedEvent extends EventObject {
    private static final long serialVersionUID = -2850419114550927108L;

    private Player elim;

    /**
     * @param source
     * @param dead player eliminated
     */
    public PlayerEliminatedEvent(Object source, Player dead) {
	super(source);

	elim = dead;
    }

    /**
     * @return the player that got eliminated
     */
    public Player getEliminatedPlayer() {
	return elim;
    }
}
