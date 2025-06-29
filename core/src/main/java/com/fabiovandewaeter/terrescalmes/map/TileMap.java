package com.fabiovandewaeter.terrescalmes.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TileMap {

    private static final int MAP_SIZE = 4;
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 192;
    private static final int CUBE_WIDTH = 256;
    private static final int CUBE_HEIGHT = 256;
    private static final int TREE_WIDTH = 256;
    private static final int TREE_HEIGHT = 512;

    public LinkedList<Tile> base;
    public LinkedList<Tile> objects;
    private Map<String, Texture> textureMap;
    private Map<String, TextureRegion> textureRegionMap;
    private String[][] map;

    public TileMap() {
        textureMap = new HashMap<>();
        textureRegionMap = new HashMap<>();
        textureMap.put("tiles", new Texture("map/256x192_Tiles.png"));
        textureRegionMap.put("grass_tile", new TextureRegion(textureMap.get("tiles"), 0, 0, TILE_WIDTH, TILE_HEIGHT));
        textureRegionMap.put("water_tile",
                new TextureRegion(textureMap.get("tiles"), 0, 3 * 192, TILE_WIDTH, TILE_HEIGHT));

        textureMap.put("cubes", new Texture("map/256x256_Cubes.png"));
        textureRegionMap.put("grass_cube", new TextureRegion(textureMap.get("cubes"), 0, 0, CUBE_WIDTH, CUBE_HEIGHT));
        textureRegionMap.put("water_cube",
                new TextureRegion(textureMap.get("cubes"), 0, 2 * CUBE_HEIGHT, CUBE_WIDTH, CUBE_HEIGHT));

        textureMap.put("trees", new Texture("map/256x512_Trees.png"));
        textureRegionMap.put("first_tree", new TextureRegion(textureMap.get("trees"), 0, 0, TREE_WIDTH, TREE_HEIGHT));

        base = new LinkedList<Tile>();
        objects = new LinkedList<Tile>();
        map = new String[MAP_SIZE][MAP_SIZE];
        try {
            fillMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(SpriteBatch batch) {
        /*
         * for (Tile t : this.base) {
         * t.render(batch);
         * }
         */
        ListIterator<Tile> it = this.base.listIterator(this.base.size());
        while (it.hasPrevious()) {
            Tile element = it.previous();
            element.render(batch);
        }
        for (Tile t : this.objects) {
            t.render(batch);
        }
    }

    public void fillMap() throws IOException {
        FileHandle fh = Gdx.files.internal("map/mapBase");
        BufferedReader br = new BufferedReader(new FileReader(fh.path()));
        String s = "";
        int count = 0;
        while ((s = br.readLine()) != null) {
            map[count] = s.split(" ");
            count++;
        }
        br.close();

        Random r = new Random();
        for (int row = MAP_SIZE - 1; row >= 0; row--) {
            for (int col = MAP_SIZE - 1; col >= 0; col--) {
                float x = (col - row) * (CUBE_WIDTH / 2f);
                float y = (col + row) * (CUBE_HEIGHT / 4f);

                String textureName = "";
                if (map[row][col].equals("g")) {
                    textureName = "grass_cube";

                    int num = r.nextInt(100);
                    if (num > 70) {
                        // objects.add(new Tile(this.textureRegionMap.get("first_tree"), new
                        // Vector2(row, col),new Vector2(x, y)));
                    }
                } else if (map[row][col].equals("w")) {
                    textureName = "water_cube";
                }
                if (textureName != "") {
                    base.add(
                            new Tile(this.textureRegionMap.get(textureName), new Vector2(row, col), new Vector2(x, y)));
                }
            }
        }

        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                System.out.print(map[row][col]);
            }
            System.out.print("\n");
        }

        objects.add(new Tile(this.textureRegionMap.get("first_tree"), new Vector2(0, 0),
                new Vector2((0 - 0) * (CUBE_WIDTH / 2f), (0 + 0) * (CUBE_HEIGHT / 4f) - CUBE_HEIGHT / 2f)));
        objects.add(new Tile(this.textureRegionMap.get("first_tree"), new Vector2(0, 0),
                // new Vector2(500, 0)));
                new Vector2((3 - 0) * (CUBE_WIDTH / 2f), (3 + 0) * (CUBE_HEIGHT / 4f) - CUBE_HEIGHT / 2f)));
        objects.add(new Tile(this.textureRegionMap.get("first_tree"), new Vector2(0, 0),
                // new Vector2(500, 500)));
                new Vector2((3 - 3) * (CUBE_WIDTH / 2f), (3 + 3) * (CUBE_HEIGHT / 4f) - CUBE_HEIGHT / 2f)));
    }
}
