package com.terrescalmes.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.terrescalmes.CameraManager;
import com.terrescalmes.entities.attacks.RangedAttack;
import com.terrescalmes.entities.attacks.Attack;

public class Player extends Entity {

    public Player(TextureRegion textureRegion, Vector2 position) {
        super(textureRegion, position);
        Attack rangedAttack = new RangedAttack(50, 20, HP, 10);
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

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            isSprinting = true;
        } else {
            isSprinting = false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();
            Vector3 world3 = new Vector3(mouseX, mouseY, 0);
            CameraManager.getInstance().unproject(world3);
            Vector2 world2 = new Vector2(world3.x, world3.y);
            Vector2 target = CameraManager.displayToGameCoordinates(world2); // coordinate on the map; to clic on tiles
            attacks.get(0).execute(this, target);
        }

        float speed = ACCELERATION * delta;
        if (isSprinting) {
            speed *= 1.5;
        }

        if (dir.len() > 0) {
            // 1) on normalise “à l’écran” et on récupère la translation appropriée
            Vector2 move = CameraManager.normalizeIsometric(dir, speed);
            // 2) sprint ?
            if (isSprinting)
                move.scl(1.5f);

            Vector2 old = new Vector2(position.x, position.y);
            position.add(move);
        }
    }
}
