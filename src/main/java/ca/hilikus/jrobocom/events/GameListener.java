package ca.hilikus.jrobocom.events;

import ca.hilikus.jrobocom.World.WorldListener;
import ca.hilikus.jrobocom.robot.Robot.RobotListener;

/**
 * Aggregation of all the interfaces that produce status events
 * 
 * @author hilikus
 */
public interface GameListener extends WorldListener, RobotListener {

}
