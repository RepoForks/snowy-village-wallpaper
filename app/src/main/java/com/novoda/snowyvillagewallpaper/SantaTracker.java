package com.novoda.snowyvillagewallpaper;

class SantaTracker {

    private static final float SPEED = 6.0f;
    private static final long MILLISECONDS_IN_SECONDS = 1000;
    private static final long MIN_INTERVAL_BETWEEN_VISITS = 2 * MILLISECONDS_IN_SECONDS;

    private final float skyWidth;
    private final float skyHeight;
    private final float santaWidth;
    private final float santaHeight;

    private float x;
    private float y;
    private Direction direction;

    private final Clock clock;
    private long nextTimeInTown;

    public SantaTracker(float skyWidth, float skyHeight, float santaWidth, float santaHeight, Clock clock) {
        this.skyWidth = skyWidth;
        this.skyHeight = skyHeight;
        this.santaWidth = santaWidth;
        this.santaHeight = santaHeight;
        this.clock = clock;

        direction = Direction.TO_RIGHT;
        resetPosition();
        calculateNextVisitTime();
    }

    private void resetPosition() {
        y = initRandomY();
        if (direction == Direction.TO_RIGHT) {
            x = -santaWidth;
        } else {
            x = skyWidth;
        }
    }

    private float initRandomY() {
        return (float) ((Math.random() * (skyHeight - santaHeight) * 0.6f) + (skyHeight - santaHeight) * 0.1f);
    }

    public void updatePosition() {
        if (direction == Direction.TO_RIGHT) {
            x += SPEED;
        } else {
            x -= SPEED;
        }
        if (x > skyWidth || x < -santaWidth) {
            calculateNextVisitTime();
            flipDirection();
            resetPosition();
        }
    }

    private void calculateNextVisitTime() {
        nextTimeInTown = clock.getCurrentTime() + MIN_INTERVAL_BETWEEN_VISITS;
    }

    private void flipDirection() {
        if (direction == Direction.TO_LEFT) {
            direction = Direction.TO_RIGHT;
        } else {
            direction = Direction.TO_LEFT;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean movingToRight() {
        return direction == Direction.TO_RIGHT;
    }

    public boolean isSantaInTown() {
        return clock.getCurrentTime() > nextTimeInTown;
    }

    private enum Direction {
        TO_LEFT,
        TO_RIGHT
    }
}
