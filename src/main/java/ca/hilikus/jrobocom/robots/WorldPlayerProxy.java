package ca.hilikus.jrobocom.robots;

import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.WorldInfo;
import ca.hilikus.jrobocom.GameSettings.Timing;
import ca.hilikus.jrobocom.robots.Robot.TurnManager;

/**
 * Interface used from Banks. Maps one-to-one with robots on one end; on the other end it can change
 * banks
 * 
 * @author hilikus
 * 
 */
public class WorldPlayerProxy implements WorldInfo {

    private final World world;

    private final TurnManager turnsControl;

    /**
     * @param turnManager turns handler for calling robot
     * @param pWorld the world of the calling robot
     */
    public WorldPlayerProxy(TurnManager turnManager, World pWorld) {
	turnsControl = turnManager;
	world = pWorld;
    }

    @Override
    public int getBotsCount(int teamId, boolean invert) {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);

	return world.getBotsCount(teamId, invert);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.WorldInfo#getWorldAge()
     */
    @Override
    public int getWorldAge() {
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);

	return world.getAge();
    }

}
