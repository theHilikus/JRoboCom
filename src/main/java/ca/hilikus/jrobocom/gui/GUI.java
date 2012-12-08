package ca.hilikus.jrobocom.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.Session;
import ca.hilikus.jrobocom.gui.ColouredCellRenderer.ColourProvider;
import ca.hilikus.jrobocom.gui.events.GameListener;
import ca.hilikus.jrobocom.gui.events.RobotAdded;
import ca.hilikus.jrobocom.gui.events.RobotRemoved;
import ca.hilikus.jrobocom.robot.Robot;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * The composed UI
 * 
 * @author hilikus
 */
public class GUI implements ColourProvider {

    private JFrame frame;

    private Controller controller = new Controller();

    private JButton btnNewGame;

    private static final Logger log = LoggerFactory.getLogger(GUI.class);

    private JToggleButton tglbtnStart;

    private JSlider speedSlider;

    private JButton btnStep;

    private DefaultListModel<Player> playersModel;

    private BoardPanel board;

    private Map<Integer, Color> teamsColours;


    /**
     * Event handler in charge of updating the UI and receiving user input
     * 
     */
    public class Controller implements GameListener, ActionListener {

	private Session session;

	@Override
	public void update(RobotAdded evt) {
	    Robot source = (Robot) evt.getSource();
	    board.addRobot(evt.getCoordinates(), teamsColours.get(source.getData().getTeamId()));

	}

	@Override
	public void update(RobotRemoved evt) {
	    board.removeRobot(evt.getCoordinates());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == btnNewGame) {
		NewGameDialog newGameDialog = new NewGameDialog(frame);
		newGameDialog.setVisible(true);
		if (newGameDialog.getResult() == JOptionPane.OK_OPTION) {
		    // user pressed ok
		    assert newGameDialog.getSelectedTeams() != null;
		    List<Player> players;
		    players = newGameDialog.getSelectedTeams();
		    teamsColours = newGameDialog.getColourMappings();
		    session = new Session(players, controller);
		    
		    sessionReady(newGameDialog.getSelectedTeams().size() > 0);
		    playersModel.clear();
		    for (Player player : players) {
			
			playersModel.addElement(player);
		    }

		}
	    }

	}

    }

    /**
     * Create the application.
     * 
     * @param title the name of the frame
     */
    public GUI(String title) {
	initialize(title);
    }

    /**
     * Changes the UI depending on whether the session is ready or not
     * 
     * @param isReady true if session was configured correctly; false otherwise
     */
    public void sessionReady(boolean isReady) {
	tglbtnStart.setEnabled(isReady);
	speedSlider.setEnabled(isReady);
	btnStep.setEnabled(isReady);

    }

    /**
     * Initialize the contents of the frame.
     * 
     * @param title
     */
    private void initialize(String title) {
	frame = new JFrame(title);
	frame.setBounds(100, 100, 773, 669);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(new BorderLayout(0, 0));

	JToolBar toolBar = new JToolBar();
	frame.getContentPane().add(toolBar, BorderLayout.NORTH);

	btnNewGame = new JButton("New game");
	btnNewGame.addActionListener(controller);
	toolBar.add(btnNewGame);

	JButton btnReload = new JButton("Reload");
	toolBar.add(btnReload);
	toolBar.addSeparator();

	tglbtnStart = new JToggleButton("Start");
	tglbtnStart.setEnabled(false);
	toolBar.add(tglbtnStart);

	speedSlider = new JSlider();
	speedSlider.setEnabled(false);
	speedSlider.setMaximumSize(new Dimension(100, 16));
	toolBar.add(speedSlider);

	btnStep = new JButton("Step");
	btnStep.setEnabled(false);
	toolBar.add(btnStep);

	JPanel mainPanel = new JPanel();
	frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

	board = new BoardPanel();
	mainPanel.add(board);
	board.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new BevelBorder(
		BevelBorder.RAISED, null, null, null, null)));

	JPanel rightPanel = new JPanel();
	rightPanel.setPreferredSize(new Dimension(155, 10));
	rightPanel.setMinimumSize(new Dimension(300, 300));
	rightPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
	rightPanel.setMaximumSize(new Dimension(300, 32767));
	mainPanel.add(rightPanel);

	JList<Player> list = new JList<>();
	list.setAlignmentY(Component.TOP_ALIGNMENT);
	list.setBackground(Color.WHITE);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.setMaximumSize(new Dimension(300, 100));
	list.setMinimumSize(new Dimension(300, 100));
	list.setCellRenderer(new ColouredCellRenderer(this));
	playersModel = new DefaultListModel<>();
	list.setModel(playersModel);

	JLabel lblTeams = new JLabel("Teams");
	GroupLayout gl_rightPanel = new GroupLayout(rightPanel);
	gl_rightPanel.setHorizontalGroup(gl_rightPanel
		.createParallelGroup(Alignment.LEADING)
		.addGroup(
			gl_rightPanel.createSequentialGroup()
				.addComponent(list, GroupLayout.PREFERRED_SIZE, 144, Short.MAX_VALUE)
				.addGap(12))
		.addGroup(
			gl_rightPanel.createSequentialGroup().addGap(48)
				.addComponent(lblTeams, GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE)
				.addGap(62)));
	gl_rightPanel.setVerticalGroup(gl_rightPanel.createParallelGroup(Alignment.LEADING).addGroup(
		gl_rightPanel.createSequentialGroup().addGap(9).addComponent(lblTeams)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(list, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(364, Short.MAX_VALUE)));
	rightPanel.setLayout(gl_rightPanel);

	frame.setVisible(true);
    }

    /**
     * Changes the title of the application
     * 
     * @param title new window title
     */
    public void setTitle(String title) {
	frame.setTitle(title);
    }

    /**
     * @return the event manager
     */
    public GameListener getController() {
	return controller;
    }

    /**
     * Changes the team's colour
     * 
     * @param teamId id of the player
     * @param newColour the new colour for the team
     */
    public void setTeamColour(int teamId, Color newColour) {
	teamsColours.put(teamId, newColour);
    }


    @Override
    public Color getTeamColour(int teamId) {
	return teamsColours.get(teamId);
    }

}
