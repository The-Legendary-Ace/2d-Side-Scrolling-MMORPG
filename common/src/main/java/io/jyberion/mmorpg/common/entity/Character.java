package io.jyberion.mmorpg.common.entity;

import javax.persistence.*;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level;
    private int experience;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, getters, and setters

    public Character() {
        this.level = 1;
        this.experience = 0;
    }

    // Getters and Setters...
}
