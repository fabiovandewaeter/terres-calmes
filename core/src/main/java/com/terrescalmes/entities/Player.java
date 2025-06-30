package com.terrescalmes.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

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
            dir.nor(); // Normaliser pour avoir une longueur = 1
            dir.scl(speed); // Appliquer la vitesse
            Vector2 old = new Vector2(position.x, position.y);
            position.add(dir); // Déplacer le joueur

            // Limites de la carte (ajuster selon votre MAP_SIZE)
            if (position.x < 0) {
                position.x = old.x;
            }
            if (position.y < 0) {
                position.y = old.y;
            }
        }
    }
}
