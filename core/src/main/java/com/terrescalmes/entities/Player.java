package com.terrescalmes.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;
import com.terrescalmes.entities.attacks.RangedAttack;
import com.terrescalmes.entities.attacks.Attack;

public class Player extends Entity {
    private static final float ATTACK_INTERVAL = 0.2f;

    private float attackCooldown = 0f;

    public Player(TextureRegion textureRegion, Vector2 position, float speed) {
        super(textureRegion, position, speed);
        Attack rangedAttack = new RangedAttack(50, 20, HP, 20);
        attacks.add(rangedAttack);
    }

    public void handleInputs(float delta) {
        Vector2 dir = new Vector2();

        // Déplacement isométrique : les touches correspondent aux directions à l'écran
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1;
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1;
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.x -= 1;
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.x += 1;
            dir.y += 1;
        }

        attackCooldown = Math.max(0f, attackCooldown - delta);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && attackCooldown <= 0f) {
            Vector2 target = CameraManager.getInstance().mouseToGameCoordinates();
            attacks.get(0).execute(this, target);
            attackCooldown = ATTACK_INTERVAL;
        }

        isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        float speed = acceleration * delta;
        if (isSprinting) {
            speed *= 1.5;
        }

        if (dir.len() > 0) {
            Vector2 move = CameraManager.normalizeIsometric(dir, speed);
            if (isSprinting)
                move.scl(1.5f);

            Vector2 old = new Vector2(position.x, position.y);
            position.add(move);
        }
    }
}
