package io.jyberion.mmorpg.common.entity;

import javax.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_quantity")
    private int itemQuantity;

    // Getters and Setters
    // ...
}
