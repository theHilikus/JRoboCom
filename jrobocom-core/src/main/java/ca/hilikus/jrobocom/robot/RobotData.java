package ca.hilikus.jrobocom.robot;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.events.GenericEventDispatcher;
import ca.hilikus.jrobocom.events.RobotChangedEvent;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.robot.Robot.RobotListener;
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

    private final GenericEventDispatcher<RobotListener> eventDispatcher;
    private final Robot owner;

    /**
     * Main constructor
     * 
     * @param pOwner the robot this data belongs to
     * @param pSet instruction set
     * @param pMobile true if the robot is mobile
     * @param pGeneration the number of ancestors this robot has
     */
    RobotData(Robot pOwner, InstructionSet pSet, boolean pMobile, int pGeneration, Direction direction) {
	this.activeState = 0;
	set = pSet;
	mobile = pMobile;
	teamId = pOwner.getOwner().getTeamId();
	generation = pGeneration;
	banksCount = pOwner.getBanksCount();
	turnsManager = pOwner.getTurnsControl();
	facing = direction;
	owner = pOwner;
	eventDispatcher = (GenericEventDispatcher<RobotListener>) pOwner.getEventHandler();

	cyclesAtCreation = turnsManager.getTurnsCount();

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
	return (int) Math.round((turnsManager.getTurnsCount() - cyclesAtCreation) / 1.0);
	// TODO: look into using thread CPU time instead?
	// http://stackoverflow.com/questions/755899/monitor-cpu-usage-per-thread-in-java
    }

    @Override
    public int getGeneration() {
	return generation;
    }

    @Override
    public void setActiveState(int newState) {
	int oldState = activeState;
	activeState = newState;

	if (newState > 0) {
	    owner.activated();
	}

	if (oldState <= 0 && newState > 0 || oldState > 0 && newState <= 0) {
	    eventDispatcher.fireEvent(new RobotChangedEvent(owner));
	}
    }

    @Override
    public Direction getFacing() {
	return facing;
    }

    void setFacing(Direction newDirection) {
	facing = newDirection;

    }

}