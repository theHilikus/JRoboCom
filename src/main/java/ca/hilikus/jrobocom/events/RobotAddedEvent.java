package ca.hilikus.jrobocom.events;

import java.awt.Point;
import java.util.EventObject;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * Event created when a new robot enters the game
 * 
 * @author hilikus
 */
public class RobotAddedEvent extends EventObject {

    private static final long serialVersionUID = 3701263690126469309L;
    private Point coordinates;

    /**
     * Main constructor
     * 
     * @param newRobot robot just added to the game
     * @param pCoordinates location of the new robot
     */
    public RobotAddedEvent(Robot newRobot, Point pCoordinates) {
	super(newRobot);
	coordinates = pCoordinates;
    }

    /**
     * @return the X, Y coordinates where the new robot was added
     */
    public Point getCoordinates() {
	return coordinates;
    }

}
