package io.jyberion.mmorpg.common.service;

import io.jyberion.mmorpg.common.entity.Player;
import io.jyberion.mmorpg.common.entity.Inventory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerService {

    private static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.cfg.xml")
            .buildSessionFactory();

    // Save or update player data in the database
    public static CompletableFuture<Void> savePlayerAsync(final Player player) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            Session session = null;
            try {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();
                session.saveOrUpdate(player);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
            } finally {
                if (session != null) session.close();
            }
        });
    }

    // Fetch player by username
    public static CompletableFuture<Player> getPlayerAsync(final String username) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Query query = session.createQuery("FROM Player WHERE username = :username");
                query.setParameter("username", username);
                Player player = (Player) query.uniqueResult();
                return player;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (session != null) session.close();
            }
        });
    }

    // Load player inventory
    public static CompletableFuture<List<Inventory>> getPlayerInventoryAsync(final int playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Query query = session.createQuery("FROM Inventory WHERE player.id = :playerId");
                query.setParameter("playerId", playerId);
                List<Inventory> inventoryList = query.list();
                return inventoryList;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (session != null) session.close();
            }
        });
    }
}
