package com.github.thehilikus.jrobocom_samples._4lunch;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * Launcher -> 4Jack
 *
 * @author Dennis C. Bemmann
 */
public class LaunchTo4Jack extends Bank {

    @Override
    public void run() {
	control.changeBank(1);

    }

}
