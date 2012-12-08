package ca.hilikus.jrobocom.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.exceptions.PlayerException;
import ca.hilikus.jrobocom.gui.ColouredCellRenderer.ColourProvider;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import java.awt.Color;

/**
 * UI to add new teams of robots
 * 
 * @author hilikus
 */
public class NewGameDialog extends JDialog implements ColourProvider {

    private static final String CANCEL = "Cancel";
    private static final String OK = "OK";
    private static final long serialVersionUID = -4348279424477797139L;
    private final JPanel contentPanel = new JPanel();
    private Controller controller = new Controller();
    private int result = -1;
    private DefaultListModel<Player> teamModel;
    private static final Logger log = LoggerFactory.getLogger(NewGameDialog.class);
    private JTextField statusField;
    private JButton btnRemoveTeam;
    private DefaultListModel<String> teamDetailsModel;
    private JList<Player> selectedTeams;
    private Map<Integer, Color> teamsColours = new HashMap<>();
    
    private static int currentColour = 0;
    private static Color[] palette = new Color[] { new Color(0x2F91D8), new Color(0xE57875),
	    new Color(0xFFDE1A), new Color(0xFF7A00), new Color(0x9A61B7), new Color(0xE0332F),
	    new Color(0x8BB1AC), new Color(0x683917) };

    private class Controller implements ActionListener, ListSelectionListener, MouseListener {

	private static final String LAST_DIR = "last_dir";

	private final class RobotFilter extends FileFilter {
	    @Override
	    public boolean accept(File pathname) {
		if (pathname.isDirectory()) {
		    return true;
		}
		if (pathname.getName().endsWith(".jar")) {
		    return true;
		}
		return false;
	    }

	    @Override
	    public String getDescription() {
		return "Robots";

	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    switch (e.getActionCommand()) {
		case OK:
		    NewGameDialog.this.setVisible(false);
		    result = JOptionPane.OK_OPTION;
		    break;
		case CANCEL:
		    NewGameDialog.this.setVisible(false);
		    result = JOptionPane.CANCEL_OPTION;
		    break;
		case "Add":
		    Preferences prefs = Preferences.userNodeForPackage(getClass());
		    String lastDir = prefs.get(LAST_DIR, "");

		    JFileChooser chooser = new JFileChooser(lastDir);
		    chooser.setFileFilter(new RobotFilter());
		    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    if (chooser.showOpenDialog(NewGameDialog.this) == JFileChooser.APPROVE_OPTION) {
			try {
			    Player newPlayer = new Player(chooser.getSelectedFile());
			    teamModel.addElement(newPlayer);
			    teamsColours.put(newPlayer.getTeamId(), getNextColour());
			    changeStatus("Added " + chooser.getSelectedFile() + " successfully");
			    try {
				prefs.put(LAST_DIR, chooser.getSelectedFile().getCanonicalPath());
			    } catch (IOException exc) {
				log.error("[actionPerformed] error saving last path", exc);
			    }
			    log.info("[actionPerformed] Successfully added robot {}",
				    chooser.getSelectedFile());
			} catch (PlayerException exc) {
			    log.error("[actionPerformed] Error creating player", exc);
			    changeStatus(exc.getMessage());
			}
		    }
		    break;
		case "Remove":
		    if (selectedTeams.getSelectedIndex() != -1) {
			teamModel.remove(selectedTeams.getSelectedIndex());
		    }
		    break;
	    }

	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
	    int selection = evt.getFirstIndex();
	    btnRemoveTeam.setEnabled(selection >= 0 && teamModel.size() > 0);
	    if (selection >= 0 && !evt.getValueIsAdjusting() && teamModel.size() > 0) {
		teamDetailsModel.add(0, "Author:\t " + teamModel.get(selection).getAuthor());
		teamDetailsModel.add(1, "Team Name:\t " + teamModel.get(selection).getTeamName());
		teamDetailsModel.add(2, "Banks count:\t " + teamModel.get(selection).getCode().length);
	    } else {
		teamDetailsModel.clear();
	    }

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    if (e.getSource() == selectedTeams) {
		if (e.getClickCount() == 2) {
		    Color ret = JColorChooser.showDialog(NewGameDialog.this, "Choose the team's colour", teamsColours.get(selectedTeams.getSelectedValue().getTeamId()));
		    if (ret != null) {
			teamsColours.put(selectedTeams.getSelectedValue().getTeamId(), ret);
		    }
		    
		}
	    }

	}

	@Override
	public void mousePressed(MouseEvent e) {
	    // nothing to do

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    // nothing to do

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    // nothing to do

	}

	@Override
	public void mouseExited(MouseEvent e) {
	    // nothing to do

	}

    }

    /**
     * Create the dialog.
     * 
     * @param parent the parent container
     */
    public NewGameDialog(JFrame parent) {
	super(parent, true);
	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	setTitle("Select Teams");
	setBounds(100, 100, 450, 300);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);

