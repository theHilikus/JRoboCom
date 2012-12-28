package ca.hilikus.jrobocom.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.jrobocom.GameSettings;
import ca.hilikus.jrobocom.GameSettings.Timing;
import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.robot.Robot.TurnManager;
import ca.hilikus.jrobocom.robot.api.RobotAction;

/**
 * Interface used from Banks. Maps one-to-one with robots on one end; on the other end it can change
 * banks
 * 
 * @author hilikus
 */
public final class RobotControlProxy implements RobotAction {
    /**
     * the robot that gets controlled
     */
    private final Robot robot;

    private final TurnManager turnsControl;

    private static final Logger log = LoggerFactory.getLogger(RobotControlProxy.class);

    /**
     * @param robot
     */
    RobotControlProxy(Robot robot) {
	this.robot = robot;
	turnsControl = robot.getTurnsControl();
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#changeBank(int)
     */
    @Override
    public void changeBank(int newBank) {
	log.trace("[changeBank] Waiting {} cycles to change to bank {}", Timing.BANK_CHANGE, newBank);
	turnsControl.waitTurns(Timing.BANK_CHANGE);
	robot.changeBank(newBank);

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#createRobot(ca.hilikus.jrobocom.player.InstructionSet, int, boolean)
     */
    @Override
    public void createRobot(String name, InstructionSet pSet, int banksCount, boolean pMobile) {
	int turnsForBanks = Timing.CREATION_BASE + Timing.CREATION_PER_BANK * banksCount;
	if (pMobile) {
	    turnsForBanks = (int) (turnsForBanks * Timing.MOBILITY_PENALTY + Timing.MOBILITY_CONSTANT);
	}
	int turnsForSet = 0;
	if (pSet == InstructionSet.ADVANCED) {
	    turnsForSet = Timing.ADVANCED_SET_PENALTY;
	}
	if (pSet == InstructionSet.SUPER) {
	    turnsForSet += Timing.SUPER_SET_PENALTY;
	}

	int totalWait = Math.min(turnsForBanks + turnsForSet, GameSettings.MAX_CREATE_WAIT);
	log.trace("[createRobot] Waiting {} cycles to create Robot {}", totalWait, name);
	turnsControl.waitTurns(totalWait);

	robot.createRobot(name, pSet, banksCount, pMobile);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#die(java.lang.String)
     */
    @Override
    public void die(String reason) {
	this.robot.die(reason);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#die()
     */
    @Override
    public void die() {
	die("No reason specified");
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#move()
     */
    @Override
    public void move() {
	log.trace("[move] Waiting {} cycles", Timing.MOVE);
	turnsControl.waitTurns(Timing.MOVE);

	robot.move();

    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#reverseTransfer(int, int)
     */
    @Override
    public int reverseTransfer(int remoteBankIndex, int localBankIndex) {
	log.trace("[reverseTransfer] Waiting {} cycles to start transfer from {} to {}",
		Timing.REMOTE_ACCESS_PENALTY + Timing.TRANSFER_BASE, remoteBankIndex, localBankIndex);
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY + Timing.TRANSFER_BASE);
	int bankComplexity = robot.reverseTransfer(localBankIndex, remoteBankIndex);

	log.trace("[reverseTransfer] Waiting {} cycles to complete transfer", Timing.TRANSFER_SINGLE
		* bankComplexity);
	turnsControl.waitTurns(Timing.TRANSFER_SINGLE * bankComplexity);

	return bankComplexity;
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#scan()
     */
    @Override
    public ScanResult scan() {
	return scan(1);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#scan(int)
     */
    @Override
    public ScanResult scan(int maxDist) {
	log.trace("[scan] Waiting {} cycles to scan {} fields", Timing.SCAN_BASE + Timing.SCAN_PER_DIST
		* (maxDist - 1), maxDist);
	turnsControl.waitTurns(Timing.SCAN_BASE + Timing.SCAN_PER_DIST * (maxDist - 1));

	return robot.scan(maxDist);
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#transfer(int, int)
     */
    @Override
    public int transfer(int localBankIndex, int remoteBankIndex) {
	log.trace("[transfer] Waiting {} cycles to start transfer from {} to {}",
		Timing.REMOTE_ACCESS_PENALTY + Timing.TRANSFER_BASE, localBankIndex, remoteBankIndex);
	turnsControl.waitTurns(Timing.REMOTE_ACCESS_PENALTY + Timing.TRANSFER_BASE);
	int bankComplexity = robot.transfer(localBankIndex, remoteBankIndex);

	log.trace("[transfer] Waiting {} cycles to complete transfer", Timing.TRANSFER_SINGLE
		* bankComplexity);
	turnsControl.waitTurns(Timing.TRANSFER_SINGLE * bankComplexity);

	return bankComplexity;
    }

    /* (non-Javadoc)
     * @see ca.hilikus.jrobocom.RobotControl#turn(boolean)
     */
    @Override
    public void turn(boolean right) {
	log.trace("[turn] Waiting {} cycles", Timing.TURN);
	turnsControl.waitTurns(Timing.TURN);
	robot.turn(right);
    }

}