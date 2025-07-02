package com.terrescalmes.map;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.TextureManager;

public class Chunk {
    public static final int CHUNK_SIZE = 16;

    private Vector2 position;
    // CHUNK_SIZE * CHUNK_SIZE
    private Tile[] tiles;
    private List<Structure> structures;

    public Chunk(Vector2 position) {
        this.position = position;
        tiles = new Tile[CHUNK_SIZE * CHUNK_SIZE];
        structures = new ArrayList<>();
        generateTiles();
        if (position.x == 0 && position.y == 0) {
            structures.add(
                    new Structure(TextureManager.getInstance().getTextureRegion("buildings", 0, 0), new Vector2(0, 0)));
        }
    }

    private void generateTiles() {
        for (int row = CHUNK_SIZE - 1; row >= 0; row--) {
            for (int col = CHUNK_SIZE - 1; col >= 0; col--) {
                float x = col;
                float y = row;

                TextureManager textureManager = TextureManager.getInstance();
                TextureRegion textureRegion = textureManager.getDefault();

                textureRegion = textureManager.getTextureRegion("cubes", 5);
                addFromLocalCoordinates(new Tile(textureRegion, new Vector2(x, y)), (int) x, (int) y);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE; i++) {
            tiles[i].render(batch);
        }
        ListIterator<Structure> it = structures.listIterator(structures.size());
        while (it.hasPrevious()) {
            Structure element = it.previous();
            element.render(batch);
        }
    }

    // getters
    public Vector2 getPosition() {
        return position;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    // returns Tile from coordinates local to the Chunk
    private Tile getTileFromLocalCoordinates(int x, int y) {
        return tiles[x + y * CHUNK_SIZE];
    }

    // adds from coordinates local to the Chunk
    private void addFromLocalCoordinates(Tile tile, int x, int y) {
        tiles[x + y * CHUNK_SIZE] = tile;
    }

    // setters
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public void setStructures(List<Structure> structures) {
        this.structures = structures;
    }
}
