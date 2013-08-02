package com.github.thehilikus.jrobocom_samples._4lunch;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * Launcher -> 4Main
 *
 * @author Dennis C. Bemmann
 */
public class LaunchTo4Main extends Bank {

    @Override
    public void run() {
	control.changeBank(3);
    }

}
