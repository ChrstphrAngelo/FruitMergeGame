package com.myTest.Test.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final OrthographicCamera camera;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final Array<Fruit> fruits;
    private final Array<Body> bodiesToDestroy;
    private final SpriteBatch spriteBatch;
    private final Texture background;
    private final ShapeRenderer shapeRenderer;
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;
    private Stage uiStage;
    private Texture buttonTexture;
    private Texture buttonDepthTexture;
    private Texture repeatIconTexture;
    private Texture scoreBackground;
    private BitmapFont scoreFont;
    private ImageButton restartButton;
    private Texture nextFruitTexture;
    private Texture homeButtonTexture;
    private Texture homeIconTexture;
    private ImageButton homeButton;
    private int nextFruitSize;
    private boolean isHoldingFruit = false;
    private Fruit heldFruit;
    private int score = 0;
    private static final int[] FRUIT_POINTS = {1, 3, 6, 10, 15, 20, 25, 30, 35};
    private float timeRemaining = 120f;
    private int lastDisplayedSecond = 120;
    private boolean hasFirstFruitDropped = false;

    // Constants
    private static final float BACKGROUND_ZOOM = 2.2f;
    private static final float PREVIEW_SIZE = 1f;
    private static final float PREVIEW_Y_OFFSET = 4.5f;
    private static final float GAME_OVER_LINE_Y = 14f;
    private static final float DASH_LENGTH = 0.4f;
    private static final float DASH_GAP = 0.3f;

    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 9, 16); // Width x Height
        world = new World(new Vector2(0, -11.0f), true);
        debugRenderer = new Box2DDebugRenderer();
        fruits = new Array<>();
        shapeRenderer = new ShapeRenderer();
        bodiesToDestroy = new Array<>();
        spriteBatch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        nextFruitSize = generateRandomFruitSize();
        nextFruitTexture = new Texture(Fruit.FRUIT_TEXTURES[nextFruitSize - 1]);
        createBoundaries();

        buttonTexture = new Texture("button_round_border.png");
        buttonDepthTexture = new Texture("button_round_depth_border.png");
        repeatIconTexture = new Texture("icon_repeat_outline.png");

        homeButtonTexture = new Texture("button_round_border.png");
        homeIconTexture = new Texture("home_outline.png");


        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);

        scoreBackground = new Texture("button_rectangle_depth_flat_yellow.png");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Kenney Future.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = (int)(28 * Gdx.graphics.getDensity());
        parameter.padTop = 2;
        parameter.padBottom = 5;
        parameter.padLeft = 8;
        parameter.padRight = 8;
        scoreFont = generator.generateFont(parameter);

        generator.dispose();


        scoreFont.getData().setScale(1.0f);
        scoreFont.setColor(Color.BLACK);

        createUIButtons();
    }

    private void createUIButtons() {
        uiStage = new Stage(new ScreenViewport());

        ImageButtonStyle restartStyle = new ImageButtonStyle();
        restartStyle.up = new TextureRegionDrawable(new TextureRegion(buttonDepthTexture));
        restartStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        restartStyle.imageUp = new TextureRegionDrawable(new TextureRegion(repeatIconTexture));

        restartButton = new ImageButton(restartStyle);
        restartButton.setSize(120, 120);
        restartButton.setPosition(40, Gdx.graphics.getHeight() - restartButton.getHeight() - 40);

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                ((FruitMergeGame)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });

        TextureRegion homeRegion = new TextureRegion(homeIconTexture);
        float scaleFactor = 0.8f;
        TextureRegionDrawable homeDrawable = new TextureRegionDrawable(homeRegion);
        homeDrawable.setMinSize(
            homeRegion.getRegionWidth() * scaleFactor,
            homeRegion.getRegionHeight() * scaleFactor
        );

        ImageButtonStyle homeStyle = new ImageButtonStyle();
        homeStyle.up = new TextureRegionDrawable(new TextureRegion(homeButtonTexture));
        homeStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDepthTexture));
        homeStyle.imageUp = homeDrawable;

        homeButton = new ImageButton(homeStyle);
        homeButton.setSize(120, 120);
        homeButton.setPosition(
            Gdx.graphics.getWidth() - homeButton.getWidth() - 40,
            Gdx.graphics.getHeight() - homeButton.getHeight() - 40
        );

        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                ((FruitMergeGame)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        uiStage.addActor(restartButton);
        uiStage.addActor(homeButton);
    }

    private void createBoundaries() {
        createStaticBody(5f, 3.6f, 5f, 0.5f);  // Floor
        createStaticBody(0f, 9.6f, 0.1f, 5.5f); // Left wall
        createStaticBody(9f, 9.6f, 0.1f, 5.5f);// Right wall
    }

    @Override
    public void show() {
        hasFirstFruitDropped = false;
        timeRemaining = 120f;
        lastDisplayedSecond = 120;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();
                handleCollision(bodyA, bodyB);
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        Gdx.input.setInputProcessor(new InputMultiplexer(
            uiStage,
            new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    handleTouchDown(screenX, screenY);
                    return true;
                }

                @Override
                public boolean touchDragged(int screenX, int screenY, int pointer) {
                    handleTouchDragged(screenX, screenY);
                    return true;
                }

                @Override
                public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    handleTouchUp();
                    return true;
                }
            }
        ));
    }

    private void handleTouchDown(int screenX, int screenY) {
        if (!isHoldingFruit) {
            Vector3 touchPos = new Vector3(screenX, screenY, 0);
            camera.unproject(touchPos);

            heldFruit = new Fruit(world, touchPos.x, 14f, nextFruitSize);
            fruits.add(heldFruit);
            heldFruit.getBody().setActive(false);
            isHoldingFruit = true;
            nextFruitSize = generateRandomFruitSize();
            nextFruitTexture = new Texture(Fruit.FRUIT_TEXTURES[nextFruitSize - 1]);
        }
    }

    private void handleTouchDragged(int screenX, int screenY) {
        if (isHoldingFruit && heldFruit != null) {
            Vector3 touchPos = new Vector3(screenX, screenY, 0);
            camera.unproject(touchPos);
            heldFruit.getBody().setTransform(touchPos.x, 14f, 0);
        }
    }

    private void handleTouchUp() {
        if (isHoldingFruit && heldFruit != null) {
            heldFruit.getBody().setActive(true);
            isHoldingFruit = false;
            heldFruit = null;

            if (!hasFirstFruitDropped) {
                hasFirstFruitDropped = true;
            }
        }
    }

    @Override
    public void render(float delta) {
        updateWorld(delta);
        renderGraphics();
    }

    private void updateWorld(float delta) {
        if (hasFirstFruitDropped) {
            timeRemaining -= delta;

            if (timeRemaining <= 0) {
                timeRemaining = 0;
                HighScoreManager.saveScore(score);
                Gdx.app.postRunnable(() -> {
                    dispose();
                    ((FruitMergeGame) Gdx.app.getApplicationListener()).setScreen(
                        new GameOverScreen(score, HighScoreManager.getHighScore())
                    );
                });
                return;
            }

            for (Fruit fruit : fruits) {
                if (fruit.getBody().getPosition().y > GAME_OVER_LINE_Y) {
                    HighScoreManager.saveScore(score);
                    Gdx.app.postRunnable(() -> {
                        dispose();
                        ((FruitMergeGame) Gdx.app.getApplicationListener()).setScreen(
                            new GameOverScreen(score, HighScoreManager.getHighScore())
                        );
                    });
                    return;
                }
            }
        }

        world.step(1 / 60f, 6, 2);

        cleanupBodies();
    }

    private void renderGraphics() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        renderBackground();
        renderDangerLine();
        renderFruits();
        renderNextFruitPreview();

        uiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        uiStage.draw();

        renderScore();
        uiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        uiStage.draw();
    }

    private void renderScore() {
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();

        float bgWidth = 350;
        float bgHeight = 100;
        float bgX = (Gdx.graphics.getWidth() - bgWidth) / 2;
        float bgY = Gdx.graphics.getHeight() * 0.15f;

        spriteBatch.draw(scoreBackground, bgX, bgY, bgWidth, bgHeight);

        String scoreText = String.valueOf(score);
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, scoreText);
        float scoreTextX = bgX + (bgWidth - scoreLayout.width) / 2;
        float scoreTextY = bgY + (bgHeight + scoreLayout.height) / 2;
        scoreFont.draw(spriteBatch, scoreText, scoreTextX, scoreTextY);

        float timerBgY = bgY - bgHeight - 20;
        spriteBatch.draw(scoreBackground, bgX, timerBgY, bgWidth, bgHeight);

        String timerText = formatTime(timeRemaining);
        GlyphLayout timerLayout = new GlyphLayout(scoreFont, timerText);
        float timerTextX = bgX + (bgWidth - timerLayout.width) / 2;
        float timerTextY = timerBgY + (bgHeight + timerLayout.height) / 2;
        scoreFont.draw(spriteBatch, timerText, timerTextX, timerTextY);

        spriteBatch.end();
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void renderDangerLine() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 0.8f);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float x = 0;
        while(x < camera.viewportWidth) {
            shapeRenderer.line(x, GAME_OVER_LINE_Y,
                x + DASH_LENGTH, GAME_OVER_LINE_Y);
            x += DASH_LENGTH + DASH_GAP;
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderBackground() {
        spriteBatch.begin();
        float aspectRatio = (float) background.getWidth() / background.getHeight();
        float bgHeight = camera.viewportHeight * BACKGROUND_ZOOM;
        float bgWidth = bgHeight * aspectRatio;
        spriteBatch.draw(background,
            camera.position.x - bgWidth/2,
            camera.position.y - bgHeight/5,
            bgWidth,
            bgHeight
        );
        spriteBatch.end();
    }

    private void renderFruits() {
        spriteBatch.begin();
        for (Fruit fruit : fruits) {
            fruit.render(spriteBatch);
        }
        spriteBatch.end();
    }

    private void renderNextFruitPreview() {
        spriteBatch.begin();
        float previewX = (camera.viewportWidth / 2) - (PREVIEW_SIZE / 2); // Center horizontally
        float previewY = camera.viewportHeight - PREVIEW_SIZE - 2f; // Move closer to the top
        spriteBatch.draw(nextFruitTexture, previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE);
        spriteBatch.end();
    }

    private void handleCollision(Body bodyA, Body bodyB) {
        Fruit fruitA = findFruitByBody(bodyA);
        Fruit fruitB = findFruitByBody(bodyB);

        if (fruitA != null && fruitB != null && fruitA.getSize() == fruitB.getSize()) {
            if (fruitA.getSize() < 9) {
                score += FRUIT_POINTS[fruitA.getSize() - 1];
                fruitA.merge();
                bodiesToDestroy.add(bodyB);
                fruits.removeValue(fruitB, true);
            } else {
                bodiesToDestroy.addAll(bodyA, bodyB);
                fruits.removeValue(fruitA, true);
                fruits.removeValue(fruitB, true);
            }
        }
    }

    private void createStaticBody(float x, float y, float halfWidth, float halfHeight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfWidth, halfHeight);

        Body body = world.createBody(bodyDef);
        body.createFixture(shape, 0f);
        shape.dispose();
    }

    private Fruit findFruitByBody(Body body) {
        for (Fruit fruit : fruits) {
            if (fruit.getBody() == body) {
                return fruit;
            }
        }
        return null;
    }

    private void cleanupBodies() {
        for (Body body : bodiesToDestroy) {
            if (body != null && body.getWorld() != null) {
                world.destroyBody(body);
            }
        }
        bodiesToDestroy.clear();

        Iterator<Fruit> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            Fruit fruit = iterator.next();
            if (fruit.getBody() == null || fruit.getBody().getFixtureList().size == 0) {
                fruit.dispose();
                iterator.remove();
            }
        }
    }

    private int generateRandomFruitSize() {
        return 1 + (int) (Math.random() * 5);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) height / width;
        camera.viewportWidth = 9;
        camera.viewportHeight = 9 * aspectRatio;
        camera.update();

        homeButton.setPosition(
            width - homeButton.getWidth() - 40,
            height - homeButton.getHeight() - 40
        );

        uiStage.getViewport().update(width, height, true);
        restartButton.setPosition(
            40,
            height - restartButton.getHeight() - 40
        );
        uiViewport.update(width, height, true);
        uiCamera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private String formatTime(float seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, secs);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        spriteBatch.dispose();
        background.dispose();
        shapeRenderer.dispose();
        nextFruitTexture.dispose();
        for (Fruit fruit : fruits) {
            fruit.dispose();
        }
        uiStage.dispose();
        buttonTexture.dispose();
        buttonDepthTexture.dispose();
        repeatIconTexture.dispose();
        scoreBackground.dispose();
        scoreFont.dispose();
        homeButtonTexture.dispose();
        homeIconTexture.dispose();
    }
}
