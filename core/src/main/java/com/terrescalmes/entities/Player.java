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
import com.terrescalmes.util.Vector2I;
import com.terrescalmes.entities.attacks.effects.ZoneEffect;

public class Player extends Entity {

    private static final float MOVE_SPEED = 5f; // Vitesse du déplacement vers la case cible

    public Player(TextureRegion textureRegion, Vector2I position, int maxHP, float acceleration) {
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
        // Toujours gérer les attaques
        handleAttacks();

        // Si on est en train de suivre un chemin de pathfinding, ne pas accepter de
        // nouveaux mouvements
        if (!canMove()) {
            return;
        }

        Vector2 dir = new Vector2();

        // Déplacement continu avec les touches WASD
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dir.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dir.x += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dir.y -= 1;
        }

        // Si une direction est pressée
        if (dir.len() > 0) {
            dir.nor();

            // Essayer de bouger dans cette direction
            if (moveInDirection(Vector2I.from(dir))) {
                System.out.println("Déplacement vers: " + Vector2I.from(dir));
            }
        }

        // Clic milieu pour pathfinding
        if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
            Vector2 target = TopDownCameraManager.getInstance(Main.DEFAULT_DISPLAY_WIDTH, Main.DEFAULT_DISPLAY_HEIGHT)
                    .mouseToGameCoordinates();

            boolean success = moveToPosition(Vector2I.from(target), 1000);
            if (success) {
                System.out.println("Déplacement pathfinding vers " + target);
            }
        }
    }

    private void handleAttacks() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 target = TopDownCameraManager.getInstance(Main.DEFAULT_DISPLAY_WIDTH, Main.DEFAULT_DISPLAY_HEIGHT)
                    .mouseToGameCoordinates();
            attacks.get(0).execute(this, Vector2I.from(target), null);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector2 target = TopDownCameraManager.getInstance(Main.DEFAULT_DISPLAY_WIDTH, Main.DEFAULT_DISPLAY_HEIGHT)
                    .mouseToGameCoordinates();
            if (weapon != null) {
                weapon.attack(this, Vector2I.from(target));
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
