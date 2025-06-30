package com.terrescalmes.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

public class Entity {
    protected static final int ACCELERATION = 2;
    protected static final float SIZE = 0.5f; // half a cube

    private TextureRegion textureRegion;
    public Vector2 position;
    protected boolean isSprinting;
    protected int maxHP = 100;
    protected int HP;
    public Rectangle hitbox;
    public Rectangle screenBounds;

    public Entity(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.isSprinting = false;
        this.maxHP = 100;
        this.HP = maxHP;
        this.hitbox = new Rectangle(position.x - SIZE, position.y - SIZE, SIZE, SIZE);
        this.screenBounds = new Rectangle();
        updateWorldBounds();
    }

    public void update(float delta) {
        hitbox.setPosition(position.x - SIZE, position.y - SIZE);
        updateWorldBounds();
    }

    // calcul position of the sprite on the screen based on in-game position
    private void updateWorldBounds() {
        float x = position.x * CameraManager.CUBE_WIDTH - (SIZE * CameraManager.CUBE_WIDTH) / 2f;
        float y = position.y * CameraManager.CUBE_HEIGHT;
        float w = SIZE * CameraManager.CUBE_WIDTH;
        float h = SIZE * CameraManager.CUBE_HEIGHT;
        screenBounds.set(x, y, w, h);
    }

    public void render(SpriteBatch batch) {
        batch.draw(
                textureRegion,
                screenBounds.x, screenBounds.y,
                screenBounds.width, screenBounds.height);
    }

    public void takeDamage(int amount) {
        HP = Math.max(0, HP - amount);
        System.out.println("Damages taken: " + amount);
    }

    public boolean collide(Vector2 position) {
        return hitbox.contains(position);
    }

    public boolean isDead() {
        return HP <= 0;
    }
}
