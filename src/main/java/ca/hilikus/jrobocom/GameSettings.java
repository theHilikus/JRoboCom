package ca.hilikus.jrobocom;

/**
 * All the game settings
 * 
 * @author hilikus
 * 
 */
public class GameSettings {

    /**
     * Timing delays
     * 
     */
    public class Timing {
	/**
	 * cycles to interface with remote entities
	 */
	public static final int REMOTE_ACCESS_PENALTY = 6; // durRemoteAcc
	/**
	 * cycles to read local data
	 */
	public static final int LOCAL_READ = 1;
	/**
	 * cycles to write local data
	 */
	public static final int LOCAL_WRITE = 2;
	/**
	 * fixed cycles when transferring
	 */
	public static final int TRANSFER_BASE = 0;
	/**
	 * cycles for each complexity unit of a bank
	 */
	public static final int TRANSFER_SINGLE = 0;
	/**
	 * fixed cycles for creating a robot
	 */
	public static final int CREATION_BASE = 0;
	/**
	 * cycles for each extra bank in the new robot
	 */
	public static final int CREATION_PER_BANK = 0;
	/**
	 * extra cycles coefficient for creating a mobile robot
	 */
	public static final int MOBILITY_PENALTY = 0;
	/**
	 * fixed cycles for creating a mobile robot
	 */
	public static final int MOBILITY_CONSTANT = 0;
	/**
	 * extra cycles for creating a robot with the advanced instruction set
	 */
	public static final int ADVANCED_SET_PENALTY = 0;
	/**
	 * extra cycles for creating a robot with the super instruction set
	 */
	public static final int SUPER_SET_PENALTY = 0;
	/**
	 * cycles to move
	 */
	public static final int MOVE = 0;
	/**
	 * cycles to change banks
	 */
	public static final int BANK_CHANGE = 2;
	/**
	 * fixed cycles to scan
	 */
	public static final int SCAN_BASE = 0;
	/**
	 * cycles for every extra field to scan
	 */
	public static final int SCAN_PER_DIST = 0;
	/**
	 * cycles to turn
	 */
	public static final int TURN = 0;
    }

    /**
     * maximum cycles when creating a robot
     */
    public static final int MAX_CREATE_WAIT = 0;
    /**
     * maximum number of banks in a robot
     */
    public static final int MAX_BANKS = 50;
    /**
     * maximum generation before new robots start dying
     */
    public static final int MAX_GENERATION = 15;
    /**
     * number of fields in one direction of the board
     */
    public static final int BOARD_SIZE = 18;
    /**
     * maximum age of a robot before it dies
     */
    public static final int MAX_AGE = 1000;
    /**
     * maximum number of robots in a single board
     */
    public static final int MAX_BOTS = BOARD_SIZE * BOARD_SIZE;
    /**
     * maximum age for the world before the game ends
     */
    public static final int MAX_WORLD_AGE = 0;

}
