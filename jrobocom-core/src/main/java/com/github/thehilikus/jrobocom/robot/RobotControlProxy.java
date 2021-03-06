package com.github.thehilikus.jrobocom.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.jrobocom.GameSettings;
import com.github.thehilikus.jrobocom.GameSettings.Timing;
import com.github.thehilikus.jrobocom.player.InstructionSet;
import com.github.thehilikus.jrobocom.player.ScanResult;
import com.github.thehilikus.jrobocom.robot.Robot.TurnManager;
import com.github.thehilikus.jrobocom.robot.api.RobotAction;

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
     * @see com.github.thehilikus.jrobocom.RobotControl#changeBank(int)
     */
    @Override
    public void changeBank(int newBank) {
	int penalty = Timing.getInstance().BANK_CHANGE;
	log.trace("[changeBank] Waiting {} cycles to change to bank {}", penalty, newBank);
	turnsControl.waitTurns(penalty, "Change bank");
	robot.changeBank(newBank);

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#createRobot(com.github.thehilikus.jrobocom.player.InstructionSet, int, boolean)
     */
    @Override
    public void createRobot(String name, InstructionSet pSet, int banksCount, boolean pMobile) {
	int turnsForBanks = Timing.getInstance().CREATION_BASE + Timing.getInstance().CREATION_PER_BANK * banksCount;
	if (pMobile) {
	    turnsForBanks = (int) (turnsForBanks * Timing.getInstance().MOBILITY_PENALTY + Timing.getInstance().MOBILITY_CONSTANT);
	}
	int turnsForSet = 0;
	if (pSet == InstructionSet.ADVANCED) {
	    turnsForSet = Timing.getInstance().ADVANCED_SET_PENALTY;
	}
	if (pSet == InstructionSet.SUPER) {
	    turnsForSet += Timing.getInstance().SUPER_SET_PENALTY;
	}

	int totalWait = Math.min(turnsForBanks + turnsForSet, GameSettings.getInstance().MAX_CREATE_WAIT);
	log.trace("[createRobot] Waiting {} cycles to create Robot {}", totalWait, name);
	turnsControl.waitTurns(totalWait, "Create Robot");

	robot.createRobot(name, pSet, banksCount, pMobile);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#die(java.lang.String)
     */
    @Override
    public void die(String reason) {
	this.robot.die(reason);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#die()
     */
    @Override
    public void die() {
	die("No reason specified");
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#move()
     */
    @Override
    public void move() {
	int penalty = Timing.getInstance().MOVE;
	log.trace("[move] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Move");

	robot.move();

    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#reverseTransfer(int, int)
     */
    @Override
    public int reverseTransfer(int remoteBankIndex, int localBankIndex) {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY + Timing.getInstance().TRANSFER_BASE;
	log.trace("[reverseTransfer] Waiting {} cycles to start transfer from {} to {}", penalty, remoteBankIndex, localBankIndex);
	turnsControl.waitTurns(penalty, "Reverse Transfer Wait 1/2");
	int bankComplexity = robot.reverseTransfer(localBankIndex, remoteBankIndex);

	log.trace("[reverseTransfer] Waiting {} cycles to complete transfer", Timing.getInstance().TRANSFER_SINGLE * bankComplexity);
	turnsControl.waitTurns(Timing.getInstance().TRANSFER_SINGLE * bankComplexity, "Reverse Transfer Wait 2/2");

	return bankComplexity;
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#scan()
     */
    @Override
    public ScanResult scan() {
	return scan(1);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#scan(int)
     */
    @Override
    public ScanResult scan(int maxDist) {
	int penalty = Timing.getInstance().SCAN_BASE + Timing.getInstance().SCAN_PER_DIST * (maxDist - 1);
	log.trace("[scan] Waiting {} cycles to scan {} fields", penalty, maxDist);
	turnsControl.waitTurns(penalty, "Scan");

	return robot.scan(maxDist);
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#transfer(int, int)
     */
    @Override
    public int transfer(int localBankIndex, int remoteBankIndex) {
	int penalty = Timing.getInstance().REMOTE_ACCESS_PENALTY + Timing.getInstance().TRANSFER_BASE;
	log.trace("[transfer] Waiting {} cycles to start transfer from {} to {}", penalty, localBankIndex, remoteBankIndex);
	turnsControl.waitTurns(penalty, "Transfer Wait 1/2");
	int bankComplexity = robot.transfer(localBankIndex, remoteBankIndex);

	penalty = Timing.getInstance().TRANSFER_SINGLE * bankComplexity;
	log.trace("[transfer] Waiting {} cycles to complete transfer", penalty);
	turnsControl.waitTurns(penalty, "Transfer Wait 2/2");

	return bankComplexity;
    }

    /* (non-Javadoc)
     * @see com.github.thehilikus.jrobocom.RobotControl#turn(boolean)
     */
    @Override
    public void turn(boolean right) {
	int penalty = Timing.getInstance().TURN;
	log.trace("[turn] Waiting {} cycles", penalty);
	turnsControl.waitTurns(penalty, "Turn");
	robot.turn(right);
    }

}