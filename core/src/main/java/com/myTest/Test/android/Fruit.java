package com.myTest.Test.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

public class Fruit {
    private Body body;
    private int size;
    private Texture texture;
    private boolean needsResize = false;
    public static final String[] FRUIT_TEXTURES = {
            "strawberry.png",  // Size 1
            "lemon.png",       // Size 2
            "orange.png",      // Size 3
            "apple.png",       // Size 4
            "grapes.png",      // Size 5
            "melon.png",       // Size 6
            "coconut.png",     // Size 7
            "pineapple.png",   // Size 8
            "watermelon.png"   // Size 9
    };

    public Fruit(World world, float x, float y, int size) {
        this.size = size;
        loadTexture();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);
        createFixture();
    }

    private void loadTexture() {
        size = Math.max(1, Math.min(size, 9));
        texture = new Texture(FRUIT_TEXTURES[size - 1]);  // size-1 for zero-based index
        Gdx.app.log("Texture", "Loaded texture for size: " + size);
    }

    private void createFixture() {
        CircleShape shape = new CircleShape();
        shape.setRadius(getRadiusForSize(size));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    private float getRadiusForSize(int size) {
        switch (size) {
            case 1: return 0.6f;   // Strawberry
            case 2: return 0.7f;   // Lemon
            case 3: return 0.8f;   // Orange
            case 4: return 0.9f;   // Apple
            case 5: return 1.0f;   // Grapes
            case 6: return 1.2f;   // Melon
            case 7: return 1.4f;   // Coconut
            case 8: return 1.6f;   // Pineapple
            case 9: return 1.8f;   // Watermelon
            default: return 0.6f;  // Default to lemon size if something goes wrong
        }
    }

    public void merge() {
        if (size >= 9) {  // Max size reached
            Gdx.app.log("Merge", "Max size reached. Cannot merge.");
            return;
        }

        size++;
        Gdx.app.log("Merge", "Merged to size: " + size);
        loadTexture();
        needsResize = true;
    }

    public void resize() {
        if (!needsResize) return;

        while (body.getFixtureList().size > 0) {
            body.destroyFixture(body.getFixtureList().first());
        }

        // Recreate with new size
        createFixture();

        needsResize = false;
        Gdx.app.log("Resize", "Resized to: " + size);
    }

    public void render(SpriteBatch batch) {
        if (needsResize) {
            resize();
        }

        float radius = getRadiusForSize(size);
        float x = body.getPosition().x - radius;
        float y = body.getPosition().y - radius;
        float diameter = radius * 2;
        float rotation = (float) Math.toDegrees(body.getAngle());

        // Render the texture with rotation
        batch.draw(texture,
                x, y,                 // Position
                radius, radius,        // Origin (center of rotation)
                diameter, diameter,    // Width & Height
                1, 1,                  // Scale
                rotation,              // Rotation angle
                0, 0,                  // Texture region (full texture)
                texture.getWidth(), texture.getHeight(),
                false, false);         // Flip flags
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public Body getBody() { return body; }
    public int getSize() { return size; }
    public boolean needsResize() { return needsResize; }
}

