package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * Event to mark the change in one of the robot's attributes
 * 
 * @author hilikus
 */
public class RobotChangedEvent extends EventObject {

    private static final long serialVersionUID = 207547744025667582L;

    /**
     * Main constructor
     * 
     * @param source the robot that changed
     */
    public RobotChangedEvent(Robot source) {
	super(source);
    }

}
