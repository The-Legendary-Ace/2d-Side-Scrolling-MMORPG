package io.jyberion.mmorpg.channel.model;

public class PlayerState {
    private final String username;
    private int x;
    private int y;
    // Other player state fields

    public PlayerState(String username) {
        this.username = username;
        this.x = 0; // Initial position
        this.y = 0;
    }

    public void move(String direction) {
        // Simple movement logic
        switch (direction) {
            case "UP":
                y += 1;
                break;
            case "DOWN":
                y -= 1;
                break;
            case "LEFT":
                x -= 1;
                break;
            case "RIGHT":
                x += 1;
                break;
            default:
                break;
        }
    }

    public void attack(String targetUsername) {
        // Implement attack logic
    }

    // Getters and setters
}
