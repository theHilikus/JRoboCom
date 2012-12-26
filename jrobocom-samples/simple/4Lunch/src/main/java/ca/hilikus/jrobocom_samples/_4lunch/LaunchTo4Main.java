package ca.hilikus.jrobocom_samples._4lunch;

import ca.hilikus.jrobocom.player.Bank;

/**
 * Launcher -> 4Main
 *
 * @author Dennis C. Bemmann
 */
public class LaunchTo4Main extends Bank {

    public LaunchTo4Main(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	control.changeBank(3);
    }

}
