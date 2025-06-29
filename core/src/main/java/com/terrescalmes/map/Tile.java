package com.terrescalmes.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tile {
    private TextureRegion textureRegion;
    public Vector2 worldPos;

    public Tile(TextureRegion textureRegion, Vector2 worldPos) {
        this.textureRegion = textureRegion;
        this.worldPos = worldPos;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, worldPos.x, -worldPos.y);
    }
}
