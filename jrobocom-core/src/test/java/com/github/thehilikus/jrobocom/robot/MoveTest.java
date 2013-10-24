package com.github.thehilikus.jrobocom.robot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.testng.annotations.Test;

import com.github.thehilikus.jrobocom.AbstractTest;
import com.github.thehilikus.jrobocom.Player;
import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.WorldTest;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.robot.Robot;
import com.github.thehilikus.jrobocom.timing.Delayer;

/**
 * Tests the action of moving a robot. Note however that most of the actual moving logic is in
 * {@link World} and therefore it is tested in {@link WorldTest}
 * 
 * @author hilikus
 */
public class MoveTest extends AbstractTest {

    /**
     * 
     */
    public MoveTest() {
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
