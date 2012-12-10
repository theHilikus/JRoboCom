package ca.hilikus.jrobocom.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.Session;
import ca.hilikus.jrobocom.gui.events.GameListener;
import ca.hilikus.jrobocom.gui.events.RobotAdded;
import ca.hilikus.jrobocom.gui.events.RobotRemoved;
import ca.hilikus.jrobocom.robot.Robot;

/**
 * The composed UI
 * 
 * @author hilikus
 */
public class GUI implements ColourInfoProvider {

    private JFrame frame;

    private Controller controller = new Controller();


    private JToggleButton tglbtnStart;

    private JSlider speedSlider;

    private JButton btnStep;

    private DefaultListModel<Player> playersModel;

    private BoardPanel board;

    private Map<Integer, Color> teamsColours;

    private Session session;

    private List<Player> players;

    private JButton btnReload;

    /**
     * Event handler in charge of updating the UI and receiving user input
     * 
     */
    public class Controller implements GameListener, ActionListener {

	@Override
	public void update(RobotAdded evt) {
	    Robot source = (Robot) evt.getSource();
	    board.addItem(evt.getCoordinates(), source);

	}

	@Override
	public void update(RobotRemoved evt) {
	    board.removeItem(evt.getCoordinates());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    switch (e.getActionCommand()) {
		case Actions.NEW_GAME:
		    createNewSession();
		    break;
		case Actions.RELOAD:
		    reloadSession();

	    }

	}

    }

    private class Actions {
	public static final String RELOAD = "reload";
	public static final String NEW_GAME = "newGame";
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
	btnReload.setEnabled(isReady);

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

	JButton btnNewGame = new JButton("New game");
	btnNewGame.setActionCommand(Actions.NEW_GAME);
	btnNewGame.addActionListener(controller);

	toolBar.add(btnNewGame);

	btnReload = new JButton("Reload");
	btnReload.setEnabled(false);
	btnReload.setActionCommand(Actions.RELOAD);
	btnReload.addActionListener(controller);
	
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

	board = new BoardPanel(this);
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

    @Override
    public Color getTeamColour(int teamId) {
	return teamsColours.get(teamId);
    }

    private void createNewSession() {
	NewGameDialog newGameDialog = new NewGameDialog(frame);
	newGameDialog.setVisible(true);
	if (newGameDialog.getResult() == JOptionPane.OK_OPTION) {
	    // user pressed ok
	    assert newGameDialog.getSelectedTeams() != null;
	    board.clear();
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

    private void reloadSession() {
	assert players != null && players.size() > 0 : "Players undefined, UI should not allow this action yet";
	board.clear();
	session = new Session(players, controller);

    }

}
