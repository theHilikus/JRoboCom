package ca.hilikus.jrobocom.robot;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.GameSettings.Timing;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.robot.Robot.TurnManager;
import ca.hilikus.jrobocom.robot.api.RobotStatus;

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
     * @see ca.hilikus.jrobocom.RobotInfo#getActiveState()
     */
    @Override
    public int getActiveState() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().getActiveState();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getActiveState(boolean)
     */
    @Override
    public int getRemoteActiveState() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getActiveState();
	} else {
	    return 0;
	}

    }

    @Override
    public void setRemoteActiveState(int pActiveState) {

	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(robot);
	if (neighbour != null) {
	    neighbour.getData().setActiveState(pActiveState);
	}

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getGeneration()
     */
    @Override
    public int getGeneration() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().getGeneration();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getBanksCount()
     */
    @Override
    public int getBanksCount() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getBanksCount();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getBanksCount(boolean)
     */
    @Override
    public int getRemoteBanksCount() {

	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getBanksCount();
	} else {
	    return 0;
	}

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getInstructionSet()
     */
    @Override
    public InstructionSet getInstructionSet() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().getInstructionSet();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getInstructionSet(boolean)
     */
    @Override
    public InstructionSet getRemoteInstructionSet() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getInstructionSet();
	} else {
	    return InstructionSet.BASIC;
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getTeamId()
     */
    @Override
    public int getTeamId() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().getTeamId();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getTeamId(boolean)
     */
    @Override
    public int getRemoteTeamId() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getTeamId();
	} else {
	    return 0;
	}

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return robot.getData().getActiveState() > 0;
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isEnabled(boolean)
     */
    @Override
    public boolean isRemoteEnabled() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().getActiveState() > 0;
	} else {
	    return false;
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isMobile()
     */
    @Override
    public boolean isMobile() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().isMobile();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isMobile(boolean)
     */
    @Override
    public boolean isRemoteMobile() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	Robot neighbour = world.getNeighbour(this.robot);
	if (neighbour != null) {
	    return neighbour.getData().isMobile();
	} else {
	    return false;
	}

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getAge()
     */
    @Override
    public int getAge() {
	turnsControl.waitTurns(Timing.LOCAL_READ);
	return this.robot.getData().getAge();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getAge(boolean)
     */
    @Override
    public int getRemoteAge() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
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
	turnsControl.waitTurns(Timing.LOCAL_WRITE);
	this.robot.setActiveState(pActiveState);

    }
}
