package ca.hilikus.jrobocom.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ca.hilikus.jrobocom.Player;

public class ColouredCellRenderer extends JLabel implements ListCellRenderer<Player> {

    private ColourInfoProvider colourProvider;

    
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
