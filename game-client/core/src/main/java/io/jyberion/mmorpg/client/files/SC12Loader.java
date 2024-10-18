package io.jyberion.mmorpg.client.files;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Disposable;

public class SC12Loader implements Disposable {

    private AssetManager assetManager;
    private SC12FileHandleResolver sc12Resolver;

    public SC12Loader(String sc12FilePath) {
        // Initialize the custom file resolver for the SC12 file
        sc12Resolver = new SC12FileHandleResolver(sc12FilePath);

        // Initialize the AssetManager with the custom resolver
        assetManager = new AssetManager(sc12Resolver);

        // Set custom loaders if needed
        assetManager.setLoader(JsonValue.class, new JsonLoader(sc12Resolver));
        // Add other loaders if you have custom asset types
    }

    public void load(String assetPath) {
        // Determine the type of asset based on the file extension
        if (assetPath.endsWith(".png") || assetPath.endsWith(".jpg") || assetPath.endsWith(".jpeg")) {
            assetManager.load(assetPath, Texture.class);
        } else if (assetPath.endsWith(".json")) {
            assetManager.load(assetPath, JsonValue.class);
        }
        // Add other asset types as needed
    }

    public boolean update() {
        // Update the AssetManager's loading process
        return assetManager.update();
    }

    public float getProgress() {
        // Get the loading progress (0.0 to 1.0)
        return assetManager.getProgress();
    }

    public <T> T get(String assetPath, Class<T> type) {
        // Retrieve a loaded asset
        return assetManager.get(assetPath, type);
    }

    @Override
    public void dispose() {
        // Dispose of the AssetManager and other resources
        assetManager.dispose();
    }
}
