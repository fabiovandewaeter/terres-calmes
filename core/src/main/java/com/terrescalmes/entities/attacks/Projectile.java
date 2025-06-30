package com.terrescalmes.entities.attacks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;

public class Projectile extends Entity {

    public Projectile(TextureRegion textureRegion, Vector2 position) {
        super(textureRegion, position);
    }

    @Override
    public void handleCollision(Entity other) {
        other.takeDamage(0);
        HP = 0;
    }
}
