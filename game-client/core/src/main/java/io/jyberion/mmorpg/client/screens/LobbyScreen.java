package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.common.message.LoginResponseMessage.WorldWithChannels;
import io.jyberion.mmorpg.common.model.ChannelInfo;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyScreen implements Screen {
    private SpiritOnlineGame game;
    private Stage stage;
    private List<WorldWithChannels> worldsWithChannels;
    private String authToken;

    public LobbyScreen(SpiritOnlineGame game, String authToken, List<WorldWithChannels> worldsWithChannels) {
        this.game = game;
        this.authToken = authToken;
        this.worldsWithChannels = worldsWithChannels;

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

        // Extract distinct world names from the worldsWithChannels
        String[] worldNames = worldsWithChannels.stream()
                .map(WorldWithChannels::getWorldName)
                .toArray(String[]::new);

        if (worldNames.length > 0) {
            worldSelectBox.setItems(worldNames);
            worldSelectBox.setSelected(worldNames[0]);  // Auto-select the first world

            // Set initial channels for the first world
            List<ChannelInfo> initialChannels = worldsWithChannels.stream()
                    .filter(world -> world.getWorldName().equals(worldNames[0]))
                    .flatMap(world -> world.getChannels().stream())
                    .collect(Collectors.toList());

            if (!initialChannels.isEmpty()) {
                String[] initialChannelNames = initialChannels.stream()
                        .map(ChannelInfo::getChannelName)
                        .toArray(String[]::new);
                channelSelectBox.setItems(initialChannelNames);
                enterButton.setDisabled(false);  // Enable enter button
            } else {
                channelSelectBox.clearItems();
                enterButton.setDisabled(true);  // Disable enter button
                messageLabel.setText("No channels available for the selected world.");
                messageLabel.setVisible(true);
            }
        } else {
            worldSelectBox.clearItems();
            channelSelectBox.clearItems();
            enterButton.setDisabled(true);  // Disable enter button
            messageLabel.setText("No worlds available.");
            messageLabel.setVisible(true);
        }

        // Update the channels when a world is selected
        worldSelectBox.addListener(event -> {
            String selectedWorld = worldSelectBox.getSelected();
            List<ChannelInfo> channelsForSelectedWorld = worldsWithChannels.stream()
                    .filter(world -> world.getWorldName().equals(selectedWorld))
                    .flatMap(world -> world.getChannels().stream())
                    .collect(Collectors.toList());

            if (!channelsForSelectedWorld.isEmpty()) {
                String[] channelNames = channelsForSelectedWorld.stream()
                        .map(ChannelInfo::getChannelName)
                        .toArray(String[]::new);
                channelSelectBox.setItems(channelNames);
                enterButton.setDisabled(false);  // Enable enter button if channels are available
                messageLabel.setVisible(false);
            } else {
                channelSelectBox.clearItems();
                enterButton.setDisabled(true);  // Disable enter button if no channels
                messageLabel.setText("No channels available for the selected world.");
                messageLabel.setVisible(true);
            }
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
                String selectedWorld = worldSelectBox.getSelected();
                if (selectedChannel != null && selectedWorld != null) {
                    // Go to PlayerScreen and pass the selected world and channel
                    game.setScreen(new PlayerScreen(game, authToken, selectedWorld, selectedChannel));
                } else {
                    messageLabel.setText("Please select both a world and a channel.");
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
