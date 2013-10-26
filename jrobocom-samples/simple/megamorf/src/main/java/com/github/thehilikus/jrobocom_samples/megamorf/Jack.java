package com.github.thehilikus.jrobocom_samples.megamorf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.player.ReadableSettings;
import com.github.thehilikus.jrobocom.player.ScanResult;

/**
 * MegaJack prog
 * 
 * @author Dennis C. Bemmann
 */
public class Jack extends Bank {

    private int fields;
    private static final Logger log = LoggerFactory.getLogger(Jack.class);
    

    @Override
    public void run() throws BankInterruptedException {
	fields = ReadableSettings.FIELDS;

	while (true) { // row work
	    control.move();

	    test();

	    fields--;
	    if (fields == 0) {
		changeRow();
	    }
	}
    }

    private void changeRow() {
	control.turn(true);
	control.move();
	control.turn(false);

	fields = ReadableSettings.FIELDS;
    }

    private void test() {
	info.setRemoteActiveState(0);	// deactivate potential enemy on field ahead.
	ScanResult scan = control.scan();
	if (!scan.isEmpty()) {
	    examine(scan);
	}

    }

    private void examine(ScanResult scan) {
	if (scan.isEnemy()) { // is it a friend?
	    manipulate(); // no. Away with his program!
	} else {
	    // friend
	    info.setRemoteActiveState(1); // re-activate
	    changeRow(); // Better get away: next row!
	}

    }

    private void manipulate() {
	log.info("manipulate start");
	int enemyBanksCount = info.getRemoteBanksCount();

	while (enemyBanksCount > 0) {
	    control.transfer(1, enemyBanksCount - 1);
	    enemyBanksCount--;
	}

	info.setRemoteActiveState(1); // re-activate
	log.info("manipulate end");
    }

    /*
     ==== Original program ====
      
    Bank MM JACK		; 3: MegaJack prog
    @JackLoop
    Set   #16, $fields    ; #16 = Number of fields
    @JackMove
    Move                  ; Move forward
    @Test
    Set   %Active, 0      ; deactivate potential enemy on field ahead.
    Scan  #11             ; Scan the field ahead.
    Comp  #11, 0          ; Is it an enemy robot?
    Jump  @JackExamine    ; Nope. Examine!
    Sub   #16, 1          ; Decrement field counter #16
    Comp  #16, 0          ; Is it already 0?
    Jump  @JackMove       ; No. Move on.

    @JackNextRow
    Turn  1               ; \
    Move                  ;  Next row of fields
    Turn  0               ; /
    Jump  @JackLoop       ; ...from the beginning

    @JackExamine
    Comp  #11, 2          ; Is it a friend?
    Jump  @Manipulate     ; No. Away with his program!
    Set   %Active, 1      ; Re-Activate
    Jump  @JackNextRow    ; Better get away: next row!

    @Manipulate
    Set   #14, %banks
    @EraseNext
    Trans 2, #14          ; Transfer empty bank 2 to enemy
    Sub   #14, 1
    Comp  #14, 0
    Jump  @EraseNext
    Set   %Active, 1      ; Re-Activate
    Jump  @Test
     */
}
