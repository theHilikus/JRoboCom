package ca.hilikus.jrobocom.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.GameSettings;

/**
 * The playing board UI
 * 
 * @author hilikus
 */
public class BoardPanel extends JPanel {

    private static final long serialVersionUID = -3895988249674025563L;
    private int SIZE = GameSettings.BOARD_SIZE;

    private JDrawingPanel[][] data;

    private static final Logger log = LoggerFactory.getLogger(BoardPanel.class);

    /**
     * Main constructor
     * 
     * @param colourProvider colour information provider used for drawing
     */
    public BoardPanel(ColourInfoProvider colourProvider) {
	setMinimumSize(new Dimension(10 * SIZE, 10 * SIZE));
	setLayout(new GridLayout(SIZE, SIZE));
	data = new JDrawingPanel[SIZE][SIZE];
	JDrawingPanel.setColourProvider(colourProvider);

	for (int row = 0; row < SIZE; row++) {
	    for (int col = 0; col < SIZE; col++) {
		data[row][col] = new JDrawingPanel();
		data[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		add(data[row][col]);
	    }
	}

    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
	int newSize = Math.min(width, height);
	super.setBounds(x, y, newSize, newSize);
    }

    /**
     * Adds an item to draw in a particular position
     * 
     * @param coordinates the position of the item
     * @param item the drawable element
     */
    public void addItem(Point coordinates, Drawable item) {
	assertEDT();
	if (coordinates == null || item == null) {
	    throw new IllegalArgumentException("Coordinates and added item cannot be null");
	}
	log.trace("[addItem] New item added @ {}", coordinates);
	data[coordinates.x][coordinates.y].addModel(item);
	data[coordinates.x][coordinates.y].repaint();

    }

    /**
     * @param coordinates the position of the item to remove
     */
    public void removeItem(Point coordinates) {
	assertEDT();
	if (coordinates == null) {
	    throw new IllegalArgumentException("Coordinates cannot be null");
	}
	log.trace("[removeItem] Item removed from {}", coordinates);
	if (data[coordinates.x][coordinates.y].hasModel()) {
	    data[coordinates.x][coordinates.y].removeModel();
	    data[coordinates.x][coordinates.y].repaint();
	}
    }

    /**
     * Changes the position of the item in the specified location
     * 
     * @param oldCoordinates position of the item to move
     * @param newCoordinates position to move the item to
     */
    public void moveItem(Point oldCoordinates, Point newCoordinates) {
	assertEDT();
	if (oldCoordinates == null || newCoordinates == null) {
	    throw new IllegalArgumentException("Coordinates cannot be null");
	}
	if (data[newCoordinates.x][newCoordinates.y].hasModel()) {
	    throw new IllegalStateException(
		    "New position contains a model in the UI already. New position = " + newCoordinates);
	}
	if (!data[oldCoordinates.x][oldCoordinates.y].hasModel()) {
	    throw new IllegalStateException("Old position doesn't contain a model in the UI. Old position = "
		    + oldCoordinates);
	}
	// all good
	Drawable item = data[oldCoordinates.x][oldCoordinates.y].getModel();
	removeItem(oldCoordinates);
	addItem(newCoordinates, item);

    }

    /**
     * Removes all the drawn elements
     */
    public void clear() {
	assertEDT();
	log.debug("[clear] Cleaning board");
	for (int row = 0; row < SIZE; row++) {
	    for (int col = 0; col < SIZE; col++) {
		removeItem(new Point(row, col));
	    }
	}
    }

    private static void assertEDT() {
	assert SwingUtilities.isEventDispatchThread() : "Not in EDT";
    }

    /**
     * Repaints the item specified
     * 
     * @param coordinates location of the item
     */
    public void refresh(Point coordinates) {
	assertEDT();
	if (coordinates == null) {
	    throw new IllegalArgumentException("Coordinates cannot be null");
	}
	data[coordinates.x][coordinates.y].repaint();

    }
}
