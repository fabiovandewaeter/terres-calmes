package com.terrescalmes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.terrescalmes.entities.Player;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.map.TileMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private static final float SPAWN_INTERVAL = 2f;

    private SpriteBatch batch;
    private CameraManager camera;
    private ShapeRenderer shapeRenderer;
    private TileMap map;
    private EntityManager entityManager;
    private float spawnTimer = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = CameraManager.getInstance();
        shapeRenderer = new ShapeRenderer();
        map = new TileMap();
        entityManager = EntityManager.getInstance();
        Player player = new Player(new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612),
                new Vector2(0, 0));
        entityManager.add(player);
    }

    private void handleClick() {
        if (Gdx.input.justTouched()) {
            Vector2 world2 = CameraManager.getInstance().mouseCoordinates();
            Vector2 target = CameraManager.getInstance().mouseToGameCoordinates();

            entityManager.handleClick(world2);
        }
    }

    private void handleInputs(float delta) {
        handleClick();
        camera.handleInputs(delta);
        entityManager.handleInputs(delta);
    }

    private void update(float delta) {
        entityManager.update(delta);
        Player player = entityManager.getPlayer();
        if (player != null) {
            // Conversion des coordonnées de jeu du joueur vers coordonnées d'affichage
            Vector2 playerDisplayPos = CameraManager.gameToDisplayCoordinates(player.position);
            camera.position.set(playerDisplayPos.x, playerDisplayPos.y, 0);
        } else {
            camera.position.set(0, 0, 0);
        }
        camera.update();

        // spawn random entities
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL; // on décrémente plutôt que reset pour garder le surplus
            spawnEntities(2, 5f); // ex. 5 entités dans un rayon de 5
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // input
        handleInputs(delta);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // update
        update(delta);

        // render
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();

        map.render(batch);
        entityManager.render(batch);
        boolean showHitboxes = true; // ou false pour les cacher
        if (showHitboxes) {
            entityManager.renderHitboxes(batch, shapeRenderer);
        }

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
        shapeRenderer.dispose();
    }

    public void spawnEntities(int count, float radius) {
        Player player = entityManager.getPlayer();
        if (player == null)
            return; // pas de joueur, pas de spawn

        Vector2 center = player.position;
        for (int i = 0; i < count; i++) {
            // angle aléatoire en radians
            float angle = MathUtils.random(0f, MathUtils.PI2);
            // distance aléatoire (uniforme) entre 0 et radius
            float dist = MathUtils.random(0f, radius);

            float x = center.x + MathUtils.cos(angle) * dist;
            float y = center.y + MathUtils.sin(angle) * dist;

            // Crée l’entité à cette position
            Entity entity = new Entity(
                    new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612),
                    new Vector2(x, y));
            entityManager.add(entity);
        }
    }
}
