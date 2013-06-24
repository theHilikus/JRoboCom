package com.github.thehilikus.jrobocom.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.GameSettings;
import com.github.thehilikus.jrobocom.gui.ColourInfoProvider;
import com.github.thehilikus.jrobocom.gui.visitor.Drawable;

/**
 * The playing board UI
 * 
 * @author hilikus
 */
public class BoardPanel extends JPanel {

    private static final long serialVersionUID = -3895988249674025563L;
    private int SIZE = GameSettings.getInstance().BOARD_SIZE;

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
	getPanelAt(coordinates).addModel(item);
	getPanelAt(coordinates).repaint();

    }

    private JDrawingPanel getPanelAt(Point coordinates) {
	return data[coordinates.y][coordinates.x];
    }

    /**
     * @param coordinates the position of the item to remove
     */
    public void removeItem(final Point coordinates) {
	assertEDT();
	if (coordinates == null) {
	    throw new IllegalArgumentException("Coordinates cannot be null");
	}
	log.trace("[removeItem] Item removed from {}", coordinates);
	if (getPanelAt(coordinates).hasModel()) {
	    getPanelAt(coordinates).removeModel();
	    getPanelAt(coordinates).repaint();
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
	if (getPanelAt(newCoordinates).hasModel()) {
	    throw new IllegalStateException(
		    "New position contains a model in the UI already. New position = " + newCoordinates);
	}
	if (!getPanelAt(oldCoordinates).hasModel()) {
	    throw new IllegalStateException("Old position doesn't contain a model in the UI. Old position = "
		    + oldCoordinates);
	}
	// all good
	Drawable item = getPanelAt(oldCoordinates).getModel();
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
		removeItem(new Point(col, row));
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
	getPanelAt(coordinates).repaint();

    }
}
