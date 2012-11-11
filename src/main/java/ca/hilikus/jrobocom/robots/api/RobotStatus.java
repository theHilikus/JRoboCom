package ca.hilikus.jrobocom.robots.api;

import ca.hilikus.jrobocom.player.InstructionSet;

/**
 * Interface to read information about the local robot or its neighbour
 * 
 * @author hilikus
 * 
 */
public interface RobotStatus extends RobotStatusLocal {

    /**
     * Gets the activation state of the current or adjacent robot
     * 
     * @param local true if of the current robot; false if of the adjacent
     * @return a number > 0 if the robot is active; < 1 if inactive.<br>
     *         0 if remote mode and field is empty
     */
    public int getActiveState(boolean local);

    /**
     * @param local true if of the current robot; false if of the adjacent
     * @return the number of banks of the specified robot.<br>
     *         0 if remote mode and field is empty
     */
    public int getBanksCount(boolean local);

    /**
     * @param local true if of the current robot; false if of the adjacent
     * @return the executable instructions of the specified robot<br>
     *         {@link InstructionSet#BASIC} if remote mode and field is empty
     */
    public InstructionSet getInstructionSet(boolean local);

    /**
     * @param local true if of the current robot; false if of the adjacent
     * @return the team identifier of the specified robot.<br>
     *         0 if remote mode and field is empty
     */
    public int getTeamId(boolean local);

    /**
     * @param local true if the current robot; false if the adjacent
     * @return true if the robot is active; false otherwise.<br>
     *         false if remote mode and field is empty
     * @see #getActiveState(boolean)
     */
    public boolean isEnabled(boolean local);

    /**
     * @param local true if the current robot; false if the adjacent
     * @return true if the robot can move; false otherwise.<br>
     *         false if remote mode and field is empty
     */
    public boolean isMobile(boolean local);

    /**
     * @param local true if the current robot; false if the adjacent
     * @return the number of cycles elapsed since robot creation divided by 1000.<br>
     */
    public int getAge(boolean local);

    /**
     * @param pActiveState <= 0 to disable a robot; > 0 to enable
     * @param local true if the current robot; false if the adjacent
     */
    public void setActiveState(int pActiveState, boolean local);

}