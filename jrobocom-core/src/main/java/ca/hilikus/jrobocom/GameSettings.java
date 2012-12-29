package ca.hilikus.jrobocom;

import ca.hilikus.jrobocom.security.GamePermission;

/**
 * All the game settings
 * 
 * @author hilikus
 * 
 */
public final class GameSettings {

    private static final String PROPERTY_PREFIX = "ca.hilikus.jrobocom.settings.";

    private static final GameSettings instance = new GameSettings();

    private GameSettings() {
	// block instantiation
    }

    /**
     * Timing delays
     * 
     */
    public final static class Timing {

	private static final Timing theInstance = new Timing();

	private Timing() {
	    // block instantiation
	}

	/**
	 * @return the instance with all the settings as constants
	 */
	public static Timing getInstance() {
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) {
		sm.checkPermission(new GamePermission("readSettings"));
	    }
	    return theInstance;
	}

	/**
	 * cycles to interface with remote entities
	 */
	public final int REMOTE_ACCESS_PENALTY = Integer.getInteger(PROPERTY_PREFIX + "timing.remoteAccess", 6); // durRemoteAcc
	/**
	 * cycles to read local data
	 */
	public final int LOCAL_READ = Integer.getInteger(PROPERTY_PREFIX + "timing.localRead", 1);
	/**
	 * cycles to write local data
	 */
	public final int LOCAL_WRITE = Integer.getInteger(PROPERTY_PREFIX + "timing.localWrite", 2);
	/**
	 * fixed cycles when transferring
	 */
	public final int TRANSFER_BASE = Integer.getInteger(PROPERTY_PREFIX + "timing.transferBase", 2);
	/**
	 * cycles for each complexity unit of a bank
	 */
	public final int TRANSFER_SINGLE = Integer.getInteger(PROPERTY_PREFIX + "timing.transferSingle", 1);
	/**
	 * fixed cycles for creating a robot
	 */
	public final int CREATION_BASE = 5;
	/**
	 * cycles for each extra bank in the new robot
	 */
	public final int CREATION_PER_BANK = 2;
	/**
	 * extra cycles coefficient for creating a mobile robot
	 */
	public final double MOBILITY_PENALTY = 1.5;
	/**
	 * fixed cycles for creating a mobile robot
	 */
	public final int MOBILITY_CONSTANT = 4;
	/**
	 * extra cycles for creating a robot with the advanced instruction set
	 */
	public final int ADVANCED_SET_PENALTY = 3;
	/**
	 * extra cycles for creating a robot with the super instruction set
	 */
	public final int SUPER_SET_PENALTY = 7;
	/**
	 * cycles to move
	 */
	public final int MOVE = 1;
	/**
	 * cycles to change banks
	 */
	public final int BANK_CHANGE = 2;
	/**
	 * fixed cycles to scan
	 */
	public final int SCAN_BASE = 3;
	/**
	 * cycles for every extra field to scan
	 */
	public final int SCAN_PER_DIST = 1;
	/**
	 * cycles to turn
	 */
	public final int TURN = 1;
    }

    /**
     * maximum cycles when creating a robot
     */
    public final int MAX_CREATE_WAIT = 60;
    /**
     * maximum number of banks in a robot
     */
    public final int MAX_BANKS = 50;
    /**
     * maximum generation before new robots start dying
     */
    public final int MAX_GENERATION = 15;
    /**
     * number of fields in one direction of the board
     */
    public final int BOARD_SIZE = 18;
    /**
     * maximum age of a robot before it dies
     */
    public final int MAX_AGE = 1000;
    /**
     * maximum number of robots in a single board
     */
    public final int MAX_BOTS = (int) Math.round(BOARD_SIZE * BOARD_SIZE * 0.95);
    /**
     * maximum age for the world before the game ends
     */
    public final int MAX_WORLD_AGE = 10000000;

    /**
     * Checks if the caller has permission to read the game settings
     * 
     * @return the instance with all the settings as constants
     */
    public static GameSettings getInstance() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("readSettings"));
	}
	return instance;
    }

}
