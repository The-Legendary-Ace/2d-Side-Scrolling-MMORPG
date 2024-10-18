package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;

public class LoginScreen implements Screen {

    private final SpiritOnlineGame game;
    private final Stage stage;
    private final Texture loginBackground;
    private final Texture loginButton;

    public LoginScreen(SpiritOnlineGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        // Load textures from the AssetManager
        loginBackground = game.getAssetManager().get("assets/UI/login_bg.png", Texture.class);
        loginButton = game.getAssetManager().get("assets/UI/login-button.png", Texture.class);

        setupUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void setupUI() {
        Image bgImage = new Image(loginBackground);
        Image loginBtn = new Image(loginButton);

        loginBtn.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new LobbyScreen(game, "authToken", null));
                return true;
            }
            return false;
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(bgImage).expand().fill();
        table.row();
        table.add(loginBtn).padTop(20);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        loginBackground.dispose();
        loginButton.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
