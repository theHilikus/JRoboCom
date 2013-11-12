package com.github.thehilikus.jrobocom;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.thehilikus.jrobocom.player.Bank;

/**
 * Tests {@link Session}
 * 
 * @author hilikus
 */
public class SessionTest extends AbstractTest {

    private Player firstPlayer;
    private Player secondPlayer;
    private List<Player> players;

    /**
     * 
     */
    public SessionTest() {
	super(Session.class);
    }

    /**
     * Configures the mocks
     */
    @BeforeMethod
    public void beforeMethod() {
	players = new ArrayList<>();

	firstPlayer = mock(Player.class);
	players.add(firstPlayer);
	when(firstPlayer.getCode()).thenReturn(new Bank[2]);

	secondPlayer = mock(Player.class);
	players.add(secondPlayer);
	when(secondPlayer.getCode()).thenReturn(new Bank[2]);

    }

    /**
     * Tests adding two players with the same teamID
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void DuplicatePlayers() {
	when(firstPlayer.getTeamId()).thenReturn(311);
	when(secondPlayer.getTeamId()).thenReturn(311);

	@SuppressWarnings("unused")
	Session TU = new Session(players);
    }

    /**
     * Tests creating a good session
     */
    @Test
    public void Session() {
	Session TU = createGoodSession();

	assertFalse(TU.isRunning(), "Should not be running after creation");
    }

    private Session createGoodSession() {
	when(firstPlayer.getTeamId()).thenReturn(311);
	when(secondPlayer.getTeamId()).thenReturn(312);
	Session TU = new Session(players);
	return TU;
    }

    /**
     * Tests stepping the session
     */
    @Test(dependsOnMethods = "Session")
    public void testStep() {
	Session TU = createGoodSession();

	TU.step();
	assertFalse(TU.isRunning(), "Should not be running after step");
    }

    /**
     * Tests starting and then stopping the session
     */
    @Test
    public void testStartStop() {
	Session TU = createGoodSession();

	TU.start();
	assertTrue(TU.isRunning(), "Session didn't start");
	TU.stop();
	assertFalse(TU.isRunning(), "Should not be running after stop");
    }

    /**
     * Tests retrieving a real and a fake session
     */
    @Test
    public void getPlayer() {
	Session TU = createGoodSession();

	assertSame(TU.getPlayer(311), firstPlayer, "Player returned was not the right one");

	assertNull(TU.getPlayer(123543));
    }

}
