package com.terrescalmes.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

public class Player extends Entity {

    public Player(TextureRegion textureRegion, Vector2 position) {
        super(textureRegion, position);
    }

    public void handleInputs(float delta) {
        Vector2 dir = new Vector2();

        // Déplacement isométrique : les touches correspondent aux directions à l'écran
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1;
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1;
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.x -= 1;
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.x += 1;
            dir.y += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            isSprinting = true;
        } else {
            isSprinting = false;
        }

        float speed = ACCELERATION * delta;
        if (isSprinting) {
            speed *= 1.5;
        }

        if (dir.len() > 0) {
            // 1) on normalise “à l’écran” et on récupère la translation appropriée
            Vector2 move = CameraManager.normalizeIsometric(dir, speed);
            // 2) sprint ?
            if (isSprinting)
                move.scl(1.5f);

            Vector2 old = new Vector2(position.x, position.y);
            position.add(move);
        }
    }
}
