package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyScreen implements Screen {
    private SpiritOnlineGame game;
    private Stage stage;
    private List<ChannelInfo> channels;
    private String authToken;

    public LobbyScreen(SpiritOnlineGame game, String authToken, List<ChannelInfo> channels) {
        this.game = game;
        this.authToken = authToken;
        this.channels = channels;

        stage = new Stage(new ScreenViewport());

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // UI elements setup
        Label worldLabel = new Label("Select World:", skin);
        SelectBox<String> worldSelectBox = new SelectBox<>(skin);
        Label channelLabel = new Label("Select Channel:", skin);
        SelectBox<String> channelSelectBox = new SelectBox<>(skin);
        TextButton enterButton = new TextButton("Enter", skin);
        
        Label messageLabel = new Label("", skin);
        messageLabel.setVisible(false);  // Initially hidden

        // Extract distinct world names from the channels
        String[] worldNames = channels.stream()
                .map(ChannelInfo::getWorldName)
                .distinct()
                .toArray(String[]::new);

        worldSelectBox.setItems(worldNames);
        
        // Update the channels when a world is selected
        worldSelectBox.addListener(event -> {
            String selectedWorld = worldSelectBox.getSelected();
            String[] channelNames = channels.stream()
                    .filter(channelInfo -> channelInfo.getWorldName().equals(selectedWorld))
                    .map(ChannelInfo::getChannelName)
                    .toArray(String[]::new);
            channelSelectBox.setItems(channelNames);
            return true;
        });

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(worldLabel).pad(5);
        table.add(worldSelectBox).pad(5);
        table.row();
        table.add(channelLabel).pad(5);
        table.add(channelSelectBox).pad(5);
        table.row();
        table.add(enterButton).colspan(2).center().pad(10);
        table.row();
        table.add(messageLabel).colspan(2).center().pad(10);

        stage.addActor(table);
        
        // Input processor
        Gdx.input.setInputProcessor(stage);

        // Listener for the enter button
        enterButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                String selectedChannel = channelSelectBox.getSelected();
                if (selectedChannel != null) {
                    game.setScreen(new MainGameScreen(game, authToken, selectedChannel));
                } else {
                    messageLabel.setText("Please select a channel.");
                    messageLabel.setVisible(true);  // Show the message
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
