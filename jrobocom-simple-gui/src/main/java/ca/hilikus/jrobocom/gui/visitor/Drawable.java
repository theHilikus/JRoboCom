package ca.hilikus.jrobocom.gui.visitor;

import java.awt.Graphics2D;

/**
 * Interface to be implemented by any element that needs to be drawn in the GUI
 * 
 * @author hilikus
 */
public interface Drawable {
    /**
     * Visitor method. Implementers should just call visitor.draw(g2, this)
     * 
     * @param g2 graphics element to draw in 
     * @param visitor the actual implementer of the action
     */
    public void accept(Graphics2D g2, ModelDrawingVisitor visitor);
}