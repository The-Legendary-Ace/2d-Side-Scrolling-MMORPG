package io.jyberion.mmorpg.login.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static Map<String, String> sessions = new HashMap<>();

    public static String cereateSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, username);
        return sessionId;
    }

    public static String getUsername(String sessionId) {
        return sessions.get(sessionId);
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
