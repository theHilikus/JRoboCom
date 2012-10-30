package ca.hilikus.jrobocom.player;

import ca.hilikus.jrobocom.Robot.RobotControl;

/**
 * The class that each player needs to extend to control their robots
 * 
 */
public abstract class Bank {
    /**
     * the interface to control the robot to execute external instructions
     */
    protected RobotControl control;
    
    private String name;
    
    /**
     * read-only settings
     */
    protected ReadableSettings settings;

    /**
     * the method that will be executed once the bank becomes active. This is where the player's
     * code is defined
     */
    public abstract void run();

    /**
     * @return the relative cost of a bank's logic
     */
    final public int getCost() {
	// TODO Auto-generated method stub
	return 0;
    }

    /**
     * User-friendly name. Used for debugging only
     * 
     * @param bankName
     */
    final protected void setName(String bankName) {
	name = bankName;
    }

    /**
     * @return the player-assigned name for the bank
     */
    final public String getName() {
	return name;
    }

    /**
     * @return true if the bank doesn't run any external instructions
     */
    final public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
    }

}
