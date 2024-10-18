package io.jyberion.mmorpg.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.jyberion.mmorpg.client.network.ClientNetworkManager;
import io.jyberion.mmorpg.client.screens.AssetLoaderScreen;
import io.jyberion.mmorpg.client.screens.LoginScreen;

public class SpiritOnlineGame extends Game {

    private final AssetManager assetManager = new AssetManager(); // Asset Manager
    private ClientNetworkManager networkManager;

    @Override
    public void create() {
        // Initialize the network manager
        networkManager = new ClientNetworkManager();

        // Preload necessary assets (if any)
        preloadAssets();

        // Start with the Asset Loader Screen
        setScreen(new AssetLoaderScreen(this));
    }

    // Preload assets (example usage)
    private void preloadAssets() {
        assetManager.load("assets/UI/login_bg.png", Texture.class);
        assetManager.load("assets/UI/login-button.png", Texture.class);
        // Add other critical assets to load here
    }

    // Provide access to the AssetManager
    public AssetManager getAssetManager() {
        return assetManager;
    }

    // Provide access to the ClientNetworkManager
    public ClientNetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void dispose() {
        super.dispose();

        // Shutdown the network manager
        if (networkManager != null) {
            networkManager.shutdown();
        }

        // Dispose of assets properly
        assetManager.dispose();
    }
}
