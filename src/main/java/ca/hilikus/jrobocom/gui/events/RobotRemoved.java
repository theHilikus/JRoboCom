package ca.hilikus.jrobocom.gui.events;

import java.awt.Point;
import java.util.EventObject;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * Event created when a robot is removed from the game
 * 
 * @author hilikus
 */
public class RobotRemoved extends EventObject {

    private static final long serialVersionUID = 8962044780427247123L;
    private Point lastPosition;

    /**
     * Main constructor
     * 
     * @param robot robot removed
     * @param pLastPosition last coordinates of the robot
     */
    public RobotRemoved(Robot robot, Point pLastPosition) {
	super(robot);

	lastPosition = pLastPosition;
    }

    /**
     * @return the last known position of the robot
     */
    public Point getCoordinates() {
	return lastPosition;
    }

}
