package com.github.thehilikus.jrobocom.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.github.thehilikus.jrobocom.Player;

/**
 * List renderer where the background of the cell matches the Player's colour
 * 
 * @author hilikus
 */
public class ColouredCellRenderer extends JLabel implements ListCellRenderer<Player> {

    private static final long serialVersionUID = 7166052045180116950L;
    private ColourInfoProvider colourProvider;

    /**
     * Constructs a renderer
     * 
     * @param provider object to query to get colour information
     */
    public ColouredCellRenderer(ColourInfoProvider provider) {
	setOpaque(true);

	colourProvider = provider;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Player> list, Player value, int index,
	    boolean isSelected, boolean cellHasFocus) {
	setText(value.getTeamName());

	setBackground(colourProvider.getTeamColour(value.getTeamId()));
	return this;
    }

}
