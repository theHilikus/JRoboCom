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
	DRAW
    }

    /**
     * Constructs an event for a draw
     * 
     * @param source event creator
     */
    public ResultEvent(Object source) {
	super(source);
	result = Result.DRAW;
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
     * @return the player that won
     */
    public Player getWinner() {
        return player;
    }


}