	JButton btnAddTeam = new JButton("Add...");
	btnAddTeam.setActionCommand("Add");
	btnAddTeam.addActionListener(controller);

	btnRemoveTeam = new JButton("Remove");
	btnRemoveTeam.setEnabled(false);
	btnRemoveTeam.addActionListener(controller);

	selectedTeams = new JList<>();
	selectedTeams.setBorder(new LineBorder(new Color(0, 0, 0)));
	selectedTeams.addListSelectionListener(controller);
	selectedTeams.setCellRenderer(new ColouredCellRenderer(this));
	selectedTeams.addMouseListener(controller);
	teamModel = new DefaultListModel<>();

	selectedTeams.setModel(teamModel);

	JList<String> teamDetails = new JList<>();
	teamDetails.setBorder(new LineBorder(new Color(0, 0, 0)));
	teamDetails.setBackground(UIManager.getColor("Button.background"));

	teamDetailsModel = new DefaultListModel<>();
	teamDetails.setModel(teamDetailsModel);

	JLabel lblSelectedTeams = new JLabel("Selected Teams");

	JLabel lblDetails = new JLabel("Details");

	statusField = new JTextField();
	changeStatus("Status");
	statusField.setEditable(false);
	statusField.setColumns(10);
	GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
	gl_contentPanel.setHorizontalGroup(gl_contentPanel
		.createParallelGroup(Alignment.LEADING)
		.addGroup(
			gl_contentPanel
				.createSequentialGroup()
				.addGroup(
					gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
							gl_contentPanel.createSequentialGroup().addGap(122)
								.addComponent(btnAddTeam))
						.addGroup(
							gl_contentPanel
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(selectedTeams,
									GroupLayout.DEFAULT_SIZE, 186,
									Short.MAX_VALUE)))
				.addGroup(
					gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
							gl_contentPanel.createSequentialGroup().addGap(27)
								.addComponent(btnRemoveTeam))
						.addGroup(
							gl_contentPanel
								.createSequentialGroup()
								.addGap(18)
								.addComponent(teamDetails,
									GroupLayout.DEFAULT_SIZE, 210,
									Short.MAX_VALUE))).addContainerGap())
		.addGroup(
			gl_contentPanel.createSequentialGroup().addGap(36).addComponent(lblSelectedTeams)
				.addPreferredGap(ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
				.addComponent(lblDetails).addGap(93))
		.addComponent(statusField, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE));
	gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
		gl_contentPanel
			.createSequentialGroup()
			.addContainerGap()
			.addGroup(
				gl_contentPanel.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnAddTeam).addComponent(btnRemoveTeam))
			.addGap(7)
			.addGroup(
				gl_contentPanel.createParallelGroup(Alignment.BASELINE)
					.addComponent(lblSelectedTeams).addComponent(lblDetails))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(
				gl_contentPanel
					.createParallelGroup(Alignment.LEADING)
					.addComponent(selectedTeams, GroupLayout.DEFAULT_SIZE, 124,
						Short.MAX_VALUE)
					.addComponent(teamDetails, GroupLayout.DEFAULT_SIZE, 124,
						Short.MAX_VALUE))
			.addGap(18)
			.addComponent(statusField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)));
	contentPanel.setLayout(gl_contentPanel);
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		JButton okButton = new JButton(OK);
		okButton.setActionCommand(OK);
		buttonPane.add(okButton);
		okButton.addActionListener(controller);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton cancelButton = new JButton(CANCEL);
		cancelButton.setActionCommand(CANCEL);
		cancelButton.addActionListener(controller);
		buttonPane.add(cancelButton);
	    }
	}
    }

    private void changeStatus(String status) {
	statusField.setText(status);
	statusField.setToolTipText(status);

    }

    /**
     * Returns the reason for closing the dialog
     * 
     * @return {@link JOptionPane#OK_OPTION} if the user pressed OK; otherwise
     *         {@link JOptionPane#CANCEL_OPTION}
     */
    int getResult() {
	return result;
    }

    /**
     * @return a list of paths to the players' code selected. If the dialog was cancelled, this
     *         returns null
     */
    List<Player> getSelectedTeams() {
	if (result != JOptionPane.OK_OPTION) {
	    return null;
	}
	List<Player> teams = new ArrayList<>();
	for (int pos = 0; pos < teamModel.getSize(); pos++) {
	    teams.add(teamModel.getElementAt(pos));
	}
	return teams;
    }

    Map<Integer, Color> getColourMappings() {
	return teamsColours;
    }

    @Override
    public Color getTeamColour(int teamId) {
	return teamsColours.get(teamId);
    }
    
    private static Color getNextColour() {
	Color ret = palette[currentColour];
	currentColour = (currentColour + 1) % palette.length;

	return ret;
    }
}
