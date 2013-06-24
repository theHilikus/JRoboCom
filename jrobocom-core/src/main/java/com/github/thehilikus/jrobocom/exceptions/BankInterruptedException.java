package com.github.thehilikus.jrobocom.exceptions;

/**
 * Exception thrown when the normal execution of a bank is interrupted
 * 
 * @author hilikus
 */
public class BankInterruptedException extends RuntimeException {

    private static final long serialVersionUID = -4623374909936589899L;

    /**
     * @param msg the detail message
     * @param inner the cause of the exception
     */
    public BankInterruptedException(String msg, Exception inner) {
	super(msg, inner);
    }
    
    /**
     * @param msg the detail message
     */
    public BankInterruptedException(String msg) {
	super(msg);
    }

    /**
     * Default constructor
     */
    public BankInterruptedException() {
    }
    

}
