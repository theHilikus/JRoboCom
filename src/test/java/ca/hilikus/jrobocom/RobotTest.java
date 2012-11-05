package ca.hilikus.jrobocom;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;

/**
 * Tests the robots, but not the control class
 * 
 * @author hilikus
 * 
 */
public class RobotTest {

    private static final Logger log = LoggerFactory.getLogger(RobotTest.class);
    
        
    /**
     * Initializes each test
     * 
     * @param met test about to be called
     */
    @BeforeMethod
    public void setUp(Method met) {
	log.info("\n====== Starting " + met.getName() + " ======");

    }

    /**
     * Tests creation of subsequent robots
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" }, groups = "init")
    public void testOtherRobotsCreation() {

	World mockWorld = mock(World.class);
	Bank[] dummyBanks = new Bank[3];
	Robot eve = new Robot(mockWorld, dummyBanks);

	int banks = 3;

	InstructionSet set = InstructionSet.ADVANCED;
	Robot TU = new Robot(set, banks, false, eve);
	assertFalse(TU.isMobile());
	assertEquals(TU.getBanksCount(), banks);
	assertEquals(TU.getAge(), 0);
	assertEquals(TU.getInstructionSet(), set);
	assertEquals(TU.getSerialNumber(), eve.getSerialNumber() + 1,
		"Serial number increases every time");
	assertEquals(TU.getGeneration(), eve.getGeneration() + 1);

	Robot TU2 = new Robot(set, 8, true, eve);
	assertEquals(TU2.getGeneration(), eve.getGeneration() + 1);
	assertEquals(TU2.getSerialNumber(), TU.getSerialNumber() + 1);

    }

    /**
     * Tests creation of first robot
     */
    @Test(groups = "init")
    public void testFirstRobotCreation() {
	World mockWorld = mock(World.class);
	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, dummyBanks);

	// check generation and serial #?
	verify(mockWorld).validateTeamId(anyInt());

	assertTrue(TU.isMobile(), "First robot should be mobile");
	assertEquals(TU.getBanksCount(), GameSettings.MAX_BANKS,
		"First robot should have all banks");
	assertEquals(TU.getAge(), 0, "Age at construction is 0");
	assertEquals(TU.getInstructionSet(), InstructionSet.SUPER,
		"First robot should have all instruction sets");
	assertEquals(TU.getSerialNumber(), 0, "Serial number of first robot should be 0");
    }

    /**
     * Tests an infertile robot trying to create
     */
    @Test(expectedExceptions = IllegalArgumentException.class, dependsOnGroups = { "init.*" })
    public void testInvalidCreation() {
	Robot mockParent = mock(Robot.class);
	when(mockParent.getInstructionSet()).thenReturn(InstructionSet.ADVANCED);

	Robot TU = new Robot(InstructionSet.BASIC, 3, true, mockParent);
	fail("Should not have created");
    }

    @Test(enabled = false)
    public void testReboot() {
	World mockWorld = mock(World.class);
	Bank[] dummyBanks = new Bank[3];

	// TODO: complete, but possible??
    }

    /**
     * 
     */
    @Test(dependsOnMethods = { "testFirstRobotCreation" })
    public void testDie() {
	World mockWorld = mock(World.class);
	Bank[] dummyBanks = new Bank[3];
	Robot TU = new Robot(mockWorld, dummyBanks);

	TU.die("test die");

	verify(mockWorld).remove(TU);
    }

}
