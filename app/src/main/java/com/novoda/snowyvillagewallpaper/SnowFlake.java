package com.novoda.snowyvillagewallpaper;

class SnowFlake {

    private final int flakeId;
    private final float speed;
    private final float x;
    private float y;

    public SnowFlake(float x, float y, int flakeId, float speed) {
        this.x = x;
        this.y = y;
        this.flakeId = flakeId;
        this.speed = speed;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getFlakeId() {
        return flakeId;
    }

    public float getSpeed() {
        return speed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
