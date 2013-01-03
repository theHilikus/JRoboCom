package ca.hilikus.jrobocom.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ca.hilikus.jrobocom.Player;

public class TeamsTableRenderer extends DefaultTableCellRenderer {
    private ColourInfoProvider colourProvider;
    private boolean disableSelection;

    public TeamsTableRenderer(ColourInfoProvider provider) {
	colourProvider = provider;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	    int row, int column) {
	Component comp = super.getTableCellRendererComponent(table, value, disableSelection ? false : isSelected,
		disableSelection ? false : hasFocus, row, column);

	if (column == 0) {
	    assert value instanceof Player;
	    Player player = (Player) value;
	    setValue(player.getTeamName());
	    comp.setBackground(colourProvider.getTeamColour(player.getTeamId()));
	}

	return comp;
    }

    public void setDisableSelection(boolean disable) {
	disableSelection = disable;
    }

    public boolean isSelectionDisabled() {
	return disableSelection;
    }

}
