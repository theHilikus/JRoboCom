package ca.hilikus.jrobocom.player;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.ScanResult;


public class PlayerOperationsTests extends Bank {

	public PlayerOperationsTests() {
	super(321);
    }

	private Logger log = LoggerFactory.getLogger(PlayerOperationsTests.class);
	
	@Override
	public void run() {
		ScanResult res = control.scan();
		log.debug("Scanned: " + res);
		
		control.move();
		
		
	}

}
