package com.github.thehilikus.jrobocom.player;

import com.github.thehilikus.jrobocom.GameSettings;

/**
 * Settings that may be used by players in their banks
 * 
 * @author hilikus
 * 
 */
public class ReadableSettings {

    /**
     * board size
     */
    public static int FIELDS = GameSettings.getInstance().BOARD_SIZE;

    /**
     * maximum number of generations (ancestors)
     */
    public static int MAX_GENERATION = GameSettings.getInstance().MAX_GENERATION;

    /**
     * maximum number of I/O instructions before a robot dies
     */
    public static int MAX_AGE = GameSettings.getInstance().MAX_AGE;

    /**
     * maximum number of robots in each team
     */
    public static int MAX_BOTS_PER_TEAM = GameSettings.getInstance().MAX_BOTS;
    
    /**
     * age of the world at which the game ends or -1 for infinite
     */
    public static int MAX_WORLD_AGE = GameSettings.getInstance().MAX_WORLD_AGE;
}
