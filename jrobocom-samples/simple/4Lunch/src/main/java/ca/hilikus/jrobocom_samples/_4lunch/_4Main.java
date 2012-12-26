package ca.hilikus.jrobocom_samples._4lunch;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;

/**
 * 4Main
 * 
 * @author Dennis C. Bemmann
 */
public class _4Main extends Bank {

    public _4Main(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	while (true) {
	    int children = 0;
	    while (children < 3) {
		control.createRobot("clon", InstructionSet.ADVANCED, 3, true);
		control.transfer(1, 0);
		control.transfer(2, 1);
		info.setRemoteActiveState(1);
		children++;
	    }
	    // Turn after 3 new children
	    control.turn(false);
	}

    }

}
