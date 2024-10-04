package io.jyberion.mmorpg.client;

import com.badlogic.gdx.Game;
import io.jyberion.mmorpg.client.network.ClientNetworkManager;
import io.jyberion.mmorpg.client.screens.LoginScreen;

public class SpiritOnlineGame extends Game {

    private ClientNetworkManager networkManager;

    @Override
    public void create() {
        networkManager = new ClientNetworkManager();
        setScreen(new LoginScreen(this));
    }

    public ClientNetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (networkManager != null) {
            networkManager.shutdown();
        }
    }
}
