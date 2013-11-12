package com.github.thehilikus.jrobocom;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.thehilikus.jrobocom.exceptions.BankInterruptedException;
import com.github.thehilikus.jrobocom.exceptions.PlayerException;
import com.github.thehilikus.jrobocom.player.Bank;
import com.github.thehilikus.jrobocom.robot.Robot;
import com.github.thehilikus.jrobocom.robot.api.RobotStatusLocal;

/**
 * Tests for {@link Player}
 * 
 * @author hilikus
 */
public class PlayerTest extends AbstractTest {

    /**
     */
    public PlayerTest() {
	super(Player.class);
    }

    private Player TU;
    private URLClassLoader loader;
    private static final Logger log = LoggerFactory.getLogger(PlayerTest.class);

    private static class DummyBank extends Bank {
	@SuppressWarnings("unused")
	public DummyBank() {

	}

	@Override
	public void run() throws BankInterruptedException {

	}

    }

    private static class BadBank extends Bank {

	@SuppressWarnings("unused")
	public BadBank(int someArgument, String other) {
	}

	@Override
	public void run() throws BankInterruptedException {
	}

    }

    /**
     * Configures the common fixtures
     * 
     * @throws PlayerException
     */
    @BeforeMethod
    public void beforeMethod() throws PlayerException {
	loader = mock(URLClassLoader.class);

    }

    private void simulatePropertiesFile(String content) throws PlayerException {
	InputStream value = null;
	try {
	    value = new ByteArrayInputStream(content.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException exc) {
	    log.error("[setPropertiesFile]", exc);
	}
	when(loader.getResourceAsStream(anyString())).thenReturn(value);
	TU = new Player(loader, "unit/test/path");
    }

    /**
     * Tests when there is no property file
     * 
     * @throws PlayerException
     */
    @Test(expectedExceptions = PlayerException.class)
    public void constructNoPropertiesFile() throws PlayerException {
	TU = new Player(loader, "unit/test/path");
    }

    /**
     * Tests when the property file has no "banks" entry
     * 
     * @throws PlayerException
     */
    @Test(expectedExceptions = PlayerException.class)
    public void constructNoBanks() throws PlayerException {
	simulatePropertiesFile("Author=unit-tester");
    }

    /**
     * Tests when the property file has an empty "banks" entry
     * 
     * @throws PlayerException
     */
    @Test(expectedExceptions = PlayerException.class)
    public void constructEmptyBanks() throws PlayerException {
	simulatePropertiesFile("Author=unit-tester\nBanks=");
    }

    /**
     * Tests when the bank referenced does not exist
     * 
     * @throws PlayerException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = PlayerException.class)
    public void constructMissingBanks() throws PlayerException, ClassNotFoundException {
	final String CLASS = "crazy.class.Bank";
	when(loader.loadClass(CLASS)).thenThrow(ClassNotFoundException.class);
	simulatePropertiesFile("Author=unit-tester\nBanks=" + CLASS);
    }

    /**
     * Tests loading a class from the player that doesn't extend Bank
     * 
     * @throws ClassNotFoundException
     * @throws PlayerException
     */
    @Test(expectedExceptions = PlayerException.class)
    public void constructBankNotBank() throws ClassNotFoundException, PlayerException {
	doReturn(PlayerTest.class).when(loader).loadClass("someBank"); // any non-Bank class
	simulatePropertiesFile("Banks=someBank");
    }

    /**
     * Tests loading a bank that doesn't have the expected constructor
     * 
     * @throws ClassNotFoundException
     * @throws PlayerException
     */
    @Test(expectedExceptions = PlayerException.class)
    public void constructBankNoConstructor() throws ClassNotFoundException, PlayerException {
	doReturn(BadBank.class).when(loader).loadClass("someBank");
	simulatePropertiesFile("Banks=someBank");
    }

    /**
     * Tests building a good player with dummy banks
     * 
     * @throws ClassNotFoundException
     * @throws PlayerException
     */
    @Test
    public void constructGoodBanks() throws ClassNotFoundException, PlayerException {
	doReturn(DummyBank.class).when(loader).loadClass("DummyBank");
	final String AUTHOR = "unit-tester";
	final String TEAM = "the_testers";
	simulatePropertiesFile("Author=" + AUTHOR + "\nBanks=DummyBank\n" + "Team=" + TEAM);
	assertEquals(TU.getAuthor(), AUTHOR, "Author's name was not loaded");
	assertEquals(TU.getTeamName(), TEAM, "Team's name was not loaded");

	// validate banks
	assertEquals(TU.getCode().length, 1, "Wrong number of banks loaded");
	assertEquals(TU.getCode()[0].getClass(), DummyBank.class);
    }

    /**
     * Tests loading players from paths that don't have anything
     * 
     * @throws PlayerException
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadInexistentPlayers() throws PlayerException {
	List<String> paths = new ArrayList<>();
	paths.add("whatever/foo");
	Player.loadPlayers(paths);
    }

    /**
     * Tests whether starting a robot notifies the robot about its change in state
     */
    @Test
    public void startRobot() {
	Robot mockRobot = mock(Robot.class);
	RobotStatusLocal mockData = mock(RobotStatusLocal.class);
	when(mockRobot.getData()).thenReturn(mockData);
	TU.startRobot(mockRobot);
	verify(mockData).setActiveState(Player.DEFAULT_START_STATE);
    }

    /**
     * Tests cleaning the Player
     * 
     * @throws IOException
     */
    @Test(dependsOnMethods = "constructGoodBanks")
    public void clean() throws IOException {
	try {
	    simulatePropertiesFile("Author=unit-tester");
	} catch (PlayerException exc) {
	    // do nothing
	}
	TU.clean();
	verify(loader).close();
    }
}
