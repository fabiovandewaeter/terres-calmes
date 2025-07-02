package com.terrescalmes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.Player;

public class CameraManager extends OrthographicCamera {
    private static CameraManager instance;

    public static final int DISPLAY_WIDTH = 1920;
    public static final int DISPLAY_HEIGHT = 1080;
    public static final int CUBE_WIDTH = 256;
    public static final int CUBE_HEIGHT = 256;

    private CameraManager() {
        super(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        zoom *= 1.5;
    }

    public static CameraManager getInstance() {
        if (instance == null) {
            instance = new CameraManager();
        }
        return instance;
    }

    public static Vector2 gameToDisplayCoordinates(Vector2 gameCoordinates) {
        float x = (gameCoordinates.x - gameCoordinates.y) * (CUBE_WIDTH / 2f);
        float y = -((gameCoordinates.x + gameCoordinates.y) * (CUBE_HEIGHT / 4f));
        return new Vector2(x, y);
    }

    // returns the coordinates the user see on screen ; useful when clic on the
    // sprite of an entity
    public Vector2 mouseCoordinates() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        Vector3 world3 = new Vector3(mouseX, mouseY, 0);
        instance.unproject(world3);
        return new Vector2(world3.x, world3.y);
    }

    // returns the in-game coordinates
    public Vector2 mouseToGameCoordinates() {
        Vector2 world2 = mouseCoordinates();
        return displayToGameCoordinates(world2);
    }

    public static void reset() {
        instance = new CameraManager();
    }

    public static Vector2 displayToGameCoordinates(Vector2 displayCoords) {
        // Inverse de la transformation gameToDisplayCoordinates
        float dx = displayCoords.x;
        float dy = -displayCoords.y;

        // Résolution du système d'équations :
        // dx = (gx - gy) * (CUBE_WIDTH/2)
        // dy = (gx + gy) * (CUBE_HEIGHT/4)
        float diff = dx / (CUBE_WIDTH / 2f); // gx - gy
        float sum = dy / (CUBE_HEIGHT / 4f); // gx + gy

        float gx = (sum + diff) / 2f;
        float gy = (sum - diff) / 2f;

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
        Player player = EntityManager.getInstance().getPlayer();
        if (player != null) {
            // Conversion des coordonnées de jeu du joueur vers coordonnées d'affichage
            Vector2 playerDisplayPos = CameraManager.gameToDisplayCoordinates(player.getPosition());
            position.set(playerDisplayPos.x, playerDisplayPos.y, 0);
        }
        super.update();
    }

    @Override
    public void update(boolean updateFrustum) {
        super.update(updateFrustum);
    }

    // make entity move faster in north/south axis so it moves at constant speed on
    // screen
    public static Vector2 normalizeIsometric(Vector2 worldDir, float speed) {
        // 1) direction projetée en pixels
        Vector2 disp = gameToDisplayCoordinates(new Vector2(worldDir.x, worldDir.y));
        // 2) vitesse désirée en pixels (on prend CUBE_WIDTH comme référence)
        float desiredPixelSpeed = speed * CUBE_WIDTH;
        float dispLen = disp.len();
        if (dispLen == 0)
            return new Vector2(0, 0);
        // 3) facteur pour uniformiser en pixels
        float factor = desiredPixelSpeed / dispLen;
        // 4) on retourne la vraie translation en unités "monde"
        return new Vector2(worldDir.x * factor, worldDir.y * factor);
    }
}
