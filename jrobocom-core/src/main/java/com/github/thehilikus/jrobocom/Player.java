/**
 * 
 */
package com.github.thehilikus.jrobocom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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

import com.github.thehilikus.jrobocom.exceptions.PlayerException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.robot.Robot;

/**
 * Encapsulates each team in the game
 * 
 * @author hilikus
 */
public class Player {

    static final int DEFAULT_START_STATE = 1;

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

    private boolean leader = false;

    /**
     * The common parent of all player thread-groups
     */
    public final static ThreadGroup PLAYERS_GROUP = new ThreadGroup("Players' common ancestor");

    private URLClassLoader loader;

    /**
     * Internal constructor
     * 
     * @param pLoader the loader used to import the code into the game
     * @param codePath file system path of the location to load from
     * @throws PlayerException if there's an exception loading the player's code
     */
    Player(URLClassLoader pLoader, String codePath) throws PlayerException {
	loader = pLoader;

	if (Thread.currentThread().getPriority() < ROBOTS_MAX_PRIORITY) {
	    throw new PlayerException("Robot priority cannot be greater than game's");
	}

	try {
	    InputStream stream = loader.getResourceAsStream(PLAYER_PROPERTIES_FILE);

	    if (stream == null) {
		throw new PlayerException("Player Specification File not found: " + codePath + "!"
			+ PLAYER_PROPERTIES_FILE);

	    } else {
		Properties playerInfo = new Properties();
		playerInfo.load(stream);

		// read parameters
		author = playerInfo.getProperty("Author", "Unknown developer");

		teamName = playerInfo.getProperty("Team", "Unknown team");

		robotsThreads = new ThreadGroup(PLAYERS_GROUP, teamName + " Threads");
		robotsThreads.setMaxPriority(ROBOTS_MAX_PRIORITY);

		String banksList = playerInfo.getProperty("Banks");

		if (banksList == null || banksList.isEmpty()) {
		    throw new PlayerException("Error loading configuration property: No banks definition found");
		}
		String[] banksClasses = banksList.split(",");

		teamId = getNextTeamId();
		teamIds.add(teamId);

		banks = loadBanks(loader, banksClasses);

		log.info("[Player] Successfully loaded Player: {}. Banks found = {}", this, banksClasses.length);
	    }

	} catch (ClassCastException | IOException | ClassNotFoundException | InstantiationException
		| IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException exc) {
	    closeClassLoader();
	    throw new PlayerException("Error loading player's code", exc);

	} catch (PlayerException exc) {
	    closeClassLoader();
	    throw exc;
	}
    }

    /**
     * Main constructor
     * 
     * @param codePath path to player's code. If ends in '/' it assumes the code is in .class'es in
     *            a directory; otherwise assumes a jar
     * @throws PlayerException if there is a problem loading the code
     */
    public Player(File codePath) throws PlayerException {
	this(classLoaderCreator(codePath), codePath.getAbsolutePath());
    }

    private static URLClassLoader classLoaderCreator(File codePath) throws PlayerException {
	if (!codePath.exists() || !codePath.canRead()) {
	    throw new IllegalArgumentException("Path is invalid: " + codePath
		    + ". It can't be read or it doesn't exist");
	}

	try {
	    return new URLClassLoader(new URL[] { codePath.toURI().toURL() });
	} catch (MalformedURLException exc) {
	    throw new PlayerException("Error loading player's code", exc);
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
	    String className = banksClasses[pos].trim();
	    Class<? extends Bank> bankClass = (Class<? extends Bank>) pLoader.loadClass(className);
	    if (bankClass == null) {
		throw new PlayerException("Bank class \"" + className + "\" was not found in jar");
	    }
	    try {
		playerBanks[pos] = bankClass.getDeclaredConstructor().newInstance();
		playerBanks[pos].setTeamId(teamId);
	    } catch (NoSuchMethodException exc) {
		throw new PlayerException("Player banks need a no-arg constructor", exc);
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
	newRobot.getData().setActiveState(DEFAULT_START_STATE);
	Thread newThread = new Thread(robotsThreads, newRobot, "Bot-" + newRobot.getSerialNumber());
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
	if (playersFiles.size() < 1) {
	    log.warn("[loadPlayers] No players to load");
	}

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
     * Releases the player resources
     */
    public void clean() {
	log.info("[clean] Cleaning player {}", this);

	closeClassLoader();

    }

    private void closeClassLoader() {
	if (loader != null) {
	    try {
		loader.close();
	    } catch (IOException exc) {
		log.error("[clean] Problem closing class loader of " + this, exc);
	    }
	}
    }

    /**
     * @return the leader
     */
    public boolean isLeader() {
	return leader;
    }

    /**
     * @param leader true if the player is one of the leaders now
     */
    public void setLeader(boolean leader) {
	this.leader = leader;
    }

}
