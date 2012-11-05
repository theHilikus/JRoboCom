package ca.hilikus.jrobocom;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.GameSettings.Timing;
import ca.hilikus.jrobocom.player.Bank;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.timing.MasterClock;

/**
 * Keeps the state of each robot in the board
 * 
 * @author hilikus
 * 
 */
public final class Robot implements Comparable<Robot> {

    private static int lastSerial = 0;

    private boolean mobile;

    private int activeState = 0;

    private TurnManager turnsControl;

    private InstructionSet set;

    private Direction facing;

    private Bank[] banks;

    private long creationTimestamp = System.currentTimeMillis();

    private int generation;

    private int teamId;

    private int serialNumber;

    private World world;

    private int runningBank;

    private final RobotControl control = new RobotControl();

    private Logger log = LoggerFactory.getLogger(Robot.class);

    /**
     * Interface used from Banks
     * 
     * 
     */
    public final class RobotControl {
	/**
	 * Changes the running bank once the current bank <b>ends its execution</b>
	 * 
	 * @param newBank the 0-based index of the bank to run
	 * 
	 */
	public void changeBank(int newBank) {
	    turnsControl.waitTurns(Timing.BANK_CHANGE);

	    Robot.this.changeBank(newBank);

	}

	/**
	 * Creates a new, empty robot in the adjacent field in the robot's current direction
	 * 
	 * @param pSet the instruction set of the new robot
	 * @param banksCount the number of empty banks to create
	 * @param pMobile true if the robot should be able to move
	 */
	public void createRobot(InstructionSet pSet, int banksCount, boolean pMobile) {
	    int turnsForBanks = Timing.CREATION_BASE + Timing.CREATION_PER_BANK * banksCount;
	    if (pMobile) {
		turnsForBanks *= Timing.MOBILITY_PENALTY + Timing.MOBILITY_CONSTANT;
	    }
	    int turnsForSet = 0;
	    if (pSet == InstructionSet.ADVANCED) {
		turnsForSet = Timing.ADVANCED_SET_PENALTY;
	    }
	    if (pSet == InstructionSet.SUPER) {
		turnsForSet += Timing.SUPER_SET_PENALTY;
	    }

	    int totalWait = Math.min(turnsForBanks + turnsForSet, GameSettings.MAX_CREATE_WAIT);
	    turnsControl.waitTurns(totalWait);

	    if (set != InstructionSet.SUPER) {
		Robot.this.die("Robot cannot create other robots");
	    }

	    int robotsCount = world.getBotsCount(Robot.this.getTeamId(), false);
	    if (generation < GameSettings.MAX_GENERATION && robotsCount < GameSettings.MAX_BOTS) {
		Robot child = new Robot(pSet, banksCount, pMobile, Robot.this);
		world.add(Robot.this, child); // world does further verification so add is not
					      // guaranteed yet
	    }
	}

	/**
	 * kills the robot
	 * 
	 * @param reason an optional description of the death
	 * @see #die()
	 */
	public void die(String reason) {
	    Robot.this.die(reason);
	}

	/**
	 * Kill the robot with an unspecified reason
	 * 
	 * @see Robot#die(String)
	 */
	public void die() {
	    die("No reason specified");
	}

	/**
	 * Gets the activation state of the current robot
	 * 
	 * @return a number > 0 if the robot is active; < 1 if inactive
	 * @see #getActiveState(boolean)
	 */
	public int getActiveState() {
	    return getActiveState(true);
	}

