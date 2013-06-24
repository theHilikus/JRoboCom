package com.github.thehilikus.jrobocom.events;

import com.github.thehilikus.jrobocom.GameTracker.GameStatusListener;
import com.github.thehilikus.jrobocom.World.WorldListener;
import com.github.thehilikus.jrobocom.robot.Robot.RobotListener;

/**
 * Aggregation of all the interfaces that produce status events
 * 
 * @author hilikus
 */
public interface GameListener extends WorldListener, RobotListener, GameStatusListener {

}
