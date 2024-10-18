package io.jyberion.mmorpg.client.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.jyberion.mmorpg.client.SpiritOnlineGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // MacOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new SpiritOnlineGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Spirit-Online-Client");

        // Enable Vsync (limits FPS to monitor refresh rate, helps eliminate screen tearing)
        configuration.useVsync(true);

        // Set the FPS limit to monitor refresh rate + 1 to support fractional refresh rates
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // Set window size (you can modify this)
        configuration.setWindowedMode(600, 800);

        // Set the window to borderless (remove window decoration including minimize, maximize, and close buttons)
        configuration.setDecorated(false);  // This is the correct way to set the window as borderless

        // Optionally set window icons
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
