package io.jyberion.mmorpg.client.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;

public class UILoader {

    private Stage stage;

    public UILoader(Stage stage) {
        this.stage = stage;
    }

    public void loadUI(String configPath) {
        JsonValue config = UIConfigLoader.loadConfig(configPath);

        // Load background
        Texture backgroundTexture = new Texture(Gdx.files.internal(config.get("background").getString("image")));
        Image background = new Image(new TextureRegionDrawable(backgroundTexture));
        background.setSize(config.get("background").getInt("width"), config.get("background").getInt("height"));
        background.setPosition(config.get("background").getInt("x"), config.get("background").getInt("y"));
        stage.addActor(background);

        // Load button
        Texture buttonUp = new Texture(Gdx.files.internal(config.get("button").getString("imageUp")));
        Texture buttonDown = new Texture(Gdx.files.internal(config.get("button").getString("imageDown")));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUp));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDown));
        TextButton button = new TextButton("Login", buttonStyle);
        button.setSize(config.get("button").getInt("width"), config.get("button").getInt("height"));
        button.setPosition(config.get("button").getInt("x"), config.get("button").getInt("y"));
        stage.addActor(button);

        // Load text field
        Texture textFieldBg = new Texture(Gdx.files.internal(config.get("textfield").getString("background")));
        Texture cursor = new Texture(Gdx.files.internal(config.get("textfield").getString("cursor")));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(textFieldBg));
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursor));
        textFieldStyle.font = new BitmapFont();  // Use a default font or load your own
        textFieldStyle.fontColor = Color.WHITE;

        TextField textField = new TextField("", textFieldStyle);
        textField.setSize(config.get("textfield").getInt("width"), config.get("textfield").getInt("height"));
        textField.setPosition(config.get("textfield").getInt("x"), config.get("textfield").getInt("y"));
        stage.addActor(textField);
    }
}
