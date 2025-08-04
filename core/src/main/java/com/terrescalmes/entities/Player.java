package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.TopDownCameraManager;
import com.terrescalmes.entities.attacks.RangedAttack;
import com.terrescalmes.entities.attacks.Attack;
import com.terrescalmes.entities.attacks.effects.IAttackEffect;
import com.terrescalmes.items.Weapon;
import com.terrescalmes.entities.attacks.effects.ZoneEffect;

public class Player extends Entity {

    public Player(TextureRegion textureRegion, Vector2 position, int maxHP, float acceleration) {
        super(textureRegion, position, maxHP, acceleration);
        faction = "Player";
        fillAttacks();
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
    }

    public void handleInputs(float delta) {
        Vector2 dir = new Vector2();

        // Déplacement top-down : les touches correspondent directement aux axes X et Y
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1; // Gauche
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1; // Droite
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.y += 1; // Haut
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.y -= 1; // Bas
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 target = TopDownCameraManager.getInstance().mouseToGameCoordinates();
            attacks.get(0).execute(this, target, null);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 target = TopDownCameraManager.getInstance().mouseToGameCoordinates();
            if (weapon != null) {
                weapon.attack(this, target);
            }
        }

        isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        float speed = acceleration * delta;

        if (dir.len() > 0) {
            Vector2 move = TopDownCameraManager.normalize(dir, speed);
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
