package com.github.thehilikus.jrobocom.robot;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import com.github.thehilikus.jrobocom.AbstractTest;
import com.github.thehilikus.jrobocom.Direction;
import com.github.thehilikus.jrobocom.World;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.robot.Robot.TurnManager;
import com.github.thehilikus.jrobocom.robot.api.RobotStatusLocal;

/**
 * Tests {@link RobotStatusProxy}. It confirms that the robot is called in the back and that the
 * delayer blocked. Most of these tests are functional tests
 * 
 * @author hilikus
 */
public class RobotStatusProxyTest extends AbstractTest {
    private RobotStatusProxy TU;
    private Robot mockRobot;
    private TurnManager turnManager;
    private RobotStatusLocal mockState;
    private Robot mockNeighbour;
    private RobotStatusLocal mockRemoteState;
    private int waits;

    /**
     * 
     */
    public RobotStatusProxyTest() {
	super(RobotStatusProxy.class);
    }

    /**
     * prepares all the mocks
     */
    @BeforeMethod
    public void beforeMethod() {
	
	waits = 1;

	mockRobot = mock(Robot.class);
	turnManager = mock(TurnManager.class);
	when(mockRobot.getTurnsControl()).thenReturn(turnManager);
	mockState = mock(RobotStatusLocal.class);
	when(mockRobot.getData()).thenReturn(mockState);
	World mockWorld = mock(World.class);
	mockNeighbour = mock(Robot.class);
	mockRemoteState = mock(RobotStatusLocal.class);
	when(mockNeighbour.getData()).thenReturn(mockRemoteState);
	when(mockWorld.getNeighbour(any(Robot.class))).thenReturn(mockNeighbour);
	TU = new RobotStatusProxy(mockRobot, mockWorld);
    }

    /**
     * confirms that the proxy blocked
     */
    @AfterMethod
    public void afterMethod() {
	verify(turnManager, times(waits)).waitTurns(anyInt(), anyString()); // it should have always blocked
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getActiveState() {
	when(mockState.getActiveState()).thenReturn(123);
	assertEquals(TU.getActiveState(), 123);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getAge() {
	when(mockState.getAge()).thenReturn(311);
	assertEquals(TU.getAge(), 311);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getBanksCount() {
	when(mockRobot.getBanksCount()).thenReturn(311);
	assertEquals(TU.getBanksCount(), 311);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getFacing() {
	when(mockState.getFacing()).thenReturn(Direction.WEST);
	assertEquals(TU.getFacing(), Direction.WEST);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getGeneration() {
	when(mockState.getGeneration()).thenReturn(311);
	assertEquals(TU.getGeneration(), 311);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getInstructionSet() {
	when(mockState.getInstructionSet()).thenReturn(InstructionSet.SUPER);
	assertEquals(TU.getInstructionSet(), InstructionSet.SUPER);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void getRemoteActiveState() {
	when(mockRemoteState.getActiveState()).thenReturn(311);
	assertEquals(TU.getRemoteActiveState(), 311);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void getRemoteAge() {
	when(mockRemoteState.getAge()).thenReturn(311);
	assertEquals(TU.getRemoteAge(), 311);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void getRemoteBanksCount() {
	when(mockNeighbour.getBanksCount()).thenReturn(311);
	assertEquals(TU.getRemoteBanksCount(), 311);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void getRemoteInstructionSet() {
	when(mockRemoteState.getInstructionSet()).thenReturn(InstructionSet.SUPER);
	assertEquals(TU.getRemoteInstructionSet(), InstructionSet.SUPER);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void getRemoteTeamId() {
	when(mockRemoteState.getTeamId()).thenReturn(311);
	assertEquals(TU.getRemoteTeamId(), 311);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void getTeamId() {
	when(mockState.getTeamId()).thenReturn(311);
	assertEquals(TU.getTeamId(), 311);
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void isEnabled() {
	when(mockState.getActiveState()).thenReturn(0).thenReturn(21);
	assertFalse(TU.isEnabled());
	assertTrue(TU.isEnabled());
	
	waits = 2;
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void isMobile() {
	when(mockState.isMobile()).thenReturn(false).thenReturn(true);
	assertFalse(TU.isMobile());
	assertTrue(TU.isMobile());
	
	waits = 2;
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void isRemoteEnabled() {
	when(mockRemoteState.getActiveState()).thenReturn(0).thenReturn(21);
	assertFalse(TU.isRemoteEnabled());
	assertTrue(TU.isRemoteEnabled());
	
	waits = 2;
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void isRemoteMobile() {
	when(mockRemoteState.isMobile()).thenReturn(false).thenReturn(true);
	assertFalse(TU.isRemoteMobile());
	assertTrue(TU.isRemoteMobile());
	
	waits = 2;
    }

    /**
     * tests the call gets passed to the real backend after waiting
     */
    @Test
    public void setActiveState() {
	TU.setActiveState(311);
	verify(mockState).setActiveState(311);
    }

    /**
     * tests the call gets passed to the real neighbour after waiting
     */
    @Test
    public void setRemoteActiveState() {
	TU.setRemoteActiveState(311);
	verify(mockRemoteState).setActiveState(311);
    }
}
