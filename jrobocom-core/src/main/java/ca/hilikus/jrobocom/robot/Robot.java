package ca.hilikus.jrobocom.robot;

import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.Direction;
import ca.hilikus.jrobocom.GameSettings;
import ca.hilikus.jrobocom.Player;
import ca.hilikus.jrobocom.World;
import ca.hilikus.jrobocom.events.EventDispatcher;
import ca.hilikus.jrobocom.events.GenericEventDispatcher;
import ca.hilikus.jrobocom.events.RobotChangedEvent;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.robot.api.RobotAction;
import ca.hilikus.jrobocom.robot.api.RobotStatusLocal;
import ca.hilikus.jrobocom.security.GamePermission;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * Keeps the state of each robot in the board
 * 
 * @author hilikus
 * 
 */
public class Robot implements RobotAction, Runnable {

    private static int lastSerial = 0;

    private static RobotListener inheritedListener;

    private final TurnManager turnsControl;

    private final World world;

    private Logger log = LoggerFactory.getLogger(Robot.class);

    private RobotStatusLocal data;

    private boolean alive;

    private boolean pendingBankChange;

    private final Bank[] banks;

    private int runningBank;

    private int serialNumber;

    private final String name;

    private final Player owner;

    private GenericEventDispatcher<RobotListener> eventDispatcher = new GenericEventDispatcher<>();

    /**
     * Events interface
     * 
     */
    public interface RobotListener extends EventListener {
	/**
	 * @param evt the change object
	 */
	public void update(RobotChangedEvent evt);
    }

    /**
     * Common constructor for all robots
     * 
     * @param theWorld the world the robots lives in
     * @param clock the clock that controls the robot turns
     * @param banksCount number of banks
     */
    private Robot(World theWorld, MasterClock clock, int banksCount, String pName, Player pOwner) {
	if (theWorld == null || clock == null || pOwner == null) {
	    throw new IllegalArgumentException("Arguments cannot be null");
	}
	serialNumber = Robot.getNextSerialNumber();

	world = theWorld;
	turnsControl = new TurnManager(clock);
	banks = new Bank[banksCount];
	owner = pOwner;
	name = pName;
    }

    /**
     * Creates first robot in the world. It is the responsibility of the caller to start the robot's
     * thread
     * 
     * @param theWorld the environment of the robot
     * @param clock the ticker to control turns
     * @param allBanks the code to execute
     * @param name this robot's name
     * @param pOwner the player that created this robot
     */
    public Robot(World theWorld, MasterClock clock, Bank[] allBanks, String name, Player pOwner) {
	this(theWorld, clock, allBanks.length, name, pOwner);

	Direction randomDir = Direction.fromInt(World.getRandGenerator().nextInt(Direction.COUNT));
	data = new RobotData(this, InstructionSet.SUPER, false, 0, randomDir);

	for (int pos = 0; pos < allBanks.length; pos++) {
	    setBank(allBanks[pos], pos);
	}

	alive = data.getGeneration() < GameSettings.MAX_GENERATION;
    }

    /**
     * Creates a child robot. It is the responsibility of the caller to start the robot's thread
     * 
     * @param pSet maximum instruction set supported
     * @param banksCount number of banks
     * @param pMobile true if robot is mobile
     * @param parent creator of this robot
     * @param name a name of this single robot
     */
    private Robot(InstructionSet pSet, int banksCount, boolean pMobile, Robot parent, String name) {
	this(parent.world, parent.getTurnsControl().clock, banksCount, name, parent.owner);

	if (banksCount > GameSettings.MAX_BANKS) {
	    throw new IllegalArgumentException("Too many banks");
	}
	if (parent.data.getInstructionSet().isLessThan(InstructionSet.SUPER)) {
	    throw new IllegalArgumentException("Parent could not have created child");
	}

	data = new RobotData(this, pSet, pMobile, parent.data.getGeneration() + 1, parent.data.getFacing());
    }


    @Override
    public int reverseTransfer(int localBankIndex, int remoteBankIndex) {
	if (data.getInstructionSet().isLessThan(InstructionSet.ADVANCED)) {
	    die("Invalid action in Instruction Set");
	}
	Robot neighbour = world.getNeighbour(this);
	if (neighbour != null) {
	    Bank remoteBank = neighbour.getBankCopy(remoteBankIndex);
	    setBank(remoteBank, localBankIndex);
	    return remoteBank.getCost();
	} else {

	    return 0;
	}
    }

