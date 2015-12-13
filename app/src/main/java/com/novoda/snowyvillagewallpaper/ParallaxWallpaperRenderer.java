package com.novoda.snowyvillagewallpaper;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.halfninja.wallpaper.parallax.gl.Capabilities;
import uk.co.halfninja.wallpaper.parallax.gl.Quad;
import uk.co.halfninja.wallpaper.parallax.gl.Texture;
import uk.co.halfninja.wallpaper.parallax.gl.TextureLoader;
import uk.co.halfninja.wallpaper.parallax.gl.Utils;

import static javax.microedition.khronos.opengles.GL10.*;
import static com.novoda.snowyvillagewallpaper.ParallaxWallpaper.TAG;

public final class ParallaxWallpaperRenderer implements GLSurfaceView.Renderer {

    private static final int MAX_SNOW_FLAKES_COUNT = 40;

    private static final float SKY_COLOR_R = 0.05f;
    private static final float SKY_COLOR_G = 0.06f;
    private static final float SKY_COLOR_B = 0.156f;
    private static final float SKY_COLOR_A = 1f;

    private final String[] PORTRAIT_LAYERS_FILES_NAMES = {
            "village_1.png",
            "village_2.png",
            "village_3.png",
            "village_4.png",
            "village_5.png"
    };

    private final String[] LANDSCAPE_LAYERS_FILES_NAMES = {
            "village_land_1.png",
            "village_land_2.png",
            "village_land_3.png",
            "village_land_4.png",
            "village_land_5.png"
    };

    private final String SNOW_FILE_NAME = "snow.png";

    private float offset = 0.0f;
    private int height;
    private int width;
    private int maxSnowflakeHeight;

    private final Capabilities capabilities = new Capabilities();
    private final TextureLoader textureLoader;
    private List<Quad> portraitLayers = new ArrayList<>(PORTRAIT_LAYERS_FILES_NAMES.length);
    private List<Quad> landscapeLayers = new ArrayList<>(LANDSCAPE_LAYERS_FILES_NAMES.length);
    private List<Quad> snowFlakesQuads = new ArrayList<>(SnowFlakeTypes.count());
    private List<Quad> currentLayers = new ArrayList<>();
    private List<SnowFlake> snowFlakes = new ArrayList<>(MAX_SNOW_FLAKES_COUNT);

    private GL10 gl;

    public ParallaxWallpaperRenderer(AssetManager assets) {
        this.textureLoader = new TextureLoader(capabilities, assets);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig cfg) {
        this.gl = gl;
        capabilities.reload(gl);

        try {
            reloadLayers();
        } catch (IOException e) {
            Log.e(TAG, "Error loading textures", e);
        }
    }

    public void reloadLayers() throws IOException {
        if (gl != null && layersNotAlreadyLoaded()) {
            portraitLayers.clear();
            landscapeLayers.clear();
            snowFlakesQuads.clear();
            textureLoader.clear(gl);
            for (String bitmapPath : LANDSCAPE_LAYERS_FILES_NAMES) {
                loadLayerTo(bitmapPath, landscapeLayers);
            }
            for (String bitmapPath : PORTRAIT_LAYERS_FILES_NAMES) {
                loadLayerTo(bitmapPath, portraitLayers);
            }
            loadSnowFlakesLayers();
        }
    }

    private boolean layersNotAlreadyLoaded() {
        return snowFlakesQuads.isEmpty() && portraitLayers.isEmpty() && landscapeLayers.isEmpty();
    }

    private void loadLayerTo(String bitmapPath, List<Quad> layerList) throws IOException {
        Quad quad = new Quad();
        Texture tex = textureLoader.loadTextureFromFile(gl, bitmapPath);
        quad.setTexture(tex);
        layerList.add(0, quad);
    }

