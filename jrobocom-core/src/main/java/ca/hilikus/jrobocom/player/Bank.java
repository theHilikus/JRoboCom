package ca.hilikus.jrobocom.player;

import ca.hilikus.jrobocom.WorldInfo;
import ca.hilikus.jrobocom.robot.api.RobotAction;
import ca.hilikus.jrobocom.robot.api.RobotStatus;
import ca.hilikus.jrobocom.security.GamePermission;

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

    /**
     * the id of the author of the bank
     */
    private final int teamId;

    /**
     * the method that will be executed once the bank becomes active. This is where the player's
     * code is defined
     */
    public abstract void run();

    /**
     * Constructs a bank
     * 
     * @param pTeamId the team ID must be a parameter in the players' banks. This number comes from
     *            the game and should <u>not</u> be hardcoded by the player. In other words, it
     *            should be left as a parameter in the constructor of the player's bank
     */
    public Bank(int pTeamId) {
	teamId = pTeamId;
    }

    /**
     * @return the relative cost of a bank's logic
     */
    final public int getCost() {
	// TODO implement
	return 1;
    }

    /**
     * Override to set a bank's name
     * 
     * @return the player-assigned name for the bank
     */
    public String getName() {
	return "Unnamed Bank";
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
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("connectBank"));
	}
	control = newControl;
	info = newStatus;
	world = newWorld;

    }

    /**
     * @return the id of the player that created the bank
     */
    final public int getTeamId() {
	return teamId;
    }

}
