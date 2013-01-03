package ca.hilikus.jrobocom_samples.blackjacks;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.ReadableSettings;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.player.ScanResult.Found;

/**
 * The program for Jack
 * 
 * @author Dennis C. Bemmann
 */
public class BlackJack extends Bank {

    /**
     * @param pTeamId
     */
    public BlackJack(int pTeamId) {
	super(pTeamId);
    }

    @Override
    public void run() {
	while (true) {
	    int pos = ReadableSettings.FIELDS;
	    while (true) {
		// keep moving
		control.move(); // Move forward
		info.setRemoteActiveState(0); // deactivate potential enemy on field ahead.
		ScanResult result = control.scan(); // Scan the field ahead.
		if (result.getResult() != Found.EMPTY) { // Is it an empty field?
		    examine(result); // Nope. Examine!
		    changeRow(); // Better get away: next row!
		    break; // ...from the beginning
		} else {
		    pos--; // Decrement field counter #1
		    if (pos == 0) { // Is it already 0?
			changeRow();
			break; // ...from the beginning
		    }
		}
	    }
	}
	/*@Main
	  Set   #1, $fields     ; #1 = Number of fields
	  @KeepMoving
	  Move                  ; Move forward
	  Set   %Active, 0      ; deactivate potential enemy on field ahead.
	  Scan  #2              ; Scan the field ahead.
	  Comp  #2, 0           ; Is it an empty field?
	  Jump  @Examine        ; Nope. Examine!
	  Sub   #1, 1           ; Decrement field counter #1
	  Comp  #1, 0           ; Is it already 0?
	  Jump  @KeepMoving     ; No. Move on.

	  @NextRow
	  Turn  1               ; \
	  Move                  ;  } Next row of fields
	  Turn  0               ; /
	  Jump  @Main           ; ...from the beginning

	  @Examine
	  Comp  #2, 2           ; Is it a friend?
	  Trans 2, 1            ; No, transfer BlackBank to enemy
	  Set   %Active, 1      ; Re-Activate
	  Jump  @NextRow        ; Better get away: next row!
	*/
    }

    /*
     * Next row of fields
     */
    private void changeRow() {
	control.turn(true);
	control.move();
	control.turn(false);

    }

    private void examine(ScanResult result) {
	if (result.getResult() != Found.FRIEND) { // Is it a friend?
	    control.transfer(1, 0); // No, transfer BlackBank to enemy
	}
	info.setRemoteActiveState(1); // Re-Activate

    }

}
