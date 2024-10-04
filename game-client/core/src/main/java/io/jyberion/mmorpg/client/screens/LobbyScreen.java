package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.util.List;

public class LobbyScreen implements Screen {

    private SpiritOnlineGame game;
    private String token;
    private List<ChannelInfo> channels;

    public LobbyScreen(SpiritOnlineGame game, String token, List<ChannelInfo> channels) {
        this.game = game;
        this.token = token;
        this.channels = channels;

        // Initialize lobby UI elements here
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw lobby UI elements here
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing if necessary
    }

    @Override
    public void pause() {
        // Handle pause event
    }

    @Override
    public void resume() {
        // Handle resume event
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    @Override
    public void dispose() {
        // Clean up resources
    }
}
