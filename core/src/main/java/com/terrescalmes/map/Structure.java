package com.terrescalmes.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

// on top of a Tile
public class Structure {
    protected static final float SIZE = 0.5f; // half a cube

    private TextureRegion textureRegion;
    private Vector2 position;
    public Vector2 displayPosition;
    private Rectangle hitbox;
    private String faction;

    public Structure(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        hitbox = new Rectangle(position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
        faction = "";
        this.displayPosition = CameraManager.gameToDisplayCoordinates(position);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, displayPosition.x - CameraManager.CUBE_WIDTH / 2,
                displayPosition.y - CameraManager.CUBE_HEIGHT / 2f, CameraManager.CUBE_WIDTH,
                CameraManager.CUBE_HEIGHT);
    }

    // getters
    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDisplayPositin() {
        return displayPosition;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public String getFaction() {
        return faction;
    }
}
