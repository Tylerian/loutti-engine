package com.luotti.engine.utilities.math;

public enum Direction {

    EAST, WEST, NORTH, SOUTH,
    NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST;

    public int num;
    public int modX;
    public int modY;

    public static final Direction NEUTRAL = Direction.EAST;
    public static final Direction[] VALUES = Direction.values();

    Direction()
    {
        this.num = this.ordinal();

        switch (this.num) {
            // North
            case 0:
                this.modX = 0;
                this.modY = -1;
                break;

            // NorthEast
            case 1:
                this.modX = +1;
                this.modY = -1;
                break;

            // East
            case 2:
                this.modX = +1;
                this.modY = 0;
                break;

            // SouthEast
            case 3:
                this.modX = +1;
                this.modY = +1;
                break;

            // South
            case 4:
                this.modX = 0;
                this.modY = +1;
                break;

            // SouthWest
            case 5:
                this.modX = -1;
                this.modY = +1;
                break;

            // West
            case 6:
                this.modX = -1;
                this.modY = 0;
                break;

            // NorthWest
            case 7:
                this.modX = -1;
                this.modY = -1;
                break;

            // Uh...
            default:
                this.modX = 0;
                this.modY = 0;
        }
    }

    public Direction invert()
    {
        return VALUES[(this.num +
        (VALUES.length / 2)) % VALUES.length];
    }

    public static Direction random() {
        return VALUES[Randomizer.nextInt(0, 7)];
    }

    public static Direction get(int num) {
        return VALUES[num];
    }

    public final Direction transform(Direction dir) {
        return VALUES[(this.num + dir.num) % VALUES.length];
    }

    public static Direction calculate(int x1, int y1, int x2, int y2) {

        int degrees = (int) Math.toDegrees(
            Math.atan2((y2 - y1), (x2 - x1))
        );

        switch (degrees)
        {
            case 0: {
                return Direction.EAST;
            }

            case 45: {
                return Direction.NORTH_EAST;
            }

            case 90: {
                return Direction.NORTH;
            }

            case 135: {
                return Direction.NORTH_WEST;
            }

            case 180: {
                return Direction.WEST;
            }

            case -135: {
                return Direction.SOUTH_WEST;
            }

            case -90: {
                return Direction.SOUTH;
            }

            case -45: {
                return Direction.SOUTH_EAST;
            }

            default: return Direction.NEUTRAL;
        }
    }
}
