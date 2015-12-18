package com.novoda.snowyvillagewallpaper;

class SantaTracker {

    private static final float SPEED = 4.0f;

    private final float skyWidth;
    private final float skyHeight;
    private final float santaWidth;
    private final float santaHeight;

    private float x;
    private float y;

    public SantaTracker(float skyWidth, float skyHeight, float santaWidth, float santaHeight) {
        this.skyWidth = skyWidth;
        this.skyHeight = skyHeight;
        this.santaWidth = santaWidth;
        this.santaHeight = santaHeight;
        x = 0;
        y = initRandomY();
    }

    private float initRandomY() {
        return (float) ((Math.random() * (skyHeight - santaHeight) * 0.6f) + (skyHeight - santaHeight) * 0.1f);
    }

    public void updatePosition() {
        x += SPEED;
        if (x > skyWidth) {
            x = -santaWidth;
            y = initRandomY();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
