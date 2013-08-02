package com.github.thehilikus.jrobocom_samples.blackjacks;

import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.InstructionSet;

/**
 * Here the main program is stored
 * 
 * @author Dennis C. Bemmann
 */
public class JackBuilder extends Bank {

    @Override
    public void run() {
	while (true) {
	    control.createRobot("Jack", InstructionSet.ADVANCED, 2, true); // Create mobile robot
									   // with adv. instr. and 2
									   // banks.
	    control.transfer(1, 0); // Transfer program from bank 2 into new robot.
	    control.transfer(2, 1); // Transfer BlackBank into new robot.
	    info.setRemoteActiveState(1); // Activate new robot.
	    control.turn(true); // turn right
	}
    }

}
