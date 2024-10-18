package io.jyberion.mmorpg.client.files;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Array;

public class JsonLoader extends AsynchronousAssetLoader<JsonValue, JsonLoader.JsonLoaderParameter> {

    private JsonValue jsonValue;

    public JsonLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, JsonLoaderParameter parameter) {
        JsonReader reader = new JsonReader();
        jsonValue = reader.parse(file.reader());
    }

    @Override
    public JsonValue loadSync(AssetManager manager, String fileName, FileHandle file, JsonLoaderParameter parameter) {
        return jsonValue;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, JsonLoaderParameter parameter) {
        return null;
    }

    public static class JsonLoaderParameter extends AssetLoaderParameters<JsonValue> {
    }
}
