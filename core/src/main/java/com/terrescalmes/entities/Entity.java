package com.terrescalmes.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

public class Entity {
    protected static final int ACCELERATION = 400;

    private TextureRegion textureRegion;
    public Vector2 position;
    protected boolean isSprinting;

    public Entity(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.isSprinting = false;
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        final float targetSize = CameraManager.CUBE_HEIGHT / 2;
        batch.draw(
                textureRegion,
                position.x - targetSize / 2f,
                position.y - targetSize / 2f,
                targetSize,
                targetSize);
    }
}
