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
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.y -= 1;
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
            dir.nor(); // longueur = 1
            dir.scl(speed); // vitesse constante
            position.add(dir); // on bouge le joueur
        }
    }
}
