package com.github.thehilikus.jrobocom_samples.bank_jumper;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * Simple bank that jumps to the next bank. Intended to be a "sitting target"
 *
 * @author hilikus
 */
public class JumpBank extends Bank {
    
    private static int currentBank = 0;

    @Override
    public void run() {
	control.turn(true);
	currentBank = (currentBank + 1) % info.getBanksCount();
	control.changeBank(currentBank);
    }

}
