package ca.hilikus.jrobocom_samples.blackjacks;

import ca.hilikus.jrobocom.player.Bank;

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
