package ca.hilikus.jrobocom.player;

import ca.hilikus.jrobocom.WorldInfo;
import ca.hilikus.jrobocom.robot.api.RobotAction;
import ca.hilikus.jrobocom.robot.api.RobotStatus;

/**
 * The class that each player needs to extend to control their robots
 * 
 */
public abstract class Bank {
    /**
     * interface to control the robot to execute external instructions. Changes as the bank changes
     * robots
     */
    protected RobotAction control;

    /**
     * interface for information about the state of the robot
     */
    protected RobotStatus info;

    /**
     * interface to query about the world's state
     */
    protected WorldInfo world;

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
	return 1;
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
	return getCost() == 0;
    }

    /**
     * Attaches a control to a bank
     * 
     * @param newControl control to attach
     * @param newStatus status provider to attach
     * @param newWorld world info provider to attach
     */
    final public void plugInterfaces(RobotAction newControl, RobotStatus newStatus, WorldInfo newWorld) {
	control = newControl;
	info = newStatus;
	world = newWorld;

    }

    private void verify() {
	// TODO: implement
    }

}
