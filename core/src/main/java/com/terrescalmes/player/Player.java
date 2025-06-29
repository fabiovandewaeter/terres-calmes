package com.terrescalmes.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private static final int ACCELERATION = 400;

    private TextureRegion textureRegion;
    public Vector2 position;
    private float width, height;
    private boolean isSprinting;

    public Player(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
        this.isSprinting = false;
    }

    public void handleInputs(float delta){
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
        if (isSprinting){
            speed *= 1.5;
        }

        if (dir.len() > 0) {
            dir.nor(); // longueur = 1
            dir.scl(speed); // vitesse constante
            position.add(dir); // on bouge le joueur
        }
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        float targetSize = 512f / 2;
        batch.draw(
                textureRegion,
                position.x - width / 2f,
                position.y - height / 2f,
                targetSize,
                targetSize);
    }
}
