package ca.hilikus.jrobocom.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.Session;
import ca.hilikus.jrobocom.events.GameListener;
import ca.hilikus.jrobocom.events.ResultEvent;
import ca.hilikus.jrobocom.events.ResultEvent.Result;
import ca.hilikus.jrobocom.events.RobotAddedEvent;
import ca.hilikus.jrobocom.events.RobotChangedEvent;
import ca.hilikus.jrobocom.events.RobotMovedEvent;
import ca.hilikus.jrobocom.events.RobotRemovedEvent;
import ca.hilikus.jrobocom.gui.drawables.DrawableRobot;
import ca.hilikus.jrobocom.gui.panels.BoardPanel;
import ca.hilikus.jrobocom.gui.panels.NewGameDialog;
import ca.hilikus.jrobocom.robot.Robot;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * The composed UI
 * 
 * @author hilikus
 */
public class GUI implements ColourInfoProvider {

    private JFrame frame;

    private Controller controller = new Controller();


    private JSlider speedSlider;


    private DefaultListModel<Player> playersModel;

    private BoardPanel board;

    private Map<Integer, Color> teamsColours;

    private Session session;

    private List<Player> players;


    private static final Logger log = LoggerFactory.getLogger(GUI.class);

    private UIAction startAction;

    private UIAction stepAction;

    private UIAction reloadAction;

    /**
     * Event handler in charge of updating the UI
     * 
     */
    public class Controller implements GameListener {
	private Map<Robot, Point> robots;

