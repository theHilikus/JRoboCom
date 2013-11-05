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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getActiveState] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get State");
	return this.robot.getData().getActiveState();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getActiveState(boolean)
     */
    @Override
    public int getRemoteActiveState() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getRemoteActiveState] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Neighbour State");
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getActiveState();
	} else {
	    return 0;
	}

    }

    @Override
    public void setRemoteActiveState(int pActiveState) {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[setRemoteActiveState] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Set Neighbour State");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getGeneration] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Generation");
	return this.robot.getData().getGeneration();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getBanksCount()
     */
    @Override
    public int getBanksCount() {
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getBanksCount] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Bank count");
	return this.robot.getBanksCount();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getBanksCount(boolean)
     */
    @Override
    public int getRemoteBanksCount() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getRemoteBanksCount] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Neighbour's Bank count");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getInstructionSet] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Instruction set");
	return this.robot.getData().getInstructionSet();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getInstructionSet(boolean)
     */
    @Override
    public InstructionSet getRemoteInstructionSet() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getRemoteInstructionSet] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Neighbour's Instruction set");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getTeamId] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Team Id");
	return this.robot.getData().getTeamId();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getTeamId(boolean)
     */
    @Override
    public int getRemoteTeamId() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getRemoteTeamId] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Remote Team Id");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[isEnabled] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get is enabled");
	return robot.getData().getActiveState() > 0;
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isEnabled(boolean)
     */
    @Override
    public boolean isRemoteEnabled() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[isRemoteEnabled] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get is Neighbour enabled");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[isMobile] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get is Mobile");
	return this.robot.getData().isMobile();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#isMobile(boolean)
     */
    @Override
    public boolean isRemoteMobile() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[isRemoteMobile] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get is Neighbour Mobile");
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
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getAge] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Age");
	return this.robot.getData().getAge();
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotInfo#getAge(boolean)
     */
    @Override
    public int getRemoteAge() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getRemoteAge] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Neighbour's Age");
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getAge();
	} else {
	    return 0;
	}
    }

    @Override
    public Direction getFacing() {
	int penalty = Timing.getInstance().LOCAL_READ;
	log.trace("[getAge] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Direction");
	return robot.getData().getFacing();
    }

    @Override
    public void setActiveState(int pActiveState) {
	int penalty = Timing.getInstance().LOCAL_WRITE;
	log.trace("[setActiveState] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Set State");
	this.robot.getData().setActiveState(pActiveState);

    }
}
