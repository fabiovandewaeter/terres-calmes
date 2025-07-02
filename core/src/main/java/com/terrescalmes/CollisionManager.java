package com.terrescalmes;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.map.Chunk;
import com.terrescalmes.map.Structure;
import com.terrescalmes.map.TileMap;

public class CollisionManager {
    private static final float COLLISION_EPSILON = 0.001f;
    private static CollisionManager instance;

    private TileMap tilemap;

    private CollisionManager() {
    }

    public static CollisionManager getInstance() {
        if (instance == null) {
            instance = new CollisionManager();
        }
        return instance;
    }

    public boolean allowMove(Entity source, Vector2 target) {
        // Créer une hitbox temporaire à la position cible
        Rectangle targetHitbox = new Rectangle(
                target.x - Entity.SIZE / 2,
                target.y - Entity.SIZE / 2,
                Entity.SIZE,
                Entity.SIZE);

        // Vérifier les collisions avec toutes les structures
        if (tilemap != null && hasCollisionWithStructures(targetHitbox, tilemap)) {
            return false;
        }

        return true;
    }

    // Méthode principale pour calculer le mouvement avec glissement
    public Vector2 calculateSlideMovement(Entity source, Vector2 targetPosition) {
        Vector2 originalPosition = source.getPosition();
        Vector2 movement = targetPosition.cpy().sub(originalPosition);

        // Si pas de mouvement, retourner la position actuelle
        if (movement.len2() < COLLISION_EPSILON * COLLISION_EPSILON) {
            return originalPosition.cpy();
        }

        // Tester le mouvement direct
        if (allowMove(source, targetPosition)) {
            return targetPosition.cpy();
        }

        // Essayer le glissement horizontal puis vertical
        Vector2 horizontalTarget = new Vector2(targetPosition.x, originalPosition.y);
        Vector2 verticalTarget = new Vector2(originalPosition.x, targetPosition.y);

        // Tester mouvement horizontal seulement
        if (allowMove(source, horizontalTarget)) {
            return horizontalTarget;
        }

        // Tester mouvement vertical seulement
        if (allowMove(source, verticalTarget)) {
            return verticalTarget;
        }

        // Aucun mouvement possible, rester à la position actuelle
        return originalPosition.cpy();
    }

    private static boolean hasCollisionWithStructures(Rectangle hitbox, TileMap tilemap) {
        // Parcourir tous les chunks de la TileMap
        for (Chunk chunk : tilemap.getChunks().values()) {
            for (Structure structure : chunk.getStructures()) {
                if (hitbox.overlaps(structure.getHitbox())) {
                    return true;
                }
            }
        }
        return false;
    }

    // setters
    public void setTileMap(TileMap tilemap) {
        this.tilemap = tilemap;
    }
}
