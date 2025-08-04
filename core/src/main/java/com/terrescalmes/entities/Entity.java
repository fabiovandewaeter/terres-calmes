package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
// import com.terrescalmes.IsometricCameraManager;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.MeleeAttack;
import com.terrescalmes.entities.behavior.BasicEnemyBehavior;
import com.terrescalmes.entities.behavior.EntityBehavior;
import com.terrescalmes.items.Weapon;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.entities.attacks.effects.SinglePointEffect;

public class Entity {

    public static final float DEFAULT_SIZE = 0.5f; // half a cube
    public static final String DEFAULT_FACTION = "";

    private TextureRegion textureRegion;
    protected Vector2 position; // Position en coordonnées de jeu
    protected float acceleration;
    protected boolean isSprinting;
    protected int maxHP;
    protected int HP;
    protected float hitboxSize;
    protected float renderingSize;
    protected Rectangle hitbox;
    protected Rectangle screenBounds;
    protected List<Attack> attacks;
    protected String faction;
    protected int xp;
    protected EntityBehavior behavior;
    protected Weapon weapon;

    public Entity(TextureRegion textureRegion, Vector2 position, int maxHP, float acceleration) {
        this(textureRegion, position, maxHP, acceleration, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_FACTION);
    }

    public Entity(TextureRegion textureRegion, Vector2 position, int maxHP, float acceleration, float hitboxSize,
            float renderingSize, String faction) {
        this.textureRegion = textureRegion;
        this.position = position;
        this.acceleration = acceleration;
        isSprinting = false;
        this.maxHP = maxHP;
        HP = maxHP;
        this.hitboxSize = hitboxSize;
        this.renderingSize = renderingSize;
        hitbox = new Rectangle(position.x - hitboxSize / 2, position.y - hitboxSize / 2, hitboxSize, hitboxSize);
        screenBounds = new Rectangle();
        this.faction = faction;
        attacks = new ArrayList<>();
        xp = 0;
        updateWorldBounds();
        if ("Enemies".equals(faction)) {
            this.behavior = new BasicEnemyBehavior(this);
            fillEnemyAttacks();
        }
    }

    private void fillEnemyAttacks() {
        List<IAttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new SinglePointEffect(15)); // Dégâts de mêlée
        Attack meleeAttack = new MeleeAttack(1.0f, 1.5f, attackEffects); // portée 1.0, cooldown 1.5s
        attacks.add(meleeAttack);
    }

    public void update(float delta) {
        hitbox.setPosition(position.x - hitboxSize / 2, position.y - hitboxSize / 2);

        // update cooldowns and attack logic
        for (Attack attack : attacks) {
            attack.update(delta);
        }

        if (behavior != null) {
            behavior.update(delta);
        }

        if (weapon != null) {
            weapon.update(delta);
        }
    }

    // Calcul position de l'entité à l'écran basé sur sa position dans le jeu
    private void updateWorldBounds() {
        // Conversion des coordonnées de jeu vers coordonnées d'affichage
        Vector2 displayPos = TopDownCameraManager.gameToDisplayCoordinates(position);

        // Taille en pixels de l'entité
        float w = renderingSize * TopDownCameraManager.CUBE_WIDTH;
        float h = renderingSize * TopDownCameraManager.CUBE_HEIGHT;

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

        // En vue top-down, la hitbox est un simple rectangle
        // Conversion de la position de la hitbox vers les coordonnées d'affichage
        Vector2 hitboxDisplayPos = TopDownCameraManager.gameToDisplayCoordinates(new Vector2(hitbox.x, hitbox.y));

        // Taille de la hitbox en pixels
        float displayWidth = hitbox.width * TopDownCameraManager.CUBE_WIDTH;
        float displayHeight = hitbox.height * TopDownCameraManager.CUBE_HEIGHT;

        // Dessine le rectangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(hitboxDisplayPos.x, hitboxDisplayPos.y, displayWidth, displayHeight);
        shapeRenderer.end();

        // On relance le batch
        batch.begin();
    }

    public void takeDamage(int amount) {
        System.out.println("takeDamage(): " + amount);
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
        Vector2 moveVector = TopDownCameraManager.normalize(direction, acceleration * delta);
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
        if (!victim.getFaction().equals(faction)) {
            xp += victim.xpDrop();
        }
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

    public int getMaxHP() {
        return maxHP;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getHitboxSize() {
        return hitboxSize;
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
