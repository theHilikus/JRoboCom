package ca.hilikus.jrobocom.player;

import ca.hilikus.jrobocom.robot.api.RobotAction;

/**
 * The maximum group of operations supported by robots. Every set contains all the previous sets
 * 
 */
public enum InstructionSet {
    /**
     * The first level of instructions:<br>
     * {@link RobotAction#changeBank(int)}, {@link RobotAction#die(String)},
     * {@link RobotAction#move()}, {@link RobotAction#turn(boolean)}
     */
    BASIC, /**
     * The first + second level of instructions:<br>
     * BASIC + {@link RobotAction#scan(int)}, {@link RobotAction#reverseTransfer(int, int)}
     */
    ADVANCED, /**
     * The complete set of instructions:<br>
     * BASIC + ADVANCED + {@link RobotAction#createRobot(String name, InstructionSet pSet, int banksCount, boolean pMobile)}
     */
    SUPER;

    /**
     * @param other the Set to compare this with
     * @return true if this is simpler than other
     */
    public boolean isLessThan(InstructionSet other) {
	return ordinal() < other.ordinal();

    }
}