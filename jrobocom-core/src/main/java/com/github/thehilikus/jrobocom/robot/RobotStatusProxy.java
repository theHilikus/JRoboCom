package com.github.thehilikus.jrobocom.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.Direction;
import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.GameSettings.Timing;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.robot.Robot.TurnManager;
import com.github.thehilikus.jrobocom.robot.api.RobotStatus;

/**
 * Interface used from Banks. Maps one-to-one with robots on one end; on the other end it can change
 * banks
 * 
 * @author hilikus
 * 
 */
public class RobotStatusProxy implements RobotStatus {

    private Robot robot;

    private final TurnManager turnsControl;

    private final World world;
    
    private static final Logger log = LoggerFactory.getLogger(RobotStatusProxy.class);

    /**
     * @param pRobot the robot mapped to this proxy
     * @param pWorld the world where the robot lives
     */
    RobotStatusProxy(Robot pRobot, World pWorld) {
	robot = pRobot;
	world = pWorld;
	turnsControl = pRobot.getTurnsControl();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getActiveState()
     */
    @Override
    public int getActiveState() {
	log.trace("[getActiveState] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().getActiveState();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getActiveState(boolean)
     */
    @Override
    public int getRemoteActiveState() {
	log.trace("[getRemoteActiveState] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getActiveState();
	} else {
	    return 0;
	}

    }

    @Override
    public void setRemoteActiveState(int pActiveState) {
	log.trace("[setRemoteActiveState] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(robot);
	if (neighbour != null) {
	    neighbour.getData().setActiveState(pActiveState);
	}

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getGeneration()
     */
    @Override
    public int getGeneration() {
	log.trace("[getGeneration] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().getGeneration();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getBanksCount()
     */
    @Override
    public int getBanksCount() {
	log.trace("[getBanksCount] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getBanksCount();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getBanksCount(boolean)
     */
    @Override
    public int getRemoteBanksCount() {
	log.trace("[getRemoteBanksCount] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getBanksCount();
	} else {
	    return 0;
	}

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getInstructionSet()
     */
    @Override
    public InstructionSet getInstructionSet() {
	log.trace("[getInstructionSet] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().getInstructionSet();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getInstructionSet(boolean)
     */
    @Override
    public InstructionSet getRemoteInstructionSet() {
	log.trace("[getRemoteInstructionSet] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getInstructionSet();
	} else {
	    return InstructionSet.BASIC;
	}
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getTeamId()
     */
    @Override
    public int getTeamId() {
	log.trace("[getTeamId] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().getTeamId();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getTeamId(boolean)
     */
    @Override
    public int getRemoteTeamId() {
	log.trace("[getRemoteTeamId] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getTeamId();
	} else {
	    return 0;
	}

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	log.trace("[isEnabled] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return robot.getData().getActiveState() > 0;
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isEnabled(boolean)
     */
    @Override
    public boolean isRemoteEnabled() {
	log.trace("[isRemoteEnabled] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getActiveState() > 0;
	} else {
	    return false;
	}
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isMobile()
     */
    @Override
    public boolean isMobile() {
	log.trace("[isMobile] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().isMobile();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isMobile(boolean)
     */
    @Override
    public boolean isRemoteMobile() {
	log.trace("[isRemoteMobile] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().isMobile();
	} else {
	    return false;
	}

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getAge()
     */
    @Override
    public int getAge() {
	log.trace("[getAge] Waiting {} cycles", Timing.getInstance().LOCAL_READ);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_READ);
	return this.robot.getData().getAge();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getAge(boolean)
     */
    @Override
    public int getRemoteAge() {
	log.trace("[getRemoteAge] Waiting {} cycles", Timing.getInstance().REMOTE_ACCESS_PENALTY);
	turnsControl.waitTurns(Timing.getInstance().REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getAge();
	} else {
	    return 0;
	}
    }

    @Override
    public Direction getFacing() {
	return robot.getData().getFacing();
    }

    @Override
    public void setActiveState(int pActiveState) {
	log.trace("[setActiveState] Waiting {} cycles", Timing.getInstance().LOCAL_WRITE);
	turnsControl.waitTurns(Timing.getInstance().LOCAL_WRITE);
	this.robot.getData().setActiveState(pActiveState);

    }
}
