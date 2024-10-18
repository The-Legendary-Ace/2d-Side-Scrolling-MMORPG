package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;

import java.io.File;

public class AssetLoaderScreen implements Screen {

    private final SpiritOnlineGame game;
    private final Stage stage;
    private final AssetManager assetManager;
    private final JsonReader jsonReader;

    public AssetLoaderScreen(SpiritOnlineGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.assetManager = new AssetManager();
        this.jsonReader = new JsonReader();

        Gdx.input.setInputProcessor(stage);
        queueAssets();  // Queue all assets for loading
    }

    // Queue assets from the predefined folders
    private void queueAssets() {
        loadAssetsFromFolder("assets/UI");
        loadAssetsFromFolder("assets/Characters");
        loadAssetsFromFolder("assets/Item");
        loadAssetsFromFolder("assets/Equipment");
        loadAssetsFromFolder("assets/Cash");
        loadAssetsFromFolder("assets/NPC");
        loadAssetsFromFolder("assets/Monster");
        loadAssetsFromFolder("assets/Sounds");
        loadAssetsFromFolder("assets/Skills");
    }

    // Load assets from a given folder (recursive handling for subdirectories)
    private void loadAssetsFromFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the folder exists and is a directory
        if (!folder.exists() || !folder.isDirectory()) {
            Gdx.app.error("AssetLoader", "Invalid folder: " + folderPath);
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            Gdx.app.log("AssetLoader", "No files found in: " + folderPath);
            return;
        }

        // Process each file or subdirectory in the folder
        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively load assets from subdirectories
                loadAssetsFromFolder(file.getPath());
            } else if (file.getName().endsWith(".png")) {
                // Queue PNG files for loading as textures
                assetManager.load(file.getPath(), Texture.class);
                Gdx.app.log("AssetLoader", "Queued: " + file.getPath());
            } else if (file.getName().endsWith(".json")) {
                // Load JSON files for potential use
                loadJson(file);
            }
        }
    }

    // Load JSON file and log its content
    private void loadJson(File jsonFile) {
        try {
            JsonValue json = jsonReader.parse(Gdx.files.absolute(jsonFile.getPath()));
            Gdx.app.log("AssetLoader", "Loaded JSON: " + jsonFile.getName());
        } catch (Exception e) {
            Gdx.app.error("AssetLoader", "Failed to load JSON: " + jsonFile.getName(), e);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update()) {
            // Switch to the login screen after assets are loaded
            game.setScreen(new LoginScreen(game));
        } else {
            // Log the current loading progress
            float progress = assetManager.getProgress() * 100;
            Gdx.app.log("AssetLoaderScreen", "Loading: " + (int) progress + "%");
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        assetManager.dispose();
    }

    @Override
    public void show() {
        // Optional setup when the screen is shown
    }

    @Override
    public void hide() {
        // Optional cleanup when the screen is hidden
    }

    @Override
    public void pause() {
        // Handle game pause state
    }

    @Override
    public void resume() {
        // Handle game resume state
    }
}
