package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.common.model.CharacterInfo;
import io.jyberion.mmorpg.common.model.PlayerStats;

import java.util.List;

public class PlayerScreen implements Screen {

    private SpiritOnlineGame game;
    private Stage stage;
    private String selectedWorld;
    private String selectedChannel;
    private String authToken;
    private List<CharacterInfo> characters;

    // Move characterSelectBox to the class level so it can be used in other methods
    private SelectBox<String> characterSelectBox;

    public PlayerScreen(SpiritOnlineGame game, String authToken, String selectedWorld, String selectedChannel) {
        this.game = game;
        this.authToken = authToken;
        this.selectedWorld = selectedWorld;
        this.selectedChannel = selectedChannel;

        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // World and Channel Labels in top-left corner
        Label worldLabel = new Label("World: " + selectedWorld, skin);
        Label channelLabel = new Label("Channel: " + selectedChannel, skin);

        Table topLeftBox = new Table(skin);
        topLeftBox.setBackground("default-round");
        topLeftBox.add(worldLabel).align(Align.left).pad(5);
        topLeftBox.row();
        topLeftBox.add(channelLabel).align(Align.left).pad(5);
        topLeftBox.setPosition(10, Gdx.graphics.getHeight() - 10);
        topLeftBox.top().left();
        topLeftBox.setColor(0.5f, 0.5f, 0.5f, 1);
        stage.addActor(topLeftBox);

        // Character selection elements
        Label characterLabel = new Label("Select Character:", skin);
        characterSelectBox = new SelectBox<>(skin);  // Initialize characterSelectBox here
        TextButton playButton = new TextButton("Play", skin);
        TextButton createCharacterButton = new TextButton("Create New Character", skin);

        // Fetch characters for the selected world and channel from the server
        fetchCharactersForWorldAndChannel(selectedWorld, selectedChannel);

        Table characterTable = new Table();
        characterTable.setFillParent(true);
        characterTable.center();

        characterTable.add(characterLabel).pad(5);
        characterTable.add(characterSelectBox).pad(5);
        characterTable.row();
        characterTable.add(playButton).pad(5);
        characterTable.add(createCharacterButton).pad(5);
        stage.addActor(characterTable);

        // Stats display in the right-hand corner
        Table statsBox = new Table(skin);
        statsBox.setBackground("default-round");
        statsBox.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 300); // Right corner
        statsBox.top().right();
        statsBox.setVisible(false); // Initially hidden
        stage.addActor(statsBox);

        // Listener for character selection
        characterSelectBox.addListener(event -> {
            String selectedCharacter = characterSelectBox.getSelected();
            if (selectedCharacter != null) {
                PlayerStats selectedStats = getPlayerStats(selectedCharacter); // Fetch stats for selected character
                displayCharacterStats(selectedStats, statsBox);
                statsBox.setVisible(true); // Show stats box when character is selected
            }
            return true;
        });

        // Listener for Play button
        playButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                String selectedCharacter = characterSelectBox.getSelected();
                startGameWithCharacter(selectedCharacter);
                return true;
            }
            return false;
        });

        // Listener for Create New Character button
        createCharacterButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new CharacterCreationScreen(game, authToken, selectedWorld, selectedChannel));
                return true;
            }
            return false;
        });

        Gdx.input.setInputProcessor(stage);
    }

    private void fetchCharactersForWorldAndChannel(String world, String channel) {
        // TODO: Send a request to the server to fetch the list of characters for the selected world and channel
        // Mocked data for now
        characters = List.of(
                new CharacterInfo("SinfulLightx", "Hero", 126, 14340, 49052, 618, 4, 4, 4, 90000)
        );

        // Populate the characterSelectBox with character names
        String[] characterNames = characters.stream()
                .map(CharacterInfo::getName) // Get the names of the characters
                .toArray(String[]::new);  // Convert to array for the select box

        if (characterNames.length > 0) {
            characterSelectBox.setItems(characterNames);  // Set the character names in the select box
            characterSelectBox.setSelected(characterNames[0]);  // Optionally select the first character
        } else {
            characterSelectBox.clearItems();  // Clear if no characters are available
        }
    }

    private PlayerStats getPlayerStats(String characterName) {
        // TODO: Fetch the player's stats from the server
        // For now, return mock data
        return new PlayerStats("SinfulLightx", "Hero", 126, 14340, 49052, 618, 4, 4, 4, 90000);
    }

    private void displayCharacterStats(PlayerStats stats, Table statsBox) {
        statsBox.clear(); // Clear existing stats

        Label nameLabel = new Label("Lv. " + stats.getLevel() + " " + stats.getPlayerName(), new Skin(Gdx.files.internal("uiskin.json")));
        Label jobLabel = new Label("Job: " + stats.getJob(), new Skin(Gdx.files.internal("uiskin.json")));
        Label overallRankLabel = new Label("Overall Ranking: " + stats.getOverallRanking(), new Skin(Gdx.files.internal("uiskin.json")));
        Label worldRankLabel = new Label("World Ranking: " + stats.getWorldRanking(), new Skin(Gdx.files.internal("uiskin.json")));
        Label strLabel = new Label("STR: " + stats.getStrength(), new Skin(Gdx.files.internal("uiskin.json")));
        Label intLabel = new Label("INT: " + stats.getIntelligence(), new Skin(Gdx.files.internal("uiskin.json")));
        Label dexLabel = new Label("DEX: " + stats.getDexterity(), new Skin(Gdx.files.internal("uiskin.json")));
        Label lukLabel = new Label("LUK: " + stats.getLuck(), new Skin(Gdx.files.internal("uiskin.json")));
        Label expLabel = new Label("EXP: " + stats.getExperience(), new Skin(Gdx.files.internal("uiskin.json")));

        statsBox.add(nameLabel).pad(5);
        statsBox.row();
        statsBox.add(jobLabel).pad(5);
        statsBox.row();
        statsBox.add(overallRankLabel).pad(5);
        statsBox.row();
        statsBox.add(worldRankLabel).pad(5);
        statsBox.row();
        statsBox.add(strLabel).pad(5);
        statsBox.row();
        statsBox.add(intLabel).pad(5);
        statsBox.row();
        statsBox.add(dexLabel).pad(5);
        statsBox.row();
        statsBox.add(lukLabel).pad(5);
        statsBox.row();
        statsBox.add(expLabel).pad(5);
    }

    private void startGameWithCharacter(String character) {
        // TODO: Start the game with the selected character
        Gdx.app.log("PlayerScreen", "Starting game with character: " + character);
    }

    @Override
    public void show() {
    }

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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
