package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.client.render.CharacterRenderer;
import io.jyberion.mmorpg.common.model.*;

public class CharacterCreationScreen implements Screen {

    private SpiritOnlineGame game;
    private Stage stage;
    private SpriteBatch batch;
    private CharacterRenderer characterRenderer;
    private CharacterCustomization customization;
    private String authToken;
    private String selectedWorld;
    private String selectedChannel;

    // Updated constructor to include authToken, selectedWorld, and selectedChannel
    public CharacterCreationScreen(SpiritOnlineGame game, String authToken, String selectedWorld, String selectedChannel) {
        this.game = game;
        this.authToken = authToken;
        this.selectedWorld = selectedWorld;
        this.selectedChannel = selectedChannel;
        this.batch = new SpriteBatch();
        this.characterRenderer = new CharacterRenderer();

        // Default customization
        customization = new CharacterCustomization(BodyType.AVERAGE, FaceType.HAPPY, HairType.SHORT, ClothingType.CASUAL);

        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // UI elements for customization
        Label bodyLabel = new Label("Select Body Type:", skin);
        SelectBox<BodyType> bodySelectBox = new SelectBox<>(skin);
        bodySelectBox.setItems(BodyType.values());

        Label faceLabel = new Label("Select Face Type:", skin);
        SelectBox<FaceType> faceSelectBox = new SelectBox<>(skin);
        faceSelectBox.setItems(FaceType.values());

        Label hairLabel = new Label("Select Hair Type:", skin);
        SelectBox<HairType> hairSelectBox = new SelectBox<>(skin);
        hairSelectBox.setItems(HairType.values());

        Label clothingLabel = new Label("Select Clothing Type:", skin);
        SelectBox<ClothingType> clothingSelectBox = new SelectBox<>(skin);
        clothingSelectBox.setItems(ClothingType.values());

        TextButton createButton = new TextButton("Create Character", skin);

        Table table = new Table();
        table.setFillParent(true);
        table.add(bodyLabel).pad(5);
        table.add(bodySelectBox).pad(5);
        table.row();
        table.add(faceLabel).pad(5);
        table.add(faceSelectBox).pad(5);
        table.row();
        table.add(hairLabel).pad(5);
        table.add(hairSelectBox).pad(5);
        table.row();
        table.add(clothingLabel).pad(5);
        table.add(clothingSelectBox).pad(5);
        table.row();
        table.add(createButton).pad(10);

        stage.addActor(table);

        // Update customization when options are changed
        bodySelectBox.addListener(event -> {
            customization.setBodyType(bodySelectBox.getSelected());
            return false;
        });
        faceSelectBox.addListener(event -> {
            customization.setFaceType(faceSelectBox.getSelected());
            return false;
        });
        hairSelectBox.addListener(event -> {
            customization.setHairType(hairSelectBox.getSelected());
            return false;
        });
        clothingSelectBox.addListener(event -> {
            customization.setClothingType(clothingSelectBox.getSelected());
            return false;
        });

        createButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                // Switch to the PlayerScreen with the selected parameters
                game.setScreen(new PlayerScreen(game, authToken, selectedWorld, selectedChannel));
                return true;
            }
            return false;
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        // Render the character with the selected customization
        batch.begin();
        characterRenderer.render(batch, customization, 100, 100);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        characterRenderer.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
