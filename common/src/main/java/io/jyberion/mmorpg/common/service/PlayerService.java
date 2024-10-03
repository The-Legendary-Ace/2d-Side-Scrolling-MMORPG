package io.jyberion.mmorpg.common.service;

import io.jyberion.mmorpg.common.entity.Player;
import io.jyberion.mmorpg.common.entity.Inventory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerService {

    private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    // Save or update player data in the database
    public static CompletableFuture<Void> savePlayerAsync(Player player) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();
                session.saveOrUpdate(player);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        });
    }

    // Fetch player by username
    public static CompletableFuture<Player> getPlayerAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("FROM Player WHERE username = :username", Player.class)
                              .setParameter("username", username)
                              .uniqueResult();
            }
        });
    }

    // Load player inventory
    public static CompletableFuture<List<Inventory>> getPlayerInventoryAsync(int playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                return session.createQuery("FROM Inventory WHERE player_id = :playerId", Inventory.class)
                              .setParameter("playerId", playerId)
                              .list();
            }
        });
    }
}
