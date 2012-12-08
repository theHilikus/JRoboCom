package ca.hilikus.jrobocom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.exceptions.PlayerException;
import ca.hilikus.jrobocom.gui.GUI;
import ca.hilikus.jrobocom.security.GameSecurityManager;

/**
 * Creates each component and connects them
 * 
 * @author hilikus
 * 
 */
public final class Initializer {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private static GUI frame;

    /**
     * @param args
     */
    public static void main(String[] args) {
	System.setSecurityManager(new GameSecurityManager());

	List<String> playersData = null;
	if (args.length > 0) {
	    log.info("[main] Initializing with {} main args: {}", args.length, Arrays.toString(args));
	    playersData = new ArrayList<>(Arrays.asList(args));
	    start(playersData);
	} else {
	    launchUI();

	}

    }

    private static void start(List<String> playersData) {
	try {

	    List<Player> players = Player.loadPlayers(playersData);
	    Session session = new Session(players);

	    session.start();

	} catch (PlayerException exc) {
	    log.error("[main]", exc);
	}
    }

    private static void launchUI() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		frame = new GUI("JRobotCom");

	    }
	});

    }

}
