package com.github.thehilikus.jrobocom;

/**
 * Provides information about the worl
 * 
 * @author hilikus
 * 
 */
public interface WorldInfo {

    /**
     * Number of friend or foe robots
     * 
     * @param teamId id of the team to search for
     * @param invert if true, returns number of robots not in the specified team
     * @return the number of robots in the specified group
     */
    public int getBotsCount(int teamId, boolean invert);

    /**
     * @return number of cycles elapsed / 1000
     */
    public int getWorldAge();

}