package com.github.thehilikus.jrobocom.exceptions;

/**
 * Parent of all JRobocom exceptions
 * 
 * @author hilikus
 */
public class GameException extends Exception {

    private static final long serialVersionUID = 2193319261467508410L;

    /**
     * Constructs an exception that wraps another exception
     * 
     * @param msg human-readable message about the error
     * @param inner the cause of the exception
     */
    public GameException(String msg, Exception inner) {
	super(msg, inner);
    }

    /**
     * @param msg the detail message
     */
    public GameException(String msg) {
	super(msg);
    }

    /**
     * Empty constructor
     */
    public GameException() {

    }

}
