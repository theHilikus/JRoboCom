/**
 * 
 */
package ca.hilikus.jrobocom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.exceptions.PlayerException;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.robot.Robot;

/**
 * Encapsulates each team in the game
 * 
 * @author hilikus
 */
public class Player {

    private static final String PLAYER_PROPERTIES_FILE = System.getProperty("jrobocom.player-properties",
	    "player.properties");

    private final String teamName;

    private final String author;

    private final Bank[] banks;

    private static final Logger log = LoggerFactory.getLogger(Player.class);

    private static final int ROBOTS_MAX_PRIORITY = 3;

    private final ThreadGroup robotsThreads;

    private final int teamId;

    private static Set<Integer> teamIds = new HashSet<>();

    /**
     * The common parent of all player thread-groups
     */
    public final static ThreadGroup PLAYERS_GROUP = new ThreadGroup("Players' common ancestor");

    private URLClassLoader loader;

    /**
     * @param codePath path to player's code. If ends in '/' it assumes the code is in .class'es in
     *            a directory; otherwise assumes a jar
     * @throws PlayerException if there is a problem loading the code
     */
    public Player(File codePath) throws PlayerException {
	if (!codePath.exists() || !codePath.canRead()) {
	    throw new IllegalArgumentException("Path is invalid: " + codePath
		    + ". It can't be read or it doesn't exist");
	}

	if (Thread.currentThread().getPriority() < ROBOTS_MAX_PRIORITY) {
	    throw new PlayerException("Robot priority cannot be greater than game's");
	}

	try {
	    loader = new URLClassLoader(new URL[] { codePath.toURI().toURL() });
	    InputStream stream = loader.getResourceAsStream(PLAYER_PROPERTIES_FILE);

	    if (stream == null) {
		throw new PlayerException("Player Specification File not found: " + codePath + "!"
			+ PLAYER_PROPERTIES_FILE);

	    } else {
		Properties playerInfo = new Properties();
		playerInfo.load(stream);

		// read parameters
		author = playerInfo.getProperty("Author", "Unknown programmer");

		teamName = playerInfo.getProperty("Team", "Unknown team");

		robotsThreads = new ThreadGroup(PLAYERS_GROUP, teamName + " Threads");
		robotsThreads.setMaxPriority(ROBOTS_MAX_PRIORITY);

		String banksList = playerInfo.getProperty("Banks");

		if (banksList == null) {
		    throw new PlayerException(
			    "Error loading configuration property: No banks definition found");
		}
		String[] banksClasses = banksList.split(",");

		teamId = getNextTeamId();
		teamIds.add(teamId);

		banks = loadBanks(loader, banksClasses);

		log.info("[Player] Successfully loaded Player: {}. Banks found = {}", this,
			banksClasses.length);
	    }

	} catch (ClassCastException | IOException | ClassNotFoundException | InstantiationException
		| IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| SecurityException exc) {
	    throw new PlayerException("Error loading player's code", exc);

	} catch (PlayerException exc) {

	    try {
		loader.close();
	    } catch (IOException exc1) {
		log.error("[Player] problem closing loader", exc1);
	    }
	    throw exc;

	}

    }

    private static int getNextTeamId() {
	int potentialTeamId;
	do {
	    Random generator = new Random();
	    potentialTeamId = generator.nextInt(1000);

	} while (teamIds.contains(potentialTeamId));
	// found good one
	return potentialTeamId;
    }

    private Bank[] loadBanks(SecureClassLoader pLoader, String[] banksClasses) throws ClassNotFoundException,
	    InstantiationException, IllegalAccessException, ClassCastException, PlayerException,
	    IllegalArgumentException, InvocationTargetException, SecurityException {
	log.debug("[loadCode] Attempting to load code for {}", teamName);
	if (banksClasses.length == 0) {
	    throw new PlayerException("Error loading configuration property: No banks found");
	}

	Bank[] playerBanks = new Bank[banksClasses.length];
	for (int pos = 0; pos < playerBanks.length; pos++) {
	    Class<? extends Bank> bankClass = (Class<? extends Bank>) pLoader.loadClass(banksClasses[pos]
		    .trim());
	    try {
		playerBanks[pos] = bankClass.getDeclaredConstructor(int.class).newInstance(teamId);
	    } catch (NoSuchMethodException exc) {
		log.error(
			"[loadBanks] Player banks need a constructor(int) to be able to assign a teamId to it",
			exc);
		throw new PlayerException(
			"Player banks need a constructor(int) to be able to assign a teamId to it", exc);
	    }
	}

	return playerBanks;

    }

    /**
     * @return all the banks in the player's code
     */
    public Bank[] getCode() {
	return banks;
    }

    /**
     * @return the name of the player's team
     */
    public String getTeamName() {
	return teamName;
    }

    @Override
    public String toString() {
	return author + " (" + teamName + ")";
    }

    /**
     * Starts the thread of a robot in the player's thread group
     * 
     * @param newRobot the robot to start
     */
    public void startRobot(Robot newRobot) {
	Thread newThread = new Thread(robotsThreads, newRobot, "Robot " + newRobot.getSerialNumber());
	newThread.start(); // jumpstarts the robot

    }

    /**
     * Creates a list of players using the paths provided
     * 
     * @param playersFiles list of paths (jars or dirs) to the players code
     * @return list of all created players
     * @throws PlayerException if there was a problem loading one of the players
     */
    public static List<Player> loadPlayers(List<String> playersFiles) throws PlayerException {
	log.info("[loadPlayers] Loading all players");
	List<Player> players = new ArrayList<>();

	for (String singlePath : playersFiles) {
	    Player single = new Player(new File(singlePath));
	    players.add(single);
	}

	return players;
    }

    /**
     * @return the robot's creator
     */
    public String getAuthor() {
	return author;
    }

    /**
     * @return the unique team identification for this player
     */
    public int getTeamId() {
	return teamId;
    }

    /**
     * Called when all the robots of the player are finished
     */
    public void clean() {
	log.info("[clean] Cleaning player {}", this);
	if (loader != null) {
	    try {
		loader.close();
	    } catch (IOException exc) {
		log.error("[clean] Problem closing class loader of " + this, exc);
	    }
	}
    }

}
