package ca.hilikus.jrobocom.player;

import org.apache.log4j.Logger;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.ScanResult;


public class PlayerOperationsTests extends Bank {

	private Logger log = Logger.getLogger(PlayerOperationsTests.class);
	
	@Override
	public void run() {
		ScanResult res = control.scan();
		log.debug("Scanned: " + res);
		
		control.move();
		
		
	}

}
