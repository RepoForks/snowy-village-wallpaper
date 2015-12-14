package com.novoda.snowyvillagewallpaper.snow;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlizzardMaster {

    private static final int MAX_SNOWFLAKES_COUNT = 40;
    private static final double SPEED_X_OFFSET = .5d;
    private static final float MAX_SPEED_X = 1.5f;

    private int viewportWidth;
    private int viewportHeight;
    private float portraitRatio;

    private List<Snowflake> snowflakes = new ArrayList<>(MAX_SNOWFLAKES_COUNT);
    private Random rng = new Random();
    private int snowTextureSize;

    public void onViewportSizeChanged(int viewportWidth, int viewportHeight, float portraitRatio) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.portraitRatio = portraitRatio;

        removeAllSnowflakes();
        createSnowflakes();
    }

    private void removeAllSnowflakes() {
        snowflakes.clear();
    }

    private void createSnowflakes() {
        for (int i = 0; i < MAX_SNOWFLAKES_COUNT; i++) {
            SnowflakeType snowflakeType = getRandomSnowflakeTypeUsing(rng);
            float speedY = getRandomSnowflakeSpeedYUsing(rng, snowflakeType);
            float speedX = getRandomSpeedXUsing(rng);

            int snowflakeSize = getSnowflakeSizeFor(snowflakeType);
            Snowflake snowflake = new Snowflake(snowflakeType, snowflakeSize, speedX, speedY);

            setRandomInitialPositionFor(snowflake);

            snowflakes.add(snowflake);
        }
    }

    private void setRandomInitialPositionFor(Snowflake snowflake) {
        float initialX = rng.nextFloat() * viewportWidth;
        float initialY = 0 - rng.nextFloat() * viewportHeight;
        snowflake.resetPositionTo(initialX, initialY);
    }

    private SnowflakeType getRandomSnowflakeTypeUsing(Random rng) {
        int typeIndex = rng.nextInt(SnowflakeType.count());
        return SnowflakeType.values()[typeIndex];
    }

    private float getRandomSnowflakeSpeedYUsing(Random rng, SnowflakeType snowflakeType) {
        return snowflakeType.getBaseSpeed() + rng.nextFloat();
    }

    private float getRandomSpeedXUsing(Random rng) {
        return (float) (rng.nextFloat() - SPEED_X_OFFSET) * MAX_SPEED_X;
    }

    public Iterable<Snowflake> getSnowflakes() {
        return snowflakes;
    }

    public void advanceFrame() {
        for (Snowflake snowflake : snowflakes) {
            snowflake.updatePosition();

            if (hasLeftViewport(snowflake)) {
                setRandomInitialPositionFor(snowflake);
            }
        }
    }

    private boolean hasLeftViewport(Snowflake snowflake) {
        return !snowflake.isInViewport(viewportWidth, viewportHeight);
    }

    public int getSnowflakeSizeFor(SnowflakeType snowflakeType) {
        return (int) (portraitRatio * snowflakeType.getTextureRatio() * snowTextureSize);
    }

    public void setSnowTextureSize(int snowTextureSize) {
        this.snowTextureSize = snowTextureSize;
    }
}
