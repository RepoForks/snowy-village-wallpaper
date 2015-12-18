package com.novoda.snowyvillagewallpaper;

class SantaTracker {

    private static final float SPEED = 6.0f;

    private final float skyWidth;
    private final float skyHeight;
    private final float santaWidth;
    private final float santaHeight;

    private float x;
    private float y;
    private Direction direction;

    public SantaTracker(float skyWidth, float skyHeight, float santaWidth, float santaHeight) {
        this.skyWidth = skyWidth;
        this.skyHeight = skyHeight;
        this.santaWidth = santaWidth;
        this.santaHeight = santaHeight;
        x = 0;
        direction = Direction.TO_RIGHT;
        resetPosition();
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
            flipDirection();
            resetPosition();
        }
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

    private enum Direction {
        TO_LEFT,
        TO_RIGHT
    }
}
