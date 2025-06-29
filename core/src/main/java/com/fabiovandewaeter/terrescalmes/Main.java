package com.fabiovandewaeter.terrescalmes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.fabiovandewaeter.terrescalmes.map.TileMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private TextureRegion[] texture_regions;
    private OrthographicCamera camera;
    private TileMap map;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("map/256x192_Tiles.png");
        texture_regions = new TextureRegion[] { new TextureRegion(image, 0, 0, 256, 192) };

        camera = new OrthographicCamera(1920, 1080);
        map = new TileMap();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined);

        cameraInput();
        camera.update();

        batch.begin();
        // batch.draw(texture_regions[0], 0, 0);
        map.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    private void cameraInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && camera.zoom > 0.005) {
            camera.zoom -= 0.005;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += 0.005;
        }
    }
}
