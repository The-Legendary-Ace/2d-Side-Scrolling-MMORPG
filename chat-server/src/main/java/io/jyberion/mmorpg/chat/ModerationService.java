package io.jyberion.mmorpg.chat;

import java.util.HashSet;
import java.util.Set;

public class ModerationService {
    private final Set<String> mutedPlayers = new HashSet<>();
    private final Set<String> bannedWords = new HashSet<>();

    public ModerationService() {
        // Add some default banned words
        bannedWords.add("spam");
        bannedWords.add("offensiveWord"); // Add more words as needed
    }

    public boolean isMuted(String player) {
        return mutedPlayers.contains(player);
    }

    public void mutePlayer(String player) {
        mutedPlayers.add(player);
    }

    public void unmutePlayer(String player) {
        mutedPlayers.remove(player);
    }

    public boolean containsBannedWords(String message) {
        for (String word : bannedWords) {
            if (message.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }
}
