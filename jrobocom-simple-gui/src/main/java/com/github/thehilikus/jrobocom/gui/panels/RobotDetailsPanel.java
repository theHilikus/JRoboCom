package com.github.thehilikus.jrobocom.gui.panels;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.Player;
import com.github.thehilikus.jrobocom.exceptions.PlayerException;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.awt.Dimension;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

/**
 * Panel to show details of a player's robot in a file chooser dialog
 * 
 * @author hilikus
 */
public class RobotDetailsPanel extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 6511292611453687468L;
    private static final Logger log = LoggerFactory.getLogger(RobotDetailsPanel.class);
    private DefaultListModel<String> teamDetailsModel;

    /**
     * Constructs a panel and adds it to the chooser
     * 
     * @param chooser the file chooser to attach the panel to
     */
    public RobotDetailsPanel(JFileChooser chooser) {
	setBorder(new TitledBorder(null, "Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	setPreferredSize(new Dimension(240, 155));
	teamDetailsModel = new DefaultListModel<>();

	chooser.addPropertyChangeListener(this);

	JList<String> list = new JList<>();
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.setBorder(new LineBorder(new Color(0, 0, 0)));
	list.setBackground(UIManager.getColor("Button.background"));
	list.setModel(teamDetailsModel);
	add(list);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
	boolean update = false;
	String prop = evt.getPropertyName();
	File file = null;
	if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)
		|| JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
	    update = true;
	    file = (File) evt.getNewValue();
	}

	if (update) {
	    if (isShowing() && (file.isDirectory() || file.getName().toLowerCase().endsWith(".jar"))) {
		showDetails(file);
	    }
	}
    }

    private void showDetails(File file) {
	try {
	    teamDetailsModel.clear();
	    Player potential = new Player(file);
	    teamDetailsModel.add(0, "Author:\t " + potential.getAuthor());
	    teamDetailsModel.add(1, "Team Name:\t " + potential.getTeamName());
	    teamDetailsModel.add(2, "Banks count:\t " + potential.getCode().length);
	    potential.clean(); // since it's for previewing only
	} catch (PlayerException exc) {
	    log.debug("[showDetails] Selected file is not a valid player's code", exc);

	}

    }

}
