package com.terrescalmes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.entities.Player;

public class TopDownCameraManager extends OrthographicCamera {

    private static TopDownCameraManager instance;

    public static final int CUBE_WIDTH = 128;
    public static final int CUBE_HEIGHT = 128;

    private TopDownCameraManager(int displayWidth, int displayHeight) {
        super(displayWidth, displayHeight);
    }

    public static TopDownCameraManager getInstance(int displayWidth, int displayHeight) {
        if (instance == null) {
            instance = new TopDownCameraManager(displayWidth, displayHeight);
        }
        return instance;
    }

    // Pour une vue top-down, les coordonnées de jeu sont directement
    // proportionnelles aux coordonnées d'affichage
    public static Vector2 gameToDisplayCoordinates(Vector2 gameCoordinates) {
        float x = gameCoordinates.x * CUBE_WIDTH;
        float y = gameCoordinates.y * CUBE_HEIGHT;
        return new Vector2(x, y);
    }

    // Retourne les coordonnées que l'utilisateur voit à l'écran ; utile lors du
    // clic sur le sprite d'une entité
    public Vector2 mouseCoordinates() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        Vector3 world3 = new Vector3(mouseX, mouseY, 0);
        instance.unproject(world3);
        return new Vector2(world3.x, world3.y);
    }

    // Retourne les coordonnées de jeu
    public Vector2 mouseToGameCoordinates() {
        Vector2 world2 = mouseCoordinates();
        return displayToGameCoordinates(world2);
    }

    public static void reset(int displayWidth, int displayHeight) {
        instance = new TopDownCameraManager(displayWidth, displayHeight);
    }

    public static Vector2 displayToGameCoordinates(Vector2 displayCoords) {
        // Simple conversion inverse pour la vue top-down
        float gx = displayCoords.x / CUBE_WIDTH;
        float gy = displayCoords.y / CUBE_HEIGHT;
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
            Vector2 playerDisplayPos = TopDownCameraManager.gameToDisplayCoordinates(player.getPosition());
            // position.set(playerDisplayPos.x, playerDisplayPos.y, 0);
            // Centrer la caméra sur le milieu du sprite du joueur
            position.set(playerDisplayPos.x + (CUBE_WIDTH * player.getHitboxSize()) / 2,
                    playerDisplayPos.y + (CUBE_HEIGHT * player.getHitboxSize()) / 2, 0);

        }
        super.update();
    }

    @Override
    public void update(boolean updateFrustum) {
        super.update(updateFrustum);
    }

    // Pour une vue top-down, pas besoin de normalisation complexe - toutes les
    // directions ont la même vitesse
    public static Vector2 normalize(Vector2 worldDir, float speed) {
        if (worldDir.len() == 0) {
            return new Vector2(0, 0);
        }

        // Normalise la direction et applique la vitesse
        Vector2 normalized = worldDir.cpy().nor();
        return normalized.scl(speed);
    }
}
