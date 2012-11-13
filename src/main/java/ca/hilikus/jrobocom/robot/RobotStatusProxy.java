package ca.hilikus.jrobocom.robot;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.GameSettings.Timing;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.robot.Robot.TurnManager;
import ca.hilikus.jrobocom.robot.api.RobotStatus;

/**
 * Interface used from Banks. Maps one-to-one with robots on one end; on the other end it can change banks
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
    public RobotStatusProxy(Robot pRobot, World pWorld) {
	robot = pRobot;
	world = pWorld;
	turnsControl = pRobot.getTurnsControl();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getActiveState()
     */
    @Override
    public int getActiveState() {
	return getActiveState(true);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getActiveState(boolean)
     */
    @Override
    public int getActiveState(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);
	    return this.robot.getData().getActiveState();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().getActiveState();
	    } else {
		return 0;
	    }
	}
    }

    @Override
    public void setActiveState(int pActiveState, boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_WRITE);
	    this.robot.setActiveState(pActiveState);
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(robot);
	    if (neighbour != null) {
		setActiveState(pActiveState, true);
	    }
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
	return getBanksCount(true);

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getBanksCount(boolean)
     */
    @Override
    public int getBanksCount(boolean local) {
	if (local) {

	    turnsControl.waitTurns(Timing.LOCAL_READ);
	    return this.robot.getBanksCount();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getBanksCount();
	    } else {
		return 0;
	    }
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getInstructionSet()
     */
    @Override
    public InstructionSet getInstructionSet() {
	return getInstructionSet(true);

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getInstructionSet(boolean)
     */
    @Override
    public InstructionSet getInstructionSet(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);

	    return this.robot.getData().getInstructionSet();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().getInstructionSet();
	    } else {
		return InstructionSet.BASIC;
	    }
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getTeamId()
     */
    @Override
    public int getTeamId() {
	return getTeamId(true);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getTeamId(boolean)
     */
    @Override
    public int getTeamId(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);
	    return this.robot.getData().getTeamId();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().getTeamId();
	    } else {
		return 0;
	    }
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isEnabled()
     */
    @Override
    public boolean isEnabled() {
	return isEnabled(true);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isEnabled(boolean)
     */
    @Override
    public boolean isEnabled(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);
	    return robot.getData().getActiveState() > 0;
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().getActiveState() > 0;
	    } else {
		return false;
	    }
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isMobile()
     */
    @Override
    public boolean isMobile() {
	return isMobile(true);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#isMobile(boolean)
     */
    @Override
    public boolean isMobile(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);

	    return this.robot.getData().isMobile();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().isMobile();
	    } else {
		return false;
	    }
	}
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getAge()
     */
    @Override
    public int getAge() {
	return getAge(true);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotInfo#getAge(boolean)
     */
    @Override
    public int getAge(boolean local) {
	if (local) {
	    turnsControl.waitTurns(Timing.LOCAL_READ);

	    return this.robot.getData().getAge();
	} else {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
	    Robot neighbour = world.getNeighbour(this.robot);
	    if (neighbour != null) {
		return neighbour.getData().getAge();
	    } else {
		return 0;
	    }
	}
    }

    @Override
    public Direction getFacing() {
	return robot.getData().getFacing();
    }

    @Override
    public void setActiveState(int pActiveState) {
	robot.setActiveState(pActiveState);

    }
}
