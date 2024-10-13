package io.jyberion.mmorpg.world.model;

public class Player implements GameEntity {
    private String id;
    private String name;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        // Update player state logic
        System.out.println("Updating player: " + name);
    }

    @Override
    public String toString() {
        return "Player{id='" + id + "', name='" + name + "'}";
    }
}
