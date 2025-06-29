package com.terrescalmes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraManager extends OrthographicCamera {
    public static final int DISPLAY_WIDTH = 1920;
    public static final int DISPLAY_HEIGHT = 1080;
    public static final int CUBE_WIDTH = 256;
    public static final int CUBE_HEIGHT = 256;
    public static final int CUBE_FACE_HEIGHT = CUBE_HEIGHT / 2;

    public CameraManager() {
        super(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    public static Vector2 gameToDisplayCoordinates(Vector2 gameCoordinates) {
        float x = (gameCoordinates.x - gameCoordinates.y) * (CUBE_WIDTH / 2f) -
                (CUBE_WIDTH / 2f);
        float y = -((gameCoordinates.x + gameCoordinates.y) * (CUBE_HEIGHT / 4f)) -
                CUBE_HEIGHT;
        return new Vector2(x, y);
    }

    public static Vector2 displayToGameCoordinates(Vector2 disp) {
        // décale pour annuler le -CUBE_WIDTH/2 et le -CUBE_HEIGHT de ta formule
        // d'affichage
        float dx = disp.x + CUBE_WIDTH / 2f;
        float dy = -(disp.y + CUBE_HEIGHT);

        // on sait que :
        // dx = (gx - gy) * (CUBE_WIDTH/2)
        // dy = (gx + gy) * (CUBE_HEIGHT/4)
        // donc :
        float diff = dx / (CUBE_WIDTH / 2f); // gx - gy
        float sum = dy / (CUBE_HEIGHT / 4f); // gx + gy

        float gx = (sum + diff) / 2;
        float gy = (sum - diff) / 2;

        // offset because we use top of cube and not all cube
        gx += 1.5;
        gy += 2.5;

        return new Vector2(gx, gy);
    }

    public void handleInputs(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && zoom > 0.005) {
            zoom -= 0.005;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            zoom += 0.005;
        }
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void update(boolean updateFrustum) {
        super.update(updateFrustum);
    }
}
