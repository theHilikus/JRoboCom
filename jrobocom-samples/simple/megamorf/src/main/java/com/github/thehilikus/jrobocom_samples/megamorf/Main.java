package com.github.thehilikus.jrobocom_samples.megamorf;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;

/**
 * Main
 * 
 * @author Dennis C. Bemmann
 */
public class Main extends Bank {

    @Override
    public void run() throws BankInterruptedException {
	control.createRobot("Morf", InstructionSet.SUPER, 2, true); // build morf
	control.transfer(1, 0); // morf prog
	control.transfer(2, 1); // megaJack prog
	info.setRemoteActiveState(1); // activate morf
	control.turn(false); // turn left
	
	//auto-reboot since there are no further instructions
    }

}