	@Override
	public void update(final RobotAddedEvent evt) {
	    final Robot source = evt.getSource();
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    board.addItem(evt.getCoordinates(), new DrawableRobot(source));
		}
	    });

	    if (robots == null) { // lazy init
		robots = new HashMap<>();
	    }
	    robots.put(source, evt.getCoordinates());
	}

	@Override
	public void update(final RobotRemovedEvent evt) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    board.removeItem(evt.getCoordinates());
		}
	    });

	    if (robots == null) { // lazy init
		robots = new HashMap<>();
	    }
	    robots.remove(evt.getSource());
	}

	@Override
	public void update(final RobotChangedEvent evt) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    board.refresh(robots.get(evt.getSource()));
		}
	    });

	}

	@Override
	public void update(final RobotMovedEvent mov) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    board.moveItem(mov.getOldPosition(), mov.getNewPosition());
		}
	    });

	    robots.put(mov.getSource(), mov.getNewPosition());
	}

	@Override
	public void update(ResultEvent result) {
	    displayResult(result);

	}

    }

    private class UIAction extends AbstractAction {

	private static final long serialVersionUID = 2865727921724644884L;

	public UIAction(String actionName) {
	    super(actionName);
	    putValue(ACTION_COMMAND_KEY, actionName);
	    putValue(SHORT_DESCRIPTION, getDescription(actionName));
	    putValue(MNEMONIC_KEY, getMnemonic(actionName));
	    
	    if (ActionNames.NEW_GAME.equals(actionName)) {
		setEnabled(true);
	    } else {
		setEnabled(false);
	    }
	}

	private int getMnemonic(String actionName) {
	    switch (actionName) {
		case ActionNames.NEW_GAME:
		    return KeyEvent.VK_N;
		case ActionNames.RELOAD:
		    return KeyEvent.VK_R;
		case ActionNames.STEP:
		    return KeyEvent.VK_T;
		case ActionNames.START:
		    return KeyEvent.VK_S;
	    }
	    return -1;
	}

	private String getDescription(String actionName) {
	    switch (actionName) {
		case ActionNames.NEW_GAME:
		    return "Configures a new session";
		case ActionNames.RELOAD:
		    return "Starts a new session with the same robots";
		case ActionNames.STEP:
		    return "Executes a single clock of the Master clock";
		case ActionNames.START:
		    return "Starts or stops the Master clock";
	    }
	    return "n/a";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    switch (e.getActionCommand()) {
		case ActionNames.NEW_GAME:
		    createNewSession();
		    break;
		case ActionNames.RELOAD:
		    reloadSession();
		    break;
		case ActionNames.STEP:
		    singleStep();
		    break;
		case ActionNames.START:
		    start(!((JToggleButton) e.getSource()).isSelected());

	    }

	}

    }

    private class ActionNames {
	public static final String RELOAD = "Reload";
	public static final String NEW_GAME = "New Game";
	public static final String STEP = "Step";
	public static final String START = "Start";
    }

    /**
     * @param args ignored
     */
    public static void main(String[] args) {

	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		@SuppressWarnings("unused")
		GUI view = new GUI("JRobotCom");

	    }
	});

    }

    /**
     * Create the application.
     * 
     * @param title the name of the frame
     */
    public GUI(String title) {
	initialize(title);
    }

    private void singleStep() {
	session.step();
    }

    private void start(boolean pause) {
	if (pause) {
	    assert session.isRunning();
	    session.stop();
	} else {
	    assert !session.isRunning();
	    session.start();
	}
    }

    /**
     * Changes the UI depending on whether the session is ready or not
     * 
     * @param isReady true if session was configured correctly; false otherwise
     */
    public void sessionReady(boolean isReady) {
	if (isReady) {
	    speedSlider.setValue(session.getClockPeriod());
	}
	startAction.setEnabled(isReady);
	speedSlider.setEnabled(isReady);
	stepAction.setEnabled(isReady);
	reloadAction.setEnabled(isReady);

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

	UIAction newAction = new UIAction(ActionNames.NEW_GAME);
	JButton btnNewGame = new JButton(newAction);
	btnNewGame.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"),
		ActionNames.NEW_GAME);
	btnNewGame.getActionMap().put(ActionNames.NEW_GAME, newAction);


	toolBar.add(btnNewGame);

	reloadAction = new UIAction(ActionNames.RELOAD);
	JButton btnReload = new JButton(reloadAction);
	//btnReload.setEnabled(false);
	btnReload.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"),
		ActionNames.RELOAD);
	btnReload.getActionMap().put(ActionNames.RELOAD, reloadAction);

	toolBar.add(btnReload);
	toolBar.addSeparator();

	startAction = new UIAction(ActionNames.START);
	JToggleButton tglbtnStart = new JToggleButton(startAction);
	//tglbtnStart.setEnabled(false);
	toolBar.add(tglbtnStart);

	speedSlider = new JSlider();
	speedSlider.setInverted(true);
	speedSlider.setMinimum(MasterClock.MIN_PERIOD);
	speedSlider.setMaximum(1000);
	speedSlider.setEnabled(false);
	speedSlider.setMaximumSize(new Dimension(100, 16));
	toolBar.add(speedSlider);

	stepAction = new UIAction(ActionNames.STEP);
	JButton btnStep = new JButton(stepAction);
	//btnStep.setEnabled(false);
	btnStep.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"),
		ActionNames.STEP);
	btnStep.getActionMap().put(ActionNames.STEP, stepAction);
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

	assertEDT();
	frame.setVisible(true);
    }

    private static void assertEDT() {
	assert SwingUtilities.isEventDispatchThread() : "Not in EDT";
    }

    /**
     * Changes the title of the application
     * 
     * @param title new window title
     */
    public void setTitle(String title) {
	assertEDT();
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
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		board.clear();
	    }
	});

	session = new Session(players, controller);

    }

    private void displayResult(ResultEvent result) {
	String title;
	String msg;
	Icon icon = null;
	if (result.getResult() == Result.DRAW) {
	    title = "Draw";
	    msg = "There were no winners in this run";
	    icon = loadIcon("/images/draw.jpg"); // TODO: find draw image
	} else {
	    title = "And the Winner is...";
	    msg = "The winner is " + result.getWinner().getAuthor() + " with "
		    + result.getWinner().getTeamName();
	    icon = loadIcon("/images/cup.jpg");
	}

	JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.INFORMATION_MESSAGE, icon);
    }

    private Icon loadIcon(String path) {
	URL imgURL = getClass().getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    log.warn("[loadIcon] Could not load icon from {}", path);
	    return null;
	}
    }

}
