package ca.hilikus.jrobocom;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.events.event_manager.api.EventDispatcher;
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
    private EventDispatcher dispatcher;

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
	dispatcher = mock(EventDispatcher.class);
	TU.setEventDispatcher(dispatcher);
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

	verify(dispatcher, atLeastOnce()).fireEvent(argThat(isOfType(Result.WIN)));
	verify(dispatcher, atLeastOnce()).fireEvent(argThat(isPlayer(mockPlayer)));
	verify(dispatcher).fireEvent(isA(ResultEvent.class)); // called once
    }

    private static TypeSafeMatcher<ResultEvent> isPlayer(final Player mockPlayer) {
	return new TypeSafeMatcher<ResultEvent>() {

	    @Override
	    public void describeTo(Description description) {
		description.appendText("Check winner is the correct one");
	    }

	    @Override
	    public boolean matchesSafely(ResultEvent event) {
		return event.getWinner().equals(mockPlayer);
	    }
	};
    }

    private static TypeSafeMatcher<ResultEvent> isOfType(final Result expected) {
	return new TypeSafeMatcher<ResultEvent>() {

	    @Override
	    public void describeTo(Description description) {
		description.appendText("Check result was a " + expected);
	    }

	    @Override
	    public boolean matchesSafely(ResultEvent event) {
		return event.getResult() == expected;
	    }
	};
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
	verify(dispatcher, never()).fireEvent(isA(ResultEvent.class));

	TU.getEventsReceiver().update(new RobotRemovedEvent(mockRobot, new Point()));

	verify(dispatcher, atLeastOnce()).fireEvent(argThat(isOfType(Result.END)));
    }

    /**
     * Tests that the tracker ends the game when the age of the world is too high
     */
    @Test
    public void detectEndOfGame() {

	TU.getEventsReceiver().tick(GameSettings.getInstance().MAX_WORLD_AGE + 2);

	verify(dispatcher).fireEvent(argThat(isOfType(Result.DRAW)));
    }

    public void detectNewSingleLeader() {
	// TODO: implement
    }

    public void detectNewTieForLeader() {
	// TODO: implement
    }
}
