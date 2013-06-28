package com.github.thehilikus.jrobocom;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;

import ch.qos.logback.classic.Level;

/**
 * Common class for all tests using TestNG
 *
 * @author hilikus
 */
public abstract class AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractTest.class);

    /**
     * 
     * Changes TU debug level to trace
     * 
     * @param testingUnitClass class of the testing unit
     */
    public AbstractTest(Class<?> testingUnitClass) {
	ch.qos.logback.classic.Logger TULog = (ch.qos.logback.classic.Logger) LoggerFactory
		.getLogger(testingUnitClass);
	TULog.setLevel(Level.TRACE);
    }

    /**
     * Initializes each test
     * 
     * @param met test about to be called
     */
    @BeforeMethod
    public void setUp(Method met) {
	log.info("\n====== Starting " + met.getName() + " ======");

    }

}
