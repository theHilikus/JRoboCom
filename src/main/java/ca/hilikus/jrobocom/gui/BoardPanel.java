package ca.hilikus.jrobocom.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

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

    private JPanel[][] data;

    private static final Logger log = LoggerFactory.getLogger(BoardPanel.class);

    /**
     * Main constructor
     */
    public BoardPanel() {
	setMinimumSize(new Dimension(10 * SIZE, 10 * SIZE));
	setLayout(new GridLayout(SIZE, SIZE));
	data = new JPanel[SIZE][SIZE];

	for (int row = 0; row < SIZE; row++) {
	    for (int col = 0; col < SIZE; col++) {
		data[row][col] = new JPanel();
		data[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		add(data[row][col]);
	    }
	}

    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	/*Graphics2D g2 = (Graphics2D) g;

	int canvasSize = Math.min(getWidth(), getHeight());

	g2.setStroke(new BasicStroke(gridThickness));
	int separation = Math.round((canvasSize) / (float)SIZE);
	int xOffset = 5;
	for (int pos = 0; pos < SIZE+1; pos++) {
	    if (pos == 0 || pos == SIZE) {
		//outter line
		g2.setStroke(new BasicStroke(gridThickness*3));
	    } else {
		g2.setStroke(new BasicStroke(gridThickness));
	    }
	    g.drawLine(separation * pos + xOffset, 0, separation * pos + xOffset, canvasSize); //vertical
	    g.drawLine(xOffset, separation * pos, canvasSize + xOffset, separation * pos); //horizontal
	}*/
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
	int newSize = Math.min(width, height);
	super.setBounds(x, y, newSize, newSize);
    }

    public void addRobot(Point coordinates, Color teamColour) {
	// TODO Auto-generated method stub
	
    }

    public void removeRobot(Point coordinates) {
	// TODO Auto-generated method stub
	
    }
}
