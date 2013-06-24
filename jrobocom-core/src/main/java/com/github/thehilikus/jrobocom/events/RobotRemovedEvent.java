package com.github.thehilikus.jrobocom.events;

import java.awt.Point;

import com.github.thehilikus.jrobocom.robot.Robot;

/**
 * Event created when a robot is removed from the game
 * 
 * @author hilikus
 */
public class RobotRemovedEvent extends RobotEvent {

    private static final long serialVersionUID = 8962044780427247123L;
    private Point lastPosition;

    /**
     * Main constructor
     * 
     * @param robot robot removed
     * @param pLastPosition last coordinates of the robot
     */
    public RobotRemovedEvent(Robot robot, Point pLastPosition) {
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
