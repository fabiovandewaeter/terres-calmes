package com.terrescalmes.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.TextureManager;

public class TileMap {
    private static final int MAP_SIZE = 20;

    public LinkedList<Tile> base;
    public LinkedList<Tile> objects;
    private String[][] map;

    public TileMap() {
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

                TextureManager textureManager = TextureManager.getInstance();
                TextureRegion textureRegion = textureManager.getDefault();
                if (map[row][col].equals("g")) {
                    textureRegion = textureManager.getTextureRegion("cubes", 5);

                    int num = r.nextInt(100);
                    if (num > 90) {
                        objects.add(new Tile(textureManager.getTextureRegion("trees", 0, 0), new Vector2(x, y), true));
                    }
                } else if (map[row][col].equals("w")) {
                    textureRegion = textureManager.getTextureRegion("cubes", 0, 3);
                }
                base.add(new Tile(textureRegion, new Vector2(x, y)));
            }
        }
    }
}
