package com.terrescalmes.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EntityManager {
    private static EntityManager instance;

    public List<Entity> entities;
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

    public static void reset() {
        instance = new EntityManager();
    }

    public void add(Entity entity) {
        entities.add(entity);
    }

    public void add(Player player) {
        this.player = player;
        entities.add(player);
    }

    // takes mouse coordinates
    public boolean handleClick(Vector2 world2) {
        for (Entity entity : entities) {
            if (entity.getScreenBounds().contains(world2)) {
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
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update(delta);
            if (entity.getFaction().equals("Enemies")) {
                entity.moveTo(player.getPosition(), delta);
            }
        }
        checkCollisions();
        removeDeadEntities();
    }

    private void checkCollisions() {
        // Version simple (à optimiser si besoin)
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                if (a.collide(b)) {
                    a.handleCollision(b);
                    b.handleCollision(a);
                }
            }
        }
    }

    // returns the list of all entities that have part of their hitbox in the circle
    public List<Entity> getEntitiesInCircle(Circle circle) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.isDead())
                continue;

            // Vérifie la collision cercle-rectangle
            if (circleOverlapsRectangle(circle, entity.getHitbox())) {
                result.add(entity);
            }
        }
        return result;
    }

    private static boolean circleOverlapsRectangle(Circle circle, Rectangle rectangle) {
        return Intersector.overlaps(circle, rectangle);
    }

    private void removeDeadEntities() {
        ArrayList<Integer> deadEntityIndexes = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.isDead()) {
                deadEntityIndexes.add(i);
            }
        }
        // remove entities backward because indexes changes when using .remove()
        for (int i = deadEntityIndexes.size() - 1; i >= 0; i--) {
            int index = deadEntityIndexes.get(i);
            entities.remove(index);
        }
    }

    public void render(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);
        }
    }

    public void renderHitboxes(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        for (Entity entity : entities) {
            entity.renderHitbox(batch, shapeRenderer);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
