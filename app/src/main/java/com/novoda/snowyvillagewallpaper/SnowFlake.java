package com.novoda.snowyvillagewallpaper;

class SnowFlake {

    private final int flakeImageId;
    private final float speed;
    private final float x;
    private float y;

    public SnowFlake(float x, float y, int flakeImageId, float speed) {
        this.x = x;
        this.y = y;
        this.flakeImageId = flakeImageId;
        this.speed = speed;
    }

    public int getFlakeImageId() {
        return flakeImageId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void update(final int surfaceHeight, final int maxSnowflakeHeight) {
        y += speed;
        if (y > surfaceHeight) {
            y = -maxSnowflakeHeight;
        }
    }
}
