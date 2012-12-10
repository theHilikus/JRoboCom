package ca.hilikus.jrobocom.gui;

import java.awt.Graphics2D;

import ca.hilikus.jrobocom.robot.Robot;

/**
 * Implementation of drawing several elements
 * 
 * @author hilikus
 */
public interface ModelDrawingVisitor {
    /**
     * Show visual representation of a robot
     * 
     * @param g2 the graphics element to use to draw 
     * @param robot the element to draw
     */
    public void draw(Graphics2D g2, Robot robot);
}