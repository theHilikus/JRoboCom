package ca.hilikus.jrobocom.gui;

import java.awt.Color;

/**
 * Interface to give information about colours used
 * 
 * @author hilikus
 */
public interface ColourInfoProvider {
    /**
     * Gets the colour of a particular player
     * 
     * @param teamId the player ID to query
     * @return the colour of the specified team or null if the team doesn't exist
     */
    public Color getTeamColour(int teamId);
}
