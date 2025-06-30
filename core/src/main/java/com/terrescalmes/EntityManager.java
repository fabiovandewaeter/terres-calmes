package com.terrescalmes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.Player;

public class EntityManager {
    private static EntityManager instance;

    private List<Entity> entities;
    private Player player;

    private EntityManager() {
        entities = new ArrayList<>();
    }

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    public void add(Entity entity) {
        entities.add(entity);
    }

    public void add(Player player) {
        this.player = player;
        entities.add(player);
    }

    public boolean handleClick(Vector2 world2) {
        for (Entity entity : entities) {
            if (entity.screenBounds.contains(world2)) {
                entity.takeDamage(0);
                return true;
            }
        }
        return false;
    }

    public void handleInputs(float delta) {
        if (player != null) {
            player.handleInputs(delta);
        }
    }

    public void update(float delta) {
        ArrayList<Integer> deadEntityIndexes = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update(delta);
            if (entity.isDead()) {
                deadEntityIndexes.add(i);
            }
        }

        for (Integer index : deadEntityIndexes) {
            entities.remove((int) index);
        }
    }

    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
    }

    public Player getPlayer() {
        return player;
    }
}
