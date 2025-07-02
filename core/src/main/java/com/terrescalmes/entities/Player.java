package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CameraManager;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.entities.attacks.RangedAttack;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.MeleeAttack;
import com.terrescalmes.entities.attacks.effects.AttackEffect;
import com.terrescalmes.entities.attacks.effects.BasicEffect;
import com.terrescalmes.entities.attacks.effects.ExplosionEffect;

public class Player extends Entity {

    public Player(TextureRegion textureRegion, Vector2 position, float speed) {
        super(textureRegion, position, speed);
        faction = "Player";
        fillAttacks();
    }

    private void fillAttacks() {
        List<AttackEffect> attackEffects = new ArrayList<>();
        attackEffects.add(new ExplosionEffect(2f, 50));
        Attack rangedAttack = new RangedAttack(20, 0.2f, 2, attackEffects);
        attacks.add(rangedAttack);

        List<AttackEffect> meleeEffects = new ArrayList<>();
        meleeEffects.add(new BasicEffect(50));
        Attack meleeAttack = new MeleeAttack(1f, 0.2f, meleeEffects);
        attacks.add(meleeAttack);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // update cooldowns and attack logic
        for (Attack attack : attacks) {
            attack.update(delta);
        }
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

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 target = CameraManager.getInstance().mouseToGameCoordinates();
            attacks.get(0).execute(this, target);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 target = CameraManager.getInstance().mouseToGameCoordinates();
            attacks.get(1).execute(this, target);
        }

        isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        float speed = acceleration * delta;

        if (dir.len() > 0) {
            Vector2 move = CameraManager.normalizeIsometric(dir, speed);
            if (isSprinting)
                move.scl(1.5f);

            Vector2 potentialDestination = position.cpy().add(move);

            // Utiliser le système de glissement au lieu du simple allowMove
            Vector2 newPosition = CollisionManager.getInstance().calculateSlideMovement(this, potentialDestination);

            // Mettre à jour la position seulement si elle a changé
            if (!newPosition.equals(position)) {
                position.set(newPosition);
            }
        }
    }
}