	// TODO: move all gets to an "information" class since they don't control
	/**
	 * Gets the activation state of the current or adjacent robot
	 * 
	 * @param local true if of the current robot; false if of the adjacent
	 * @return a number > 0 if the robot is active; < 1 if inactive
	 */
	public int getActiveState(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);
		return Robot.this.getActiveState();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().getActiveState();
		} else {
		    return 0;
		}
	    }
	}

	private Bank getBank(int localBankIndex) {
	    if (banks == null || localBankIndex > banks.length) {
		throw new IllegalArgumentException("Bank doesn't exist");
	    }

	    return banks[localBankIndex];
	}

	/**
	 * 
	 * @return the number of banks of the current robot
	 * @see #getBanksCount(boolean)
	 */
	public int getBanksCount() {
	    return getBanksCount(true);

	}

	/**
	 * @param local true if of the current robot; false if of the adjacent
	 * @return the number of banks of the current robot
	 */
	public int getBanksCount(boolean local) {
	    if (local) {

		turnsControl.waitTurns(Timing.LOCAL_READ);
		return Robot.this.getBanksCount();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().getBanksCount();
		} else {
		    return 0;
		}
	    }
	}

	public InstructionSet getInstructionSet() {
	    return getInstructionSet(true);

	}

	public InstructionSet getInstructionSet(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);

		return Robot.this.getInstructionSet();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().getInstructionSet();
		} else {
		    return InstructionSet.BASIC;
		}
	    }
	}

	public int getTeamId() {
	    return getTeamId(true);
	}

	public int getTeamId(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);
		return Robot.this.getTeamId();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().getTeamId();
		} else {
		    return 0;
		}
	    }
	}

	public boolean isEnabled() {
	    return isEnabled(true);
	}

	public boolean isEnabled(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);
		return activeState > 0;
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().isEnabled();
		} else {
		    return false;
		}
	    }
	}

	public boolean isMobile() {
	    return isMobile(true);
	}

	public boolean isMobile(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);

		return Robot.this.isMobile();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().isMobile();
		} else {
		    return false;
		}
	    }
	}

	public int getAge() {
	    return getAge(true);
	}

	public int getAge(boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_READ);

		return Robot.this.getAge();
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    return neighbour.getControl().getAge();
		} else {
		    return 0;
		}
	    }
	}

	public int getMyBotsCount() {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);

	    return world.getBotsCount(Robot.this.getTeamId(), false);
	}

	public int getOtherBotsCount() {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);

	    return world.getBotsCount(Robot.this.getTeamId(), true);
	}

	public int getWorldAge() {
	    turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);

	    return world.getAge();
	}

	/**
	 * @return the number of ancestors
	 */
	public int getGeneration() {
	    turnsControl.waitTurns(Timing.LOCAL_READ);
	    return Robot.this.getGeneration();
	}

	public void move() {
	    turnsControl.waitTurns(Timing.MOVE);
	    if (!isMobile()) {
		die();
	    }

	    world.move(Robot.this);

	}

	public void reverseTransfer(int localBankIndex, int remoteBankIndex) {
	    turnsControl.waitTurns(Timing.TRANSFER_BASE);
	    if (set.isLessThan(InstructionSet.ADVANCED)) {
		die();
	    }
	    Robot neighbour = world.getNeighbour(Robot.this);
	    if (neighbour != null) {
		Bank remoteBank = neighbour.getControl().getBank(remoteBankIndex);
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY + Timing.TRANSFER_SINGLE
			* remoteBank.getCost());
		setBank(remoteBank, localBankIndex);
	    }
	}

	public ScanResult scan() {
	    return scan(1);
	}

	public ScanResult scan(int maxDist) {
	    turnsControl.waitTurns(Timing.SCAN_BASE + Timing.SCAN_PER_DIST * (maxDist - 1));
	    if (maxDist < 1) {
		throw new IllegalArgumentException("Scan argument has to be positive");
	    }

	    if (set.isLessThan(InstructionSet.ADVANCED)) {
		die();
	    }

	    ScanResult res = null;
	    for (int dist = 1; dist < maxDist; dist++) {
		res = world.scan(Robot.this, dist);
		if (!res.isEmpty()) {
		    return res;
		}
	    }

	    return res;
	}

	private void setBank(Bank bank, int localBankIndex) {
	    if (banks == null || localBankIndex > banks.length) {
		throw new IllegalArgumentException("Bank doesn't exist");
	    }

	    banks[localBankIndex] = bank;
	}

	public void setEnabled(int pActiveState) {
	    setEnabled(pActiveState, true);
	}

	public void setEnabled(int pActiveState, boolean local) {
	    if (local) {
		turnsControl.waitTurns(Timing.LOCAL_WRITE);
		activeState = pActiveState;
	    } else {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		Robot neighbour = world.getNeighbour(Robot.this);
		if (neighbour != null) {
		    neighbour.getControl().setEnabled(pActiveState);
		}
	    }
	}

	public void transfer(int localBankIndex, int remoteBankIndex) {

	    turnsControl.waitTurns(Timing.TRANSFER_BASE + Timing.TRANSFER_SINGLE
		    * banks[localBankIndex].getCost());
	    if (set.isLessThan(InstructionSet.ADVANCED)) {
		die();
	    }
	    Robot neighbour = world.getNeighbour(Robot.this);
	    if (neighbour != null) {
		turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY);
		neighbour.getControl().setBank(banks[localBankIndex], remoteBankIndex);

	    }
	}

	public void turn(boolean right) {
	    turnsControl.waitTurns(Timing.TURN);
	    if (right) {
		facing = facing.right();
	    } else {
		facing = facing.left();
	    }

	}

    }

    /**
     * Creates a child robot
     * 
     * @param pSet maximum instruction set supported
     * @param banksCount number of banks
     * @param pMobile true if robot is mobile
     * @param parent creator of this robot
     */
    public Robot(InstructionSet pSet, int banksCount, boolean pMobile, Robot parent) {
	if (parent == null) {
	    throw new IllegalArgumentException("Parent cannot be null");
	}
	if (banksCount > GameSettings.MAX_BANKS) {
	    throw new IllegalArgumentException("Too many banks");
	}
	if (parent.getInstructionSet().isLessThan(InstructionSet.SUPER)) {
	    throw new IllegalArgumentException("Parent could not have created child");
	}
	set = pSet;
	banks = new Bank[banksCount];
	mobile = pMobile;

	world = parent.world;
	teamId = parent.teamId;
	turnsControl = new TurnManager(world.getClock());

	generation = parent.generation + 1; // TODO: check what to do if max generation reached
	serialNumber = Robot.getNextSerialNumber();
    }

    private static int getNextSerialNumber() {
	return lastSerial++;
    }

    /**
     * Creates first robot in the world
     * 
     * @param theWorld the environment of the robot
     * @param allBanks All the player's banks
     */
    public Robot(World theWorld, Bank[] allBanks) {
	if (theWorld == null || allBanks == null) {
	    throw new IllegalArgumentException("Arguments can't be null");
	}

	world = theWorld;
	set = InstructionSet.SUPER;
	generation = 0;
	serialNumber = 0;
	mobile = true;
	turnsControl = new TurnManager(world.getClock());

	Random generator = new Random();
	do {
	    teamId = generator.nextInt();
	} while (!world.validateTeamId(teamId));

	banks = new Bank[GameSettings.MAX_BANKS];

	for (int pos = 0; pos < allBanks.length; pos++) {
	    banks[pos] = allBanks[pos];
	}

    }

    /**
     * The main loop of the robot
     */
    public void run() {
	try {
	    while (true) { // TODO: improve
		if (runningBank == 0) {
		    if (banks[0] == null || banks[0].isEmpty()) {
			die("Data Hunger");
		    }
		} else {
		    if (runningBank > banks.length) {
			if (runningBank >= GameSettings.MAX_BANKS) {
			    die("Impossible Bank number");
			} else {
			    runningBank = 0;
			}
		    }
		}

		banks[runningBank].run();
	    }
	} catch (Exception all) {
	    log.error("[run] Problem running robot " + this, all);
	    die("Execution Error: " + all.getClass().getName());
	}
    }

    private void changeBank(int newBank) {
	runningBank = newBank;

	// maybe throw an exception to force bank switch?? for now the player has to cooperate

    }

    /**
     * Kills the robot and removes it from the board
     * 
     * @param reason user-friendly explanaition for the death
     */
    public void die(String reason) {
	world.remove(Robot.this);
    }

    private RobotControl getControl() {
	return control;
    }

    /**
     * @return the direction the robot is facing
     */
    public Direction getFacing() {
	return facing;

    }

    /**
     * @return the unique team Id
     */
    public int getTeamId() {
	return teamId;
    }

    private TurnManager getTurns() {
	return turnsControl;
    }

    private World getWorld() {
	return world;
    }

    public boolean isMobile() {
	return mobile;
    }

    public int getAge() {
	return (int) Math.round(turnsControl.getTurnsCount() / 1000.0);
    }

    public int getSerialNumber() {
	return serialNumber;
    }

    public int getGeneration() {
	return generation;
    }

    public InstructionSet getInstructionSet() {
	return set;
    }

    public int getActiveState() {
	return activeState;
    }

    public int getBanksCount() {
	if (banks == null) {
	    return 0;
	}
	return banks.length;
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
	    if (turnsCounter > GameSettings.MAX_AGE) {
		die("Old Age");
	    } else {
		clock.waitFor(Robot.this.getSerialNumber(), turns);
		turnsCounter += turns;
	    }
	}

	/**
	 * @return the number of turns so far
	 */
	public int getTurnsCount() {
	    return turnsCounter;
	}
    }

    @Override
    public int compareTo(Robot o) {
	// TODO Auto-generated method stub
	return 0;
    }

}
