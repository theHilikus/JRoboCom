package ca.hilikus.jrobocom;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.awt.Point;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.GameTracker.GameStatusListener;
import ca.hilikus.jrobocom.events.LeaderChangedEvent;
import ca.hilikus.jrobocom.events.PlayerEliminatedEvent;
import ca.hilikus.jrobocom.events.ResultEvent;
import ca.hilikus.jrobocom.events.ResultEvent.Result;
import ca.hilikus.jrobocom.events.RobotAddedEvent;
import ca.hilikus.jrobocom.events.RobotRemovedEvent;
import ca.hilikus.jrobocom.robot.Robot;

/**
 * Tests for the tracker of the game's status
 * 
 * @author hilikus
 */
public class GameTrackerTest extends AbstractTest {

    private GameTracker TU;

    private EventReceiver listener;

    /**
     * Unit test event receiver
     * 
     */
    public class EventReceiver implements GameStatusListener {
	private ResultEvent result;

	private LeaderChangedEvent leaderChange;

	private PlayerEliminatedEvent eliminated;

	@Override
	public void update(ResultEvent pResult) {
	    result = pResult;

	}

	@Override
	public void update(LeaderChangedEvent event) {
	    leaderChange = event;
	}

	@Override
	public void update(PlayerEliminatedEvent event) {
	    eliminated = event;

	}

    }

    /**
     * 
     */
    public GameTrackerTest() {
	super(GameTracker.class);
    }

    /**
     * Configures the testing unit
     */
    @BeforeMethod
    public void setUp() {
	TU = new GameTracker();
	listener = new EventReceiver();
	TU.getEventHandler().addListener(listener);
    }

    /**
     * Tests if a winner is declared when a second team completely dies
     */
    @Test
    public void declareWinner() {
	Player mockPlayer = mock(Player.class);
	Robot mockRobot = mock(Robot.class);
	when(mockRobot.getOwner()).thenReturn(mockPlayer);
	TU.getEventsReceiver().update(new RobotAddedEvent(mockRobot, new Point()));

	Player mockPlayer2 = mock(Player.class);
	Robot mockRobot2 = mock(Robot.class);
	when(mockRobot2.getOwner()).thenReturn(mockPlayer2);
	TU.getEventsReceiver().update(new RobotAddedEvent(mockRobot2, new Point()));

	TU.getEventsReceiver().update(new RobotRemovedEvent(mockRobot2, new Point()));

	assertNotNull(listener.result, "Check end of game event was generated");
	assertEquals(listener.result.getResult(), Result.WIN, "Check result was a win");
	assertEquals(listener.result.getWinner(), mockPlayer, "Check winner is the correct one");
    }

    /**
     * Verifies that if only one team is added, practice mode is detected
     */
    @Test
    public void detectPractice() {
	Player mockPlayer = mock(Player.class);
	Robot mockRobot = mock(Robot.class);
	when(mockRobot.getOwner()).thenReturn(mockPlayer);
	TU.getEventsReceiver().update(new RobotAddedEvent(mockRobot, new Point()));

	Robot mockRobot2 = mock(Robot.class);
	when(mockRobot2.getOwner()).thenReturn(mockPlayer);
	TU.getEventsReceiver().update(new RobotAddedEvent(mockRobot2, new Point()));

	TU.getEventsReceiver().update(new RobotRemovedEvent(mockRobot2, new Point()));
	assertNull(listener.result, "Result was generated");

	TU.getEventsReceiver().update(new RobotRemovedEvent(mockRobot, new Point()));
	assertNotNull(listener.result, "Result was not generated");
	assertEquals(listener.result.getResult(), Result.END);
    }

    /**
     * Tests that the tracker ends the game when the age of the world is too high
     */
    @Test
    public void detectEndOfGame() {

	TU.getEventsReceiver().tick(GameSettings.getInstance().MAX_WORLD_AGE + 2);

	assertNotNull(listener.result, "Draw event was not generated");
	assertEquals(listener.result.getResult(), Result.DRAW, "Event was not of type draw");
    }

    public void detectNewSingleLeader() {
	// TODO: implement
    }

    public void detectNewTieForLeader() {
	// TODO: implement
    }
}
