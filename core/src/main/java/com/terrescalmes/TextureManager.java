package com.terrescalmes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public class TextureManager {

    private static TextureManager instance;

    // texture name -> Texture
    private Map<String, Texture> textureMap;
    // texture name -> list of TextureRegion
    private Map<String, List<TextureRegion>> textureListMap;
    // texture name -> nb of columns in the texture
    private Map<String, Integer> nbColumnsMap;

    private TextureManager() {
        textureMap = new HashMap<>();
        textureListMap = new HashMap<>();
        nbColumnsMap = new HashMap<>();

        loadTextures();
        fillTextureList();
    }

    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    public static void reset() {
        instance = new TextureManager();
    }

    private void loadTextures() {
        // textureMap.put("tiles", new Texture("map/256x192_Tiles.png"));
        textureMap.put("trees", new Texture("map/256x512_Trees.png"));
        textureMap.put("buildings", new Texture("map/buildings/Isometric_Assets_3.png"));
        textureMap.put("crystals", new Texture("map/512x512_Crystals_Transparent.png"));
        textureMap.put("tiles", new Texture("map/summer.png"));
    }

    private void fillTextureList() {
        // // tiles
        // int nbColumns = 10;
        // String name = "tiles";
        // textureListMap.put(name, createTextureRegions(textureMap.get(name),
        // nbColumns, 9, 256, 192));
        // nbColumnsMap.put(name, nbColumns);
        // tiles
        int nbColumns = 1;
        String name = "tiles";
        textureListMap.put(name, createTextureRegions(textureMap.get(name), nbColumns, 1, 16, 16));
        nbColumnsMap.put(name, nbColumns);
        // trees
        nbColumns = 6;
        name = "trees";
        textureListMap.put(name, createTextureRegions(textureMap.get(name), nbColumns, 2, 256, 512));
        nbColumnsMap.put(name, nbColumns);
        // buildings
        nbColumns = 2;
        name = "buildings";
        textureListMap.put(name, createTextureRegions(textureMap.get(name), nbColumns, 2, 1050, 1050));
        nbColumnsMap.put(name, nbColumns);
        // crystals
        nbColumns = 15;
        name = "crystals";
        textureListMap.put(name, createTextureRegions(textureMap.get(name), nbColumns, 8, 512, 512));
        nbColumnsMap.put(name, nbColumns);
    }

    private List<TextureRegion> createTextureRegions(Texture texture, int nbColumns, int nbRows, int elementWidth,
            int elementHeight) {
        List<TextureRegion> res = new ArrayList<>();
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbColumns; j++) {
                res.add(new TextureRegion(
                        texture,
                        j * elementWidth,
                        i * elementHeight,
                        elementWidth,
                        elementHeight));
            }
        }
        return res;
    }

    public TextureRegion getTextureRegion(String textureName, int index) {
        return textureListMap.get(textureName).get(index);
    }

    public TextureRegion getTextureRegion(String textureName, int column, int row) {
        return textureListMap.get(textureName).get(column + row * (nbColumnsMap.get(textureName)));
    }

    public TextureRegion getDefault() {
        return getTextureRegion("tiles", 0);
    }
}
