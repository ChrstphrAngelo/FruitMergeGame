package com.myTest.Test.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "FruitMergeGame";
    private static final String HIGH_SCORE_KEY = "highScore";

    public static int getHighScore() {
        Preferences prefs = getPreferences();
        return prefs.getInteger(HIGH_SCORE_KEY, 0);
    }

    public static void saveScore(int score) {
        int currentHigh = getHighScore();
        if (score > currentHigh) {
            Preferences prefs = getPreferences();
            prefs.putInteger(HIGH_SCORE_KEY, score);
            prefs.flush();
        }
    }

//    public static void resetHighScore() {
//        Preferences prefs = getPreferences();
//        prefs.putInteger(HIGH_SCORE_KEY, 0); // Reset high score to 0
//        prefs.flush();
//    }

    private static Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }
}
