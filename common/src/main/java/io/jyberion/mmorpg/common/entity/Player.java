package io.jyberion.mmorpg.common.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    private int level;
    private int experience;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventory;

    // Constructors
    public Player() {}

    public Player(String username) {
        this.username = username;
        this.level = 1;
        this.experience = 0;
    }

    // Getters and Setters
    public int getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public List<Inventory> getInventory() { return inventory; }
    public void setInventory(List<Inventory> inventory) { this.inventory = inventory; }
}