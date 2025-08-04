package com.terrescalmes.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.TopDownCameraManager;

public class Tile {

    private TextureRegion textureRegion;
    public Vector2 position;
    public Vector2 displayPosition;
    private boolean aboveMap;

    public Tile(TextureRegion textureRegion, Vector2 position) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.displayPosition = TopDownCameraManager.gameToDisplayCoordinates(position);
    }

    public Tile(TextureRegion textureRegion, Vector2 position, boolean aboveMap) {
        this(textureRegion, position);
        this.aboveMap = aboveMap;
    }

    public void render(SpriteBatch batch) {
        // En vue top-down, les tuiles sont simplement centrées sur leur position
        batch.draw(textureRegion,
                displayPosition.x - TopDownCameraManager.CUBE_WIDTH / 2f,
                displayPosition.y - TopDownCameraManager.CUBE_HEIGHT / 2f,
                TopDownCameraManager.CUBE_WIDTH,
                TopDownCameraManager.CUBE_HEIGHT);
    }
}
