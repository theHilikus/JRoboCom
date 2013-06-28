package com.github.thehilikus.jrobocom.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.events.event_manager.api.EventPublisher;
import com.github.thehilikus.jrobocom.Direction;
import com.github.thehilikus.jrobocom.events.RobotChangedEvent;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.robot.api.RobotStatusLocal;

/**
 * The robot's current data. Everything here needs to serve the implemented interface; otherwise it
 * should be in the robot itself
 * 
 */
public class RobotData implements RobotStatusLocal, EventPublisher {
    private final boolean mobile;
    private int activeState;
    private final InstructionSet set;
    private Direction facing;
    private final int generation;
    private final int teamId;

    private final int banksCount;
    private final int cyclesAtCreation;

    private EventDispatcher eventDispatcher;
    private final Robot owner;
    
    private static final Logger log = LoggerFactory.getLogger(RobotData.class);
    

    /**
     * Main constructor
     * 
     * @param pOwner the robot this data belongs to
     * @param pSet instruction set
     * @param pMobile true if the robot is mobile
     * @param pGeneration the number of ancestors this robot has
     * @param direction starting facing
     */
    RobotData(Robot pOwner, InstructionSet pSet, boolean pMobile, int pGeneration, Direction direction) {
	if (pOwner == null || pSet == null || pGeneration < 0 || direction == null) {
	    throw new IllegalArgumentException("Invalid argument in constructor");
	}
	this.activeState = 0;
	set = pSet;
	mobile = pMobile;
	teamId = pOwner.getOwner().getTeamId();
	generation = pGeneration;
	banksCount = pOwner.getBanksCount();
	facing = direction;
	owner = pOwner;
	eventDispatcher = pOwner.getEventDispatcher();

	cyclesAtCreation = pOwner.getTurnsControl().getTurnsCount();

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
	return (int) Math.round((owner.getTurnsControl().getTurnsCount() - cyclesAtCreation) / 1.0);
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
	
	log.trace("[setActiveState] Changed {} state from {} to {}", owner, oldState, newState);
    }

    @Override
    public Direction getFacing() {
	return facing;
    }

    void setFacing(Direction newDirection) {
	facing = newDirection;

    }

    @Override
    public void setEventDispatcher(EventDispatcher dispatcher) {
	eventDispatcher = dispatcher;
	
    }

}