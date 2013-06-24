package com.github.thehilikus.jrobocom.robot.api;

import com.github.thehilikus.jrobocom.player.InstructionSet;

/**
 * Interface to read information about the local robot or its neighbour
 * 
 * @author hilikus
 * 
 */
public interface RobotStatus extends RobotStatusLocal {

    /**
     * Gets the activation state of the adjacent robot
     * 
     * @return a number > 0 if the neighbour robot is active; < 1 if inactive.<br>
     *         0 if remote mode and field is empty
     */
    public int getRemoteActiveState();

    /**
     * @return the number of banks of the neighbour robot.<br>
     *         0 if remote mode and field is empty
     */
    public int getRemoteBanksCount();

    /**
     * @return the executable instructions of the neighbour robot<br>
     *         {@link InstructionSet#BASIC} if remote mode and field is empty
     */
    public InstructionSet getRemoteInstructionSet();

    /**
     * @return the team identifier of the neighbour robot.<br>
     *         0 if remote mode and field is empty
     */
    public int getRemoteTeamId();

    /**
     * @return true if the neighbour robot is active; false otherwise.<br>
     *         false if remote mode and field is empty
     * @see #getRemoteActiveState()
     */
    public boolean isRemoteEnabled();

    /**
     * @return true if the neighbour robot can move; false otherwise.<br>
     *         false if remote mode and field is empty
     */
    public boolean isRemoteMobile();

    /**
     * @return the number of cycles elapsed since neighbour robot creation divided by 1000 //TODO: fix divisor
     */
    public int getRemoteAge();

    /**
     * Sets the state of the neighbour robot
     * 
     * @param pActiveState <= 0 to disable a robot; > 0 to enable
     */
    public void setRemoteActiveState(int pActiveState);

}