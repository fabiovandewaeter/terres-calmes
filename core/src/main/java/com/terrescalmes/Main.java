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
import com.terrescalmes.items.ItemFactory;
import com.terrescalmes.items.ItemId;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.map.TileMap;
import com.terrescalmes.util.Vector2I;

public class Main extends ApplicationAdapter {

    private static final float SPAWN_INTERVAL = 2f;
    private static final int SPAWN_LIMIT = 3;
    public static final int DEFAULT_DISPLAY_WIDTH = 1280;
    public static final int DEFAULT_DISPLAY_HEIGHT = 720;

    private SpriteBatch batch;
    private TopDownCameraManager camera;
    private ShapeRenderer shapeRenderer;
    private TileMap map;
    private EntityManager entityManager;
    private float spawnTimer = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // camera = IsometricCameraManager.getInstance();
        camera = TopDownCameraManager.getInstance(DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        map = new TileMap();
        entityManager = EntityManager.getInstance();
        CollisionManager.getInstance().setTileMap(map);
        addEntities();
    }

    private void addEntities() {
        Player player = new Player(new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612),
                new Vector2I(0, 0), 100000000, 2);
        player.equipWeapon(ItemFactory.createWeapon(ItemId.IRON_SWORD));
        entityManager.add(player);
        Entity entity = new Entity(
                new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612),
                new Vector2I(5, 5), 200, 2);
        entityManager.add(entity);
    }

    private void handleClick() {
        if (Gdx.input.justTouched()) {
            Vector2 world2 = camera.mouseCoordinates();
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

        camera.update();

        // spawn random entities
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL; // on décrémente plutôt que reset pour garder le surplus
            // spawnEntities(2, 10f); // ex. 5 entités dans un rayon de 5
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
            entityManager.renderHitMarkers(batch, shapeRenderer);
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
        if (EntityManager.getInstance().getEntities().size() >= SPAWN_LIMIT) {
            return;
        }

        Player player = entityManager.getPlayer();
        if (player == null)
            return; // pas de joueur, pas de spawn

        Vector2 center = player.getPosition().toVector2();
        for (int i = 0; i < count; i++) {
            // angle aléatoire en radians
            float angle = MathUtils.random(0f, MathUtils.PI2);
            // distance aléatoire (uniforme) entre 0 et radius
            float dist = MathUtils.random(5f, radius);

            float x = center.x + MathUtils.cos(angle) * dist;
            float y = center.y + MathUtils.sin(angle) * dist;

            // Crée l’entité à cette position
            Entity entity = new Entity(
                    new TextureRegion(new Texture("entities/moai.png"), 0, 0, 612, 612),
                    new Vector2I(x, y), 20, 1, Entity.DEFAULT_SIZE, Entity.DEFAULT_SIZE * 2, "Enemies");
            entityManager.add(entity);
        }
    }
}
