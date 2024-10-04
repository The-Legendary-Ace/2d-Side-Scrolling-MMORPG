package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.client.network.ClientNetworkManager;
import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;

import java.util.function.Consumer;

public class LoginScreen implements Screen {

    private final SpiritOnlineGame game;
    private final Stage stage;
    private final TextField usernameField;
    private final TextField passwordField;
    private final Label messageLabel;
    private final ClientNetworkManager networkManager;
    private final Skin skin;

    public LoginScreen(SpiritOnlineGame game) {
        this.game = game;
        this.networkManager = game.getNetworkManager();

        stage = new Stage(new ScreenViewport());

        // Load UI skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create UI elements
        Label usernameLabel = new Label("Username:", skin);
        usernameField = new TextField("", skin);

        Label passwordLabel = new Label("Password:", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("Login", skin);
        messageLabel = new Label("", skin);

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(usernameLabel).pad(5);
        table.add(usernameField).width(200).pad(5);
        table.row();
        table.add(passwordLabel).pad(5);
        table.add(passwordField).width(200).pad(5);
        table.row();
        table.add(loginButton).colspan(2).center().pad(10);
        table.row();
        table.add(messageLabel).colspan(2).center().pad(10);

        stage.addActor(table);

        // Set input processor
        Gdx.input.setInputProcessor(stage);

        // Add listeners
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                handleLoginAction();
            }
        });
    }

    private void handleLoginAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showPopup("Error", "Username or password cannot be empty.");
            return;
        }

        // Disable input fields during login attempt
        disableLoginFields(true);

        // Send login request to server
        sendLoginRequestWithRetry(username, password);
    }

    private void sendLoginRequestWithRetry(String username, String password) {
        LoginRequestMessage request = new LoginRequestMessage(username, password);

        networkManager.sendLoginRequest(request, response -> handleLoginResponse(response));
    }

    private void handleLoginResponse(LoginResponseMessage response) {
        Gdx.app.postRunnable(() -> {
            if (response.isSuccess()) {
                messageLabel.setText("Login successful!");
                // Proceed to the next screen (e.g., game lobby)
                game.setScreen(new LobbyScreen(game, response.getToken(), response.getChannels()));
            } else {
                showPopup("Login Failed", "Unknown username or password. Please try again.");
                disableLoginFields(false);
            }
        });
    }

    private void disableLoginFields(boolean disable) {
        usernameField.setDisabled(disable);
        passwordField.setDisabled(disable);
    }

    private void showPopup(String title, String message) {
        Dialog dialog = new Dialog(title, skin) {
            @Override
            protected void result(Object object) {
                // Called when dialog is closed
            }
        };
        dialog.text(message);
        dialog.button("OK", true); // Add an "OK" button to close the dialog
        dialog.key(com.badlogic.gdx.Input.Keys.ENTER, true); // Allow Enter key to close the dialog
        dialog.show(stage);
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen
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
        // Update the viewport on window resize
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Called when the application is paused
    }

    @Override
    public void resume() {
        // Called when the application is resumed
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
