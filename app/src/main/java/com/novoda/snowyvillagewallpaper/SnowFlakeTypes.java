package com.novoda.snowyvillagewallpaper;

enum SnowFlakeTypes {
    SMALL(1f, 0.10f),
    MEDIUM(2f, 0.28f),
    BIG(3f, 0.42f);

    private final float speed;
    private final float textureRatio;

    SnowFlakeTypes(float speed, float textureRatio) {
        this.speed = speed;
        this.textureRatio = textureRatio;
    }

    public static int count() {
        return values().length;
    }

    public float getSpeed() {
        return speed;
    }

    public float getTextureRatio() {
        return textureRatio;
    }
}
