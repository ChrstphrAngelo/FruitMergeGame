package com.myTest.Test.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Texture background;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load background
        background = new Texture("main_menu_background.png");
        Image bgImage = new Image(background);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        // Load fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Kenney Future.ttf"));

        // Title font
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 72;
        titleParams.color = Color.WHITE;
        titleFont = generator.generateFont(titleParams);

        // Button font
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParams.size = 48;
        buttonParams.color = Color.WHITE;
        buttonFont = generator.generateFont(buttonParams);

        generator.dispose();

        // Create main table for layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.defaults().pad(20);

        // High score label
        int highScore = HighScoreManager.getHighScore();
        Label highScoreLabel = new Label("High Score: " + highScore, new Label.LabelStyle(titleFont, Color.GOLD));
        mainTable.add(highScoreLabel).padBottom(50).row();

        // Play Button
        ImageButton playButton = createButton(
            "button_rectangle_gloss.png",
            "button_rectangle_depth_gloss.png",
            "Play"
        );

//        ImageButton resetButton = createButton(
//            "button_rectangle_gradient.png",
//            "button_rectangle_depth_gradient.png",
//            "Reset High Score"
//        );

        // Quit Button
        ImageButton quitButton = createButton(
            "button_rectangle_gradient.png",
            "button_rectangle_depth_gradient.png",
            "Quit"
        );

        // Add buttons to the table
        mainTable.add(playButton).width(600).height(180).row();
//        mainTable.add(resetButton).width(600).height(180).row();
        mainTable.add(quitButton).width(540).height(156);

        // Add the main table to the stage
        stage.addActor(mainTable);

        // Button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((FruitMergeGame) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });

//        resetButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                HighScoreManager.resetHighScore(); // Reset high score
//                highScoreLabel.setText("High Score: 0"); // Update UI immediately
//            }
//        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private ImageButton createButton(String upTexture, String downTexture, String text) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(new Texture(Gdx.files.internal(upTexture)));
        style.down = new TextureRegionDrawable(new Texture(Gdx.files.internal(downTexture)));

        ImageButton button = new ImageButton(style);

        Label.LabelStyle labelStyle = new Label.LabelStyle(buttonFont, Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);

        Table content = new Table();
        content.add(label).expand().fill();
        button.add(content);

        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        new Texture("button_rectangle_gloss.png").dispose();
        new Texture("button_rectangle_depth_gloss.png").dispose();
        new Texture("button_rectangle_gradient.png").dispose();
    }
}
