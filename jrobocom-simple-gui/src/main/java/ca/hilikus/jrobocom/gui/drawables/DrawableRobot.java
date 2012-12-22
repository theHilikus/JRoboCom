package ca.hilikus.jrobocom.gui.drawables;

import java.awt.Graphics2D;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.gui.Drawable;
import ca.hilikus.jrobocom.gui.ModelDrawingVisitor;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.timing.MasterClock;

public class DrawableRobot implements Drawable {

    private Robot robot;

    public DrawableRobot(Robot realRobot) {
	robot = realRobot;
    }

    @Override
    public void accept(Graphics2D g2, ModelDrawingVisitor visitor) {
	visitor.draw(g2, robot);
	
    }

}
