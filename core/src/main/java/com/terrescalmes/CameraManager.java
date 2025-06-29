package com.terrescalmes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraManager {
    public static final int DISPLAY_WIDTH = 1920;
    public static final int DISPLAY_HEIGHT = 1080;
    public static final int CUBE_WIDTH = 256;
    public static final int CUBE_HEIGHT = 256;

    private OrthographicCamera camera;

    public CameraManager(OrthographicCamera camera) {
        this.camera = camera;
    }

    public static Vector2 gameToDisplayCoordinates(Vector2 gameCoordinates) {
        float x = (gameCoordinates.x - gameCoordinates.y) * (CUBE_WIDTH / 2f);
        float y = -((gameCoordinates.x + gameCoordinates.y) * (CUBE_HEIGHT / 4f));
        return new Vector2(x, y);
    }
}
