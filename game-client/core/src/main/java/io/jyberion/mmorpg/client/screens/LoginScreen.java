package io.jyberion.mmorpg.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.jyberion.mmorpg.client.SpiritOnlineGame;
import io.jyberion.mmorpg.client.network.ClientNetworkManager;
import io.jyberion.mmorpg.common.message.LoginRequestMessage;
import io.jyberion.mmorpg.common.message.LoginResponseMessage;

public class LoginScreen implements Screen {

    private SpiritOnlineGame game;
    private Stage stage;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private ClientNetworkManager networkManager;

    public LoginScreen(SpiritOnlineGame game) {
        this.game = game;
        this.networkManager = game.getNetworkManager();

        stage = new Stage(new ScreenViewport());

        // Create UI elements
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

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
        loginButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                handleLoginAction();
                return true;
            }
            return false;
        });
    }

    private void handleLoginAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Disable input fields during login attempt
        usernameField.setDisabled(true);
        passwordField.setDisabled(true);

        // Send login request to server
        LoginRequestMessage request = new LoginRequestMessage(username, password);
        networkManager.sendLoginRequest(request, this::handleLoginResponse);
    }

    private void handleLoginResponse(LoginResponseMessage response) {
        Gdx.app.postRunnable(() -> {
            if (response.isSuccess()) {
                messageLabel.setText("Login successful!");
                
                // Proceed to the next screen (e.g., world and channel selection)
                game.setScreen(new LobbyScreen(game, response.getToken(), response.getWorlds()));
            } else {
                // Check if the account is banned
                if (response.getMessage().equalsIgnoreCase("banned")) {
                    showDialog("Login Failed", "Your account has been banned.", "OK");
                } else {
                    // Show failure message
                    showDialog("Login Failed", "Login failed: " + response.getMessage(), "Try Again");
                }

                usernameField.setDisabled(false);
                passwordField.setDisabled(false);
            }
        });
    }

    private void showDialog(String title, String message, String buttonText) {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Dialog dialog = new Dialog(title, skin) {
            public void result(Object obj) {
                // Result of dialog action, if needed
            }
        };

        dialog.text(message);
        dialog.button(buttonText); // Show a button to dismiss the dialog
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
