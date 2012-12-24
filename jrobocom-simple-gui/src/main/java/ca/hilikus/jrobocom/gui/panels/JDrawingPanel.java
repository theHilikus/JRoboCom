package ca.hilikus.jrobocom.gui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import ca.hilikus.jrobocom.gui.ColourInfoProvider;
import ca.hilikus.jrobocom.gui.visitor.Drawable;
import ca.hilikus.jrobocom.gui.visitor.ModelDrawingVisitor;
import ca.hilikus.jrobocom.robot.Robot;

/**
 * Single field panel. Drawings of elements of the board happen here
 * 
 * @author hilikus
 */
public class JDrawingPanel extends JPanel {

    private static final long serialVersionUID = -2321388417704833235L;

    private static final String MODEL = "drawingModel";

    private ModelDrawingVisitor drawer = new Drawer();

    private static ColourInfoProvider colourProvider;

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	assert getClientProperty(MODEL) == null || getClientProperty(MODEL) instanceof Drawable;
	Drawable toDraw = (Drawable) getClientProperty(MODEL);
	if (toDraw != null) {
	    // something to draw
	    toDraw.accept((Graphics2D) g, drawer);

	}
    }

    private class Drawer implements ModelDrawingVisitor {

	@Override
	public void draw(Graphics2D g2, Robot robot) {
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    // draw body
	    int OFFSET = 6;
	    int availableSize = getWidth() - OFFSET;
	    Ellipse2D.Double visualRobot = new Ellipse2D.Double(OFFSET / 2, OFFSET / 2, availableSize,
		    availableSize);
	    g2.setColor(colourProvider.getTeamColour(robot.getOwner().getTeamId()));
	    g2.fill(visualRobot);
	    g2.setColor(Color.BLACK);
	    g2.draw(visualRobot); // draw outline
	    int midPoint = availableSize / 2 + OFFSET / 2;
	    int headBase = (int) Math.round(availableSize * 0.25);
	    int baseStart = (int) Math.round(availableSize * 0.20);

	    // draw head
	    Polygon head = new Polygon();
	    switch (robot.getData().getFacing()) {
		case NORTH:
		    head.addPoint(midPoint - headBase / 2, midPoint - baseStart);
		    head.addPoint(midPoint + headBase / 2, midPoint - baseStart);
		    head.addPoint(midPoint, midPoint - baseStart * 2);
		    break;
		case EAST:
		    head.addPoint(midPoint + baseStart, midPoint - headBase / 2);
		    head.addPoint(midPoint + baseStart, midPoint + headBase / 2);
		    head.addPoint(midPoint + baseStart * 2, midPoint);
		    break;
		case SOUTH:
		    head.addPoint(midPoint - headBase / 2, midPoint + baseStart);
		    head.addPoint(midPoint + headBase / 2, midPoint + baseStart);
		    head.addPoint(midPoint, midPoint + baseStart * 2);
		    break;
		case WEST:
		    head.addPoint(midPoint - baseStart, midPoint - headBase / 2);
		    head.addPoint(midPoint - baseStart, midPoint + headBase / 2);
		    head.addPoint(midPoint - baseStart * 2, midPoint);
		    break;
	    }

	    g2.setColor(colourProvider.getTeamColour(robot.getRunningBankTeamId()));
	    g2.fill(head);

	    g2.setColor(Color.BLACK); // draw outline
	    g2.draw(head);

	    if (!robot.getData().isEnabled()) {
		// robot disabled, draw an X
		int xBorder = (int) Math.round(getWidth()/8.0);
		float lineThickness = (float) (getWidth()/9.0);
		g2.setColor(Color.LIGHT_GRAY);
		g2.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawLine(xBorder, xBorder, getWidth() - xBorder, getWidth() - xBorder);
		g2.drawLine(getWidth() - xBorder, xBorder, xBorder, getWidth() - xBorder);
	    }
	}
    }

    /**
     * Adds an object to draw
     * 
     * @param model element to draw
     */
    public void addModel(Drawable model) {
	putClientProperty(MODEL, model);

    }

    /**
     * Removes the drawn object
     */
    public void removeModel() {
	putClientProperty(MODEL, null);
    }

    /**
     * @return the model in this panel or null if there isn't one
     */
    public Drawable getModel() {
	return (Drawable) getClientProperty(MODEL);
    }

    /**
     * @param pColourProvider the colour provider used when drawing
     */
    public static void setColourProvider(ColourInfoProvider pColourProvider) {
	colourProvider = pColourProvider;
    }

    /**
     * @return true if there is an object to draw in the panel
     */
    public boolean hasModel() {
	return getClientProperty(MODEL) != null;
    }

}
