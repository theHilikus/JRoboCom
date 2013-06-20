package ca.hilikus.jrobocom.robot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.testng.annotations.Test;

import ca.hilikus.jrobocom.AbstractTest;
import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.WorldTest;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.timing.Delayer;

/**
 * Tests the action of moving a robot. Note however that most of the actual moving logic is in
 * {@link World} and therefore it is tested in {@link WorldTest}
 * 
 * @author hilikus
 */
public class MoveTests extends AbstractTest {

    /**
     * 
     */
    public MoveTests() {
	super(Robot.class);
    }

    /**
     * Tests moving the first robot, which is inmobile
     */
    @Test
    public void illegalMove() {
	World mockWorld = mock(World.class);
	Bank[] dummyBanks = new Bank[3];
	Player pla = mock(Player.class);
	Delayer clock = mock(Delayer.class);

	Robot TU = new Robot(mockWorld, clock, dummyBanks, "Unit test robot", pla);

	TU.move();
	verify(mockWorld).remove(TU);
    }

}
