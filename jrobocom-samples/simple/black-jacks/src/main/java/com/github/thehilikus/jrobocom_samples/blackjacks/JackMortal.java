package com.github.thehilikus.jrobocom_samples.blackjacks;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * The traditional
 *
 * @author Dennis C. Bemmann
 */
public class JackMortal extends Bank {

    /**
     * @param pTeamId
     */
    public JackMortal(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	control.die("Black Bank");
    }

}
