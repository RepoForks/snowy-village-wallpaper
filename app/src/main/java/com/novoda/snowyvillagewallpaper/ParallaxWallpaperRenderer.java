package com.novoda.snowyvillagewallpaper;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.novoda.snowyvillagewallpaper.snow.BlizzardMaster;
import com.novoda.snowyvillagewallpaper.snow.Snowflake;
import com.novoda.snowyvillagewallpaper.snow.SnowflakeType;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.halfninja.wallpaper.parallax.gl.Capabilities;
import uk.co.halfninja.wallpaper.parallax.gl.Quad;
import uk.co.halfninja.wallpaper.parallax.gl.Texture;
import uk.co.halfninja.wallpaper.parallax.gl.TextureLoader;
import uk.co.halfninja.wallpaper.parallax.gl.Utils;

import static com.novoda.snowyvillagewallpaper.ParallaxWallpaper.TAG;
import static javax.microedition.khronos.opengles.GL10.*;

public final class ParallaxWallpaperRenderer implements GLSurfaceView.Renderer {

    private static final float SKY_COLOR_R = 0.05f;
    private static final float SKY_COLOR_G = 0.06f;
    private static final float SKY_COLOR_B = 0.156f;
    private static final float SKY_COLOR_A = 1f;

    private static final String[] PORTRAIT_LAYERS_FILES_NAMES = {
            "village_1.png",
            "village_2.png",
            "village_3.png",
            "village_4.png",
            "village_5.png"
    };

    private static final String[] LANDSCAPE_LAYERS_FILES_NAMES = {
            "village_land_1.png",
            "village_land_2.png",
            "village_land_3.png",
            "village_land_4.png",
            "village_land_5.png"
    };

    private static final String SNOW_FILE_NAME = "snow.png";

    private float offset = 0.0f;
    private int surfaceHeight;
    private int surfaceWidth;

    private final BlizzardMaster blizzardMaster;

    private final Capabilities capabilities = new Capabilities();
    private final TextureLoader textureLoader;
    private List<Quad> portraitLayers = new ArrayList<>(PORTRAIT_LAYERS_FILES_NAMES.length);
    private List<Quad> landscapeLayers = new ArrayList<>(LANDSCAPE_LAYERS_FILES_NAMES.length);
    private List<Quad> snowflakesQuads = new ArrayList<>(SnowflakeType.count());
    private List<Quad> currentLayers = new ArrayList<>();

    private GL10 gl;
    private Texture snowTexture;

    public ParallaxWallpaperRenderer(AssetManager assets) {
        this.textureLoader = new TextureLoader(capabilities, assets);
        this.blizzardMaster = new BlizzardMaster();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig cfg) {
        this.gl = gl;
        capabilities.reload(gl);

        try {
            loadSnowTexture(gl);
            reloadLayers();
        } catch (IOException e) {
            Log.e(TAG, "Error loading textures", e);
        }
    }

    private void loadSnowTexture(GL10 gl) throws IOException {
        snowTexture = textureLoader.loadTextureFromFile(gl, SNOW_FILE_NAME);
        blizzardMaster.setSnowBitmapSize(snowTexture.getBitmapWidth());
    }

    public void reloadLayers() throws IOException {
        if (gl != null && layersNotAlreadyLoaded()) {
            portraitLayers.clear();
            landscapeLayers.clear();
            snowflakesQuads.clear();
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
        return snowflakesQuads.isEmpty() && portraitLayers.isEmpty() && landscapeLayers.isEmpty();
    }

    private void loadLayerTo(String bitmapPath, List<Quad> layerList) throws IOException {
        Quad quad = new Quad();
        Texture tex = textureLoader.loadTextureFromFile(gl, bitmapPath);
        quad.setTexture(tex);
        layerList.add(0, quad);
    }

    private void loadSnowFlakesLayers() throws IOException {
        for (SnowflakeType snowflakeType : SnowflakeType.values()) {
            Quad quad = new Quad();
            quad.setTexture(snowTexture);
            snowflakesQuads.add(snowflakeType.ordinal(), quad);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        blizzardMaster.advanceFrame();
        drawSkyOn(gl);
       // drawBackdropOn(gl);
        drawSnowflakesOn(gl);
    }

    private void drawSkyOn(GL10 gl) {
        gl.glClearColor(SKY_COLOR_R, SKY_COLOR_G, SKY_COLOR_B, SKY_COLOR_A);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glColor4f(1f, 1f, 1f, 1f);
    }

    private void drawBackdropOn(GL10 gl) {
        for (Quad quad : currentLayers) {
            quad.setX(offset * (surfaceWidth - quad.getWidth()));
            quad.draw(gl);
        }
    }

    private void drawSnowflakesOn(GL10 gl) {
        for (Snowflake flake : blizzardMaster.getSnowflakes()) {
            Quad quad = snowflakesQuads.get(flake.getSnowflakeType().ordinal());
            quad.setY(flake.getY());
            quad.setX(flake.getX());
            quad.draw(gl);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (w == surfaceWidth && h == surfaceHeight) {
            return;
        }
        surfaceWidth = w;
        surfaceHeight = h;
        Utils.pixelProjection(gl, w, h);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float portraitRatio = getPortraitRatio();
        blizzardMaster.onViewportSizeChanged(w, h, portraitRatio);
        resizeLayers();
        setCurrentLayers();
    }

    public void resizeLayers() {
        if (portraitLayers.isEmpty()) {
            return;
        }
        resizeLayers(getPortraitRatio(), portraitLayers);

        float landscapeRatio = getLandscapeRatio();
        resizeLayers(landscapeRatio, landscapeLayers);

        resizeSnowLayers();
    }

    private float getPortraitRatio() {
        int bitmapHeight = portraitLayers.get(0).getTexture().getBitmapHeight();
        return (float) surfaceHeight / bitmapHeight;
    }

    private float getLandscapeRatio() {
        int bitmapHeight = landscapeLayers.get(0).getTexture().getBitmapHeight();
        return (float) surfaceHeight / bitmapHeight;
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

    private void resizeSnowLayers() {
        for (SnowflakeType snowflakeType : SnowflakeType.values()) {
            Quad quad = snowflakesQuads.get(snowflakeType.ordinal());
            float snowflakeSize = blizzardMaster.getSnowflakeSizeFor(snowflakeType);
            Log.w("BlizzardSlave", String.format("Setting size of %1$f for type %2$s", snowflakeSize, snowflakeType.name()));
            quad.setWidth(snowflakeSize);
            quad.setHeight(snowflakeSize);
        }
    }

    private void setCurrentLayers() {
        currentLayers.clear();
        if (surfaceHeight > surfaceWidth) {
            currentLayers.addAll(portraitLayers);
        } else {
            currentLayers.addAll(landscapeLayers);
        }
    }

    public void setOffset(float xOffset) {
        offset = xOffset;
    }

}
