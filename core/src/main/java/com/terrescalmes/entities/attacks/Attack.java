package com.terrescalmes.entities.attacks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;

public interface Attack {
    void execute(Entity source, Vector2 targetPos);

    boolean update(float delta);

    void render(SpriteBatch batch);
}
