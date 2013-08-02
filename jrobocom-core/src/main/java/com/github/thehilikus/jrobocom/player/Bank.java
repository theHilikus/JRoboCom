package com.github.thehilikus.jrobocom.player;

import com.github.thehilikus.jrobocom.WorldInfo;
import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.robot.api.RobotAction;
import com.github.thehilikus.jrobocom.robot.api.RobotStatus;
import com.github.thehilikus.jrobocom.security.GamePermission;

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
    private int teamId = -1;

    /**
     * the method that will be executed once the bank becomes active. This is where the player's
     * code is defined
     * 
     * @throws BankInterruptedException If the bank's execution is stopped prematurely
     */
    public abstract void run() throws BankInterruptedException;


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
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("getTeamId"));
	}
	return teamId;
    }

    /**
     * Sets the team id. Can only be called once, otherwise it will throw an exception
     * 
     * @param newId the team id of the bank
     */
    final public void setTeamId(int newId) {
	if (teamId != -1) {
	    throw new IllegalStateException("Team Id cannot be modified");
	}
	
	teamId = newId;
    }

}
