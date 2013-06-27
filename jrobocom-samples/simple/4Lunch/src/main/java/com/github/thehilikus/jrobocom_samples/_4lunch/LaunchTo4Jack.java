package com.github.thehilikus.jrobocom_samples._4lunch;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * Launcher -> 4Jack
 *
 * @author Dennis C. Bemmann
 */
public class LaunchTo4Jack extends Bank {

    /**
     * @param pTeamId id assigned by the game
     */
    public LaunchTo4Jack(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	control.changeBank(1);

    }

}
