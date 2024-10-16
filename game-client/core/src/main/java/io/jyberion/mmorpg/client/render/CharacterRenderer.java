package io.jyberion.mmorpg.client.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import io.jyberion.mmorpg.common.model.CharacterCustomization;

public class CharacterRenderer {
    private Texture bodyTexture;
    private Texture faceTexture;
    private Texture hairTexture;
    private Texture clothingTexture;

    public CharacterRenderer() {
        // Load textures
        bodyTexture = new Texture("body.png");    // Replace with actual texture paths
        faceTexture = new Texture("face.png");
        hairTexture = new Texture("hair.png");
        clothingTexture = new Texture("clothing.png");
    }

    public void render(SpriteBatch batch, CharacterCustomization customization, float x, float y) {
        // Render the body first
        batch.draw(bodyTexture, x, y);

        // Render the face, hair, and clothing layers on top of the body
        batch.draw(faceTexture, x, y);       // Update to choose correct faceTexture based on customization.getFaceType()
        batch.draw(hairTexture, x, y);       // Update to choose correct hairTexture based on customization.getHairType()
        batch.draw(clothingTexture, x, y);   // Update to choose correct clothingTexture based on customization.getClothingType()
    }

    public void dispose() {
        bodyTexture.dispose();
        faceTexture.dispose();
        hairTexture.dispose();
        clothingTexture.dispose();
    }
}
