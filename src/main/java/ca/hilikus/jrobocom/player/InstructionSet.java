package ca.hilikus.jrobocom.player;

import ca.hilikus.jrobocom.Robot.RobotControl;

/**
 * The maximum group of operations supported by robots. Every set contains all the previous sets
 * 
 */
public enum InstructionSet {
    /**
     * The first level of instructions:<br>
     * {@link RobotControl#changeBank(int)}, {@link RobotControl#die(String)},
     * {@link RobotControl#move()}, {@link RobotControl#turn(boolean)}
     */
    BASIC, /**
     * The first + second level of instructions:<br>
     * BASIC + {@link RobotControl#scan(int)}, {@link RobotControl#reverseTransfer(int, int)}
     */
    ADVANCED, /**
     * The complete set of instructions:<br>
     * BASIC + ADVANCED + {@link RobotControl#createRobot(InstructionSet, int, boolean)}
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