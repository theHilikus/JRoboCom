package ca.hilikus.jrobocom.robot.api;

import ca.hilikus.jrobocom.player.InstructionSet;
import ca.hilikus.jrobocom.player.ScanResult;
import ca.hilikus.jrobocom.robot.Robot;

/**
 * Interface for controlling active actions in a robot
 * 
 * @author hilikus
 * 
 */
public interface RobotAction {

    /**
     * Changes the running bank <u>once the current bank ends its execution</u>
     * 
     * @param newBank the 0-based index of the bank to run
     * 
     */
    public void changeBank(int newBank);

    /**
     * Creates a new, empty robot in the adjacent field in the robot's current direction
     * 
     * @param name robot's friendly name
     * @param pSet the instruction set of the new robot
     * @param banksCount the number of empty banks to create
     * @param pMobile true if the robot should be able to move
     */
    public void createRobot(String name, InstructionSet pSet, int banksCount, boolean pMobile);

    /**
     * kills the robot
     * 
     * @param reason an optional description of the death
     * @see #die()
     */
    public void die(String reason);

    /**
     * Kill the robot with an unspecified reason
     * 
     * @see Robot#die(String)
     */
    public void die();

    /**
     * Moves the robot one field forward
     */
    public void move();

    /**
     * Uploads a local bank and overrides with it what was in the remote bank
     * 
     * @param localBankIndex 0-based index of the local bank to upload
     * @param remoteBankIndex 0-based index of remote bank space to use
     * @return the complexity of the uploaded bank or 0 if upload failed
     */
    public int transfer(int localBankIndex, int remoteBankIndex);

    /**
     * Downloads a remote bank and overrides with it what was in the local bank
     * 
     * @param localBankIndex 0-based index of the local bank space to use
     * @param remoteBankIndex 0-based index of remote bank to download
     * @return the complexity of the downloaded bank or 0 if download failed
     */
    public int reverseTransfer(int localBankIndex, int remoteBankIndex);

    /**
     * Scans the reference field
     * 
     * @return a report of the scan
     * @see #scan(int)
     */
    public ScanResult scan();

    /**
     * Progressively scans several fields in the facing direction. Stops if something is found
     * 
     * @param maxDist distance at which to stop the scan
     * @return a report of the scan
     * @see #scan()
     */
    public ScanResult scan(int maxDist);

    /**
     * Turns the robot 90 degrees
     * 
     * @param right true if robot should turn right, otherwise turn left
     */
    public void turn(boolean right);

}