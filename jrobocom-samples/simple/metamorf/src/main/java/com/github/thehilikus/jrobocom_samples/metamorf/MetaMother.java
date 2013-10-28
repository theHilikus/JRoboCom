package com.github.thehilikus.jrobocom_samples.metamorf;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;

/**
 * Mthr
 * 
 * @author Dennis C. Bemmann
 */
public class MetaMother extends Bank {

    @Override
    public void run() throws BankInterruptedException {
	control.createRobot("Morf", InstructionSet.SUPER, 2, true);
	control.transfer(1, 0); // Morf prog
	control.transfer(2, 1); // Jack prog

	info.setRemoteActiveState(1);

	control.turn(false);
    }

}
