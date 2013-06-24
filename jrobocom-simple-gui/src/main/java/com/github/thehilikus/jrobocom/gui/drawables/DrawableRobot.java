package com.github.thehilikus.jrobocom.gui.drawables;

import java.awt.Graphics2D;

import com.github.thehilikus.jrobocom.gui.visitor.Drawable;
import com.github.thehilikus.jrobocom.gui.visitor.ModelDrawingVisitor;
import com.github.thehilikus.jrobocom.robot.Robot;

/**
 * Wrapper to draw robots
 * 
 * @author hilikus
 */
public class DrawableRobot implements Drawable {

    private Robot robot;

    /**
     * Wraps a robot
     * 
     * @param realRobot the robot to draw
     */
    public DrawableRobot(Robot realRobot) {
	robot = realRobot;
    }

    @Override
    public void accept(Graphics2D g2, ModelDrawingVisitor visitor) {
	visitor.draw(g2, robot);

    }

}
