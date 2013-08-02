package com.github.thehilikus.jrobocom_samples._4lunch;

import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.ReadableSettings;
import com.github.thehilikus.jrobocom.player.ScanResult;
import com.github.thehilikus.jrobocom.player.ScanResult.Found;

/**
 * 4Jack
 * 
 * @author Dennis C. Bemmann
 */
public class _4Jack extends Bank {

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
