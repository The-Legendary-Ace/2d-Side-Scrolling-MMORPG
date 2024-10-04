package io.jyberion.mmorpg.channel.model;

public class PlayerState {
    private String username;
    private int level;
    private int experience;
    private int x;
    private int y;
    // Other player state fields

    public PlayerState(String username) {
        this.username = username;
        this.level = 1; // Default level
        this.experience = 0; // Default experience
        this.x = 0; // Initial position
        this.y = 0;
    }
    
    public String getUsername() { return username; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

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
