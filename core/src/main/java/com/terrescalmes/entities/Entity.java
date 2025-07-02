package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.entities.attacks.Attack;

public class Entity {
    public static final float SIZE = 0.5f; // half a cube

    private TextureRegion textureRegion;
    protected Vector2 position; // Position en coordonnées de jeu
    protected boolean isSprinting;
    protected int maxHP = 100;
    protected int HP;
    protected Rectangle hitbox;
    protected Rectangle screenBounds;
    protected List<Attack> attacks;
    protected float acceleration;
    protected String faction;
    protected int xp;

    public Entity(TextureRegion textureRegion, Vector2 position, float acceleration) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.acceleration = acceleration;
        isSprinting = false;
        maxHP = 100;
        HP = maxHP;
        hitbox = new Rectangle(position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
        screenBounds = new Rectangle();
        attacks = new ArrayList<>();
        faction = "";
        xp = 0;
        updateWorldBounds();
    }

    public Entity(TextureRegion textureRegion, Vector2 position, float acceleration, String faction) {
        this(textureRegion, position, acceleration);
        this.faction = faction;
    }

    public void update(float delta) {
        hitbox.setPosition(position.x - SIZE / 2, position.y - SIZE / 2);
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
        updateWorldBounds();
        if (textureRegion != null) {
            batch.draw(
                    textureRegion,
                    screenBounds.x, screenBounds.y,
                    screenBounds.width, screenBounds.height);
        }
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

    public boolean collide(Entity other) {
        return this.hitbox.overlaps(other.hitbox);
    }

    public void handleCollision(Entity other) {
        // TODO
    }

    public boolean isDead() {
        return HP <= 0;
    }

    // returns true if reached target
    public boolean moveTo(Vector2 target, float delta) {
        Vector2 direction = target.cpy().sub(position);
        float distanceToTarget = direction.len();

        if (distanceToTarget == 0) {
            return true;
        }

        // Utilisation de la normalisation isométrique
        Vector2 moveVector = CameraManager.normalizeIsometric(direction, acceleration * delta);
        float moveLength = moveVector.len();

        if (moveLength == 0) {
            return false; // Aucun déplacement nécessaire
        }

        Vector2 targetPosition;
        boolean reachedTarget = false;

        if (moveLength >= distanceToTarget) {
            targetPosition = target.cpy();
            reachedTarget = true;
        } else {
            targetPosition = position.cpy().add(moveVector);
        }

        // Utiliser le système de glissement pour calculer la nouvelle position
        Vector2 newPosition = CollisionManager.getInstance().calculateSlideMovement(this, targetPosition);

        // Mettre à jour la position seulement si elle a changé
        if (!newPosition.equals(position)) {
            position.set(newPosition);
        }

        // Retourner true seulement si on a atteint exactement la cible
        return reachedTarget && position.equals(target);
    }

    public void onKill(Entity victim) {
        xp += victim.xpDrop();
        System.out.println("Xp: " + xp);
    }

    public int xpDrop() {
        return 100;
    }

    // getters
    public Vector2 getPosition() {
        return position;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public int getHP() {
        return HP;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Rectangle getScreenBounds() {
        updateWorldBounds();
        return screenBounds;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public String getFaction() {
        return faction;
    }

    public int getXp() {
        return xp;
    }
}
