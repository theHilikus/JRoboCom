package ca.hilikus.jrobocom.robot;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.robot.Robot.TurnManager;
import ca.hilikus.jrobocom.robot.api.RobotStatusLocal;

/**
 * The robot's current data. Everything here needs to serve the implemented interface; otherwise it
 * should be in the robot itself
 * 
 */
public class RobotData implements RobotStatusLocal {
    private final boolean mobile;
    private int activeState;
    private final InstructionSet set;
    private Direction facing;
    private final int generation;
    private final int teamId;

    private final int banksCount;
    private final TurnManager turnsManager;
    private final int cyclesAtCreation;

    /**
     * Main constructor
     * 
     * @param pTurnsManager the robot's turn controller
     * @param pMobile true if the robot is mobile
     * @param pSet instruction set
     * @param pTeamId unique team identifier
     * @param pGeneration the number of ancestors this robot has
     * @param pBanksCount number of banks slots in the robot
     */
    public RobotData(TurnManager pTurnsManager, InstructionSet pSet, boolean pMobile,
	    int pTeamId, int pGeneration, int pBanksCount) {
	this.activeState = 0;
	set = pSet;
	mobile = pMobile;
	teamId = pTeamId;
	generation = pGeneration;
	banksCount = pBanksCount;
	turnsManager = pTurnsManager;
	facing = Direction.fromInt(World.generator.nextInt(Direction.COUNT));

	cyclesAtCreation = pTurnsManager.getTurnsCount();

    }

    @Override
    public int getActiveState() {
	return activeState;
    }

    @Override
    public int getBanksCount() {
	return banksCount;

    }

    @Override
    public InstructionSet getInstructionSet() {
	return set;
    }

    @Override
    public int getTeamId() {
	return teamId;
    }

    @Override
    public boolean isEnabled() {
	return activeState > 0;
    }

    @Override
    public boolean isMobile() {
	return mobile;
    }

    @Override
    public int getAge() {
	return (int) Math.round((turnsManager.getTurnsCount() - cyclesAtCreation) / 1000.0);

    }

    @Override
    public int getGeneration() {
	return generation;
    }

    @Override
    public void setActiveState(int pActiveState) {
	activeState = pActiveState;

    }

    @Override
    public Direction getFacing() {
	return facing;
    }

    void setFacing(Direction newDirection) {
	facing = newDirection;

    }

}