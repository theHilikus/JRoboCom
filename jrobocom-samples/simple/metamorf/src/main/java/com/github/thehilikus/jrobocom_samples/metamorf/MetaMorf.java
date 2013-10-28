package com.github.thehilikus.jrobocom_samples.metamorf;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.player.ScanResult;

/**
 * Morf program
 * 
 * @author Dennis C. Bemmann
 */
public class MetaMorf extends Bank {

    @Override
    public void run() throws BankInterruptedException {
	ScanResult result = control.scan();

	if (!result.isEmpty()) {
	    control.turn(true);
	}

	control.move();
	control.createRobot("Jack", InstructionSet.ADVANCED, 2, true); // build Jack
	control.transfer(1, 0); // Jack prog

	info.setRemoteActiveState(1);

	// get away
	control.turn(true);
	control.move();
	control.turn(false);
    }

}