    @Override
    public int transfer(int localBankIndex, int remoteBankIndex) {
	if (data.getInstructionSet().isLessThan(InstructionSet.ADVANCED)) {
	    die("Invalid action in Instruction Set");
	}
	Robot neighbour = world.getNeighbour(Robot.this);
	if (neighbour != null) {
	    Bank localBankCopy;
	    try {
		localBankCopy = banks[localBankIndex].getClass().getDeclaredConstructor(int.class)
			.newInstance(data.getTeamId());

		neighbour.setBank(localBankCopy, remoteBankIndex);
		return localBankCopy.getCost();
	    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException | NoSuchMethodException | SecurityException exc) {

		log.error("[transfer] Error instantiating Bank for transfer", exc);
		return 0;
	    }
	} else {
	    return 0;
	}
    }

    private static int getNextSerialNumber() {
	return lastSerial++;
    }

    /**
     * The main loop of the robot
     */
    @Override
    public void run() {
	try {
	    turnsControl.waitTurns(1); // block at the beginning so that all robots start at the
				       // same time
	    int oldRunningBank = 0;
	    assert getData().isEnabled() : "Robot is disabled";
	    while (alive) {
		if (runningBank == 0) {
		    if (banks[0] == null || banks[0].isEmpty()) {
			die("Data Hunger");
		    }
		} else {
		    if (runningBank > banks.length) {
			if (runningBank >= GameSettings.MAX_BANKS) {
			    die("Impossible Bank number");
			} else {
			    reboot("Bank not found");
			}
		    } else if (banks[runningBank] == null || banks[runningBank].isEmpty()) {
			// tried to execute empty bank, reboot
			reboot("Tried to execute empty bank");
		    }
		}

		// normal execution starts
		if (alive) {
		    if (runningBank != oldRunningBank
			    && banks[runningBank].getTeamId() != banks[oldRunningBank].getTeamId()) {
			eventDispatcher.fireEvent(new RobotChangedEvent(this));
		    }
		    oldRunningBank = runningBank;
		    banks[runningBank].run();

		    if (!pendingBankChange) {
			reboot("End of bank");
		    } else {
			pendingBankChange = false;
		    }
		}

	    }
	} catch (Exception | Error all) {
	    log.error("[run] Problem running robot " + this, all);
	    die("Execution Error -- " + all);
	}
    }

    private void reboot(String reason) {
	log.trace("[run] Rebooting robot: {}", reason);
	changeBank(0);
    }

    @Override
    public void changeBank(int newBank) {
	log.debug("[changeBank] Changing Bank of {}. Old bank = {}, new bank = {}", this, runningBank, newBank);
	runningBank = newBank;
	pendingBankChange = true; // so the robot doesn't reboot at the end of this bank

    }

    /**
     * Kills the robot and removes it from the board
     * 
     * @param reason user-friendly explanation for the death
     */
    @Override
    public void die(String reason) {
	log.info("[die] Robot {} died with reason: {}", serialNumber, reason);
	if (alive) { // if not alive it means it was killed at creation
	    alive = false;
	    world.remove(Robot.this);
	}
	eventDispatcher.removeListeners();
    }

    @Override
    public void die() {
	die("No reason given");

    }

    /**
     * @return the total number of banks (including empty) in the robot
     */
    int getBanksCount() {
	if (banks == null) {
	    return 0;
	}
	return banks.length;
    }

    private void setBank(Bank bank, int localBankIndex) {
	if (banks == null || localBankIndex > banks.length) {
	    throw new IllegalArgumentException("Bank doesn't exist");
	}

	if (bank != null) {
	    bank.plugInterfaces(new RobotControlProxy(this), new RobotStatusProxy(this, world),
		    new WorldPlayerProxy(turnsControl, world));
	    banks[localBankIndex] = bank;
	} else {
	    banks[localBankIndex] = null;
	}
    }

    private Bank getBankCopy(int localBankIndex) {
	if (banks == null || localBankIndex > banks.length) {
	    throw new IllegalArgumentException("Bank doesn't exist");
	}

	try {
	    return banks[localBankIndex].getClass().getConstructor().newInstance();
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
		| InvocationTargetException | NoSuchMethodException | SecurityException exc) {

	    log.error("[getBankCopy] Error instantiating copy of Bank to transfer", exc);
	    return null;
	}
    }

    /*    private void setTeamId(int teamId) {
    	this.teamId = teamId;
        }

        private void setTurns(TurnManager turns) {
    	this.turns = turns;
        }

        private void setWorld(World world) {
    	this.world = world;
        }*/

    /**
     * Controls waiting of I/O
     * 
     * @author hilikus
     * 
     */
    public class TurnManager {

	private int turnsCounter = 0;

