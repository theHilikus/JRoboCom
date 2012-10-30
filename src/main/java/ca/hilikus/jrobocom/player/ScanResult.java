package ca.hilikus.jrobocom.player;

/**
 * Information about a scan
 * 
 * @author hilikus
 * 
 */
public class ScanResult {
    private int distance;
    private Found result;

    /**
     * Scan status
     * 
     */
    public enum Found {
	/**
	 * the field is empty
	 */
	EMPTY, /**
	 * the field contains a robot from another team
	 */
	ENEMY, /**
	 * the field contains a robot from the same team
	 */
	FRIEND
    }

    /**
     * Main constructor
     * 
     * @param pResult the scan outcome
     * @param dist relevant distance of the outcome
     */
    public ScanResult(Found pResult, int dist) {
	result = pResult;
	distance = dist;
    }

    /**
     * @return true if the scan didn't find any robots
     */
    public boolean isEmpty() {
	return result == Found.EMPTY;
    }

    /**
     * @return the distance
     */
    public int getDistance() {
	return distance;
    }

    /**
     * @return the result
     */
    public Found getResult() {
	return result;
    }

}
