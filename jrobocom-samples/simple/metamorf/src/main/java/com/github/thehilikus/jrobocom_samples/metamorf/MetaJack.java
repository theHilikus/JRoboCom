package com.github.thehilikus.jrobocom_samples.metamorf;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.ReadableSettings;
import com.github.thehilikus.jrobocom.player.ScanResult;

/**
 * Jack program
 * 
 * @author Dennis C. Bemmann
 */
public class MetaJack extends Bank {

    private int fields;

    @Override
    public void run() throws BankInterruptedException {
	fields = ReadableSettings.FIELDS;

	while (true) {
	    control.move();
	    info.setRemoteActiveState(0); // deactivate potential enemy on field ahead.

	    ScanResult result = control.scan(); // Scan the field ahead.
	    if (!result.isEmpty()) { // Is it an empty field?
		examine(result); // Nope. Examine!
	    } else {
		fields--; // Decrement field counter
		if (fields == 0) {
		    changeRow();
		}
	    }
	}

    }

    private void examine(ScanResult scan) {
	if (scan.isEnemy()) {
	    control.transfer(1, 0); // transfer empty bank 2 to enemy
	}

	info.setRemoteActiveState(1); // Re-Activate

	changeRow();

    }

    private void changeRow() {
	control.turn(true);
	control.move();
	control.turn(false);

	fields = ReadableSettings.FIELDS;
    }

}
