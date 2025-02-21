package com.myTest.Test.android;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.physics.box2d.Box2D;

public class FruitMergeGame extends Game {
    private Music backgroundMusic;

    MainMenuScreen mainMenuScreen;

    @Override
    public void create() {
        Box2D.init();
        mainMenuScreen = new MainMenuScreen();

        // Load the background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sakura-Girl-Yay.mp3"));

        // Set the music to loop
        backgroundMusic.setLooping(true);

        // Set the volume (optional, range: 0.0 to 1.0)
        backgroundMusic.setVolume(0.5f);

        // Play the music
        backgroundMusic.play();

        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen); // Make sure this exists
    }

    @Override
    public void resize(int width, int height) {
        mainMenuScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        mainMenuScreen.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}

