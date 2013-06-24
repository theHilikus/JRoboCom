package com.github.thehilikus.jrobocom.exceptions;

/**
 * A problem with one of the player's code
 * 
 * @author hilikus
 */
public class PlayerException extends GameException {

    private static final long serialVersionUID = 6067224319127113718L;

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * Note that the detail message associated with cause is not automatically incorporated in this
     * exception's detail message.
     * 
     * @param msg human-readable message about the error
     * @param inner the cause of the exception
     */
    public PlayerException(String msg, Exception inner) {
	super(msg, inner);
    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to Throwable.initCause(java.lang.Throwable).
     * @param msg the detail message
     */
    public PlayerException(String msg) {
	super(msg);
    }

}
