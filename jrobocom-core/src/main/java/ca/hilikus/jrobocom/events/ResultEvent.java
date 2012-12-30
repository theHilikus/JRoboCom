package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.Player;

/**
 * Event triggered at the end of a session
 * 
 * @author hilikus
 */
public class ResultEvent extends EventObject {

    private static final long serialVersionUID = -8358528969962936354L;
    private Result result;
    private Player player;

    /**
     * The result of a single session
     * 
     */
    public enum Result {
	/**
	 * A single winner
	 */
	WIN,
	/**
	 * No winner
	 */
	DRAW,
	/**
	 * End of a game without competition
	 */
	END
    }

    /**
     * Constructs an event for a draw
     * 
     * @param source event creator
     * @param draw true if the result was a draw. false if it was just the end of the game
     */
    public ResultEvent(Object source, boolean draw) {
	super(source);
	if (draw) {
	    result = Result.DRAW;
	} else {
	    result = Result.END;
	}
    }

    /**
     * Constructs an event for a win
     * 
     * @param source event creator
     * @param winner the winner of the game
     */
    public ResultEvent(Object source, Player winner) {
	super(source);
	result = Result.WIN;
	player = winner;
    }

    /**
     * @return the result
     */
    public Result getResult() {
	return result;
    }

    /**
     * @return the player that won. If the result was a draw, returns null
     */
    public Player getWinner() {
	return player;
    }

}
