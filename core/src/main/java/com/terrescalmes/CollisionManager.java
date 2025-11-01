package com.terrescalmes;

import com.badlogic.gdx.math.Rectangle;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.map.Chunk;
import com.terrescalmes.map.Structure;
import com.terrescalmes.map.TileMap;
import com.terrescalmes.util.Vector2I;

public class CollisionManager {

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

    public boolean allowMove(Entity source, Vector2I target) {
        // Créer une hitbox temporaire à la position cible
        Rectangle targetHitbox = new Rectangle(
                target.x,
                target.y,
                source.getHitboxSize(),
                source.getHitboxSize());

        // Vérifier les collisions avec toutes les structures
        if (tilemap != null && hasCollisionWithStructures(targetHitbox, tilemap)) {
            return false;
        }

        return true;
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
