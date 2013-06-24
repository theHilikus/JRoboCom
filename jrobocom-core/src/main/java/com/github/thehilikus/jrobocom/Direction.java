package com.github.thehilikus.jrobocom;

/**
 * The four directions in a squared grid
 * 
 */
@SuppressWarnings("javadoc")
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public static final int COUNT = Direction.values().length;

    /**
     * @return the direction left from the current one
     */
    public Direction left() {
        int next = (ordinal() - 1) % COUNT;
        if (next < 0) {
            next += COUNT;
        }
        return values()[next];
    }

    /**
     * @return the direction right from the current one
     */
    public Direction right() {
        int next = (ordinal() + 1) % COUNT;
        return values()[next];
    }

    public static Direction fromInt(int position) {
	if (position > COUNT) {
	    throw new IllegalArgumentException("Invalid index for Direction: " + position);
	}
	return values()[position];
    }
}