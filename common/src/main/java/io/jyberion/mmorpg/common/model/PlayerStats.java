package io.jyberion.mmorpg.common.model;

public class PlayerStats {
    private String playerName;
    private String job;
    private int level;
    private int overallRanking;
    private int worldRanking;
    private int str;  // Strength
    private int dex;  // Dexterity
    private int luk;  // Luck
    private int intelligence;  // Intelligence
    private int exp;  // Experience

    public PlayerStats(String playerName, String job, int level, int overallRanking, int worldRanking, int str, int dex, int luk, int intelligence, int exp) {
        this.playerName = playerName;
        this.job = job;
        this.level = level;
        this.overallRanking = overallRanking;
        this.worldRanking = worldRanking;
        this.str = str;
        this.dex = dex;
        this.luk = luk;
        this.intelligence = intelligence;
        this.exp = exp;
    }

    // Getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getOverallRanking() {
        return overallRanking;
    }

    public void setOverallRanking(int overallRanking) {
        this.overallRanking = overallRanking;
    }

    public int getWorldRanking() {
        return worldRanking;
    }

    public void setWorldRanking(int worldRanking) {
        this.worldRanking = worldRanking;
    }

    public int getStrength() {  // Changed from getStr to getStrength
        return str;
    }

    public void setStrength(int str) {
        this.str = str;
    }

    public int getDexterity() {  // Changed from getDex to getDexterity
        return dex;
    }

    public void setDexterity(int dex) {
        this.dex = dex;
    }

    public int getLuck() {  // Changed from getLuk to getLuck
        return luk;
    }

    public void setLuck(int luk) {
        this.luk = luk;
    }

    public int getIntelligence() {  // Changed from getInt to getIntelligence
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getExperience() {  // Changed from getExp to getExperience
        return exp;
    }

    public void setExperience(int exp) {
        this.exp = exp;
    }
}
