package com.github.thehilikus.jrobocom_samples.megamorf;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;

/**
 * Morf program
 * 
 * @author Dennis C. Bemmann
 */
public class Morf extends Bank {

    @Override
    public void run() throws BankInterruptedException {
	control.move();
	control.move();

	control.createRobot("MegaJack", InstructionSet.ADVANCED, 2, true);
	control.transfer(1, 0); // megaJack prog
	info.setRemoteActiveState(1);

	control.turn(true);
	control.move();

	control.createRobot("Morf clone", InstructionSet.SUPER, 2, true); // build a clone...
									  // another Morf!
	control.transfer(0, 0);
	control.transfer(1, 1);
	info.setRemoteActiveState(1); //activate clone

	control.turn(false);
	
	//auto-reboot since there are no further instructions

    }

}
