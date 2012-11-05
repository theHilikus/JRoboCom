package ca.hilikus.jrobocom;

/**
 * The four directions in a squared grid
 * 
 */
@SuppressWarnings("javadoc")
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    private static final int size = Direction.values().length;

    /**
     * @return the direction left from the current one
     */
    public Direction left() {
        int next = (ordinal() - 1) % size;
        return values()[next];
    }

    /**
     * @return the direction right from the current one
     */
    public Direction right() {
        int next = (ordinal() + 1) % size;
        return values()[next];

    }
}