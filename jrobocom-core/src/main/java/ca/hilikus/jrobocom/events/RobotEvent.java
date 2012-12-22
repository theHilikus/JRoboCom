package ca.hilikus.jrobocom.events;

import java.util.EventObject;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * The parent of all robot events
 * 
 * @author hilikus
 */
public class RobotEvent extends EventObject {

    private static final long serialVersionUID = -2682402437090420424L;

    /**
     * Constructs an event
     * 
     * @param source the robot in question
     */
    public RobotEvent(Robot source) {
	super(source);
    }

    /* (non-Javadoc)
     * @see java.util.EventObject#getSource()
     */
    @Override
    public Robot getSource() {
	return (Robot) super.getSource();
    }

}
