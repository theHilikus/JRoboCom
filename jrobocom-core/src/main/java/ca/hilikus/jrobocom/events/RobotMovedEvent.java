package ca.hilikus.jrobocom.events;

import java.awt.Point;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * Even generated when a robot changes position
 * 
 * @author hilikus
 */
public class RobotMovedEvent extends RobotEvent {

    private static final long serialVersionUID = 860096734812275081L;
    private Point oldPosition;
    private Point newPosition;

    /**
     * Constructs an event
     * 
     * @param source the robot that moved
     * @param old the old location
     * @param pNewPosition the new location
     */
    public RobotMovedEvent(Robot source, Point old, Point pNewPosition) {
	super(source);
	this.newPosition = pNewPosition;
	oldPosition = old;
    }

    /**
     * @return the oldPosition
     */
    public Point getOldPosition() {
	return oldPosition;
    }

    /**
     * @return the newPosition
     */
    public Point getNewPosition() {
	return newPosition;
    }

}
