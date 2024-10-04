package io.jyberion.mmorpg.common.entity;

public class PlayerState {
    private String username;
    private int level;
    private int experience;

    public PlayerState(String username) {
        this.username = username;
        this.level = 1;
        this.experience = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void move(String data) {
        // Implement movement logic
    }

    public void attack(String data) {
        // Implement attack logic
    }
}