package ca.hilikus.jrobocom_samples._4lunch;

import ca.hilikus.jrobocom.player.Bank;

/**
 * Launcher -> 4Jack
 *
 * @author Dennis C. Bemmann
 */
public class LaunchTo4Jack extends Bank {

    public LaunchTo4Jack(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	control.changeBank(1);

    }

}
