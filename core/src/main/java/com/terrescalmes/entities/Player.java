package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.Main;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.entities.attacks.RangedAttack;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.items.Weapon;
import com.terrescalmes.entities.attacks.effects.ZoneEffect;

public class Player extends Entity {

    private Vector2 targetCell;
    private boolean isMoving;
    private static final float MOVE_SPEED = 5f; // Vitesse du déplacement vers la case cible

    public Player(TextureRegion textureRegion, Vector2 position, int maxHP, float acceleration) {
        super(textureRegion, position, maxHP, acceleration);
        faction = "Player";
        fillAttacks();
        targetCell = null;
        isMoving = false;
    }

    private void fillAttacks() {
        List<IAttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ZoneEffect(2f, 10));
        Attack rangedAttack = new RangedAttack(20, 0.2f, 2, attackEffects);
        attacks.add(rangedAttack);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Gère le déplacement automatique vers la case cible
        if (isMoving && targetCell != null) {
            Vector2 direction = targetCell.cpy().sub(position);
            float distance = direction.len();

            if (distance < 0.01f) {
                // Arrivé à destination
                position.set(targetCell);
                isMoving = false;
                targetCell = null;
            } else {
                // Continue le mouvement
                Vector2 move = direction.nor().scl(MOVE_SPEED * delta);
                if (move.len() > distance) {
                    position.set(targetCell);
                    isMoving = false;
                    targetCell = null;
                } else {
                    position.add(move);
                }
            }
        }
    }

    public void handleInputs(float delta) {
        // Ne pas accepter de nouveaux inputs si on est en train de bouger
        if (isMoving) {
            // Toujours gérer les attaques même en mouvement
            handleAttacks();
            return;
        }

        Vector2 dir = new Vector2();

        // Détection des touches pour le déplacement case par case
        // if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1; // Gauche
            System.out.println("A");
            // } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1; // Droite
            System.out.println("D");
            // } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.y += 1; // Haut
            System.out.println("W");
            // } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.y -= 1; // Bas
            System.out.println("S");
        }

        // Si une direction est pressée, calculer la case cible
        if (dir.len() > 0) {
            // Calculer la case actuelle (arrondir à l'entier le plus proche)
            int currentCellX = Math.round(position.x);
            int currentCellY = Math.round(position.y);

            // Calculer la case cible
            int targetCellX = currentCellX + (int) dir.x;
            int targetCellY = currentCellY + (int) dir.y;

            Vector2 potentialTarget = new Vector2(targetCellX, targetCellY);

            // Vérifier si le mouvement est possible
            if (CollisionManager.getInstance().allowMove(this, potentialTarget)) {
                targetCell = potentialTarget;
                isMoving = true;
            }
        }

        handleAttacks();
    }

    private void handleAttacks() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 target = TopDownCameraManager.getInstance(Main.DEFAULT_DISPLAY_WIDTH, Main.DEFAULT_DISPLAY_HEIGHT)
                    .mouseToGameCoordinates();
            attacks.get(0).execute(this, target, null);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 target = TopDownCameraManager.getInstance(Main.DEFAULT_DISPLAY_WIDTH, Main.DEFAULT_DISPLAY_HEIGHT)
                    .mouseToGameCoordinates();
            if (weapon != null) {
                weapon.attack(this, target);
            }
        }
    }

    // getters
    public Weapon getWeapon() {
        return weapon;
    }

    // setters
    public Weapon equipWeapon(Weapon weapon) {
        Weapon oldWeapon = this.weapon;
        this.weapon = weapon;
        return oldWeapon;
    }
}
