package com.terrescalmes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.terrescalmes.map.TileMap;
import com.terrescalmes.player.Player;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    // private OrthographicCamera camera;
    private CameraManager camera;
    private TileMap map;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new CameraManager();
        map = new TileMap();

        player = new Player(new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612), new Vector2(0, 0));
    }

    private void update(float delta) {
        player.update(delta);
        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // input
        cameraInput(delta);
        player.handleInputs(delta);

        // update
        update(delta);

        // render
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        map.render(batch);
        player.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    private void cameraInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && camera.zoom > 0.005) {
            camera.zoom -= 0.005;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += 0.005;
        }
    }
}
