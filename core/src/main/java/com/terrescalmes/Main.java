package com.terrescalmes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.terrescalmes.entities.Player;
import com.terrescalmes.map.TileMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private CameraManager camera;
    private ShapeRenderer shapeRenderer;
    private TileMap map;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new CameraManager();
        shapeRenderer = new ShapeRenderer();
        map = new TileMap();
        player = new Player(new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612), new Vector2(0, 0));
    }

    private void handleClick() {
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            Vector3 world3 = new Vector3(mouseX, mouseY, 0);
            camera.unproject(world3);
            Vector2 world2 = new Vector2(world3.x, world3.y);

            Vector2 target = CameraManager.displayToGameCoordinates(world2); // coordinate on the map; to clic on tiles

            if (player.screenBounds.contains(world2)) {
                player.takeDamage(0);
            }
        }
    }

    private void update(float delta) {
        player.update(delta);
        camera.position.set(player.position.x * CameraManager.CUBE_WIDTH,
                player.position.y * CameraManager.CUBE_HEIGHT, 0);
        camera.update();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // input
        handleClick();
        camera.handleInputs(delta);
        player.handleInputs(delta);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // update
        update(delta);

        // render
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        map.render(batch);
        player.render(batch);
        batch.end();

        renderMousePointer();
    }

    private void renderMousePointer() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // screen to world
        Vector3 worldCoords3 = new Vector3(mouseX, mouseY, 0);
        camera.unproject(worldCoords3);

        // draw circle
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(worldCoords3.x, worldCoords3.y, 5f);
        shapeRenderer.end();
    }

    public void dispose() {
        batch.dispose();
        image.dispose();
        shapeRenderer.dispose();
    }
}
