package io.jyberion.mmorpg.tools;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.tools.packager.PackagerTool;

public class ToolsLauncher extends ApplicationAdapter {

    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());

        // Load a default skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("MMORPG Tools Launcher", skin);
        TextButton packagerButton = new TextButton("Packager Tool", skin);
        TextButton mapEditorButton = new TextButton("Map Editor", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners
        packagerButton.addListener(event -> {
            if (packagerButton.isPressed()) {
                launchPackagerTool();
                return true;
            }
            return false;
        });

        mapEditorButton.addListener(event -> {
            if (mapEditorButton.isPressed()) {
                // launchMapEditorTool();
                return true;
            }
            return false;
        });

        exitButton.addListener(event -> {
            if (exitButton.isPressed()) {
                Gdx.app.exit();
                return true;
            }
            return false;
        });

        // Arrange UI elements
        table.add(titleLabel).padBottom(20).row();
        table.add(packagerButton).width(200).padBottom(10).row();
        table.add(mapEditorButton).width(200).padBottom(10).row();
        table.add(exitButton).width(200);

        Gdx.input.setInputProcessor(stage);
    }

    private void launchPackagerTool() {
        // Code to launch the packager tool
        // For simplicity, we'll start a new instance
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Packager Tool");
        new Lwjgl3Application(new PackagerTool(), config);
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