    private void loadSnowFlakesLayers() throws IOException {
        Texture tex = textureLoader.loadTextureFromFile(gl, SNOW_FILE_NAME);
        for (int i = 0; i < SnowFlakeTypes.count(); i++) {
            Quad quad = new Quad();
            quad.setTexture(tex);
            snowFlakesQuads.add(0, quad);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(SKY_COLOR_R, SKY_COLOR_G, SKY_COLOR_B, SKY_COLOR_A);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glColor4f(1f, 1f, 1f, 1f);
        for (Quad quad : currentLayers) {
            quad.setX(offset * (width - quad.getWidth()));
            quad.draw(gl);
        }

        for (SnowFlake flake : snowFlakes) {
            Quad quad = snowFlakesQuads.get(flake.getFlakeId());
            updateSnowFlakeYPosition(flake);
            quad.setY(flake.getY());
            quad.setX(flake.getX());
            quad.draw(gl);
        }
    }

    private void updateSnowFlakeYPosition(SnowFlake flake) {
        float newY = flake.getY() + flake.getSpeed();
        if (newY > height) {
            newY = -maxSnowflakeHeight;
        }
        flake.setY(newY);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (w == width && h == height) {
            return;
        }
        width = w;
        height = h;
        Utils.pixelProjection(gl, w, h);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        resizeLayers();
        setCurrentLayers();

        createSnowFlakes();
        maxSnowflakeHeight = calculateMaxSnowFlakeHeight();
    }

    public void resizeLayers() {
        if (portraitLayers.isEmpty()) {
            return;
        }
        float portraitRatio = getPortraitRatio();
        resizeLayers(portraitRatio, portraitLayers);

        float landscapeRatio = getLandscapeRatio();
        resizeLayers(landscapeRatio, landscapeLayers);

        resizeSnowLayers(portraitRatio);
    }

    private float getPortraitRatio() {
        int bitmapHeight = portraitLayers.get(0).getTexture().getBitmapHeight();
        return (float) height / bitmapHeight;
    }

    private float getLandscapeRatio() {
        int bitmapHeight = landscapeLayers.get(0).getTexture().getBitmapHeight();
        return (float) height / bitmapHeight;
    }

    private void resizeLayers(float landscapeRatio, List<Quad> layers) {
        for (Quad quad : layers) {
            resizeLayer(quad, landscapeRatio);
        }
    }

    private void resizeLayer(Quad quad, float ratio) {
        quad.setHeight(quad.getTexture().getBitmapHeight() * ratio);
        quad.setWidth(quad.getTexture().getBitmapWidth() * ratio);
    }

    private void resizeSnowLayers(float portraitRatio) {
        resizeLayer(snowFlakesQuads.get(0), portraitRatio * SnowFlakeTypes.SMALL.getTextureRatio());
        resizeLayer(snowFlakesQuads.get(1), portraitRatio * SnowFlakeTypes.MEDIUM.getTextureRatio());
        resizeLayer(snowFlakesQuads.get(2), portraitRatio * SnowFlakeTypes.BIG.getTextureRatio());
    }

    private void setCurrentLayers() {
        currentLayers.clear();
        if (height > width) {
            currentLayers.addAll(portraitLayers);
        } else {
            currentLayers.addAll(landscapeLayers);
        }
    }

    private void createSnowFlakes() {
        Random rng = new Random();
        snowFlakes.clear();
        for (int i = 0; i < MAX_SNOW_FLAKES_COUNT; i++) {
            float startX = rng.nextFloat() * width;
            float startY = 0 - rng.nextFloat() * height;
            int snowFlakeShapeIndex = rng.nextInt(SnowFlakeTypes.count());
            float speed = SnowFlakeTypes.values()[snowFlakeShapeIndex].getBaseSpeed() + rng.nextFloat();
            snowFlakes.add(new SnowFlake(startX, startY, snowFlakeShapeIndex, speed));
        }
    }

    private int calculateMaxSnowFlakeHeight() {
        int max = 0;
        for (Quad quad : snowFlakesQuads) {
            max = Math.max(max, (int) quad.getHeight());
        }
        return max;
    }

    public void setOffset(float xOffset) {
        offset = xOffset;
    }

}
