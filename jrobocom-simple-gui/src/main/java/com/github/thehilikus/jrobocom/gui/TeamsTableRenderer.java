package com.github.thehilikus.jrobocom.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.thehilikus.jrobocom.Player;

/**
 * A table renderer that uses the player's colour as the cell's background
 * 
 * @author hilikus
 */
public class TeamsTableRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1960919042086609622L;
    private ColourInfoProvider colourProvider;
    private boolean disableSelection;

    /**
     * Constructs a renderer with a colour provider
     * 
     * @param provider the provider of colour information for the players
     */
    public TeamsTableRenderer(ColourInfoProvider provider) {
	colourProvider = provider;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	    int row, int column) {
	Component comp = super.getTableCellRendererComponent(table, value, disableSelection ? false : isSelected,
		disableSelection ? false : hasFocus, row, column);

	if (value instanceof Player) {
	    assert column == 0;
	    Player player = (Player) value;
	    setValue(player.getTeamName());
	    comp.setBackground(colourProvider.getTeamColour(player.getTeamId()));
	    if (player.isLeader()) {
		comp.setFont(comp.getFont().deriveFont(Font.BOLD));
	    }
	}

	return comp;
    }

    /**
     * Sets whether selected cells look the same as unselected
     * 
     * @param disable true if selection is disabled when rendering
     */
    public void setDisableSelection(boolean disable) {
	disableSelection = disable;
    }

}
