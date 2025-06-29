package com.terrescalmes.map;

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
import com.terrescalmes.CameraManager;

public class TileMap {
    private static final int MAP_SIZE = 20;
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 192;
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
        textureRegionMap.put("grass_cube",
                new TextureRegion(textureMap.get("cubes"), 0, 0, CameraManager.CUBE_WIDTH, CameraManager.CUBE_HEIGHT));
        textureRegionMap.put("water_cube",
                new TextureRegion(textureMap.get("cubes"), 0, 2 * CameraManager.CUBE_HEIGHT, CameraManager.CUBE_WIDTH,
                        CameraManager.CUBE_HEIGHT));
        textureRegionMap.put("random",
                new TextureRegion(textureMap.get("cubes"), 0, 3 * CameraManager.CUBE_HEIGHT, CameraManager.CUBE_WIDTH,
                        CameraManager.CUBE_HEIGHT));

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
                float x = col;
                float y = row;

                String textureName = "";
                if (map[row][col].equals("g")) {
                    textureName = "grass_cube";

                    int num = r.nextInt(100);
                    if (num > 190) {
                        objects.add(new Tile(this.textureRegionMap.get("first_tree"),
                                new Vector2(x, y), true));
                    }
                } else if (map[row][col].equals("w")) {
                    textureName = "water_cube";
                }
                if (textureName != "") {
                    base.add(
                            new Tile(this.textureRegionMap.get(textureName), new Vector2(x, y)));
                }
            }
        }
        // objects.add(new Tile(this.textureRegionMap.get("random"),
        // new Vector2(0, 0), true));
    }
}
