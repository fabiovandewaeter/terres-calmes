package com.terrescalmes.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.util.Vector2I;

public class HitMarker {
    public static final float DURATION = 0.5f; // durée en secondes
    private Vector2I tile;
    private float timer;

    public HitMarker(Vector2I tile) {
        this.tile = tile.cpy();
        this.timer = DURATION;
    }

    /** retourne true si expiré */
    public boolean update(float delta) {
        timer -= delta;
        return timer <= 0f;
    }

    /** dessine un rectangle autour de la case (outline) */
    public void render(SpriteBatch batch, ShapeRenderer sr) {
        // on suit la même convention que tes renderHitbox (end/begin)
        batch.end();

        Vector2 display = TopDownCameraManager.gameToDisplayCoordinates(tile.toVector2());
        float w = TopDownCameraManager.CUBE_WIDTH;
        float h = TopDownCameraManager.CUBE_HEIGHT;

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1f, 0f, 0f, 1f); // rouge
        sr.rect(display.x, display.y, w, h);
        sr.end();

        batch.begin();
    }
}
