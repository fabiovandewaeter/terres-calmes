package com.terrescalmes.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;

public class Entity {
    protected static final int ACCELERATION = 2;
    protected static final float SIZE = 0.5f; // half a cube

    private TextureRegion textureRegion;
    public Vector2 position; // Position en coordonnées de jeu
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
        this.hitbox = new Rectangle(position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
        this.screenBounds = new Rectangle();
        updateWorldBounds();
    }

    public void update(float delta) {
        hitbox.setPosition(position.x - SIZE / 2, position.y - SIZE / 2);
        updateWorldBounds();
    }

    // Calcul position de l'entité à l'écran basé sur sa position dans le jeu
    private void updateWorldBounds() {
        // Conversion des coordonnées de jeu vers coordonnées d'affichage
        Vector2 displayPos = CameraManager.gameToDisplayCoordinates(position);

        // Taille en pixels de l'entité
        float w = SIZE * CameraManager.CUBE_WIDTH;
        float h = SIZE * CameraManager.CUBE_HEIGHT;

        // Centrage de l'entité sur sa position
        float x = displayPos.x - w / 2f;
        float y = displayPos.y - h / 2f;

        screenBounds.set(x, y, w, h);
    }

    public void render(SpriteBatch batch) {
        batch.draw(
                textureRegion,
                screenBounds.x, screenBounds.y,
                screenBounds.width, screenBounds.height);
    }

    public void renderHitbox(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // On arrête le batch pour dessiner les formes
        batch.end();

        // Récupère les coords jeu
        float hx = hitbox.x;
        float hy = hitbox.y;
        float hw = hitbox.width;
        float hh = hitbox.height;

        // Les 4 coins en jeu
        Vector2[] corners = new Vector2[] {
                new Vector2(hx, hy), // BL
                new Vector2(hx + hw, hy), // BR
                new Vector2(hx + hw, hy + hh), // TR
                new Vector2(hx, hy + hh) // TL
        };

        // Projette chacun
        float[] verts = new float[8];
        for (int i = 0; i < 4; i++) {
            Vector2 s = CameraManager.gameToDisplayCoordinates(corners[i]);
            verts[i * 2] = s.x;
            verts[i * 2 + 1] = s.y;
        }

        // Dessine le polygone
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.polygon(verts);
        shapeRenderer.end();

        // On relance le batch
        batch.begin();
    }

    public void takeDamage(int amount) {
        HP = Math.max(0, HP - amount);
    }

    public boolean collide(Vector2 position) {
        return hitbox.contains(position);
    }

    public boolean isDead() {
        return HP <= 0;
    }
}
