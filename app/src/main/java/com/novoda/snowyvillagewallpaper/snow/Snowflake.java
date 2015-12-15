package com.novoda.snowyvillagewallpaper.snow;

public class Snowflake {

    private final SnowflakeType snowflakeType;
    private final float snowflakeSize;
    private final float speedY;
    private final float speedX;

    private float x;
    private float y;

    Snowflake(SnowflakeType snowflakeType, float snowflakeSize, float speedX, float speedY) {
        this.snowflakeSize = snowflakeSize;
        this.snowflakeType = snowflakeType;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public SnowflakeType getSnowflakeType() {
        return snowflakeType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    void updatePosition() {
        x += speedX;
        y += speedY;
    }

    void resetPositionTo(float initialX, float initialY) {
        x = initialX;
        y = initialY;
    }

    @SuppressWarnings("RedundantIfStatement")  // No way man
    public boolean isInViewport(int viewportWidth, int viewportHeight) {
        if (y > viewportHeight) {
            return false;
        }
        if (x < -snowflakeSize || x > viewportWidth) {
            return false;
        }
        return true;
    }

}
