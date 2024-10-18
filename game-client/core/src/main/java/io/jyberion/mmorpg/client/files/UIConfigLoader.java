package io.jyberion.mmorpg.client.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class UIConfigLoader {

    /**
     * Loads and parses the JSON configuration file.
     *
     * @param path Path to the JSON configuration file.
     * @return Parsed JsonValue object representing the configuration.
     */
    public static JsonValue loadConfig(String path) {
        FileHandle file = Gdx.files.internal(path);
        JsonReader jsonReader = new JsonReader();
        return jsonReader.parse(file);
    }
}
