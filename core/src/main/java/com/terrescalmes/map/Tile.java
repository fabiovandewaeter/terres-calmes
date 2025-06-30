package com.terrescalmes.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

public class Tile {
    private TextureRegion textureRegion;
    public Vector2 position;
    public Vector2 displayPosition;
    private boolean aboveMap;

    public Tile(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.displayPosition = CameraManager.gameToDisplayCoordinates(position);
    }

    public Tile(TextureRegion textureRegion, Vector2 position, boolean aboveMap) {
        this(textureRegion, position);
        this.aboveMap = aboveMap;
    }

    public void render(SpriteBatch batch) {
        if (aboveMap) {
            batch.draw(textureRegion, displayPosition.x, displayPosition.y + CameraManager.CUBE_HEIGHT / 2f);
        } else {
            batch.draw(textureRegion, displayPosition.x - CameraManager.CUBE_WIDTH / 2,
                    displayPosition.y - CameraManager.CUBE_HEIGHT);
        }
    }
}
