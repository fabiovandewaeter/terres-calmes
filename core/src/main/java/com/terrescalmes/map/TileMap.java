package com.terrescalmes.map;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TileMap {

    private Map<Vector2, Chunk> chunks;

    public TileMap() {
        // base = new LinkedList<Tile>();
        chunks = new HashMap<>();
        generateChunks();
    }

    public void render(SpriteBatch batch) {
        for (Vector2 key : chunks.keySet()) {
            chunks.get(key).render(batch);
        }
    }

    private void generateChunks() {
        Vector2 pos = new Vector2(0, 0);
        Chunk chunk = new Chunk(pos);
        chunks.put(pos, chunk);
    }
}
