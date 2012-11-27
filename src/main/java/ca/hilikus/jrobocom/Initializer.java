package ca.hilikus.jrobocom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.exceptions.PlayerException;
import ca.hilikus.jrobocom.security.GameSecurityManager;

/**
 * Creates each component and connects them
 * 
 * @author hilikus
 * 
 */
public final class Initializer {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
	System.setSecurityManager(new GameSecurityManager());
	
	launchUI();
	List<String> playersData = new ArrayList<>();
	start(playersData);

    }

    private static void start(List<String> playersData) {
	try {

	    
	    List<Player> players = loadPlayers(playersData);
	    Session testSession = new Session(players);
	    testSession.start();
	} catch (PlayerException exc) {
	    log.error("[main]", exc);
	}
    }

    private static List<Player> loadPlayers(List<String> playersFiles) throws PlayerException {
	log.info("[loadPlayers] Loading all players");
	List<Player> players = new ArrayList<>();

	for (String singlePath : playersFiles) {
	    Player single = new Player(new File(singlePath));
	    players.add(single);
	}

	return players;
    }

    private static void launchUI() {
	// TODO Auto-generated method stub

    }

}
