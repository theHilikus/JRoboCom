package com.github.thehilikus.jrobocom.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.WorldInfo;
import com.github.thehilikus.jrobocom.GameSettings.Timing;
import com.github.thehilikus.jrobocom.robot.Robot.TurnManager;

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
    
    private static final Logger log = LoggerFactory.getLogger(WorldPlayerProxy.class);
    

    /**
     * @param turnManager turns handler for calling robot
     * @param pWorld the world of the calling robot
     */
    WorldPlayerProxy(TurnManager turnManager, World pWorld) {
	turnsControl = turnManager;
	world = pWorld;
    }

    @Override
    public int getBotsCount(int teamId, boolean invert) {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getBotsCount] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get Robots count");

	return world.getBotsCount(teamId, invert);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.WorldInfo#getWorldAge()
     */
    @Override
    public int getWorldAge() {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY;
	log.trace("[getWorldAge] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Get World's Age");

	return world.getAge();
    }

}
