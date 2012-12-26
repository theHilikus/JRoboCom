package ca.hilikus.jrobocom_samples._4lunch;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.ReadableSettings;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;

/**
 * 4Jack
 * 
 * @author Dennis C. Bemmann
 */
public class _4Jack extends Bank {

    public _4Jack(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	control.turn(false);
	while (true) {
	    int column = 0;
	    do {
		control.move();
		ScanResult scan = control.scan();
		info.setRemoteActiveState(0);
		if (scan.getResult() != Found.EMPTY) {
		    control.transfer(2, 0);
		}
		info.setRemoteActiveState(1);
		column++;

	    } while (column != ReadableSettings.FIELDS);

	    // change row
	    control.turn(true);
	    control.move();
	    control.turn(false);
	}
    }

}
