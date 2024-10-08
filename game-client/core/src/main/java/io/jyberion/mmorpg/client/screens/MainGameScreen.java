package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Screen;
import io.jyberion.mmorpg.client.SpiritOnlineGame;

public class MainGameScreen implements Screen {
    private SpiritOnlineGame game;
    private String authToken;
    private String selectedChannel;

    public MainGameScreen(SpiritOnlineGame game, String authToken, String selectedChannel) {
        this.game = game;
        this.authToken = authToken;
        this.selectedChannel = selectedChannel;

        // Add your main game logic here
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Render game content
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
