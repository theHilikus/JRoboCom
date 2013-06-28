package com.github.thehilikus.jrobocom;

/**
 * The four directions in a squared grid
 * 
 */
public enum Direction {
    /**
     * Up
     */
    NORTH, 
    /**
     * right
     */
    EAST,
    /**
     * down
     */
    SOUTH,
    /**
     * left
     */
    WEST;

    /**
     * number of directions
     */
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

    /**
     * Mapping from int to Direction
     * 
     * @param position the numerical representation
     * @return a mapping direction
     */
    public static Direction fromInt(int position) {
	if (position > COUNT) {
	    throw new IllegalArgumentException("Invalid index for Direction: " + position);
	}
	return values()[position];
    }
}