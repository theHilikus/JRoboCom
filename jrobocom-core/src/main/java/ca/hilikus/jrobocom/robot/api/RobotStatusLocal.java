package ca.hilikus.jrobocom.robot.api;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.player.InstructionSet;

/**
 * An interface to query the state of the local robot
 * 
 * @author hilikus
 */
public interface RobotStatusLocal {

    /**
     * Gets the activation state of the current robot
     * 
     * @return a number > 0 if the robot is active; < 1 if inactive
     */
    public int getActiveState();

    /**
     * 
     * @return the number of bank slots of the current robot. Includes empty slots
     */
    public int getBanksCount();

    /**
     * @return the set of instructions used by the robot
     */
    public InstructionSet getInstructionSet();

    /**
     * @return the unique team ID
     */
    public int getTeamId();

    /**
     * @return true if the robot is currently active
     */
    public boolean isEnabled();

    /**
     * @return true if the robot can change position
     */
    public boolean isMobile();

    /**
     * @return the number of cycles elapsed since the robot started
     */
    public int getAge();

    /**
     * @return the direction the robot is facing
     */
    public Direction getFacing();

    /**
     * @return the number of ancestors
     */
    public int getGeneration();

    /**
     * Sets the state of the local robot
     * 
     * @param pActiveState <= 0 to disable a robot; > 0 to enable
     */
    public void setActiveState(int pActiveState);

}