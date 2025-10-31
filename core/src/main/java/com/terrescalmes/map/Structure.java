package com.terrescalmes.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.TopDownCameraManager;

// on top of a Tile
public class Structure {

    // protected static final float DEFAULT_SIZE = 0.5f; // half a cube
    protected static final float DEFAULT_SIZE = 1.0f;

    private TextureRegion textureRegion;
    private Vector2 position;
    public Vector2 displayPosition;
    private float hitboxSize;
    private float renderingSize;
    private Rectangle hitbox;
    private String faction;

    public Structure(TextureRegion textureRegion, Vector2 position) {
        this(textureRegion, position, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public Structure(TextureRegion textureRegion, Vector2 position, float hitboxSize, float renderingSize) {
        this.textureRegion = textureRegion;
        this.position = new Vector2(position.x + 0.5f, position.y + 0.5f);
        this.hitboxSize = hitboxSize;
        this.renderingSize = renderingSize;
        hitbox = new Rectangle(this.position.x - hitboxSize / 2, this.position.y - hitboxSize / 2, hitboxSize,
                hitboxSize);
        faction = "";
        this.displayPosition = TopDownCameraManager.gameToDisplayCoordinates(this.position);
    }

    public void render(SpriteBatch batch) {
        // Obtenir les dimensions originales de la texture
        float textureWidth = textureRegion.getRegionWidth();
        float textureHeight = textureRegion.getRegionHeight();

        // Calculer les dimensions de rendu en respectant les proportions originales
        float renderWidth, renderHeight;

        if (textureWidth >= textureHeight) {
            // Texture plus large que haute : la largeur est basée sur CUBE_WIDTH * size
            renderWidth = TopDownCameraManager.CUBE_WIDTH * renderingSize;
            renderHeight = renderWidth * (textureHeight / textureWidth);
        } else {
            // Texture plus haute que large : la hauteur est basée sur CUBE_HEIGHT * size
            renderHeight = TopDownCameraManager.CUBE_HEIGHT * renderingSize;
            renderWidth = renderHeight * (textureWidth / textureHeight);
        }

        // En vue top-down, pas besoin d'ajustements complexes - centrage simple
        batch.draw(textureRegion,
                displayPosition.x - renderWidth / 2f,
                displayPosition.y - renderHeight / 2f,
                renderWidth,
                renderHeight);
    }

    // getters
    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDisplayPosition() {
        return displayPosition;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public String getFaction() {
        return faction;
    }
}