	private MasterClock clock;

	private final Object syncObj = new Object();

	/**
	 * @param pClock master clock used to schedule tasks
	 */
	public TurnManager(MasterClock pClock) {
	    clock = pClock;
	}

	/**
	 * Blocks the robot
	 * 
	 * @param turns the number of turns to block
	 */
	public void waitTurns(int turns) {
	    blockIfDisabled();
	    if (turnsCounter > GameSettings.MAX_AGE) {
		die("Old Age");
	    } else {
		clock.waitFor(serialNumber, turns);
		turnsCounter += turns;
	    }
	    blockIfDisabled();
	}

	private void blockIfDisabled() {
	    synchronized (syncObj) {
		while (!data.isEnabled()) {
		    try {
			syncObj.wait();
		    } catch (InterruptedException exc) {
			log.error("[waitTurns]", exc);
		    }
		}
	    }
	}

	/**
	 * @return the number of turns so far
	 */
	public int getTurnsCount() {
	    return turnsCounter;
	}

	private void activated() {
	    synchronized (syncObj) {
		syncObj.notifyAll();
	    }

	}
    }

    /**
     * @return true if the robot is still alive; false otherwise
     */
    public boolean isAlive() {
	return alive;
    }

    TurnManager getTurnsControl() {
	return turnsControl;
    }

    @Override
    public void turn(boolean right) {
	if (right) {
	    ((RobotData) data).setFacing(data.getFacing().right());
	} else {
	    ((RobotData) data).setFacing(data.getFacing().left());
	}
	eventDispatcher.fireEvent(new RobotChangedEvent(this));
    }

    @Override
    public void createRobot(String pName, InstructionSet pSet, int banksCount, boolean pMobile) {
	if (data.getInstructionSet() != InstructionSet.SUPER) {
	    die("Robot cannot create other robots");
	}

	int robotsCount = world.getBotsCount(data.getTeamId(), false);
	if (data.getGeneration() < GameSettings.MAX_GENERATION && robotsCount < GameSettings.MAX_BOTS) {
	    Robot child = new Robot(pSet, banksCount, pMobile, this, pName);

	    child.eventDispatcher.addListener(inheritedListener);
	    world.add(this, child); // world does further verification so add is not guaranteed yet

	    // all verifications passed
	    child.alive = true;
	    Thread newThread = new Thread(Thread.currentThread().getThreadGroup(), child, "Bot-"
		    + child.getSerialNumber());
	    newThread.start(); // jumpstarts the robot
	}

    }

    @Override
    public void move() {
	if (!data.isMobile()) {
	    die("Robot is not mobile but tried to move");
	}
	world.move(this);

    }

    @Override
    public ScanResult scan() {
	return scan(1);
    }

    @Override
    public ScanResult scan(int maxDist) {
	if (maxDist < 1) {
	    throw new IllegalArgumentException("Scan argument has to be positive");
	}

	if (data.getInstructionSet().isLessThan(InstructionSet.ADVANCED)) {
	    die("Robot doesn't have Scan operation in its Instruction Set");
	}

	ScanResult res = null;
	for (int dist = 1; dist <= maxDist; dist++) {
	    res = world.scan(this, dist);
	    if (!res.isEmpty()) {
		return res;
	    }
	}
	assert res != null : "Scan result can't be null";

	return res;
    }

    /**
     * @return the unique ID of the robot
     */
    public int getSerialNumber() {
	return serialNumber;
    }

    /**
     * @return an object with the state information
     */
    public RobotStatusLocal getData() {
	return data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + serialNumber;
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || getClass() != obj.getClass()) {
	    return false;
	}

	Robot other = (Robot) obj;
	return serialNumber == other.serialNumber;
    }

    /**
     * @return the name of this robot
     */
    public String getName() {
	return name;
    }

    /**
     * @return the owner
     */
    public Player getOwner() {
	return owner;
    }

    /**
     * @return the id of the creator of the running bank
     */
    public int getRunningBankTeamId() {
	if (banks[runningBank] != null) {
	    return banks[runningBank].getTeamId();
	} else {
	    return data.getTeamId(); // nothing is running, return robot's team id
	}
    }

    /**
     * @return the object in charge of events
     */
    public EventDispatcher<RobotListener> getEventHandler() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new GamePermission("eventsListener"));
	}
	return eventDispatcher;
    }

    /**
     * Sets a special listener that gets passed to robots created by robots
     * 
     * @param listener
     */
    public static void setInheritableListener(RobotListener listener) {
	inheritedListener = listener;
    }

    void activated() {
	turnsControl.activated();

    }

}
